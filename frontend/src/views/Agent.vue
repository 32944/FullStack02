<template>
  <div class="home-container">
    <Navbar />
    
    <main class="main-content">
      <div class="content-header">
        <h1>AI模块管理</h1>
      </div>
      
      <!-- 导航菜单 -->
      <div class="nav-tabs">
        <div 
          :class="['tab-item', { active: activeTab === 'upload' }]"
          @click="activeTab = 'upload'"
        >
          📄 上传RAG文件
        </div>
        <div 
          :class="['tab-item', { active: activeTab === 'chat' }]"
          @click="activeTab = 'chat'"
        >
          💬 对话窗口
        </div>
      </div>
      
      <div class="content-body">
        <!-- 上传RAG文件页面 -->
        <div v-if="activeTab === 'upload'" class="upload-page">
          <div class="panel">
            <h3 class="panel-title">📚 知识库管理</h3>
            <p class="panel-desc">上传PDF文件到RAG知识库，让AI助手能够基于您的文档回答问题</p>
            
            <div class="upload-section">
              <div class="upload-area" @click="triggerUpload" @dragover.prevent="isDragOver = true" @dragleave="isDragOver = false" @drop.prevent="handleDrop">
                <div class="upload-icon">
                  <div class="icon-circle">
                    <span>📎</span>
                  </div>
                </div>
                <div class="upload-text">点击或拖拽PDF文件到此处</div>
                <div class="upload-hint">支持PDF格式文件，单个文件最大50MB</div>
                <div class="upload-features">
                  <div class="feature-item">
                    <span class="feature-icon">✅</span>
                    <span>智能文本提取</span>
                  </div>
                  <div class="feature-item">
                    <span class="feature-icon">✅</span>
                    <span>向量嵌入存储</span>
                  </div>
                  <div class="feature-item">
                    <span class="feature-icon">✅</span>
                    <span>语义检索支持</span>
                  </div>
                </div>
              </div>
              <input 
                type="file" 
                ref="fileInput" 
                accept=".pdf" 
                @change="handleFileSelect"
                style="display: none"
                multiple
              />
            </div>
            
            <div v-if="uploading" class="uploading-status">
              <div class="spinner"></div>
              <div class="uploading-info">
                <span class="uploading-text">正在处理文件...</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: progress + '%' }"></div>
                </div>
              </div>
            </div>
            
            <div v-if="uploadResult" class="upload-result" :class="uploadResult.success ? 'success' : 'error'">
              <div class="result-icon">{{ uploadResult.success ? '🎉' : '❌' }}</div>
              <div class="result-content">
                <div class="result-title">{{ uploadResult.success ? '上传成功' : '上传失败' }}</div>
                <div class="result-message">{{ uploadResult.message }}</div>
              </div>
            </div>
            
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon">📄</div>
                <div class="stat-content">
                  <div class="stat-value">{{ stats.uploaded_files }}</div>
                  <div class="stat-label">已上传文件</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">📝</div>
                <div class="stat-content">
                  <div class="stat-value">{{ stats.knowledge_chunks }}</div>
                  <div class="stat-label">知识片段</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">🔍</div>
                <div class="stat-content">
                  <div class="stat-value">{{ stats.search_count }}</div>
                  <div class="stat-label">检索次数</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 对话窗口页面 -->
        <div v-if="activeTab === 'chat'" class="chat-page">
          <div class="chat-container">
            <div class="chat-header">
              <div class="header-left">
                <h3>🤖 RAG智能助手</h3>
                <span class="online-dot"></span>
              </div>
              <div class="header-actions">
                <button class="icon-btn" @click="clearHistory" title="清空对话">
                  🗑️
                </button>
              </div>
            </div>
            
            <!-- 功能说明卡片 -->
            <div class="features-card">
              <div class="features-header">
                <span class="features-icon">💡</span>
                <span class="features-title">我可以帮您做这些事</span>
              </div>
              <div class="features-list">
                <div class="feature-box" @click="insertQuickQuery('查询所有用户')">
                  <div class="feature-icon-box">👥</div>
                  <div class="feature-text">
                    <div class="feature-name">用户管理</div>
                    <div class="feature-desc">查询/删除/修改/禁用用户</div>
                  </div>
                </div>
                <div class="feature-box" @click="insertQuickQuery('查询所有题目')">
                  <div class="feature-icon-box">📚</div>
                  <div class="feature-text">
                    <div class="feature-name">题目管理</div>
                    <div class="feature-desc">查询/添加/修改/删除题目</div>
                  </div>
                </div>
                <div class="feature-box" @click="insertQuickQuery('帮我解释一下Java多线程')">
                  <div class="feature-icon-box">🤔</div>
                  <div class="feature-text">
                    <div class="feature-name">RAG知识库问答</div>
                    <div class="feature-desc">基于上传的文档回答问题</div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="chat-messages" ref="messagesContainer">
              <div v-if="messages.length === 0" class="welcome-section">
                <div class="welcome-icon">👋</div>
                <h3>欢迎使用RAG智能助手</h3>
                <p>您可以询问任何问题，我会基于知识库为您提供准确的回答</p>
              </div>
              <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
                <div class="message-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
                <div class="message-content">
                  <div class="message-text">{{ msg.content }}</div>
                  <div v-if="msg.sources && msg.sources.length > 0" class="message-sources">
                    <div class="sources-title">📚 参考来源：</div>
                    <div v-for="(source, sIndex) in msg.sources" :key="sIndex" class="source-item">
                      {{ source }}
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="loading" class="message assistant">
                <div class="message-avatar">🤖</div>
                <div class="message-content">
                  <div class="typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="chat-input-area">
              <textarea 
                v-model="inputMessage"
                placeholder="输入您的问题，或点击上方快速查询..."
                @keydown.enter.prevent="sendMessage"
                rows="3"
              ></textarea>
              <button class="send-btn" @click="sendMessage" :disabled="loading || !inputMessage.trim()">
                <span class="send-icon">🚀</span>
                发送
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'

