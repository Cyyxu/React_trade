import { Card, Row, Col, Statistic, Table, Tag } from 'antd'
import { UserOutlined, ShoppingOutlined, ShoppingCartOutlined, FileTextOutlined } from '@ant-design/icons'
import { useEffect, useState } from 'react'
import { userApi } from '../../../api/user'
import { commodityApi } from '../../../api/commodity'
import { orderApi } from '../../../api/order'
import { postApi } from '../../../api/post'
import './index.css'

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalCommodities: 0,
    totalOrders: 0,
    totalPosts: 0,
  })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    setLoading(true)
    try {
      // 通过调用列表接口获取总数（只查询第一页，获取 total 字段）
      const [usersRes, commoditiesRes, ordersRes, postsRes] = await Promise.all([
        userApi.getList({ current: 1, pageSize: 1 }).catch(() => ({ total: 0 })),
        commodityApi.getList({ current: 1, pageSize: 1 }).catch(() => ({ total: 0 })),
        orderApi.getList({ current: 1, pageSize: 1 }).catch(() => ({ total: 0 })),
        postApi.getList({ current: 1, pageSize: 1 }).catch(() => ({ total: 0 })),
      ])

      setStats({
        totalUsers: usersRes?.total || 0,
        totalCommodities: commoditiesRes?.total || 0,
        totalOrders: ordersRes?.total || 0,
        totalPosts: postsRes?.total || 0,
      })
    } catch (error) {
      console.error('加载统计数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="admin-dashboard">
      <h1>数据概览</h1>
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="用户总数"
              value={stats.totalUsers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="商品总数"
              value={stats.totalCommodities}
              prefix={<ShoppingOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="订单总数"
              value={stats.totalOrders}
              prefix={<ShoppingCartOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="帖子总数"
              value={stats.totalPosts}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard

