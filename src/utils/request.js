import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '@/router'

// 创建axios实例
const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 添加token到请求头
        const token = getToken()
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => {
        console.error('Error:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const { data } = response

        // 如果是文件上传等特殊响应，直接返回
        if (response.config.responseType === 'blob') {
            return response
        }

        // 检查响应状态码
        if (data.code === 200) {
            return data
        } else if (data.code === 401) {
            // token过期或无效
            ElMessage.error('Login has expired, please log in again.')
            removeToken()
            router.push('/login')
            return Promise.reject(new Error('Unauthorized'))
        } else {
            // 其他错误
            ElMessage.error(data.message || ' Request failed')
            return Promise.reject(new Error(data.message || 'Request failed'))
        }
    },
    error => {
        console.error('Response error', error)

        if (error.response) {
            const { status, data } = error.response

            switch (status) {
                case 401:
                    ElMessage.error('Login has expired, please log in again.')
                    removeToken()
                    router.push('/login')
                    break
                case 403:
                    ElMessage.error('You do not have permission to access this.')
                    break
                case 404:
                    ElMessage.error('Requested resource does not exist.')
                    break
                case 500:
                    ElMessage.error('A server error occurred.')
                    break
                default:
                    ElMessage.error(data?.message || `Request failed(${status})`)
            }
        } else if (error.request) {
            ElMessage.error('Unable to connect. Please check your network connection.')
        } else {
            ElMessage.error('Invalid request configuration.')
        }

        return Promise.reject(error)
    }
)

export default request