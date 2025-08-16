import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
    // 状态
    const userInfo = ref(null)
    const token = ref(getToken() || '')

    // 计算属性
    const isLoggedIn = computed(() => !!token.value && !!userInfo.value)
    const isAdmin = computed(() => userInfo.value?.role === 'admin')

    // 方法
    const login = async (loginData) => {
        try {
            const response = await request.post('/api/auth/login', loginData)

            if (response.code === 200) {
                const { token: newToken, user } = response.data

                // 保存token和用户信息
                token.value = newToken
                userInfo.value = user
                setToken(newToken)

                return { success: true, data: response.data }
            } else {
                return { success: false, message: response.message }
            }
        } catch (error) {
            console.error('Login failed:', error)
            return { success: false, message: error.message || 'Login failed' }
        }
    }

    const register = async (registerData) => {
        try {
            const response = await request.post('/api/auth/register', registerData)

            if (response.code === 200) {
                return { success: true, data: response.data }
            } else {
                return { success: false, message: response.message }
            }
        } catch (error) {
            console.error('Registration failed:', error)
            return { success: false, message: error.message || 'Registration failed' }
        }
    }

    const logout = async () => {
        try {
            await request.post('/api/auth/logout')
        } catch (error) {
            console.error('The logout request failed:', error)
        } finally {
            // 清除本地数据
            token.value = ''
            userInfo.value = null
            removeToken()
        }
    }

    const getCurrentUser = async () => {
        try {
            if (!token.value) {
                return { success: false, message: 'Not logged in' }
            }

            const response = await request.get('/api/auth/me')

            if (response.code === 200) {
                userInfo.value = response.data
                return { success: true, data: response.data }
            } else {
                return { success: false, message: response.message }
            }
        } catch (error) {
            console.error('获取用户信息失败:', error)
            // 如果token无效，清除本地数据
            if (error.response?.status === 401) {
                token.value = ''
                userInfo.value = null
                removeToken()
            }
            return { success: false, message: error.message || '获取用户信息失败' }
        }
    }

    const checkAuth = async () => {
        if (token.value) {
            await getCurrentUser()
        }
    }

    // 新增：更新用户信息的方法
    const updateUserInfo = (newUserInfo) => {
        userInfo.value = { ...userInfo.value, ...newUserInfo }
    }

    // 新增：设置用户信息的方法
    const setUserInfo = (newUserInfo) => {
        userInfo.value = newUserInfo
    }

    // 新增：更新用户头像的方法
    const updateAvatar = (avatarUrl) => {
        if (userInfo.value) {
            userInfo.value.avatar = avatarUrl
        }
    }

    return {
        userInfo,
        token,
        isLoggedIn,
        isAdmin,
        login,
        register,
        logout,
        getCurrentUser,
        checkAuth,
        updateUserInfo,
        setUserInfo,
        updateAvatar
    }
})