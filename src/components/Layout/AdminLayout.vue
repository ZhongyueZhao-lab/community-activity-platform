<!-- src/components/Layout/AdminLayout.vue -->
<template>
  <div class="admin-layout">
    <!-- 页面背景装饰 -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- 头部导航 -->
    <div class="admin-header">
      <div class="header-background"></div>
      <div class="header-content">
        <div class="header-left">
          <div class="admin-logo">
            <el-icon><Setting /></el-icon>
          </div>
          <h1 class="admin-title">
            <span class="title-gradient">Admin Dashboard</span>
          </h1>
        </div>
        <div class="header-right">
          <div class="user-info">
            <div class="user-avatar">
              {{ (userStore.userInfo?.nickname || 'A').charAt(0) }}
            </div>
            <span class="welcome-text">Welcome, {{ userStore.userInfo?.nickname }}</span>
          </div>
          <el-button @click="goToHome" class="header-button secondary">
            <el-icon><House /></el-icon>
            <span>Back to Site</span>
          </el-button>
          <el-button @click="handleLogout" class="header-button primary">
            <el-icon><SwitchButton /></el-icon>
            <span>Sign Out</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体布局 -->
    <div class="admin-body">
      <!-- 侧边栏 -->
      <div class="admin-sidebar">
        <div class="sidebar-background"></div>
        <div class="sidebar-content">
          <div class="sidebar-header">
            <h3 class="sidebar-title">Navigation</h3>
          </div>
          <el-menu
              :default-active="activeMenu"
              class="sidebar-menu modern-menu"
              router
              @select="handleMenuSelect"
          >
            <el-menu-item index="/admin/dashboard" class="menu-item">
              <div class="menu-icon dashboard">
                <el-icon><DataBoard /></el-icon>
              </div>
              <span class="menu-text">Dashboard</span>
            </el-menu-item>
            <el-menu-item index="/admin/activities" class="menu-item">
              <div class="menu-icon activities">
                <el-icon><Calendar /></el-icon>
              </div>
              <span class="menu-text">Events</span>
            </el-menu-item>
            <el-menu-item index="/admin/activitiesDetial" class="menu-item">
              <div class="menu-icon activities">
                <el-icon><Calendar /></el-icon>
              </div>
              <span class="menu-text">EventsDetial</span>
            </el-menu-item>
            <el-menu-item index="/admin/users" class="menu-item">
              <div class="menu-icon users">
                <el-icon><User /></el-icon>
              </div>
              <span class="menu-text">Users</span>
            </el-menu-item>
            <el-menu-item index="/admin/notices" class="menu-item">
              <div class="menu-icon notices">
                <el-icon><Bell /></el-icon>
              </div>
              <span class="menu-text">Announcements</span>
            </el-menu-item>
            <el-menu-item index="/admin/system" class="menu-item">
              <div class="menu-icon system">
                <el-icon><Setting /></el-icon>
              </div>
              <span class="menu-text">System Config</span>
            </el-menu-item>
          </el-menu>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="admin-main">
        <div class="main-background"></div>
        <div class="main-content">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataBoard, Calendar, User, Bell, Setting, House, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 计算当前激活的菜单
const activeMenu = computed(() => route.path)

// 方法
const handleMenuSelect = (index) => {
  router.push(index)
}

const goToHome = () => {
  router.push('/home')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('Are you sure you want to sign out?', 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    await userStore.logout()
    ElMessage.success('Signed out successfully')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
/* 全局样式 */
.admin-layout {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
}

.page-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.background-decoration {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1200px;
  height: 1200px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0px) rotate(0deg); }
  50% { transform: translate(-50%, -50%) translateY(-40px) rotate(180deg); }
}

.background-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="50" cy="50" r="0.5" fill="rgba(255,255,255,0.03)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.6;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
      180deg,
      transparent 0%,
      rgba(255, 255, 255, 0.02) 30%,
      rgba(255, 255, 255, 0.05) 100%
  );
}

/* 头部导航 */
.admin-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 0 20px;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  position: relative;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-logo {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.admin-title {
  font-size: 28px;
  font-weight: 800;
  margin: 0;
  line-height: 1;
}

.title-gradient {
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.9) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3)) drop-shadow(0 0 20px rgba(0, 0, 0, 0.2));
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.welcome-text {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  font-size: 14px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.header-button {
  border-radius: 12px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
}

.header-button.secondary {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.header-button.secondary:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-button.primary {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.header-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.4);
}

/* 主体布局 */
.admin-body {
  display: flex;
  margin-top: 70px;
  min-height: calc(100vh - 70px);
  position: relative;
  z-index: 1;
}

/* 侧边栏 */
.admin-sidebar {
  width: 260px;
  position: fixed;
  left: 0;
  top: 70px;
  bottom: 0;
  overflow-y: auto;
  padding: 20px;
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.1);
}

.sidebar-content {
  position: relative;
  padding: 24px;
  height: 100%;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.sidebar-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

/* 现代化菜单 */
.modern-menu {
  border: none;
  background: transparent;
}

.modern-menu :deep(.el-menu-item) {
  border-radius: 12px;
  margin-bottom: 8px;
  padding: 0;
  height: auto;
  line-height: normal;
  border: none;
  background: transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-item {
  display: flex !important;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  position: relative;
  overflow: hidden;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 1);
  transform: translateX(4px);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.modern-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.modern-menu :deep(.el-menu-item.is-active):hover {
  transform: translateX(0);
}

.menu-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.3s ease;
}

.menu-icon.dashboard {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.menu-icon.activities {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.menu-icon.users {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
}

.menu-icon.notices {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.menu-icon.system {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
}

.menu-text {
  font-size: 14px;
  font-weight: 600;
}

/* 主内容区 */
.admin-main {
  flex: 1;
  margin-left: 260px;
  padding: 20px;
  position: relative;
}

.main-content {
  position: relative;
  padding: 32px;
  min-height: calc(100vh - 130px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-sidebar {
    width: 220px;
  }

  .admin-main {
    margin-left: 220px;
  }
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 100%;
    position: relative;
    top: 0;
    height: auto;
    padding: 16px;
  }

  .admin-main {
    margin-left: 0;
    padding: 16px;
  }

  .admin-body {
    flex-direction: column;
  }

  .header-content {
    flex-direction: column;
    height: auto;
    padding: 16px 0;
    gap: 16px;
  }

  .header-right {
    flex-wrap: wrap;
    justify-content: center;
    gap: 12px;
  }

  .user-info {
    order: -1;
  }

  .sidebar-content {
    padding: 16px;
  }

  .main-content {
    padding: 20px;
  }

  .menu-item {
    padding: 12px 14px;
  }

  .menu-icon {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .admin-header {
    padding: 0 16px;
  }

  .admin-title {
    font-size: 24px;
  }

  .header-button {
    padding: 8px 16px;
    font-size: 13px;
  }

  .welcome-text {
    font-size: 13px;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    font-size: 12px;
  }

  .sidebar-content,
  .main-content {
    padding: 16px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-button) {
  font-weight: 600;
}

:deep(.el-menu) {
  background: transparent;
}

:deep(.el-menu-item) {
  background: transparent;
}

/* 自定义滚动条 */
.admin-sidebar::-webkit-scrollbar {
  width: 6px;
}

.admin-sidebar::-webkit-scrollbar-track {
  background: rgba(148, 163, 184, 0.1);
  border-radius: 3px;
}

.admin-sidebar::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
}

.admin-sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

/* 增强的动画效果 */
.sidebar-content:hover {
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.main-content:hover {
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 加载状态优化 */
.admin-layout {
  transition: all 0.3s ease;
}
</style>