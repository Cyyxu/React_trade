import { useParams } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { userAPI } from '../api/user'

export default function Profile() {
  const { userId } = useParams()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await userAPI.getProfile(userId)
        setProfile(data)
      } catch (error) {
        console.error('Failed to fetch profile:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchProfile()
  }, [userId])

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    )
  }

  if (!profile) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">用户不存在</p>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* Profile Header */}
      <div className="bg-white rounded-2xl shadow-lg p-8 mb-8">
        <div className="flex items-center gap-6 mb-6">
          <div className="w-24 h-24 bg-gradient-to-br from-purple-600 to-indigo-600 rounded-full flex items-center justify-center text-4xl">
            👤
          </div>
          <div>
            <h1 className="text-3xl font-bold text-gray-800">{profile.userAccount}</h1>
            <p className="text-gray-600 mt-2">{profile.userProfile || '暂无简介'}</p>
            <div className="flex gap-4 mt-4">
              <span className="px-4 py-2 bg-purple-100 text-purple-600 rounded-lg text-sm font-medium">
                {profile.userRole || '普通用户'}
              </span>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 pt-6 border-t border-gray-200">
          {[
            { label: '收藏数', value: '12' },
            { label: '粉丝', value: '234' },
            { label: '关注', value: '56' },
          ].map((stat, idx) => (
            <div key={idx} className="text-center">
              <div className="text-2xl font-bold text-purple-600">{stat.value}</div>
              <div className="text-sm text-gray-600">{stat.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Contact Info */}
      <div className="bg-white rounded-2xl shadow-lg p-8">
        <h2 className="text-2xl font-bold mb-6">联系方式</h2>
        <div className="space-y-4">
          <div className="flex items-center gap-4 p-4 bg-gray-50 rounded-lg">
            <span className="text-2xl">📱</span>
            <div>
              <p className="text-sm text-gray-600">电话</p>
              <p className="font-medium">{profile.userPhone || '未设置'}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
