import { Card, Form, Input, Button, message } from 'antd'
import './index.css'

const Settings = () => {
  const onFinish = (values: any) => {
    console.log('Settings:', values)
    message.success('设置保存成功')
  }

  return (
    <div className="admin-settings">
      <Card>
        <h1>系统设置</h1>
        <Form
          layout="vertical"
          onFinish={onFinish}
          style={{ maxWidth: 600, marginTop: 24 }}
        >
          <Form.Item
            label="系统名称"
            name="systemName"
            initialValue="智能 AI 校园二手交易平台"
          >
            <Input placeholder="请输入系统名称" />
          </Form.Item>
          <Form.Item
            label="系统描述"
            name="systemDescription"
            initialValue="一个智能化的校园二手交易平台"
          >
            <Input.TextArea rows={4} placeholder="请输入系统描述" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              保存设置
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default Settings

