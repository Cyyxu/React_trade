# XYESai Frontend

现代化的 React 前端项目，采用 Vite + TailwindCSS + Zustand 技术栈。

## 🚀 快速开始

### 安装依赖
```bash
cd xyesai-frontend
npm install
```

### 开发模式
```bash
npm run dev
```
访问 `http://localhost:5173`

### 生产构建
```bash
npm run build
```

## 📁 项目结构

```
src/
├── api/              # API 请求
│   ├── client.js     # Axios 实例
│   └── user.js       # 用户相关 API
├── components/       # 可复用组件
│   └── Layout.jsx    # 布局组件
├── pages/            # 页面组件
│   ├── Home.jsx      # 首页
│   ├── Login.jsx     # 登录页
│   ├── Register.jsx  # 注册页
│   ├── Profile.jsx   # 用户资料页
│   └── Favorites.jsx # 收藏页
├── store/            # 状态管理
│   └── authStore.js  # 认证状态
├── App.jsx           # 应用入口
├── main.jsx          # React 入口
└── index.css         # 全局样式
```

## 🎨 技术栈

- **React 18** - UI 框架
- **Vite** - 构建工具
- **React Router v6** - 路由管理
- **TailwindCSS** - 样式框架
- **Zustand** - 状态管理
- **Axios** - HTTP 客户端

## 🔑 主要特性

- ✨ 现代化的渐变色 UI 设计
- 🎯 响应式布局（移动端友好）
- 🔐 JWT 认证集成
- 📦 轻量级状态管理
- 🚀 快速开发体验（HMR）
- 🌈 丰富的色彩方案

## 🔗 API 集成

前端通过 Axios 与后端 Spring Boot API 通信。配置代理：

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, ''),
  },
}
```

## 📝 环境变量

复制 `.env.example` 为 `.env.local`：

```
VITE_API_URL=http://localhost:8080
```

## 🎯 后续开发建议

1. **添加更多页面** - 商品详情、搜索、分类等
2. **完善组件库** - 按钮、表单、卡片等可复用组件
3. **增强状态管理** - 添加商品、购物车等全局状态
4. **性能优化** - 代码分割、懒加载、图片优化
5. **测试覆盖** - 单元测试、集成测试

## 📄 许可证

MIT
