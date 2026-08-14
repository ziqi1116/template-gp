package com.gp.business.ai.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.business.ai.config.AiChatProperties;
import com.gp.business.ai.domain.AiChatRequest;
import com.gp.business.ai.domain.AiConversation;
import com.gp.business.ai.domain.AiMessage;
import com.gp.business.ai.mapper.AiConversationMapper;
import com.gp.business.ai.mapper.AiMessageMapper;
import com.gp.business.student.domain.Student;
import com.gp.business.student.mapper.StudentMapper;
import com.gp.common.exception.BusinessException;
import com.gp.common.utils.SecurityUtils;
import com.gp.framework.domain.SysLogininfor;
import com.gp.framework.domain.SysOperLog;
import com.gp.framework.mapper.SysLogininforMapper;
import com.gp.framework.mapper.SysOperLogMapper;
import com.gp.system.mapper.SysRoleMapper;
import com.gp.system.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.stereotype.Service;

/**
 * AI 对话核心服务
 *
 * 调用链路：前端 fetch /ai/chat（SSE）→ 本服务异步线程 → RestTemplate 流式请求
 * 大模型 /v1/chat/completions → 逐段解析 data: {...} → SseEmitter 推回前端打字机效果。
 */
@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    /** OpenAI 兼容接口的对话端点 */
    private static final String CHAT_API_PATH = "/v1/chat/completions";

    /** 流式输出线程池：个人毕设场景小队列即可 */
    private final ExecutorService executor = new ThreadPoolExecutor(
            1, 4, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50),
            new ThreadFactory());

    @Autowired
    private AiChatProperties properties;

    @Autowired
    private RestTemplate aiRestTemplate;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private AiMessageMapper messageMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SysLogininforMapper logininforMapper;

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 发送对话（SSE 流式返回）
     * 事件约定：message=增量内容 / done=结束(含会话ID与标题) / error=出错信息
     */
    public SseEmitter chat(AiChatRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (StrUtil.isBlank(request.getMessage())) {
            throw new BusinessException("消息内容不能为空");
        }
        AiConversation conversation = conversationMapper.selectById(request.getConversationId());
        if (conversation == null || !userId.equals(conversation.getUserId())) {
            throw new BusinessException("会话不存在或无权访问");
        }
        String message = request.getMessage().trim();

        // 1. 落库用户消息
        saveMessage(conversation.getId(), userId, AiMessage.ROLE_USER, message, null);

        // 2. 取最近 N 条历史（含刚保存的提问）
        List<AiMessage> history = messageMapper.selectList(new LambdaQueryWrapper<AiMessage>()
                .eq(AiMessage::getConversationId, conversation.getId())
                .orderByDesc(AiMessage::getId)
                .last("LIMIT " + properties.getMaxHistory()));
        Collections.reverse(history);

        // 3. 异步流式输出，立即返回 emitter
        SseEmitter emitter = new SseEmitter(properties.getTimeoutMillis() + 10_000L);
        boolean useMock = isMockMode();
        executor.execute(() -> doChat(emitter, conversation, history, message, useMock));
        return emitter;
    }

    /** 在异步线程中完成：调接口/模拟 → 推流 → 落库 → 发结束事件 */
    private void doChat(SseEmitter emitter, AiConversation conversation,
                        List<AiMessage> history, String question, boolean useMock) {
        StringBuilder full = new StringBuilder();
        try {
            if (useMock) {
                mockStream(emitter, question, full);
            } else {
                apiStream(emitter, history, full);
            }
            // 落库 AI 回答
            String model = useMock ? "mock" : properties.getModel();
            saveMessage(conversation.getId(), conversation.getUserId(), AiMessage.ROLE_ASSISTANT, full.toString(), model);
            // 首次对话自动生成会话标题
            String title = conversation.getTitle();
            if ("新对话".equals(title)) {
                title = question.length() > 20 ? question.substring(0, 20) + "…" : question;
                AiConversation update = new AiConversation();
                update.setId(conversation.getId());
                update.setTitle(title);
                conversationMapper.updateById(update);
            }
            JSONObject done = JSONUtil.createObj()
                    .set("conversationId", conversation.getId())
                    .set("title", title);
            emitter.send(SseEmitter.event().name("done").data(done));
            emitter.complete();
        } catch (Exception e) {
            log.error("AI 对话流式输出失败: {}", e.getMessage());
            try {
                emitter.send(SseEmitter.event().name("error")
                        .data(JSONUtil.createObj().set("msg", "AI 服务调用失败：" + e.getMessage())));
                emitter.complete();
            } catch (Exception ignored) {
            }
        }
    }

    /** 真实调用大模型：RestTemplate.execute 拿到原始流，逐行解析 SSE */
    private void apiStream(SseEmitter emitter, List<AiMessage> history, StringBuilder full) {
        String url = properties.getBaseUrl() + CHAT_API_PATH;
        JSONObject body = buildRequestBody(history, true);

        aiRestTemplate.execute(url, HttpMethod.POST,
                request -> {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getHeaders().setBearerAuth(properties.getApiKey());
                    byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
                    request.getBody().write(payload);
                },
                response -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.startsWith("data:")) {
                                continue;
                            }
                            String payload = line.substring(5).trim();
                            if ("[DONE]".equals(payload)) {
                                break;
                            }
                            String content = extractDeltaContent(payload);
                            if (StrUtil.isNotEmpty(content)) {
                                full.append(content);
                                emitter.send(SseEmitter.event().name("message")
                                        .data(JSONUtil.createObj().set("content", content)));
                            }
                        }
                    }
                    return null;
                });
    }

    /** 从一段 data: {...} 中取出增量文本（兼容 role 首包、usage 尾包等空增量） */
    private String extractDeltaContent(String payload) {
        try {
            JSONObject chunk = JSONUtil.parseObj(payload);
            JSONArray choices = chunk.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return null;
            }
            JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
            if (delta == null) {
                return null;
            }
            return delta.getStr("content");
        } catch (Exception e) {
            return null;
        }
    }

    /** 演示模式：本地模拟打字机效果，不依赖外部网络与 Key */
    private void mockStream(SseEmitter emitter, String question, StringBuilder full) throws Exception {
        String answer = "【演示模式】当前未配置大模型 API Key，这是一条本地模拟回复。\n\n"
                + "你的问题：「" + question + "」\n\n"
                + "开启真实 AI 的方法：\n"
                + "1. 打开 application-dev.yml 的 gp.ai 配置；\n"
                + "2. 把 mock 改为 false，并在 api-key 填入你的 Key；\n"
                + "3. 重启系统即可，DeepSeek / 通义千问 / Kimi / OpenAI 均可对接。";
        int step = 4;
        for (int i = 0; i < answer.length(); i += step) {
            String piece = answer.substring(i, Math.min(i + step, answer.length()));
            full.append(piece);
            emitter.send(SseEmitter.event().name("message")
                    .data(JSONUtil.createObj().set("content", piece)));
            Thread.sleep(30);
        }
    }

    /** 组装 OpenAI 格式请求体：system 提示词 + 历史消息 */
    private JSONObject buildRequestBody(List<AiMessage> history, boolean stream) {
        List<JSONObject> messages = new ArrayList<>();
        messages.add(JSONUtil.createObj().set("role", "system").set("content", properties.getSystemPrompt()));
        for (AiMessage item : history) {
            messages.add(JSONUtil.createObj().set("role", item.getRole()).set("content", item.getContent()));
        }
        return new JSONObject()
                .set("model", properties.getModel())
                .set("temperature", properties.getTemperature())
                .set("stream", stream)
                .set("messages", messages);
    }

    /**
     * 系统数据智能分析：汇总用户/角色/业务/日志统计，让大模型生成运行报告
     */
    public String summarizeOverview() {
        JSONObject stats = collectStats();
        if (isMockMode()) {
            return mockSummary(stats);
        }
        String prompt = "以下是某管理系统的实时运行数据（JSON）：\n" + stats.toStringPretty() + "\n\n"
                + "请生成一段 200 字左右的运行分析报告：先总体概述系统使用情况，"
                + "再指出 1-2 个值得注意的点（如存在登录失败、操作量异常等），最后给出 1 条简短运维建议。";
        List<JSONObject> messages = new ArrayList<>();
        messages.add(JSONUtil.createObj().set("role", "system")
                .set("content", "你是系统运行分析助手，擅长根据统计数据输出简明的中文分析报告。"));
        messages.add(JSONUtil.createObj().set("role", "user").set("content", prompt));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());
        String body = new JSONObject()
                .set("model", properties.getModel())
                .set("temperature", 0.3)
                .set("stream", false)
                .set("messages", messages)
                .toString();

        ResponseEntity<String> response = aiRestTemplate.postForEntity(
                properties.getBaseUrl() + CHAT_API_PATH, new HttpEntity<>(body, headers), String.class);
        JSONObject result = JSONUtil.parseObj(response.getBody());
        JSONArray choices = result.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new BusinessException("AI 接口返回异常");
        }
        return choices.getJSONObject(0).getJSONObject("message").getStr("content");
    }

    /** 汇总系统运行统计：用户/角色/学生/今日登录/今日操作 */
    private JSONObject collectStats() {
        Date today = DateUtil.beginOfDay(new Date());
        Long userCount = userMapper.selectCount(null);
        Long roleCount = roleMapper.selectCount(null);
        Long studentCount = studentMapper.selectCount(null);
        Long todayLoginTotal = logininforMapper.selectCount(new LambdaQueryWrapper<SysLogininfor>()
                .ge(SysLogininfor::getLoginTime, today));
        Long todayLoginFail = logininforMapper.selectCount(new LambdaQueryWrapper<SysLogininfor>()
                .ge(SysLogininfor::getLoginTime, today)
                .eq(SysLogininfor::getStatus, "1"));
        Long todayOperCount = operLogMapper.selectCount(new LambdaQueryWrapper<SysOperLog>()
                .ge(SysOperLog::getOperTime, today));

        return JSONUtil.createObj()
                .set("date", DateUtil.today())
                .set("userCount", userCount)
                .set("roleCount", roleCount)
                .set("studentCount", studentCount)
                .set("todayLoginTotal", todayLoginTotal)
                .set("todayLoginFail", todayLoginFail)
                .set("todayOperCount", todayOperCount);
    }

    /** 演示模式下的分析报告（同样基于真实统计数据） */
    private String mockSummary(JSONObject stats) {
        return "【演示模式】基于真实系统统计生成的模拟分析报告：\n\n"
                + "截至 " + stats.getStr("date") + "，系统共有注册用户 " + stats.getInt("userCount")
                + " 人、角色 " + stats.getInt("roleCount") + " 个、学生数据 " + stats.getInt("studentCount")
                + " 条；今日登录 " + stats.getInt("todayLoginTotal") + " 次（其中失败 "
                + stats.getInt("todayLoginFail") + " 次），累计业务操作 "
                + stats.getInt("todayOperCount") + " 次。\n\n"
                + "整体运行平稳。配置 gp.ai.api-key 后，本报告将改由大模型基于同样的数据自动生成，"
                + "并能给出更细致的风险提示与运维建议。";
    }

    /** 未配置 Key 或显式开启 mock 时进入演示模式 */
    private boolean isMockMode() {
        return properties.isMock() || StrUtil.isBlank(properties.getApiKey());
    }

    private void saveMessage(Long conversationId, Long userId, String role, String content, String model) {
        AiMessage message = new AiMessage();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setModel(model);
        messageMapper.insert(message);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    /** 简单命名线程工厂，方便日志排查 */
    private static class ThreadFactory implements java.util.concurrent.ThreadFactory {
        private final AtomicInteger index = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ai-chat-" + index.getAndIncrement());
        }
    }

}
