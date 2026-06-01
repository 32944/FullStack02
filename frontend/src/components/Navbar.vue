<template>
  <header class="navbar">
    <div class="navbar-content">
      <div class="navbar-left">
        <div class="logo-icon">
          <div class="logo-shape">
            <div class="inner-square square-1"></div>
            <div class="inner-square square-2"></div>
          </div>
        </div>
        <div class="logo-text">
          <h1>智慧就业</h1>
          <p>管理系统</p>
        </div>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/home" class="nav-item" :class="{ active: $route.path === '/home' }">
          <span class="nav-icon">🏠</span>
          <span class="nav-text">首页</span>
        </router-link>
        
        <router-link to="/home/user" class="nav-item" :class="{ active: $route.path === '/home/user' }">
          <span class="nav-icon">👥</span>
          <span class="nav-text">用户管理</span>
        </router-link>
        
        <router-link to="/home/problem" class="nav-item" :class="{ active: $route.path === '/home/problem' }">
          <span class="nav-icon">📚</span>
          <span class="nav-text">题目管理</span>
        </router-link>
        
        <router-link to="/home/agent" class="nav-item" :class="{ active: $route.path === '/home/agent' }">
          <span class="nav-icon">🤖</span>
          <span class="nav-text">AI模块</span>
        </router-link>
      </nav>
      
      <div class="navbar-right">
        <div class="user-info">
          <div class="user-avatar">
            <span>{{ username ? username.charAt(0).toUpperCase() : 'A' }}</span>
          </div>
          <div class="user-details">
            <span class="user-name">{{ username }}</span>
            <span class="user-role">管理员</span>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <span>退出</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || '管理员'
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  width: 100%;
  background: #1a1a1a;
  display: flex;
  position: fixed;
  top: 0;
  left: 0;
  height: 70px;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.navbar-content {
  width: 100%;
  max-width: 1920px;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  width: 40px;
  height: 40px;
}

.logo-shape {
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  position: relative;
}

.inner-square {
  position: absolute;
  width: 12px;
  height: 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.square-1 {
  top: 6px;
  left: 6px;
  animation: rotate 4s linear infinite;
}

.square-2 {
  bottom: 6px;
  right: 6px;
  animation: rotate 4s linear infinite reverse;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.logo-text h1 {
  font-size: 18px;
  font-weight: 700;
  color: white;
  margin-bottom: 1px;
}

.logo-text p {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
}

.nav-menu {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.15);
  color: white;
}

.nav-icon {
  font-size: 16px;
}

.nav-text {
  white-space: nowrap;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: white;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.user-role {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.logout-btn {
  padding: 8px 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}
</style>
