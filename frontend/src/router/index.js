import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import UserManage from '../views/UserManage.vue'
import ProblemManage from '../views/ProblemManage.vue'
import Agent from '../views/Agent.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/home/user',
    name: 'UserManage',
    component: UserManage,
    meta: { requiresAuth: true }
  },
  {
    path: '/home/problem',
    name: 'ProblemManage',
    component: ProblemManage,
    meta: { requiresAuth: true }
  },
  {
    path: '/home/agent',
    name: 'Agent',
    component: Agent,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
