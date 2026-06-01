<template>
  <div class="home-container">
    <Navbar />
    
    <main class="main-content">
      <div class="content-header">
        <h1>用户管理</h1>
      </div>
      
      <div class="content-body">
        <div class="search-bar">
          <div class="search-item">
            <input 
              type="text" 
              v-model="searchForm.nickname" 
              placeholder="搜索昵称"
              class="search-input"
            />
          </div>

          <div class="search-item">
            <select v-model="searchForm.status" class="search-select">
              <option value="">全部状态</option>
              <option :value="1">正常</option>
              <option :value="0">禁用</option>
            </select>
          </div>
          <div class="search-actions">
            <button class="btn btn-primary" @click="handleSearch">
              <span>🔍</span> 搜索
            </button>
            <button class="btn btn-secondary" @click="handleReset">
              <span>↺</span> 重置
            </button>
          </div>
        </div>
        
        <div class="user-table">
          <table>
            <thead>
              <tr>
                <th>序号</th>
                <th>昵称</th>
                <th>目标岗位</th>
                <th>等级</th>
                <th>状态</th>
                <th>注册时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(user, index) in userList" :key="user.id" @click="handleViewDetail(user)" class="clickable-row">
                <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
                <td>{{ user.nickname || '-' }}</td>
                <td>{{ user.targetJob || '-' }}</td>
                <td>
                  <span class="level-badge">{{ user.currentLevel || 1 }}级</span>
                </td>
                <td>
                  <span class="status-badge" :class="{ 'status-disabled': user.status === 0 }">
                    {{ user.status === 1 ? '正常' : '禁用' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createTime) }}</td>
                <td @click.stop>
                  <div class="action-buttons">
                    <button class="btn-action btn-edit" @click="handleEdit(user)" title="编辑">
                      ✏️
                    </button>
                    <button class="btn-action btn-level" @click="handleUpdateLevel(user)" title="修改等级">
                      📊
                    </button>
                    <button class="btn-action btn-status" @click="handleToggleStatus(user)" :title="user.status === 1 ? '禁用' : '启用'">
                      {{ user.status === 1 ? '🚫' : '✅' }}
                    </button>
                    <button class="btn-action btn-delete" @click="handleDelete(user)" title="删除">
                      🗑️
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          
          <div v-if="userList.length === 0 && !loading" class="empty-state">
            <div class="empty-icon">📋</div>
            <p>暂无用户数据</p>
          </div>
          
          <div v-if="loading" class="loading-state">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>
        </div>
        
        <div class="pagination" v-if="total > 0">
          <div class="pagination-info">
            共 {{ total }} 条记录
          </div>
          <div class="pagination-controls">
            <button 
              class="page-btn" 
              :disabled="pageNum <= 1"
              @click="pageNum--; loadUsers()"
            >
              ‹
            </button>
            <span class="page-info">{{ pageNum }} / {{ totalPages }}</span>
            <button 
              class="page-btn" 
              :disabled="pageNum >= totalPages"
              @click="pageNum++; loadUsers()"
            >
              ›
            </button>
          </div>
        </div>
      </div>
    </main>
    
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑用户</h3>
          <button class="modal-close" @click="showModal = false">×</button>
        </div>
        
        <form @submit.prevent="handleSubmit" class="modal-form">
          <div class="form-row">
            <div class="form-group">
              <label>昵称</label>
              <input 
                type="text" 
                v-model="formData.nickname"
                placeholder="请输入昵称"
              />
            </div>
            <div class="form-group">
              <label>目标岗位</label>
              <select v-model="formData.targetJob">
                <option value="">请选择岗位</option>
                <option v-for="job in targetJobs" :key="job" :value="job">{{ job }}</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>当前等级</label>
              <select v-model="formData.currentLevel">
                <option v-for="level in 10" :key="level" :value="level">{{ level }}级</option>
              </select>
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="formData.status">
                <option :value="1">正常</option>
                <option :value="0">禁用</option>
              </select>
            </div>
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn btn-cancel" @click="showModal = false">取消</button>
            <button type="submit" class="btn btn-submit">确认</button>
          </div>
        </form>
      </div>
    </div>
    
    <div v-if="showLevelModal" class="modal-overlay" @click.self="showLevelModal = false">
      <div class="modal-content modal-small">
        <div class="modal-header">
          <h3>修改等级</h3>
          <button class="modal-close" @click="showLevelModal = false">×</button>
        </div>
        
        <form @submit.prevent="handleLevelSubmit" class="modal-form">
          <div class="form-group">
            <label>当前等级</label>
            <select v-model="levelForm.currentLevel">
              <option v-for="level in 10" :key="level" :value="level">{{ level }}级</option>
            </select>
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn btn-cancel" @click="showLevelModal = false">取消</button>
            <button type="submit" class="btn btn-submit">确认</button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="showDetailModal" class="modal-overlay" @click.self="showDetailModal = false">
      <div class="modal-content modal-large">
        <div class="modal-header">
          <h3>用户详情 - {{ currentUser?.nickname }}</h3>
          <button class="modal-close" @click="showDetailModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="tab-nav">
            <button 
              v-for="tab in tabs" 
              :key="tab.key"
              class="tab-btn"
              :class="{ active: activeTab === tab.key }"
              @click="activeTab = tab.key"
            >
              {{ tab.icon }} {{ tab.name }}
            </button>
          </div>

          <div v-if="activeTab === 'favorites'" class="tab-content">
            <div v-if="userDetail.favorites?.length > 0" class="list-container">
              <div v-for="(item, index) in userDetail.favorites" :key="index" class="list-item">
                <div class="item-title">{{ item.title }}</div>
                <div class="item-meta">
                  <span v-if="item.category">分类：{{ item.category }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-tab">
              <div class="empty-icon">📚</div>
              <p>暂无收藏题目</p>
            </div>
          </div>

          <div v-if="activeTab === 'wrongs'" class="tab-content">
            <div v-if="userDetail.wrongs?.length > 0" class="list-container">
              <div v-for="(item, index) in userDetail.wrongs" :key="index" class="list-item">
                <div class="item-title">{{ item.problemTitle }}</div>
                <div class="item-meta">
                  <span>错误次数：{{ item.wrongCount }}</span>
                  <span v-if="item.lastWrongTime">最后答错：{{ formatDate(item.lastWrongTime) }} {{ formatTime(item.lastWrongTime) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-tab">
              <div class="empty-icon">❌</div>
              <p>暂无错题记录</p>
            </div>
          </div>

          <div v-if="activeTab === 'clocks'" class="tab-content">
            <div v-if="userDetail.clocks?.length > 0" class="list-container">
              <div v-for="(item, index) in userDetail.clocks" :key="index" class="list-item">
                <div class="item-title">签到日期：{{ formatDate(item.clockDate) }}</div>
                <div class="item-meta">
                  <span>签到时间：{{ formatTime(item.clockTime) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-tab">
              <div class="empty-icon">📅</div>
              <p>暂无签到记录</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import Navbar from '../components/Navbar.vue'
import { 
  getUserPage, 
  updateUser, 
  deleteUser, 
  updateUserStatus,
  updateUserLevel,
  getUserDetail
} from '../api/user'
import { updateUserCount } from '../stores/dashboardStore'

const loading = ref(false)
const showModal = ref(false)
const showLevelModal = ref(false)
const showDetailModal = ref(false)

const tabs = [
  { key: 'favorites', name: '收藏题目', icon: '⭐' },
  { key: 'wrongs', name: '错题记录', icon: '❌' },
  { key: 'clocks', name: '签到记录', icon: '📅' }
]
const activeTab = ref('favorites')

const searchForm = reactive({
  nickname: '',
  status: ''
})

const userList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const currentUser = ref(null)
const userDetail = ref({
  user: null,
  favorites: [],
  wrongs: [],
  clocks: []
})

const formData = reactive({
  id: null,
  nickname: '',
  avatar: '',
  targetJob: '',
  currentLevel: 1,
  status: 1
})

const levelForm = reactive({
  id: null,
  currentLevel: 1
})

const targetJobs = ref(['Java后端', '前端开发', 'Python开发', '全栈开发', '其他'])

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

watch(total, (newCount) => {
  updateUserCount(newCount)
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      nickname: searchForm.nickname || null,
      status: searchForm.status === '' ? null : searchForm.status,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getUserPage(params)
    userList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadUsers()
}

const handleReset = () => {
  searchForm.nickname = ''
  searchForm.status = ''
  pageNum.value = 1
  loadUsers()
}

const handleEdit = (user) => {
  formData.id = user.id
  formData.nickname = user.nickname || ''
  formData.avatar = user.avatar || ''
  formData.targetJob = user.targetJob || ''
  formData.currentLevel = user.currentLevel || 1
  formData.status = user.status
  showModal.value = true
}

const handleSubmit = async () => {
  try {
    const submitData = {
      id: formData.id,
      nickname: formData.nickname,
      avatar: formData.avatar,
      targetJob: formData.targetJob,
      currentLevel: formData.currentLevel,
      status: formData.status
    }
    await updateUser(submitData)
    showModal.value = false
    loadUsers()
  } catch (error) {
    console.error('提交失败', error)
  }
}

const handleUpdateLevel = (user) => {
  levelForm.id = user.id
  levelForm.currentLevel = user.currentLevel || 1
  showLevelModal.value = true
}

const handleLevelSubmit = async () => {
  try {
    await updateUserLevel(levelForm.id, levelForm.currentLevel)
    showLevelModal.value = false
    loadUsers()
  } catch (error) {
    console.error('修改等级失败', error)
  }
}

const handleToggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  if (confirm(`确定要${action}用户 ${user.nickname} 吗？`)) {
    try {
      await updateUserStatus(user.id, newStatus)
      loadUsers()
    } catch (error) {
      console.error('修改状态失败', error)
    }
  }
}

const handleDelete = async (user) => {
  if (confirm(`确定要删除用户 ${user.nickname} 吗？此操作不可恢复！`)) {
    try {
      await deleteUser(user.id)
      loadUsers()
    } catch (error) {
      console.error('删除失败', error)
    }
  }
}

const handleViewDetail = async (user) => {
  currentUser.value = user
  showDetailModal.value = true
  activeTab.value = 'favorites'
  
  try {
    const res = await getUserDetail(user.id)
    userDetail.value = res.data || {
      user: user,
      favorites: [],
      wrongs: [],
      clocks: []
    }
  } catch (error) {
    console.error('获取用户详情失败', error)
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const formatTime = (time) => {
  if (!time) return '-'
  const t = new Date(time)
  return `${String(t.getHours()).padStart(2, '0')}:${String(t.getMinutes()).padStart(2, '0')}:${String(t.getSeconds()).padStart(2, '0')}`
}
</script>

<style scoped>
.home-container {
  display: flex;
  min-height: 100vh;
  background: #1a1a1a;
}

.main-content {
  flex: 1;
  margin-top: 70px;
  min-height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
}

.content-header {
  background: #2a2a2a;
  padding: 24px 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  position: sticky;
  top: 0;
  z-index: 50;
}

.content-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
}

.content-body {
  flex: 1;
  padding: 32px 40px;
}

.search-bar {
  background: #2a2a2a;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
  align-items: flex-end;
  flex-wrap: wrap;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.search-item {
  flex: 1;
  min-width: 200px;
}

.search-input,
.search-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #444;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #333;
  color: #fff;
}

.search-input:focus,
.search-select:focus {
  outline: none;
  border-color: #667eea;
}

.search-actions {
  display: flex;
  gap: 12px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-secondary {
  background: #444;
  color: #aaa;
}

.user-table {
  background: #2a2a2a;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #333;
  color: #fff;
}

th {
  padding: 16px 20px;
  text-align: left;
  font-weight: 600;
  font-size: 14px;
  white-space: nowrap;
}

td {
  padding: 16px 20px;
  border-bottom: 1px solid #333;
  font-size: 14px;
  color: #ddd;
}

.clickable-row {
  cursor: pointer;
  transition: all 0.3s ease;
}

.clickable-row:hover {
  background: #333;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: #43e97b;
  color: #1a1a1a;
}

.status-badge.status-disabled {
  background: #fa709a;
}

.level-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: #667eea;
  color: white;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-action {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  background: #333;
}

.btn-action:hover {
  background: #444;
}

.empty-state,
.loading-state {
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p,
.loading-state p {
  font-size: 16px;
  color: #666;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(102, 126, 234, 0.2);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #2a2a2a;
  border-radius: 16px;
  margin-top: 24px;
}

.pagination-info {
  font-size: 14px;
  color: #888;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-btn {
  width: 36px;
  height: 36px;
  border: 2px solid #444;
  border-radius: 10px;
  background: #333;
  font-size: 18px;
  font-weight: 600;
  color: #667eea;
  cursor: pointer;
}

.page-btn:hover:not(:disabled) {
  border-color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  font-weight: 600;
  color: #ddd;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  background: #2a2a2a;
  border-radius: 24px;
  overflow: hidden;
}

.modal-header {
  padding: 24px 32px;
  background: #333;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 700;
}

.modal-close {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 24px;
  cursor: pointer;
}

.modal-form {
  padding: 32px;
  max-height: calc(90vh - 100px);
  overflow-y: auto;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #ddd;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #444;
  border-radius: 10px;
  font-size: 14px;
  background: #333;
  color: #fff;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #333;
}

.btn-cancel {
  padding: 12px 32px;
  border: 2px solid #444;
  border-radius: 10px;
  background: #333;
  font-size: 14px;
  font-weight: 600;
  color: #aaa;
  cursor: pointer;
}

.btn-cancel:hover {
  border-color: #667eea;
  color: #667eea;
}

.btn-submit {
  padding: 12px 32px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.modal-small {
  max-width: 400px;
}

.modal-large {
  max-width: 900px;
}

.modal-body {
  padding: 24px 32px;
  max-height: calc(90vh - 100px);
  overflow-y: auto;
}

.tab-nav {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  border-bottom: 2px solid #333;
}

.tab-btn {
  padding: 12px 24px;
  border: none;
  background: transparent;
  color: #888;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  position: relative;
  bottom: -2px;
}

.tab-btn:hover {
  color: #fff;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-content {
  min-height: 300px;
}

.list-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-item {
  padding: 16px;
  background: #333;
  border-radius: 12px;
  border: 1px solid #444;
}

.item-title {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.item-meta {
  font-size: 13px;
  color: #888;
}

.item-meta span {
  margin-right: 16px;
}

.empty-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-tab .empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-tab p {
  font-size: 14px;
  color: #666;
}
</style>
