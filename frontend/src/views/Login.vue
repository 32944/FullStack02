<template>
  <div class="login-container">
    <!-- 网格背景 -->
    <div class="grid-bg"></div>
    
    <!-- 动态光晕效果 -->
    <div class="glow-effects">
      <div class="glow-circle glow-1"></div>
      <div class="glow-circle glow-2"></div>
      <div class="glow-circle glow-3"></div>
      <div class="glow-circle glow-4"></div>
    </div>
    
    <!-- 漂浮方块装饰 -->
    <div class="floating-blocks">
      <div class="block block-1"></div>
      <div class="block block-2"></div>
      <div class="block block-3"></div>
      <div class="block block-4"></div>
      <div class="block block-5"></div>
    </div>
    
    <!-- 星星效果 -->
    <div class="stars">
      <div class="star star-1"></div>
      <div class="star star-2"></div>
      <div class="star star-3"></div>
      <div class="star star-4"></div>
      <div class="star star-5"></div>
      <div class="star star-6"></div>
      <div class="star star-7"></div>
      <div class="star star-8"></div>
    </div>
    
    <!-- 粒子效果 -->
    <div class="particles">
      <div class="particle" v-for="i in 20" :key="i" :style="{
        width: Math.random() * 15 + 5 + 'px',
        height: Math.random() * 15 + 5 + 'px',
        left: Math.random() * 100 + '%',
        top: Math.random() * 100 + '%',
        animationDelay: Math.random() * 15 + 's',
        animationDuration: Math.random() * 10 + 15 + 's'
      }"></div>
    </div>
    
    <!-- 登录框 -->
    <div class="login-wrapper">
      <div class="login-box" :class="{ 'shake': shake }">
        <!-- 顶部装饰 -->
        <div class="top-decoration">
          <div class="glow-orb orb-1"></div>
          <div class="glow-orb orb-2"></div>
          <div class="glow-orb orb-3"></div>
        </div>
        
        <!-- 标题区域 -->
        <div class="login-header">
          <div class="logo-icon">
            <span class="logo-shape"></span>
          </div>
          <h1 class="title">
            <span class="gradient-text">智慧就业</span>
            <br>
            <span class="sub-title">指导系统</span>
          </h1>
          <p class="welcome-text">欢迎回来，管理员</p>
        </div>
        
        <!-- 表单区域 -->
        <form @submit.prevent="handleLogin" class="login-form">
          <!-- 用户名 -->
          <div class="form-group">
            <div class="input-container">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <input 
                type="text" 
                v-model="loginForm.username" 
                placeholder="请输入用户名"
                autocomplete="off"
                class="input-field"
                @focus="focusedField = 'username'"
                @blur="focusedField = ''"
              />
              <div class="input-border" :class="{ 'active': focusedField === 'username' }"></div>
            </div>
          </div>
          
          <!-- 密码 -->
          <div class="form-group">
            <div class="input-container">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M19 11H5C3.89543 11 3 11.8954 3 13V20C3 21.1046 3.89543 22 5 22H19C20.1046 22 21 21.1046 21 20V13C21 11.8954 20.1046 11 19 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M7 11V7C7 5.67392 7.52678 4.40215 8.46447 3.46447C9.40215 2.52678 10.6739 2 12 2C13.3261 2 14.5979 2.52678 15.5355 3.46447C16.4732 4.40215 17 5.67392 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <input 
                :type="showPassword ? 'text' : 'password'" 
                v-model="loginForm.password" 
                placeholder="请输入密码"
                autocomplete="off"
                class="input-field"
                @focus="focusedField = 'password'"
                @blur="focusedField = ''"
              />
              <div class="input-border" :class="{ 'active': focusedField === 'password' }"></div>
              <button type="button" class="toggle-btn" @click="showPassword = !showPassword">
                <svg v-if="!showPassword" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.94 17.94C16.2306 19.243 14.1491 19.9649 12 20C5 20 1 12 1 12C2.24389 9.6819 3.96914 7.65661 6.06 6.06" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M14.12 3.12C13.4446 2.90246 12.7283 2.78503 12 2.79999C5 2.79999 1 10.8 1 10.8C1.84566 12.4987 3.0673 14.0081 4.56 15.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M9.88 9.88C9.58015 10.4529 9.42281 11.0962 9.43 11.75C9.44817 13.0015 9.98028 14.1851 10.86 15.02C11.746 15.8641 12.8734 16.3612 14.05 16.39" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 7.02C12.7308 7.0844 13.4078 7.39519 13.91 7.89C14.6168 8.58342 14.9777 9.52243 14.93 10.48" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M1 1L23 23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>
          
          <!-- 登录按钮 -->
          <button type="submit" class="login-btn" :disabled="loading">
            <div class="btn-content" v-if="!loading">
              <span class="btn-text">立即登录</span>
              <svg class="btn-arrow" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 5L19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="loading-spinner" v-else>
              <div class="spinner"></div>
              <span class="loading-text">正在登录...</span>
            </div>
          </button>
        </form>
        
        <!-- 底部 -->
        <div class="login-footer">
          <div class="footer-decoration">
            <div class="line"></div>
            <span>后台管理</span>
            <div class="line"></div>
          </div>
          <p class="copyright">© 2024 智慧就业指导系统 · 用心服务</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const showPassword = ref(false)
