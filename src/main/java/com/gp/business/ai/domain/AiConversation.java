package com.gp.business.ai.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 对话会话
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_ai_conversation")
public class AiConversation extends BaseEntity {

    /** 所属用户ID */
    private Long userId;

    /** 会话标题（首次提问后自动用问题内容生成） */
    private String title;

}
