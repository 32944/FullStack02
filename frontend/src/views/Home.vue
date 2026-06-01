<template>
  <div class="home-container">
    <Navbar />
    
    <main class="main-content">
      <div class="content-header">
        <h1>欢迎回来，{{ username }}</h1>
        <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
      </div>
      
      <div class="content-body">
        <div class="stats-grid">
          <div class="stat-card user-card" @click="goToUserManage">
            <div class="stat-icon">👥</div>
            <div class="stat-content">
              <div class="stat-value">{{ userCount }}</div>
              <div class="stat-label">总用户数</div>
            </div>
            <div class="stat-arrow">›</div>
          </div>
          
          <div class="stat-card problem-card" @click="goToProblemManage">
            <div class="stat-icon">📚</div>
            <div class="stat-content">
              <div class="stat-value">{{ problemCount }}</div>
              <div class="stat-label">题目总数</div>
            </div>
            <div class="stat-arrow">›</div>
          </div>
          
          <div class="stat-card ai-card" @click="goToAgent">
            <div class="stat-icon">🤖</div>
            <div class="stat-content">
              <div class="stat-value">{{ aiCount }}</div>
              <div class="stat-label">AI对话次数</div>
            </div>
            <div class="stat-arrow">›</div>
          </div>
        </div>
        
        <div class="content-grid">
          <div class="panel chart-panel">
            <h3 class="panel-title">📅 每日签到统计</h3>
            <div ref="clockChart" class="clock-chart"></div>
          </div>
          
          <div class="panel">
            <h3 class="panel-title">🔥 收藏 TOP 排行</h3>
            <div class="favorite-list">
              <div v-if="topFavorites.length === 0" class="empty-favorite">
                <span>暂无收藏数据</span>
              </div>
              <div 
                v-for="(item, index) in topFavorites" 
                :key="item.id" 
                class="favorite-item"
              >
                <div class="favorite-rank" :class="'rank-' + (index + 1)">
                  {{ index + 1 }}
                </div>
                <div class="favorite-info">
                  <div class="favorite-title">{{ item.title }}</div>
                  <div class="favorite-meta">
                    <span class="difficulty-badge" :class="getDifficultyClass(item.difficulty)">
                      {{ getDifficultyText(item.difficulty) }}
                    </span>
                  </div>
                </div>
                <div class="favorite-count">
                  <span class="heart-icon">❤️</span>
                  {{ item.favoriteCount }}
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="welcome-banner">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>智慧就业管理系统</h2>
              <p>一站式就业培训与管理平台</p>
              <ul>
                <li>用户管理与数据分析</li>
                <li>题库管理与错题统计</li>
                <li>AI模拟面试与测评</li>
              </ul>
            </div>
            <div class="welcome-decoration">
              <div class="decoration-circle circle-1"></div>
              <div class="decoration-circle circle-2"></div>
              <div class="decoration-circle circle-3"></div>
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <button class="refresh-btn" @click="refreshData" title="刷新数据">
      <span>🔄</span>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import request from '../utils/request'

const clockChart = ref(null)
let chartInstance = null

const router = useRouter()
const username = ref('')
const userCount = ref(0)
const problemCount = ref(0)
const aiCount = ref(0)
const recentConversations = ref([])
const clockStats = ref([])
const maxClockCount = ref(1)
const topFavorites = ref([])

const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
})

onMounted(() => {
  username.value = localStorage.getItem('username') || '管理员'
  loadStats()
})

const loadStats = async () => {
  try {
    const [userRes, problemRes, exerciseRes, clockRes, favoriteRes] = await Promise.all([
      request.post('/admin/user/page', { pageNum: 1, pageSize: 1 }),
      request.post('/admin/problem/page', { pageNum: 1, pageSize: 1 }),
      request.get('/user/exercise/records', { params: { userId: 1 } }),
      request.get('/user/clock/daily-stats', { params: { days: 7 } }),
      request.get('/user/favorite/top', { params: { limit: 10 } })
    ])
    
    userCount.value = userRes.data.total || 0
    problemCount.value = problemRes.data.total || 0
    
    let totalExercises = 0
    if (exerciseRes.data && exerciseRes.data.length > 0) {
      exerciseRes.data.forEach(dayRecord => {
        totalExercises += dayRecord.count || 0
      })
    }
    aiCount.value = totalExercises
    
    clockStats.value = clockRes.data || []
    maxClockCount.value = Math.max(...clockStats.value.map(s => s.count), 1)
    
    await nextTick()
    initClockChart()
    
    topFavorites.value = favoriteRes.data || []
  } catch (error) {
    console.error('加载统计数据失败', error)
    userCount.value = 0
    problemCount.value = 0
    aiCount.value = 0
  }
}

