/**
 * 获取存储的token
 */
export function getToken() {
    return localStorage.getItem('token') || sessionStorage.getItem('token')
}

/**
 * 设置token
 */
export function setToken(token, remember = false) {
    if (remember) {
        localStorage.setItem('token', token)
        // 清除sessionStorage中的token
        sessionStorage.removeItem('token')
    } else {
        sessionStorage.setItem('token', token)
        // 清除localStorage中的token
        localStorage.removeItem('token')
    }
}

/**
 * 移除token
 */
export function removeToken() {
    localStorage.removeItem('token')
    sessionStorage.removeItem('token')
}

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
    return !!getToken()
}