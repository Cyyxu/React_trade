import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, Descriptions, Button, InputNumber, message, Tag, Image, Space, Divider } from 'antd'
import { ShoppingCartOutlined, HeartOutlined, LeftOutlined } from '@ant-design/icons'
import { commodityApi, Commodity } from '../../api/commodity'
import { favoritesApi } from '../../api/favorites'
import { useAuthStore } from '../../store/authStore'
import './index.css'

const CommodityDetail = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { token } = useAuthStore()
  const [loading, setLoading] = useState(false)
  const [commodity, setCommodity] = useState<Commodity | null>(null)
  const [buyNumber, setBuyNumber] = useState(1)
  const [isFavorited, setIsFavorited] = useState(false)
  const [favoriteId, setFavoriteId] = useState<number | null>(null)

  useEffect(() => {
    if (id) {
      loadDetail()
    }
  }, [id])

  const loadDetail = async () => {
    setLoading(true)
    try {
      const data = await commodityApi.getDetail(Number(id))
      setCommodity(data)
      // 商品加载完成后，如果已登录，检查收藏状态
      if (token && data) {
        checkFavoriteStatus(data.id)
      }
    } catch (error) {
      message.error('加载商品详情失败')
    } finally {
      setLoading(false)
    }
  }

  const handlePurchase = async () => {
    if (!token) {
      message.warning('请先登录')
      navigate('/login')
      return
    }
    if (!commodity) return

    try {
      await commodityApi.purchase({
        commodityId: commodity.id,
        buyNumber,
      })
      message.success('购买成功')
      navigate('/order')
    } catch (error: any) {
      message.error(error.message || '购买失败')
    }
  }

  const checkFavoriteStatus = async (commodityId: number) => {
    if (!token) return
    try {
      const res = await favoritesApi.getByCommodityId(commodityId)
      if (res?.records && res.records.length > 0) {
        const favorite = res.records[0]
        setIsFavorited(true)
        setFavoriteId(favorite.id)
      } else {
        setIsFavorited(false)
        setFavoriteId(null)
      }
    } catch (error) {
      // 查询失败，默认为未收藏
      setIsFavorited(false)
      setFavoriteId(null)
    }
  }

  const handleToggleFavorite = async () => {
    if (!token) {
      message.warning('请先登录')
      navigate('/login')
      return
    }
    if (!commodity) return

    try {
      if (isFavorited && favoriteId) {
        // 取消收藏
        await favoritesApi.delete(favoriteId)
        message.success('已取消收藏')
        setIsFavorited(false)
        setFavoriteId(null)
        // 刷新商品详情以更新收藏数
        loadDetail()
      } else {
        // 添加收藏
        const id = await favoritesApi.add({ commodityId: commodity.id })
        message.success('已添加到收藏')
        setIsFavorited(true)
        setFavoriteId(id)
        // 刷新商品详情以更新收藏数
        loadDetail()
      }
    } catch (error: any) {
      message.error(error.message || '操作失败')
    }
  }

  if (!commodity) {
    return <Card loading={loading}>加载中...</Card>
  }

  return (
    <div className="commodity-detail-container">
      <Button
        icon={<LeftOutlined />}
        onClick={() => navigate(-1)}
        style={{ marginBottom: 16 }}
      >
        返回
      </Button>
      <Card>
        <div className="detail-content">
          <div className="detail-image">
            <Image
              src={commodity.commodityAvatar || 'https://via.placeholder.com/500'}
              alt={commodity.commodityName}
              style={{ width: '100%', borderRadius: 8 }}
            />
          </div>
          <div className="detail-info">
            <h1>{commodity.commodityName}</h1>
            <div className="price-section">
              <span className="price">¥{commodity.price}</span>
              <Tag color="blue">{commodity.commodityTypeName}</Tag>
            </div>
            <Divider />
            <Descriptions column={1} bordered>
              <Descriptions.Item label="商品描述">
                {commodity.commodityDescription}
              </Descriptions.Item>
              <Descriptions.Item label="新旧程度">
                {commodity.degree}
              </Descriptions.Item>
              <Descriptions.Item label="库存数量">
                {commodity.commodityInventory}
              </Descriptions.Item>
              <Descriptions.Item label="浏览量">
                {commodity.viewNum}
              </Descriptions.Item>
              <Descriptions.Item label="收藏数">
                {commodity.favourNum}
              </Descriptions.Item>
            </Descriptions>
            <div className="action-section">
              <Space size="large">
                <div>
                  <span style={{ marginRight: 8 }}>数量：</span>
                  <InputNumber
                    min={1}
                    max={commodity.commodityInventory}
                    value={buyNumber}
                    onChange={(value) => setBuyNumber(value || 1)}
                  />
                </div>
                <Button
                  type="primary"
                  size="large"
                  icon={<ShoppingCartOutlined />}
                  onClick={handlePurchase}
                  disabled={commodity.commodityInventory === 0}
                >
                  立即购买
                </Button>
                <Button
                  type={isFavorited ? 'primary' : 'default'}
                  size="large"
                  icon={<HeartOutlined />}
                  onClick={handleToggleFavorite}
                >
                  {isFavorited ? '已收藏' : '收藏'}
                </Button>
              </Space>
            </div>
          </div>
        </div>
      </Card>
    </div>
  )
}

export default CommodityDetail

