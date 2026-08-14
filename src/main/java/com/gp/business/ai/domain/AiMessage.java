package com.gp.business.ai.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 对话消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_ai_message")
public class AiMessage extends BaseEntity {

    /** 消息角色：user-用户提问 / assistant-AI回答 */
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    /** 所属会话ID */
    private Long conversationId;

    /** 所属用户ID */
    private Long userId;

    /** 消息角色 */
    private String role;

    /** 消息内容 */
    private String content;

    /** 生成该回答的模型（演示模式为 mock） */
    private String model;

}
