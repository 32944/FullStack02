import { ref } from 'vue'

// 共享状态
export const userCount = ref(0)
export const problemCount = ref(0)
export const conversationCount = ref(0)
export const recentConversations = ref([])

// 保存状态到 localStorage
export function saveState() {
  localStorage.setItem('dashboardData', JSON.stringify({
    userCount: userCount.value,
    problemCount: problemCount.value,
    conversationCount: conversationCount.value,
    recentConversations: recentConversations.value
  }))
}

// 从 localStorage 加载状态
export function loadState() {
  const data = localStorage.getItem('dashboardData')
  if (data) {
    try {
      const parsed = JSON.parse(data)
      userCount.value = parsed.userCount || 0
      problemCount.value = parsed.problemCount || 0
      conversationCount.value = parsed.conversationCount || 0
      recentConversations.value = parsed.recentConversations || []
    } catch (e) {
      console.error('加载状态失败', e)
    }
  }
}

// 更新用户计数
export function updateUserCount(count) {
  userCount.value = count
  saveState()
}

// 更新题目计数
export function updateProblemCount(count) {
  problemCount.value = count
  saveState()
}

// 添加对话记录
export function addConversation(message) {
  conversationCount.value++
  recentConversations.value.unshift({
    content: message.substring(0, 50) + (message.length > 50 ? '...' : ''),
    time: new Date().toLocaleString()
  })
  if (recentConversations.value.length > 5) {
    recentConversations.value.pop()
  }
  saveState()
}
