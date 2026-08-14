package com.gp.business.ai.controller;

import java.util.List;

import com.gp.business.ai.domain.AiChatRequest;
import com.gp.business.ai.domain.AiConversation;
import com.gp.business.ai.domain.AiMessage;
import com.gp.business.ai.service.AiChatService;
import com.gp.business.ai.service.AiConversationService;
import com.gp.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "AI 智能助手")
@RestController
@RequestMapping("/ai")
public class AiChatController {

    @Autowired
    private AiConversationService conversationService;

    @Autowired
    private AiChatService aiChatService;

    @Operation(summary = "我的会话列表")
    @GetMapping("/conversation/list")
    public Result<List<AiConversation>> listConversation() {
        return Result.success(conversationService.listMine());
    }

    @Operation(summary = "新建会话")
    @PostMapping("/conversation")
    public Result<AiConversation> createConversation() {
        return Result.success(conversationService.createMine());
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/conversation/{id}")
    public Result<Void> removeConversation(@PathVariable Long id) {
        conversationService.removeMine(id);
        return Result.success();
    }

    @Operation(summary = "会话消息记录")
    @GetMapping("/message/{conversationId}")
    public Result<List<AiMessage>> listMessage(@PathVariable Long conversationId) {
        return Result.success(conversationService.messagesOf(conversationId));
    }

    @Operation(summary = "AI 对话（SSE 流式输出）")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody AiChatRequest request) {
        return aiChatService.chat(request);
    }

    @Operation(summary = "系统数据智能分析")
    @GetMapping("/summary/overview")
    public Result<String> summaryOverview() {
        return Result.success(aiChatService.summarizeOverview());
    }

}
