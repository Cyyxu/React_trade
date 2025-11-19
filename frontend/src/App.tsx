import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/authStore'
import Layout from './components/Layout'
import AdminLayout from './components/AdminLayout'
import AdminRoute from './components/AdminRoute'
import Login from './pages/Login'
import Register from './pages/Register'
import Home from './pages/Home'
import CommodityList from './pages/CommodityList'
import CommodityDetail from './pages/CommodityDetail'
import CommodityManage from './pages/CommodityManage'
import OrderList from './pages/OrderList'
import PostList from './pages/PostList'
import PostDetail from './pages/PostDetail'
import PostCreate from './pages/PostCreate'
import UserCenter from './pages/UserCenter'
import AIChat from './pages/AIChat'
import Favorites from './pages/Favorites'
import NoticeList from './pages/NoticeList'
import PrivateMessagePage from './pages/PrivateMessage'
import AdminDashboard from './pages/Admin/Dashboard'
import AdminUserManage from './pages/Admin/UserManage'
import AdminCommodityManage from './pages/Admin/CommodityManage'
import AdminOrderManage from './pages/Admin/OrderManage'
import AdminPostManage from './pages/Admin/PostManage'
import AdminStatistics from './pages/Admin/Statistics'
import AdminSettings from './pages/Admin/Settings'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { token } = useAuthStore()
  return token ? <>{children}</> : <Navigate to="/login" />
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="commodity" element={<CommodityList />} />
          <Route path="commodity/:id" element={<CommodityDetail />} />
          <Route path="commodity-manage" element={<PrivateRoute><CommodityManage /></PrivateRoute>} />
          <Route path="order" element={<PrivateRoute><OrderList /></PrivateRoute>} />
          <Route path="post" element={<PostList />} />
          <Route path="post/:id" element={<PostDetail />} />
          <Route path="post/create" element={<PrivateRoute><PostCreate /></PrivateRoute>} />
          <Route path="user" element={<PrivateRoute><UserCenter /></PrivateRoute>} />
          <Route path="ai-chat" element={<PrivateRoute><AIChat /></PrivateRoute>} />
          <Route path="favorites" element={<PrivateRoute><Favorites /></PrivateRoute>} />
          <Route path="notice" element={<NoticeList />} />
          <Route path="message" element={<PrivateRoute><PrivateMessagePage /></PrivateRoute>} />
        </Route>
        <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
          <Route index element={<Navigate to="/admin/dashboard" replace />} />
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="users" element={<AdminUserManage />} />
          <Route path="commodities" element={<AdminCommodityManage />} />
          <Route path="orders" element={<AdminOrderManage />} />
          <Route path="posts" element={<AdminPostManage />} />
          <Route path="statistics" element={<AdminStatistics />} />
          <Route path="settings" element={<AdminSettings />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App