const refreshData = () => {
  loadStats()
}

const goToUserManage = () => {
  router.push('/home/user')
}

const goToProblemManage = () => {
  router.push('/home/problem')
}

const goToAgent = () => {
  router.push('/home/agent')
}

const formatTime = (time) => {
  if (!time) return '-'
  const t = new Date(time)
  const hours = String(t.getHours()).padStart(2, '0')
  const minutes = String(t.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const parts = dateStr.split('-')
  if (parts.length === 3) {
    return `${parseInt(parts[1])}月${parseInt(parts[2])}日`
  }
  return dateStr
}

const getBarWidth = (count) => {
  const percentage = (count / maxClockCount.value) * 100
  return `${Math.max(percentage, 5)}%`
}

const initClockChart = () => {
  if (!clockChart.value || clockStats.value.length === 0) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(clockChart.value)
  
  const dates = clockStats.value.map(item => formatDate(item.date))
  const counts = clockStats.value.map(item => item.count)
  
  const option = {
    backgroundColor: 'transparent',
    grid: {
      top: 40,
      left: 50,
      right: 30,
      bottom: 40
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(42, 42, 42, 0.9)',
      borderColor: '#444',
      textStyle: {
        color: '#fff'
      },
      formatter: function(params) {
        return `${params[0].name}<br/>签到人数: <strong>${params[0].value}</strong>`
      }
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      },
      axisLabel: {
        color: '#aaa',
        fontSize: 12
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisLabel: {
        color: '#aaa',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#333',
          type: 'dashed'
        }
      }
    },
    series: [{
      name: '签到人数',
      type: 'line',
      data: counts,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 1,
          y2: 0,
          colorStops: [{
            offset: 0, color: '#667eea'
          }, {
            offset: 1, color: '#764ba2'
          }]
        }
      },
      itemStyle: {
        color: '#667eea',
        borderColor: '#fff',
        borderWidth: 2
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(102, 126, 234, 0.3)'
          }, {
            offset: 1, color: 'rgba(102, 126, 234, 0.05)'
          }]
        }
      }
    }]
  }
  
  chartInstance.setOption(option)
  
  window.addEventListener('resize', () => {
    chartInstance && chartInstance.resize()
  })
}

const getDifficultyText = (difficulty) => {
  const map = {
    1: '入门',
    2: '初级',
    3: '中等',
    4: '困难'
  }
  return map[difficulty] || '未知'
}

const getDifficultyClass = (difficulty) => {
  const map = {
    1: 'easy',
    2: 'primary',
    3: 'medium',
    4: 'hard'
  }
  return map[difficulty] || ''
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
  padding: 30px 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.content-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #fff;
}

.content-header p {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.content-body {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: #2a2a2a;
  border-radius: 20px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.stat-icon {
  font-size: 28px;
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover .stat-icon::before {
  opacity: 1;
}

.user-card .stat-icon {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.1));
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.user-card .stat-icon::before {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.15));
}

.problem-card .stat-icon {
  background: linear-gradient(135deg, rgba(67, 233, 123, 0.15), rgba(56, 249, 215, 0.1));
  border: 1px solid rgba(67, 233, 123, 0.2);
}

.problem-card .stat-icon::before {
  background: linear-gradient(135deg, rgba(67, 233, 123, 0.2), rgba(56, 249, 215, 0.15));
}

.ai-card .stat-icon {
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.15), rgba(252, 176, 69, 0.1));
  border: 1px solid rgba(250, 112, 154, 0.2);
}

.ai-card .stat-icon::before {
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.2), rgba(252, 176, 69, 0.15));
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #888;
  font-weight: 500;
}

.stat-arrow {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  transition: transform 0.3s ease;
}

