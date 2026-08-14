package com.gp.business.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 助手配置项（application-dev.yml 中 gp.ai 前缀）
 *
 * 兼容所有 OpenAI 风格 /v1/chat/completions 接口：
 * - DeepSeek:  base-url = https://api.deepseek.com        model = deepseek-chat
 * - 通义千问:   base-url = https://dashscope.aliyuncs.com/compatible-mode/v1  model = qwen-plus
 * - Kimi:     base-url = https://api.moonshot.cn/v1       model = moonshot-v1-8k
 * - 智谱GLM:  base-url = https://open.bigmodel.cn/api/paas/v4  model = glm-4-flash
 * - OpenAI:   base-url = https://api.openai.com           model = gpt-4o-mini
 */
@Data
@Component
@ConfigurationProperties(prefix = "gp.ai")
public class AiChatProperties {

    /** 演示模式：true 时不调用外部 API，本地模拟流式回复（答辩演示不怕断网/无 Key） */
    private boolean mock = true;

    /** OpenAI 兼容接口地址（填到域名或 /v1 为止，代码会拼接 /v1/chat/completions） */
    private String baseUrl = "https://api.deepseek.com";

    /** API Key，为空时自动进入演示模式 */
    private String apiKey = "";

    /** 模型名称 */
    private String model = "deepseek-chat";

    /** 采样温度 0.0 ~ 2.0 */
    private Double temperature = 0.7;

    /** 接口超时时间（毫秒） */
    private Long timeoutMillis = 60000L;

    /** 每次对话携带的历史消息条数 */
    private Integer maxHistory = 20;

    /** 系统提示词：设定 AI 的角色和行为 */
    private String systemPrompt = "你是 GP 智能助手，一个友好、专业的中文 AI 助手。回答保持简洁、准确、条理清晰。";

}
