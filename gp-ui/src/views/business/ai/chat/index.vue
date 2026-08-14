<template>
  <div class="ai-chat-container">
    <!-- 左侧：会话列表 -->
    <el-card shadow="never" class="conversation-card">
      <el-button type="primary" class="new-btn" :icon="Plus" @click="handleNewConversation">新建对话</el-button>
      <div class="conversation-list">
        <div
          v-for="item in conversations"
          :key="item.id"
          class="conversation-item"
          :class="{ active: item.id === currentId }"
          @click="handleSwitch(item)"
        >
          <el-icon class="item-icon"><ChatDotRound /></el-icon>
          <div class="item-info">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-time">{{ item.createTime }}</div>
          </div>
          <el-icon class="item-del" @click.stop="handleDelete(item)"><Delete /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 右侧：聊天区 -->
    <el-card shadow="never" class="chat-card">
      <template #header>
        <div class="chat-header">
          <span class="chat-title">{{ currentTitle }}</span>
          <el-button type="primary" plain size="small" :icon="DataAnalysis" :loading="summaryLoading" @click="handleSummary">
            AI 数据分析
          </el-button>
        </div>
      </template>

      <div ref="messageArea" class="message-area">
        <div v-if="messages.length === 0" class="empty-tip">
          <el-icon :size="46" color="#c0c4cc"><ChatDotRound /></el-icon>
          <p>你好，我是 GP 智能助手，可以回答技术问题、帮你梳理思路，快开始对话吧～</p>
        </div>
        <div v-for="msg in messages" :key="msg._key ?? msg.id" class="message-row" :class="msg.role">
          <div class="avatar">{{ msg.role === 'user' ? '我' : 'AI' }}</div>
          <div class="bubble">{{ msg.content }}<span v-if="msg.streaming" class="cursor">▍</span></div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="input"
          type="textarea"
          :rows="3"
          resize="none"
          placeholder="输入你的问题，Enter 发送，Shift + Enter 换行"
          :disabled="streaming"
          @keydown.enter.prevent="handleSend"
        />
        <el-button
          type="primary"
          class="send-btn"
          :icon="Promotion"
          :loading="streaming"
          :disabled="!input.trim()"
          @click="handleSend"
        >{{ streaming ? '回答中' : '发 送' }}</el-button>
      </div>
    </el-card>

    <!-- 系统数据智能分析结果 -->
    <el-dialog title="系统运行智能分析" v-model="summaryVisible" width="560px">
      <div class="summary-text">{{ summaryText }}</div>
      <template #footer>
        <el-button type="primary" @click="summaryVisible = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, getCurrentInstance } from 'vue'
import { Plus, Delete, DataAnalysis, Promotion } from '@element-plus/icons-vue'
import { listConversations, createConversation, delConversation, listMessages, getSummary } from '@/api/business/ai'
import { getToken } from '@/utils/auth'

const { proxy } = getCurrentInstance()

const conversations = ref([])
const currentId = ref(null)
const currentTitle = ref('新对话')
const messages = ref([])
const input = ref('')
const streaming = ref(false)
const messageArea = ref(null)
const summaryVisible = ref(false)
const summaryText = ref('')
const summaryLoading = ref(false)

onMounted(async () => {
  await loadConversations()
  // 首次进入自动创建一个会话
  if (conversations.value.length === 0) {
    await handleNewConversation()
  } else {
    handleSwitch(conversations.value[0])
  }
})

/** 加载左侧会话列表 */
function loadConversations() {
  return listConversations().then(res => {
    conversations.value = res.data || []
  })
}

/** 新建会话并切换过去 */
function handleNewConversation() {
  createConversation().then(res => {
    conversations.value.unshift(res.data)
    handleSwitch(res.data)
  })
}

/** 切换会话：拉取历史消息 */
function handleSwitch(item) {
  if (streaming.value || item.id === currentId.value) return
  currentId.value = item.id
  currentTitle.value = item.title
  listMessages(item.id).then(res => {
    messages.value = (res.data || []).map(m => ({ role: m.role, content: m.content, id: m.id }))
    scrollToBottom()
  })
}

/** 删除会话 */
function handleDelete(item) {
  proxy.$modal.confirm('确认删除会话「' + item.title + '」？').then(() => {
    return delConversation(item.id)
  }).then(async () => {
    proxy.$modal.msgSuccess('删除成功')
    if (item.id === currentId.value) {
      currentId.value = null
      messages.value = []
      await loadConversations()
      if (conversations.value.length > 0) handleSwitch(conversations.value[0])
      else handleNewConversation()
    } else {
      loadConversations()
    }
  }).catch(() => {})
}

