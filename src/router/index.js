// src/router/index.js - 完整的路由配置
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
    },
    {
        path: '/activities',
        name: 'Activities',
        component: () => import('@/views/Activities.vue')
    },

    {
        path: '/activities/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/ActivityDetail.vue')
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { requiresAuth: true }
    },
    // 管理后台路由 - 使用统一布局
    {
        path: '/admin',
        component: () => import('@/components/Layout/AdminLayout.vue'),
        meta: { requiresAuth: true, requiresAdmin: true },
        children: [
            {
                path: '',
                redirect: '/admin/dashboard'
            },
            {
                path: 'dashboard',
                name: 'AdminDashboard',
                component: () => import('@/views/admin/Dashboard.vue')
            },
            {
                path: 'activities',
                name: 'AdminActivities',
                component: () => import('@/views/admin/ActivityManager.vue')
            },
            {
                path: 'activitiesDetial',
                name: 'activitiesDetial',
                component: () => import('@/views/admin/ActivityRegistrationManager.vue')
            },
            {
                path: 'users',
                name: 'AdminUsers',
                component: () => import('@/views/admin/UserManager.vue')
            },
            {
                path: 'notices',
                name: 'AdminNotices',
                component: () => import('@/views/admin/NoticeManager.vue')
            },
            {
                path: 'system',
                name: 'AdminSystem',
                component: () => import('@/views/admin/SystemConfig.vue')
            }
        ]
    },
    // 404页面
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()

    // 如果有token但没有用户信息，尝试获取用户信息
    if (userStore.token && !userStore.userInfo) {
        await userStore.getCurrentUser()
    }

    // 需要登录的页面
    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
        ElMessage.warning('Please log in first')
        next('/login')
        return
    }

    // 需要管理员权限的页面
    if (to.meta.requiresAdmin && !userStore.isAdmin) {
        ElMessage.error('Insufficient permissions')
        next('/home')
        return
    }

    // 已登录用户访问登录/注册页面
    if (to.meta.requiresGuest && userStore.isLoggedIn) {
        next('/home')
        return
    }

    next()
})

export default router