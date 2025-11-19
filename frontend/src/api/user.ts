import api from './request'

export interface LoginRequest {
  userAccount: string
  userPassword: string
}

export interface RegisterRequest {
  userAccount: string
  userPassword: string
  checkPassword: string
  userName?: string
}

export interface LoginResponse {
  token: string
  user: {
    id: number
    userAccount: string
    userName: string
    userAvatar: string
    userRole: string
  }
}

export interface UserQuery {
  current?: number
  pageSize?: number
  userName?: string
  userRole?: string
}

export interface User {
  id: number
  userAccount: string
  userName: string
  userAvatar: string
  userRole: string
  createTime: string
}

export const userApi = {
  login: (data: LoginRequest) => api.post<LoginResponse>('/user/login', data),
  register: (data: RegisterRequest) => api.post<number>('/user/register', data),
  logout: () => api.post<boolean>('/user/logout'),
  getCurrentUser: () => api.get('/user/get/login'),
  getList: (params: UserQuery) => api.post('/user/list/page/vo', params),
  delete: (id: number) => api.post<boolean>('/user/delete', { id }),
}

