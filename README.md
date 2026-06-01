# FullStack02 — 全栈智能招聘助手

## 项目简介

一个面向求职者和学习者的全栈智能招聘助手平台，融合了算法练习、AI 智能面试和 RAG 知识库检索三大核心功能。

## 技术栈

### 前端
- **Vue 3** + Vite + Vue Router + Pinia + Axios

### 后端
- **SpringBoot** + MyBatis + MySQL + Redis + Redisson + JWT

### AI 服务
- **Python LangChain + FastAPI**，提供智能面试问答和 RAG 知识库检索

### 小程序
- **微信小程序**（原生框架），作为移动端入口

## 功能模块

| 模块 | 说明 |
|------|------|
| **算法练习** | 在线刷题，支持题目分类、代码提交、测试用例判定、做题记录 |
| **智能面试** | 基于 LangChain 的 AI 模拟面试，覆盖常见八股文和项目问答 |
| **RAG 知识库检索** | 通过 RAG（检索增强生成）技术，从简历和学习资料中检索相关内容并生成回答 |
| **学习路线** | 展示系统化的学习路径规划 |
| **用户系统** | 微信扫码登录、JWT 鉴权、学习打卡、收藏与错题本 |

## 项目结构

```
├── agent/               # Python AI 服务（LangChain + FastAPI）
│   ├── main.py          # FastAPI 入口
│   ├── rag_service.py   # RAG 知识库检索服务
│   └── static/          # 静态资源
├── backend/             # SpringBoot 后端
│   └── graduationProject/
│       ├── src/main/java/com/itheima/graduation/
│       │   ├── controller/   # 接口层
│       │   ├── service/      # 业务逻辑
│       │   ├── mapper/       # 数据访问
│       │   ├── entity/       # 实体类
│       │   ├── config/       # 配置（JWT、Redis、微信等）
│       │   └── utils/        # 工具类
│       └── src/main/resources/
│           └── mapper/       # MyBatis XML 映射
├── frontend/            # Vue 3 前端
│   └── src/
│       ├── views/       # 页面组件
│       ├── api/         # API 封装
│       ├── stores/      # Pinia 状态管理
│       └── router/      # 路由配置
├── wxapp/               # 微信小程序
│   ├── pages/           # 页面
│   ├── components/      # 公共组件
│   └── custom-tab-bar/  # 自定义 tab 栏
└── user_favorite_and_wrong_tables.sql  # 收藏与错题表结构
```
