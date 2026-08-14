import request from '@/utils/request'

// 我的会话列表
export function listConversations() {
  return request({ url: '/ai/conversation/list', method: 'get' })
}

// 新建会话
export function createConversation() {
  return request({ url: '/ai/conversation', method: 'post' })
}

// 删除会话
export function delConversation(id) {
  return request({ url: '/ai/conversation/' + id, method: 'delete' })
}

// 会话消息记录
export function listMessages(conversationId) {
  return request({ url: '/ai/message/' + conversationId, method: 'get' })
}

// 系统数据智能分析
export function getSummary() {
  return request({ url: '/ai/summary/overview', method: 'get' })
}