/** 发送消息：SSE 流式接收 AI 回答 */
async function handleSend() {
  const text = input.value.trim()
  if (!text || streaming.value || !currentId.value) return
  input.value = ''
  streaming.value = true

  // 先在页面追加上用户消息与空的 AI 气泡
  messages.value.push({ role: 'user', content: text, _key: 'u' + Date.now() })
  const aiMsg = { role: 'assistant', content: '', streaming: true, _key: 'a' + Date.now() }
  messages.value.push(aiMsg)
  scrollToBottom()

  try {
    const resp = await fetch(import.meta.env.VITE_APP_BASE_API + '/ai/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
      },
      body: JSON.stringify({ conversationId: currentId.value, message: text })
    })
    if (!resp.ok || !resp.body) throw new Error('HTTP ' + resp.status)

    const reader = resp.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      // SSE 帧以空行分隔，逐帧解析
      const frames = buffer.split('\n\n')
      buffer = frames.pop()
      frames.forEach(frame => handleFrame(frame, aiMsg))
    }
    if (buffer) handleFrame(buffer, aiMsg)
  } catch (e) {
    aiMsg.content += (aiMsg.content ? '\n\n' : '') + '（连接 AI 服务失败：' + e.message + '，请检查后端与 gp.ai 配置）'
  } finally {
    aiMsg.streaming = false
    streaming.value = false
  }
}

/** 解析单帧 SSE：message=增量内容 / done=结束 / error=异常 */
function handleFrame(frame, aiMsg) {
  let eventName = 'message'
  let data = ''
  frame.split('\n').forEach(line => {
    if (line.startsWith('event:')) eventName = line.slice(6).trim()
    else if (line.startsWith('data:')) data += line.slice(5).trim()
  })
  if (!data) return
  try {
    const obj = JSON.parse(data)
    if (eventName === 'message' && obj.content) {
      aiMsg.content += obj.content
      scrollToBottom()
    } else if (eventName === 'done') {
      // 首次提问后后端生成了新标题，同步到左侧列表
      const item = conversations.value.find(c => c.id === obj.conversationId)
      if (item) item.title = obj.title
      if (obj.conversationId === currentId.value) currentTitle.value = obj.title
    } else if (eventName === 'error') {
      aiMsg.content += '\n（' + (obj.msg || 'AI 服务异常') + '）'
    }
  } catch (e) {
    // 非 JSON 帧直接忽略
  }
}

/** 系统数据智能分析 */
function handleSummary() {
  summaryLoading.value = true
  getSummary().then(res => {
    summaryText.value = res.data
    summaryVisible.value = true
  }).finally(() => {
    summaryLoading.value = false
  })
}

function scrollToBottom() {
  nextTick(() => {
    if (messageArea.value) messageArea.value.scrollTop = messageArea.value.scrollHeight
  })
}
</script>

<style scoped lang="scss">
.ai-chat-container {
  display: flex;
  gap: 16px;
  height: calc(100vh - 140px);
}

/* 左侧会话列表 */
.conversation-card {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 14px;
  }
}

.new-btn {
  width: 100%;
  margin-bottom: 12px;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover { background: #f5f7fa; }
  &.active { background: #ecf5ff; }

  .item-icon { color: #409eff; flex-shrink: 0; }
  .item-del { color: #c0c4cc; flex-shrink: 0; margin-left: auto;
    &:hover { color: #f56c6c; } }

  .item-info {
    flex: 1;
    min-width: 0;

    .item-title {
      font-size: 13px;
      color: #303133;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .item-time {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }
}

/* 右侧聊天区 */
.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .chat-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}

.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 8px 4px;
}

.empty-tip {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;

  p { margin-top: 12px; font-size: 13px; }
}

.message-row {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;

  .avatar {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
    color: #fff;
  }

  .bubble {
    max-width: 72%;
    padding: 10px 14px;
    border-radius: 8px;
    font-size: 14px;
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-word;
  }

  &.user {
    flex-direction: row-reverse;

    .avatar { background: #409eff; }
    .bubble { background: #ecf5ff; color: #303133; }
  }

  &.assistant {
    .avatar { background: #67c23a; }
    .bubble { background: #f4f4f5; color: #303133; }
  }
}

/* 打字机光标 */
.cursor {
  display: inline-block;
  color: #409eff;
  animation: blink 0.8s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 底部输入区 */
.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .el-textarea { flex: 1; }
  .send-btn { height: 40px; }
}

.summary-text {
  white-space: pre-wrap;
  line-height: 1.8;
  font-size: 14px;
  color: #303133;
  max-height: 50vh;
  overflow-y: auto;
}
</style>
