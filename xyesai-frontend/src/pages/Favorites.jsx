import { useAuthStore } from '../store/authStore'

export default function Favorites() {
  const { user } = useAuthStore()

  if (!user) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">请先登录查看收藏</p>
      </div>
    )
  }

  const favorites = [
    { id: 1, name: '精美手工艺品', price: 199, image: '🎨', date: '2024-01-15' },
    { id: 2, name: '限量版收藏品', price: 599, image: '⭐', date: '2024-01-14' },
    { id: 3, name: '创意家居装饰', price: 299, image: '🏠', date: '2024-01-13' },
  ]

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="bg-white rounded-2xl shadow-lg p-8">
        <h1 className="text-3xl font-bold text-gray-800">我的收藏</h1>
        <p className="text-gray-600 mt-2">共 {favorites.length} 件收藏</p>
      </div>

      {/* Favorites Grid */}
      {favorites.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {favorites.map((item) => (
            <div
              key={item.id}
              className="bg-white rounded-xl shadow-md hover:shadow-2xl transition-all duration-300 overflow-hidden group"
            >
              {/* Image */}
              <div className="h-48 bg-gradient-to-br from-purple-100 to-indigo-100 flex items-center justify-center text-6xl group-hover:scale-110 transition">
                {item.image}
              </div>

              {/* Content */}
              <div className="p-6">
                <h3 className="text-lg font-bold text-gray-800 mb-2">{item.name}</h3>
                <p className="text-sm text-gray-500 mb-4">收藏于 {item.date}</p>
                <div className="flex justify-between items-center">
                  <span className="text-2xl font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
                    ¥{item.price}
                  </span>
                  <button className="px-4 py-2 bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition">
                    取消收藏
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-lg p-12 text-center">
          <div className="text-6xl mb-4">📭</div>
          <p className="text-gray-600 text-lg">暂无收藏，去首页逛逛吧</p>
        </div>
      )}
    </div>
  )
}
