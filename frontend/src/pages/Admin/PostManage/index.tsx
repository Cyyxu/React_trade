import { useState, useEffect } from 'react'
import { Table, Button, Input, Space, Tag, Popconfirm, message, Card } from 'antd'
import { SearchOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { postApi } from '../../../api/post'
import './index.css'

interface Post {
  id: number
  title: string
  content: string
  thumbNum: number
  favourNum: number
  createTime: string
}

const PostManage = () => {
  const [loading, setLoading] = useState(false)
  const [posts, setPosts] = useState<Post[]>([])
  const [total, setTotal] = useState(0)
  const [query, setQuery] = useState({
    current: 1,
    pageSize: 10,
    searchText: '',
  })

  useEffect(() => {
    loadData()
  }, [query])

  const loadData = async () => {
    setLoading(true)
    try {
      const res = await postApi.getList(query)
      setPosts(res?.records || [])
      setTotal(res?.total || 0)
    } catch (error) {
      console.error('加载帖子列表失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id: number) => {
    try {
      await postApi.delete(id)
      message.success('删除成功')
      loadData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const columns: ColumnsType<Post> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
    },
    {
      title: '点赞数',
      dataIndex: 'thumbNum',
      key: 'thumbNum',
      width: 100,
    },
    {
      title: '收藏数',
      dataIndex: 'favourNum',
      key: 'favourNum',
      width: 100,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} size="small">
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个帖子吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />} size="small">
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div className="admin-post-manage">
      <Card>
        <div className="page-header">
          <h1>帖子管理</h1>
          <Space>
            <Input
              placeholder="搜索帖子标题"
              prefix={<SearchOutlined />}
              value={query.searchText}
              onChange={(e) => setQuery({ ...query, searchText: e.target.value, current: 1 })}
              style={{ width: 200 }}
            />
          </Space>
        </div>
        <Table
          columns={columns}
          dataSource={posts}
          rowKey="id"
          loading={loading}
          pagination={{
            current: query.current,
            pageSize: query.pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, size) => setQuery({ ...query, current: page, pageSize: size }),
          }}
        />
      </Card>
    </div>
  )
}

export default PostManage