const loading = ref(false)
const shake = ref(false)
const focusedField = ref('')

const loginForm = reactive({
  username: '',
  password: ''
})

const triggerShake = () => {
  shake.value = true
  setTimeout(() => shake.value = false, 600)
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    triggerShake()
    return
  }
  
  loading.value = true
  try {
    const res = await request.post('/admin/login', loginForm)
    const { token, userId, username } = res.data
    
    localStorage.setItem('token', token)
    localStorage.setItem('userId', userId)
    localStorage.setItem('username', username)
    
    // 登录成功动画效果
    const wrapper = document.querySelector('.login-wrapper')
    if (wrapper) {
      wrapper.style.animation = 'successPop 0.6s ease forwards'
      setTimeout(() => {
        router.push('/home')
      }, 600)
    } else {
      router.push('/home')
    }
  } catch (error) {
    triggerShake()
    console.error('登录失败', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #0a0a0a;
}

/* 网格背景 */
.grid-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background-image: 
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
}

/* 动态光晕效果 */
.glow-effects {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
}

.glow-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.4;
}

.glow-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.5) 0%, transparent 70%);
  top: -100px;
  left: -100px;
  animation: glowPulse 8s ease-in-out infinite;
}

.glow-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(156, 39, 176, 0.4) 0%, transparent 70%);
  bottom: -80px;
  right: -80px;
  animation: glowPulse 10s ease-in-out infinite reverse;
}

.glow-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(100, 200, 255, 0.3) 0%, transparent 70%);
  top: 50%;
  right: 20%;
  transform: translateY(-50%);
  animation: glowPulse 12s ease-in-out infinite;
}

.glow-4 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(79, 172, 254, 0.3) 0%, transparent 70%);
  bottom: 30%;
  left: 10%;
  animation: glowPulse 9s ease-in-out infinite reverse;
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.1);
  }
}

/* 漂浮方块装饰 */
.floating-blocks {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;
}

.block {
  position: absolute;
  background: rgba(102, 126, 234, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;
  animation: floatBlock linear infinite;
}

.block-1 {
  width: 60px;
  height: 60px;
  top: 10%;
  left: 15%;
  animation-duration: 25s;
}

.block-2 {
  width: 40px;
  height: 40px;
  top: 25%;
  right: 18%;
  animation-duration: 30s;
  animation-delay: -5s;
}

.block-3 {
  width: 50px;
  height: 50px;
  bottom: 20%;
  left: 25%;
  animation-duration: 28s;
  animation-delay: -10s;
}

.block-4 {
  width: 35px;
  height: 35px;
  top: 60%;
  right: 25%;
  animation-duration: 32s;
  animation-delay: -15s;
}

.block-5 {
  width: 45px;
  height: 45px;
  bottom: 15%;
  right: 10%;
  animation-duration: 26s;
  animation-delay: -8s;
}

@keyframes floatBlock {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.5;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 星星效果 */
.stars {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;
}

.star {
  position: absolute;
  background: white;
  border-radius: 50%;
  animation: twinkle infinite;
}

.star-1 { width: 2px; height: 2px; top: 12%; left: 8%; animation-duration: 2s; }
.star-2 { width: 1px; height: 1px; top: 18%; left: 45%; animation-duration: 3s; animation-delay: 0.5s; }
.star-3 { width: 2px; height: 2px; top: 8%; left: 72%; animation-duration: 2.5s; animation-delay: 1s; }
.star-4 { width: 1px; height: 1px; top: 45%; left: 15%; animation-duration: 3.5s; animation-delay: 0.3s; }
.star-5 { width: 2px; height: 2px; top: 32%; left: 85%; animation-duration: 2.8s; animation-delay: 1.5s; }
.star-6 { width: 1px; height: 1px; top: 75%; left: 30%; animation-duration: 3s; animation-delay: 0.8s; }
.star-7 { width: 2px; height: 2px; top: 82%; left: 60%; animation-duration: 2.3s; animation-delay: 1.2s; }
.star-8 { width: 1px; height: 1px; top: 55%; left: 92%; animation-duration: 3.2s; animation-delay: 0.6s; }

@keyframes twinkle {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

/* 粒子效果 */
.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(102, 126, 234, 0.6);
  border-radius: 50%;
  animation: rise linear infinite;
}

@keyframes rise {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    transform: translateY(-100px) scale(1);
    opacity: 0;
  }
}

/* 登录框容器 */
.login-wrapper {
  position: relative;
  z-index: 10;
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes successPop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(0);
    opacity: 0;
  }
}