.stat-card:hover .stat-arrow {
  transform: translateX(5px);
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.panel {
  background: #2a2a2a;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.chart-panel {
  min-height: 320px;
}

.clock-chart {
  width: 100%;
  height: 250px;
}

.panel-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.action-btn {
  background: #333;
  border-radius: 14px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  border: 2px solid transparent;
}

.action-btn:hover {
  background: #3a3a3a;
  transform: translateY(-3px);
  border-color: #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

.action-btn:hover .action-icon,
.action-btn:hover .action-text {
  color: #667eea;
}

.action-icon {
  font-size: 32px;
  transition: color 0.3s ease;
  color: #666;
}

.action-text {
  font-size: 14px;
  font-weight: 600;
  color: #aaa;
  transition: color 0.3s ease;
}

.clock-stats-table {
  overflow-x: auto;
}

.clock-stats-table table {
  width: 100%;
  border-collapse: collapse;
}

.clock-stats-table thead {
  background: #333;
  border-radius: 8px;
}

.clock-stats-table th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 13px;
  color: #aaa;
  white-space: nowrap;
}

.clock-stats-table tbody tr {
  border-bottom: 1px solid #333;
  transition: background 0.2s ease;
}

.clock-stats-table tbody tr:hover {
  background: #333;
}

.clock-stats-table td {
  padding: 14px 16px;
  font-size: 14px;
  color: #ddd;
  position: relative;
}

.count-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  min-width: 50px;
  text-align: center;
}

.count-bar {
  position: absolute;
  left: 80px;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  height: 8px;
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.1));
  border-radius: 4px;
  transition: width 0.3s ease;
}

.empty-stats {
  padding: 40px 20px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 320px;
  overflow-y: auto;
}

.favorite-list::-webkit-scrollbar {
  width: 4px;
}

.favorite-list::-webkit-scrollbar-track {
  background: #2a2a2a;
}

.favorite-list::-webkit-scrollbar-thumb {
  background: #444;
  border-radius: 4px;
}

.empty-favorite {
  padding: 40px 20px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.favorite-item {
  background: #333;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.favorite-item:hover {
  background: #3a3a3a;
  transform: translateX(5px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.favorite-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  color: white;
  flex-shrink: 0;
}

.favorite-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  box-shadow: 0 2px 10px rgba(255, 215, 0, 0.4);
}

.favorite-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #a0a0a0);
  box-shadow: 0 2px 10px rgba(192, 192, 192, 0.4);
}

.favorite-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #a05a2c);
  box-shadow: 0 2px 10px rgba(205, 127, 50, 0.4);
}

.favorite-rank:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #444;
}

.favorite-info {
  flex: 1;
  min-width: 0;
}

.favorite-title {
  font-size: 14px;
  color: #ddd;
  font-weight: 500;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.favorite-meta {
  display: flex;
  gap: 8px;
}

.difficulty-badge {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 12px;
  font-weight: 600;
}

.difficulty-badge.easy {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.difficulty-badge.primary {
  background: rgba(79, 172, 254, 0.2);
  color: #4facfe;
}

.difficulty-badge.medium {
  background: rgba(237, 137, 54, 0.2);
  color: #ed8936;
}

.difficulty-badge.hard {
  background: rgba(245, 101, 101, 0.2);
  color: #f56565;
}

.favorite-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 600;
  flex-shrink: 0;
}

.heart-icon {
  font-size: 16px;
}

.welcome-banner {
  background: #2a2a2a;
  border-radius: 20px;
  padding: 32px;
  color: white;
  position: relative;
  overflow: hidden;
  margin-bottom: 32px;
  border: 1px solid #444;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.welcome-text h2 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
}

.welcome-text p {
  font-size: 15px;
  margin-bottom: 12px;
  opacity: 0.9;
}

.welcome-text ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.welcome-text li {
  font-size: 14px;
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
  opacity: 0.9;
}

.welcome-text li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: rgba(255, 255, 255, 0.7);
  font-weight: bold;
}

.welcome-decoration {
  position: relative;
  width: 150px;
  height: 150px;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 80px;
  height: 80px;
  top: 10px;
  right: 10px;
  animation: float 3s ease-in-out infinite;
}

.circle-2 {
  width: 60px;
  height: 60px;
  top: 40px;
  left: 20px;
  animation: float 3s ease-in-out infinite 0.5s;
}

.circle-3 {
  width: 40px;
  height: 40px;
  bottom: 10px;
  right: 40px;
  animation: float 3s ease-in-out infinite 1s;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.refresh-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.refresh-btn:active {
  transform: scale(0.95);
}
</style>
