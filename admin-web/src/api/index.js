import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api', // Proxy to http://localhost:3000
  timeout: 5000
})

// Request interceptor
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      ElMessage.error('Unauthorized, please login again')
      localStorage.removeItem('admin_token')
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || 'Request failed')
    }
    return Promise.reject(error)
  }
)

export default api
