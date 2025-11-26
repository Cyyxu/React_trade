import client from './client'

export const userAPI = {
  login: (userAccount, userPassword) =>
    client.post('/user/login', { userAccount, userPassword }),

  register: (userAccount, userPassword, confirmPassword) =>
    client.post('/user/register', { userAccount, userPassword, confirmPassword }),

  getProfile: (userId) =>
    client.get(`/user/${userId}/profile`),

  updateProfile: (userInfo) =>
    client.put('/user/update', userInfo),

  addFavorite: (userId, commodityId, remark = '') =>
    client.post('/user/CommodityFavorite/add', { userId, commodityId, remark }),

  logout: () =>
    client.post('/user/logout'),
}
