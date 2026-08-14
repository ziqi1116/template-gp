package com.gp.business.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.business.ai.domain.AiConversation;
import com.gp.business.ai.domain.AiMessage;
import com.gp.business.ai.mapper.AiConversationMapper;
import com.gp.business.ai.mapper.AiMessageMapper;
import com.gp.common.exception.BusinessException;
import com.gp.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI 会话管理：会话与消息都按当前登录用户隔离
 */
@Service
public class AiConversationService extends ServiceImpl<AiConversationMapper, AiConversation> {

    @Autowired
    private AiMessageMapper messageMapper;

    /** 当前用户的会话列表（新会话在前） */
    public List<AiConversation> listMine() {
        return this.list(new LambdaQueryWrapper<AiConversation>()
                .eq(AiConversation::getUserId, SecurityUtils.getUserId())
                .orderByDesc(AiConversation::getId));
    }

    /** 新建会话 */
    public AiConversation createMine() {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(SecurityUtils.getUserId());
        conversation.setTitle("新对话");
        this.save(conversation);
        return conversation;
    }

    /** 删除会话（连同消息一起逻辑删除） */
    public void removeMine(Long id) {
        checkOwner(id);
        this.removeById(id);
        messageMapper.delete(new LambdaQueryWrapper<AiMessage>()
                .eq(AiMessage::getConversationId, id));
    }

    /** 某会话下的全部消息（按时间正序） */
    public List<AiMessage> messagesOf(Long conversationId) {
        checkOwner(conversationId);
        return messageMapper.selectList(new LambdaQueryWrapper<AiMessage>()
                .eq(AiMessage::getConversationId, conversationId)
                .orderByAsc(AiMessage::getId));
    }

    /** 校验会话归属，防止越权读取他人对话 */
    private void checkOwner(Long conversationId) {
        AiConversation conversation = this.getById(conversationId);
        if (conversation == null || !SecurityUtils.getUserId().equals(conversation.getUserId())) {
            throw new BusinessException("会话不存在或无权访问");
        }
    }

}
