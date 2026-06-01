<template>
  <div class="home-container">
    <Navbar />
    
    <main class="main-content">
      <div class="content-header">
        <h1>题目管理</h1>
      </div>
      
      <div class="content-body">
        <div class="search-bar">
          <div class="search-item">
            <input 
              type="text" 
              v-model="searchForm.keyword" 
              placeholder="搜索题目标题"
              class="search-input"
            />
          </div>
          
          <div class="search-item">
            <select v-model="searchForm.categoryId" class="search-select">
              <option value="">全部分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>

          <div class="search-item">
            <select v-model="searchForm.status" class="search-select">
              <option value="">全部状态</option>
              <option :value="1">启用</option>
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
        
        <div class="action-bar">
          <button class="btn btn-add" @click="handleAdd">
            <span>➕</span> 添加题目
          </button>
        </div>
        
        <div class="problem-table">
          <table>
            <thead>
              <tr>
                <th>序号</th>
                <th>题目标题</th>
                <th>分类</th>
                <th>难度</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(problem, index) in problemList" :key="problem.id">
                <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
                <td class="title-cell">{{ problem.title }}</td>
                <td>{{ getCategoryName(problem.categoryId) }}</td>
                <td>
                  <span class="level-badge" :class="getLevelClass(problem.difficulty)">
                    {{ getLevelText(problem.difficulty) }}
                  </span>
                </td>
                <td>
                  <span class="status-badge" :class="{ 'status-disabled': problem.status === 0 }">
                    {{ problem.status === 1 ? '启用' : '禁用' }}
                  </span>
                </td>
                <td>
                  <div class="action-buttons">
                    <button class="btn-action btn-status" @click="handleToggleStatus(problem)" :title="problem.status === 1 ? '禁用' : '启用'">
                      {{ problem.status === 1 ? '🚫' : '✅' }}
                    </button>
                    <button class="btn-action btn-delete" @click="handleDelete(problem)" title="删除">
                      🗑️
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          
          <div v-if="problemList.length === 0 && !loading" class="empty-state">
            <div class="empty-icon">📚</div>
            <p>暂无题目数据</p>
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
              @click="pageNum--; loadProblems()"
            >
              ‹
            </button>
            <span class="page-info">{{ pageNum }} / {{ totalPages }}</span>
            <button 
              class="page-btn" 
              :disabled="pageNum >= totalPages"
              @click="pageNum++; loadProblems()"
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
          <h3>批量添加题目</h3>
          <button class="modal-close" @click="showModal = false">×</button>
        </div>
        
        <form @submit.prevent="handleSubmit" class="modal-form">
          <div class="form-group">
            <label>题目文本（一行一个问题）</label>
            <textarea 
              v-model="formData.text"
              placeholder="请输入题目，每行一个问题&#10;&#10;例如：&#10;Java中什么是多线程？&#10;Spring Boot的核心注解有哪些？&#10;MySQL索引的作用是什么？"
              rows="10"
              required
            ></textarea>
            <p class="form-hint">系统将自动对题目进行分类和难度评估</p>
          </div>
          
          <div class="modal-footer">
            <button type="button" class="btn btn-cancel" @click="showModal = false">取消</button>
            <button type="submit" class="btn btn-submit">批量添加</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import Navbar from '../components/Navbar.vue'
import { 
  getProblemPage, 
  deleteProblem, 
  updateProblemStatus,
  getCategoryList,
  batchAddProblems
} from '../api/problem'

const loading = ref(false)
const showModal = ref(false)

const searchForm = reactive({
  keyword: '',
  categoryId: '',
  status: ''
})

const problemList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const categories = ref([])

const formData = reactive({
  id: null,
  text: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => {
  loadCategories()
  loadProblems()
})

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadProblems = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchForm.keyword || null,
      categoryId: searchForm.categoryId === '' ? null : searchForm.categoryId,
      status: searchForm.status === '' ? null : searchForm.status,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getProblemPage(params)
    problemList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载题目列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadProblems()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = ''
  searchForm.status = ''
  pageNum.value = 1
  loadProblems()
}

const handleAdd = () => {
  formData.text = ''
  showModal.value = true
}

const handleSubmit = async () => {
  try {
    await batchAddProblems(formData.text)
    showModal.value = false
    loadProblems()
  } catch (error) {
    console.error('批量添加失败', error)
  }
}

const handleToggleStatus = async (problem) => {
  const newStatus = problem.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  if (confirm(`确定要${action}题目 "${problem.title}" 吗？`)) {
    try {
      await updateProblemStatus(problem.id, newStatus)
      loadProblems()
    } catch (error) {
      console.error('修改状态失败', error)
    }
  }
}

const handleDelete = async (problem) => {
  if (confirm(`确定要删除题目 "${problem.title}" 吗？此操作不可恢复！`)) {
    try {
      await deleteProblem(problem.id)
      loadProblems()
    } catch (error) {
      console.error('删除失败', error)
    }
  }
}

const getCategoryName = (categoryId) => {
  const cat = categories.value.find(c => c.id === categoryId)
  return cat ? cat.name : '-'
}

const getLevelText = (level) => {
  const levels = { 1: '入门', 2: '初级', 3: '中等', 4: '困难' }
  return levels[level] || '未知'
}

const getLevelClass = (level) => {
  const classes = { 1: 'level-easy', 2: 'level-primary', 3: 'level-medium', 4: 'level-hard' }
  return classes[level] || ''
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
  margin-bottom: 16px;
  display: flex;
  gap: 16px;
  align-items: flex-end;
  flex-wrap: wrap;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.search-item {
  flex: 1;
  min-width: 180px;
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

.btn-add {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: #1a1a1a;
}

.action-bar {
  margin-bottom: 24px;
}

.problem-table {
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

.title-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
}

.level-easy {
  background: #43e97b;
  color: #1a1a1a;
}

.level-primary {
  background: #4facfe;
  color: #1a1a1a;
}

.level-medium {
  background: #f093fb;
  color: #1a1a1a;
}

.level-hard {
  background: #fa709a;
  color: #1a1a1a;
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #444;
  border-radius: 10px;
  font-size: 14px;
  background: #333;
  color: #fff;
  resize: vertical;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-hint {
  font-size: 12px;
  color: #666;
  margin-top: 8px;
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
</style>
