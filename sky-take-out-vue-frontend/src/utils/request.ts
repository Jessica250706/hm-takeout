import axios from 'axios'
import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/stores/modules/user'
import { getRequestKey, removePending } from './requestOptimize'
import router from '@/router'

const service = axios.create({
  // Vite 环境变量必须以 VITE_ 前缀暴露
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 600000,
})

// Request interceptors
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // ⚠️ 必须在拦截器内部调用 useUserStore()，不能提到模块顶层
    const userStore = useUserStore()

    if (userStore.token) {
      config.headers['token'] = userStore.token
    } else if (userStore.token && config.url !== '/login') {
      window.location.href = '/login'
      return Promise.reject(new Error('未登录'))
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  },
)

// Response interceptors
service.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.data.status === 401) {
      router.push('/login')
    }
    // 请求响应中的 config.url 会带上代理的 api，需要去掉
    response.config.url = response.config.url?.replace('/api', '') ?? ''
    // 请求完成，删除请求中状态
    const key = getRequestKey(response.config)
    removePending(key)
    if (response.data.code === 1) {
      return response
    }
    return response
  },
  (error: AxiosError) => {
    if (error && error.response) {
      switch (error.response.status) {
        case 401:
          router.push('/login')
          break
        case 405:
          error.message = '请求错误'
      }
    }
    // 请求响应中的 config.url 会带上代理的 api，需要去掉
    if (error.config) {
      error.config.url = error.config.url?.replace('/api', '') ?? ''
      // 请求完成，删除请求中状态
      const key = getRequestKey(error.config)
      removePending(key)
    }
    return Promise.reject(error)
  },
)

export default service
