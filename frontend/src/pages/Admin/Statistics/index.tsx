import { Card, Row, Col, Statistic } from 'antd'
import { UserOutlined, ShoppingOutlined, ShoppingCartOutlined, FileTextOutlined } from '@ant-design/icons'
import { useEffect, useState } from 'react'
import { userApi } from '../../../api/user'
import { commodityApi } from '../../../api/commodity'
import { orderApi } from '../../../api/order'
import { postApi } from '../../../api/post'
import dayjs from 'dayjs'
import './index.css'

const Statistics = () => {
  const [stats, setStats] = useState({
    todayUsers: 0,
    todayCommodities: 0,
    todayOrders: 0,
    todayPosts: 0,
  })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadTodayStats()
  }, [])

  const loadTodayStats = async () => {
    setLoading(true)
    try {
      const today = dayjs().format('YYYY-MM-DD')

      // 获取所有数据（使用较大的pageSize获取更多数据）
      const [usersRes, commoditiesRes, ordersRes, postsRes] = await Promise.all([
        userApi.getList({ current: 1, pageSize: 1000 }).catch(() => ({ records: [], total: 0 })),
        commodityApi.getList({ current: 1, pageSize: 1000 }).catch(() => ({ records: [], total: 0 })),
        orderApi.getList({ current: 1, pageSize: 1000 }).catch(() => ({ records: [], total: 0 })),
        postApi.getList({ current: 1, pageSize: 1000 }).catch(() => ({ records: [], total: 0 })),
      ])

      // 过滤今日创建的数据
      const todayUsers = (usersRes?.records || []).filter((user: any) => {
        if (!user.createTime) return false
        const createDate = dayjs(user.createTime).format('YYYY-MM-DD')
        return createDate === today
      }).length

      const todayCommodities = (commoditiesRes?.records || []).filter((commodity: any) => {
        if (!commodity.createTime) return false
        const createDate = dayjs(commodity.createTime).format('YYYY-MM-DD')
        return createDate === today
      }).length

      const todayOrders = (ordersRes?.records || []).filter((order: any) => {
        if (!order.createTime) return false
        const createDate = dayjs(order.createTime).format('YYYY-MM-DD')
        return createDate === today
      }).length

      const todayPosts = (postsRes?.records || []).filter((post: any) => {
        if (!post.createTime) return false
        const createDate = dayjs(post.createTime).format('YYYY-MM-DD')
        return createDate === today
      }).length

      setStats({
        todayUsers,
        todayCommodities,
        todayOrders,
        todayPosts,
      })
    } catch (error) {
      console.error('加载统计数据失败', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="admin-statistics">
      <h1>数据统计</h1>
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日新增用户"
              value={stats.todayUsers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#3f8600' }}
              loading={loading}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日新增商品"
              value={stats.todayCommodities}
              prefix={<ShoppingOutlined />}
              valueStyle={{ color: '#1890ff' }}
              loading={loading}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日订单数"
              value={stats.todayOrders}
              prefix={<ShoppingCartOutlined />}
              valueStyle={{ color: '#cf1322' }}
              loading={loading}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日新增帖子"
              value={stats.todayPosts}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#722ed1' }}
              loading={loading}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Statistics

