import { useState } from 'react'
import { useAuthStore } from '../store/authStore'

export default function Home() {
  const { user } = useAuthStore()
  const [commodities] = useState([
    { id: 1, name: '精美手工艺品', price: 199, image: '🎨', category: '工艺品' },
    { id: 2, name: '限量版收藏品', price: 599, image: '⭐', category: '收藏品' },
    { id: 3, name: '创意家居装饰', price: 299, image: '🏠', category: '家居' },
    { id: 4, name: '艺术摄影作品', price: 399, image: '📸', category: '艺术' },
    { id: 5, name: '手工珠宝首饰', price: 899, image: '💎', category: '珠宝' },
    { id: 6, name: '古董收藏品', price: 1299, image: '🏺', category: '古董' },
  ])

  return (
    <div className="space-y-12">
      {/* Hero Section */}
      <section className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-purple-600 via-indigo-600 to-blue-600 p-12 text-white shadow-2xl">
        <div className="absolute inset-0 opacity-20">
          <div className="absolute top-0 left-0 w-96 h-96 bg-white rounded-full mix-blend-multiply filter blur-3xl"></div>
          <div className="absolute bottom-0 right-0 w-96 h-96 bg-purple-300 rounded-full mix-blend-multiply filter blur-3xl"></div>
        </div>
        <div className="relative z-10">
          <h1 className="text-5xl font-bold mb-4">欢迎来到 XYESai</h1>
          <p className="text-xl text-purple-100 mb-6">发现独特的商品，分享你的收藏故事</p>
          {!user && (
            <button className="px-8 py-3 bg-white text-purple-600 font-bold rounded-lg hover:shadow-lg transition transform hover:scale-105">
              立即开始
            </button>
          )}
        </div>
      </section>

      {/* Products Grid */}
      <section>
        <h2 className="text-3xl font-bold mb-8 bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
          精选商品
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {commodities.map((item) => (
            <div
              key={item.id}
              className="group bg-white rounded-xl shadow-md hover:shadow-2xl transition-all duration-300 overflow-hidden hover:scale-105"
            >
              {/* Image */}
              <div className="h-48 bg-gradient-to-br from-purple-100 to-indigo-100 flex items-center justify-center text-6xl group-hover:scale-110 transition">
                {item.image}
              </div>

              {/* Content */}
              <div className="p-6">
                <div className="inline-block px-3 py-1 bg-purple-100 text-purple-600 text-sm rounded-full mb-3">
                  {item.category}
                </div>
                <h3 className="text-lg font-bold text-gray-800 mb-2">{item.name}</h3>
                <div className="flex justify-between items-center">
                  <span className="text-2xl font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
                    ¥{item.price}
                  </span>
                  <button className="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:shadow-lg transition">
                    ❤️
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Features */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {[
          { icon: '🔒', title: '安全可靠', desc: '严格的身份验证和交易保护' },
          { icon: '⚡', title: '快速便捷', desc: '一键收藏，随时查看' },
          { icon: '🌟', title: '精选优品', desc: '精心挑选的优质商品' },
        ].map((feature, idx) => (
          <div key={idx} className="bg-white rounded-xl p-8 shadow-md hover:shadow-xl transition text-center">
            <div className="text-5xl mb-4">{feature.icon}</div>
            <h3 className="text-xl font-bold mb-2">{feature.title}</h3>
            <p className="text-gray-600">{feature.desc}</p>
          </div>
        ))}
      </section>
    </div>
  )
}