/* 登录框 */
.login-box {
  width: 440px;
  padding: 45px 40px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    0 0 60px rgba(102, 126, 234, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.login-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
  animation: shine 3s infinite;
}

@keyframes shine {
  0% {
    left: -100%;
  }
  100% {
    left: 200%;
  }
}

.login-box.shake {
  animation: shake 0.6s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-10px); }
  20%, 40%, 60%, 80% { transform: translateX(10px); }
}

/* 顶部装饰 */
.top-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100px;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.5;
  animation: pulse 3s ease-in-out infinite;
}

.orb-1 {
  background: #667eea;
  top: -30px;
  left: -30px;
}

.orb-2 {
  background: #f093fb;
  top: -20px;
  right: -20px;
  animation-delay: 1s;
}

.orb-3 {
  background: #4facfe;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  animation-delay: 2s;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.3);
    opacity: 0.3;
  }
}

/* 标题区域 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.logo-icon {
  width: 90px;
  height: 90px;
  margin: 0 auto 20px;
  position: relative;
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.logo-shape {
  display: block;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.5);
  position: relative;
}

.logo-shape::before,
.logo-shape::after {
  content: '';
  position: absolute;
  width: 30px;
  height: 30px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
}

.logo-shape::before {
  top: 15px;
  left: 15px;
  animation: rotate 4s linear infinite;
}

.logo-shape::after {
  bottom: 15px;
  right: 15px;
  animation: rotate 4s linear infinite reverse;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.title {
  font-size: 36px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 8px;
}

.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 200%;
  animation: gradientMove 3s ease infinite;
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.sub-title {
  background: linear-gradient(135deg, #fff 0%, rgba(255,255,255,0.7) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 1px;
}

/* 表单 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  width: 100%;
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 52px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  overflow: hidden;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.input-container:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
}

.input-icon {
  width: 60px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.input-icon svg {
  width: 24px;
  height: 24px;
}

.input-container:focus-within .input-icon {
  color: #667eea;
  transform: scale(1.1);
}

.input-field {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  font-size: 16px;
  color: #fff;
  outline: none;
  padding-right: 20px;
}

.input-field::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #667eea);
  background-size: 300% 100%;
  transition: all 0.4s ease;
  transform: translateX(-50%);
}

.input-border.active {
  width: 100%;
  animation: borderGradient 2s linear infinite;
}

@keyframes borderGradient {
  0% { background-position: 0% 50%; }
  100% { background-position: 300% 50%; }
}

.toggle-btn {
  width: 50px;
  height: 100%;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  color: #667eea;
}

.toggle-btn svg {
  width: 22px;
  height: 22px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 58px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 200% 200%;
  color: white;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s ease;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
  margin-top: 10px;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 50px rgba(102, 126, 234, 0.6);
  background-position: 100% 50%;
}

.login-btn:active {
  transform: translateY(-1px);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-text {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
}

.btn-arrow {
  width: 24px;
  height: 24px;
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-arrow {
  transform: translateX(5px);
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 16px;
  font-weight: 600;
}

/* 底部 */
.login-footer {
  margin-top: 36px;
  text-align: center;
}

.footer-decoration {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
}

.footer-decoration span {
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.copyright {
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
}
</style>
