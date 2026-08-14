package com.gp.business.ai.domain;

import lombok.Data;

/**
 * AI 对话请求参数
 */
@Data
public class AiChatRequest {

    /** 会话ID */
    private Long conversationId;

    /** 用户提问内容 */
    private String message;

}