const activeTab = ref('upload')
const fileInput = ref(null)
const uploading = ref(false)
const uploadResult = ref(null)
const isDragOver = ref(false)
const progress = ref(0)

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)

// 统计数据
const stats = ref({
  uploaded_files: 0,
  knowledge_chunks: 0,
  search_count: 0
})

const API_BASE_URL = 'http://127.0.0.1:8001'

// 获取统计数据
const loadStatistics = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/statistics`)
    const result = await response.json()
    if (result.success) {
      stats.value = result.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 上传文件相关
const triggerUpload = () => {
  fileInput.value.click()
}

const handleDrop = (e) => {
  isDragOver.value = false
  const files = e.dataTransfer.files
  if (files.length > 0) {
    handleFile(files[0])
  }
}

const handleFileSelect = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  await handleFile(file)
}

const handleFile = async (file) => {
  if (!file.name.toLowerCase().endsWith('.pdf')) {
    uploadResult.value = { success: false, message: '只支持PDF格式文件，请选择PDF文件上传' }
    return
  }
  
  await uploadFile(file)
}

const uploadFile = async (file) => {
  uploading.value = true
  uploadResult.value = null
  progress.value = 0
  
  const progressInterval = setInterval(() => {
    if (progress.value < 90) {
      progress.value += Math.random() * 15
    }
  }, 300)
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await fetch(`${API_BASE_URL}/api/upload`, {
      method: 'POST',
      body: formData
    })
    
    progress.value = 100
    const result = await response.json()
    uploadResult.value = result
    // 上传成功后刷新统计
    if (result.success) {
      await loadStatistics()
    }
  } catch (error) {
    console.error('上传失败:', error)
    uploadResult.value = { success: false, message: '上传失败：' + error.message }
  } finally {
    clearInterval(progressInterval)
    uploading.value = false
    progress.value = 0
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// 对话相关
const insertQuickQuery = (text) => {
  inputMessage.value = text
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const question = inputMessage.value.trim()
  inputMessage.value = ''
  
  messages.value.push({
    role: 'user',
    content: question
  })
  
  scrollToBottom()
  loading.value = true
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/chat/query`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ question })
    })
    
    const result = await response.json()
    
    if (result.success) {
      messages.value.push({
        role: 'assistant',
        content: result.data.answer,
        sources: result.data.sources || []
      })
      // 对话后刷新统计
      await loadStatistics()
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，我遇到了一些问题，请稍后再试。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const clearHistory = async () => {
  try {
    await fetch(`${API_BASE_URL}/api/chat/history/clear`, {
      method: 'POST'
    })
    messages.value = []
  } catch (error) {
    console.error('清空历史失败:', error)
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 获取历史对话
const loadHistory = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/chat/history`)
    const result = await response.json()
    if (result.success) {
      messages.value = result.data
    }
  } catch (error) {
    console.error('获取历史对话失败:', error)
  }
}

// 页面加载时获取统计数据和历史记录
onMounted(() => {
  loadStatistics()
  loadHistory()
})
</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
  background: #1a1a1a;
  overflow: hidden;
}

.main-content {
  flex: 1;
  margin-top: 70px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 70px);
  overflow: hidden;
}

.content-header {
  background: #2a2a2a;
  color: white;
  padding: 24px 40px;
  flex-shrink: 0;
}

.content-header h1 {
  font-size: 26px;
  font-weight: 700;
  margin: 0;
}

.nav-tabs {
  display: flex;
  background: #2a2a2a;
  padding: 0 40px;
  border-bottom: 1px solid #333;
}

.tab-item {
  padding: 16px 32px;
  color: #888;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}

.tab-item:hover {
  color: #fff;
  background: #333;
}

.tab-item.active {
  color: #667eea;
  border-bottom-color: #667eea;
  background: #333;
}

.content-body {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
}

.panel {
  background: #2a2a2a;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.panel-title {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
}

.panel-desc {
  font-size: 14px;
  color: #888;
  margin: 0 0 32px 0;
}

/* 上传页面样式 */
.upload-section {
  margin-bottom: 32px;
}

.upload-area {
  border: 3px dashed #444;
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #333;
}

.upload-area:hover,
.isDragOver {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: scale(1.02);
}

.upload-icon {
  margin-bottom: 20px;
}

.icon-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  font-size: 40px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.upload-text {
  font-size: 20px;
  color: #fff;
  margin-bottom: 8px;
  font-weight: 600;
}

.upload-hint {
  font-size: 14px;
  color: #888;
  margin-bottom: 24px;
}

.upload-features {
  display: flex;
  justify-content: center;
  gap: 32px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #aaa;
  font-size: 14px;
}

.feature-icon {
  font-size: 16px;
}

.uploading-status {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 32px;
  padding: 24px;
  background: #333;
  border-radius: 12px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #444;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  flex-shrink: 0;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.uploading-info {
  flex: 1;
}

.uploading-text {
  display: block;
  color: #fff;
  font-size: 15px;
  margin-bottom: 12px;
}

.progress-bar {
  height: 8px;
  background: #444;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.upload-result {
  margin-top: 32px;
  padding: 24px;
  border-radius: 12px;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.upload-result.success {
  background: rgba(72, 187, 120, 0.15);
  border: 1px solid #48bb78;
}

.upload-result.error {
  background: rgba(245, 101, 101, 0.15);
  border: 1px solid #f56565;
}

.result-icon {
  font-size: 32px;
}

.result-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.upload-result.success .result-title { color: #48bb78; }
.upload-result.error .result-title { color: #f56565; }

.result-message {
  font-size: 14px;
  color: #aaa;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 32px;
}

.stat-card {
  background: #333;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #888;
}

/* 对话页面样式 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;
  background: #2a2a2a;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #333;
  border-bottom: 1px solid #444;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-header h3 {
  margin: 0;
  font-size: 18px;
  color: #fff;
}

.online-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #48bb78;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.icon-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #444;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s;
}

.icon-btn:hover {
  background: #667eea;
}

/* 功能说明卡片 */
.features-card {
  background: #333;
  padding: 20px 24px;
  border-bottom: 1px solid #444;
}

.features-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.features-icon {
  font-size: 20px;
}

.features-title {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.features-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.feature-box {
  background: #2a2a2a;
  border: 1px solid #444;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-box:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.feature-icon-box {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.feature-text {
  flex: 1;
}

.feature-name {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 12px;
  color: #888;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.welcome-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.welcome-section h3 {
  font-size: 20px;
  color: #fff;
  margin: 0 0 8px 0;
}

.welcome-section p {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 85%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message.assistant {
  align-self: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #444;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  padding: 14px 18px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.message.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-text {
  background: #444;
  color: #fff;
  border-bottom-left-radius: 4px;
}

.message-sources {
  margin-top: 12px;
  padding: 14px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 10px;
  font-size: 12px;
  border-left: 3px solid #667eea;
}

.sources-title {
  color: #667eea;
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 13px;
}

.source-item {
  color: #888;
  margin: 6px 0;
  padding: 6px 0;
  border-bottom: 1px solid #333;
}

.source-item:last-child {
  border-bottom: none;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 14px 18px;
}

.typing-indicator span {
  width: 10px;
  height: 10px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input-area {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  background: #333;
  border-top: 1px solid #444;
}

.chat-input-area textarea {
  flex: 1;
  padding: 14px 18px;
  background: #2a2a2a;
  border: 1px solid #444;
  border-radius: 12px;
  color: #fff;
  font-size: 14px;
  resize: none;
  outline: none;
  font-family: inherit;
  transition: all 0.3s;
}

.chat-input-area textarea:focus {
  border-color: #667eea;
}

.send-btn {
  padding: 0 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  align-self: flex-end;
  display: flex;
  align-items: center;
  gap: 8px;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.send-icon {
  font-size: 16px;
}
</style>
