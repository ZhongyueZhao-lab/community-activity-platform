<template>
  <div class="homepage">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-background"></div>
      <div class="container">
        <div class="header-content">
          <h1 class="site-title">
            <span class="title-gradient">{{ siteConfig.siteName || 'Community Event Platform' }}</span>
          </h1>
          <div class="header-actions">
            <template v-if="userStore.isLoggedIn">
              <div class="welcome-section">
                <div class="user-avatar">
                  {{ (userStore.userInfo?.nickname || 'U').charAt(0) }}
                </div>
                <span class="welcome-text">{{ userStore.userInfo?.nickname }}</span>
              </div>

              <!-- 通知图标 -->
              <div class="notification-wrapper">
                <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="notification-badge">
                  <button
                      class="notification-button"
                      @click="toggleNotificationDrawer"
                  >
                    <el-icon class="notification-icon"><Bell /></el-icon>
                    <div class="notification-pulse" v-if="unreadCount > 0"></div>
                  </button>
                </el-badge>
              </div>

              <el-button class="nav-button" @click="goToProfile">
                <el-icon><User /></el-icon>
                Profile
              </el-button>
              <el-button v-if="userStore.isAdmin" type="primary" class="nav-button admin-button" @click="goToAdmin">
                <el-icon><Setting /></el-icon>
                Admin Dashboard
              </el-button>
              <el-button class="nav-button logout-button" @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                Logout
              </el-button>
            </template>
            <template v-else>
              <el-button class="nav-button login-button" @click="goToLogin">
                <el-icon><User /></el-icon>
                Login
              </el-button>
              <el-button type="primary" class="nav-button register-button" @click="goToRegister">
                <el-icon><UserFilled /></el-icon>
                Register
              </el-button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- 轮播图区域 -->
    <div class="banner-section">
      <div class="banner-background">
        <div class="background-decoration"></div>
      </div>
      <div class="container">
        <div class="banner-carousel">
          <el-carousel
              v-if="banners.length > 0"
              height="500px"
              :interval="5000"
              :autoplay="true"
              indicator-position="inside"
              arrow="hover"
              class="modern-carousel"
          >
            <el-carousel-item v-for="banner in banners" :key="banner.id">
              <div class="banner-item" @click="handleBannerClick(banner)">
                <img
                    :src="banner.imageUrl"
                    :alt="banner.title"
                    class="banner-image"
                    @error="handleImageError"
                />
                <div class="banner-overlay">
                  <div class="banner-content">
                    <h3 class="banner-title">{{ banner.title }}</h3>
                    <p v-if="banner.description" class="banner-description">
                      {{ banner.description }}
                    </p>
                    <div class="banner-action">
                      <div class="action-button">
                        <span>Learn More</span>
                        <el-icon><ArrowRight /></el-icon>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>

          <!-- 无轮播图时显示的占位内容 -->
          <div v-else class="no-banners">
            <div class="no-banners-content">
              <div class="no-banners-icon">
                <el-icon><Picture /></el-icon>
              </div>
              <p class="no-banners-text">No banners available</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新活动区域 -->
    <div class="latest-activities-section">
      <div class="section-background"></div>
      <div class="container">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">Latest Events</h2>
            <p class="section-subtitle">Discover exciting events around you and participate in community life</p>
          </div>
          <el-button type="primary" class="section-button" @click="goToActivities()">
            <span>View More</span>
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="activities-grid">
          <div
              v-for="activity in latestActivities"
              :key="activity.id"
              class="activity-card modern-card"
              @click="goToActivityDetail(activity.id)"
          >
            <div class="card-image">
              <img
                  v-if="getActivityImage(activity)"
                  :src="getActivityImage(activity)"
                  :alt="activity.title"
                  @error="handleActivityImageError"
              />
              <div v-else class="placeholder-image">
                <el-icon class="placeholder-icon"><Calendar /></el-icon>
              </div>

              <div class="image-overlay"></div>

              <div class="status-badge modern-badge" :class="getStatusClass(activity.status)">
                <div class="badge-dot"></div>
                {{ getStatusText(activity.status) }}
              </div>

              <div class="fee-badge modern-fee" :class="{ 'free': activity.fee === 0 }">
                <el-icon><Money /></el-icon>
                {{ activity.fee > 0 ? `$${activity.fee}` : 'Free' }}
              </div>
            </div>

            <div class="card-content">
              <div class="card-header">
                <h3 class="activity-title">{{ activity.title }}</h3>
                <div v-if="activity.categoryName" class="category-tag modern-tag">
                  {{ activity.categoryName }}
                </div>
              </div>

              <p v-if="activity.description" class="activity-description">
                {{ truncateText(activity.description, 80) }}
              </p>

              <div class="activity-meta">
                <div class="meta-item">
                  <div class="meta-icon">
                    <el-icon><Clock /></el-icon>
                  </div>
                  <span>{{ formatDateTime(activity.startTime) }}</span>
                </div>

                <div v-if="activity.location" class="meta-item">
                  <div class="meta-icon">
                    <el-icon><Location /></el-icon>
                  </div>
                  <span>{{ activity.location }}</span>
                </div>

                <div v-if="activity.maxParticipants > 0" class="meta-item">
                  <div class="meta-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }}</span>
                </div>
              </div>

              <!-- 进度条 -->
              <div v-if="activity.maxParticipants > 0" class="progress-section">
                <div class="progress-wrapper">
                  <el-progress
                      :percentage="getParticipationRate(activity)"
                      :color="getProgressColor(activity)"
                      :stroke-width="6"
                      :show-text="false"
                      class="modern-progress"
                  />
                </div>
                <span class="progress-text">
                  {{ activity.currentParticipants }} registered
                </span>
              </div>
            </div>

            <div class="card-footer">
              <div class="tags" v-if="activity.tags">
                <el-tag
                    v-for="tag in getTagArray(activity.tags)"
                    :key="tag"
                    size="small"
                    class="modern-tag-item"
                    effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
              <div class="card-action">
                <el-icon class="action-icon"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 精选活动区域 -->
    <div class="featured-activities-section">
      <div class="container">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">Featured Events</h2>
            <p class="section-subtitle">Community featured recommendations, not to be missed</p>
          </div>
          <el-button type="primary" class="section-button" @click="goToActivities()">
            <span>View More</span>
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="activities-grid">
          <div
              v-for="activity in featuredActivities"
              :key="activity.id"
              class="activity-card modern-card featured-card"
              @click="goToActivityDetail(activity.id)"
          >
            <div class="card-image">
              <img
                  v-if="getActivityImage(activity)"
                  :src="getActivityImage(activity)"
                  :alt="activity.title"
                  @error="handleActivityImageError"
              />
              <div v-else class="placeholder-image">
                <el-icon class="placeholder-icon"><Calendar /></el-icon>
              </div>

              <div class="image-overlay"></div>

              <div class="hot-badge">
                <el-icon class="hot-icon"><Star /></el-icon>
                <span>Featured</span>
              </div>

              <div class="fee-badge modern-fee" :class="{ 'free': activity.fee === 0 }">
                <el-icon><Money /></el-icon>
                {{ activity.fee > 0 ? `$${activity.fee}` : 'Free' }}
              </div>
            </div>

            <div class="card-content">
              <div class="card-header">
                <h3 class="activity-title">{{ activity.title }}</h3>
                <div v-if="activity.categoryName" class="category-tag modern-tag">
                  {{ activity.categoryName }}
                </div>
              </div>

              <p v-if="activity.description" class="activity-description">
                {{ truncateText(activity.description, 80) }}
              </p>

              <div class="activity-meta">
                <div class="meta-item">
                  <div class="meta-icon">
                    <el-icon><Clock /></el-icon>
                  </div>
                  <span>{{ formatDateTime(activity.startTime) }}</span>
                </div>

                <div v-if="activity.location" class="meta-item">
                  <div class="meta-icon">
                    <el-icon><Location /></el-icon>
                  </div>
                  <span>{{ activity.location }}</span>
                </div>

                <div class="meta-item">
                  <div class="meta-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <span>{{ activity.currentParticipants }} registered</span>
                </div>
              </div>
            </div>

            <div class="card-footer">
              <div class="tags" v-if="activity.tags">
                <el-tag
                    v-for="tag in getTagArray(activity.tags)"
                    :key="tag"
                    size="small"
                    class="modern-tag-item"
                    effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
              <div class="card-action">
                <el-icon class="action-icon"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动分类区域 -->
    <div class="categories-section">
      <div class="categories-background"></div>
      <div class="container">
        <div class="section-title-wrapper center">
          <h2 class="section-title">Event Categories</h2>
          <p class="section-subtitle">Explore different types of exciting events</p>
        </div>
        <div class="categories-grid">
          <div
              v-for="category in categories"
              :key="category.id"
              class="category-card modern-category"
              @click="goToActivities(category.id)"
          >
            <div class="category-background"></div>
            <div class="category-icon">
              <el-icon v-if="category.icon"><component :is="category.icon" /></el-icon>
              <el-icon v-else><Grid /></el-icon>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
            <p v-if="category.description" class="category-description">
              {{ category.description }}
            </p>
            <div class="category-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计数据区域 -->
    <div class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-card modern-stat">
            <div class="stat-background"></div>
            <div class="stat-icon calendar-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.totalActivities }}</h3>
              <p class="stat-label">Total Events</p>
            </div>
            <div class="stat-decoration"></div>
          </div>
          <div class="stat-card modern-stat">
            <div class="stat-background"></div>
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.totalUsers }}</h3>
              <p class="stat-label">Registered Users</p>
            </div>
            <div class="stat-decoration"></div>
          </div>
          <div class="stat-card modern-stat">
            <div class="stat-background"></div>
            <div class="stat-icon plus-icon">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.thisMonthActivities }}</h3>
              <p class="stat-label">New Events This Month</p>
            </div>
            <div class="stat-decoration"></div>
          </div>
          <div class="stat-card modern-stat">
            <div class="stat-background"></div>
            <div class="stat-icon users-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.thisMonthUsers }}</h3>
              <p class="stat-label">New Users This Month</p>
            </div>
            <div class="stat-decoration"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 网站介绍区域 -->
    <div class="intro-section">
      <div class="intro-background">
        <div class="intro-decoration"></div>
      </div>
      <div class="container">
        <div class="intro-content">
          <h2 class="intro-title">{{ siteConfig.siteName || 'Community Event Platform' }}</h2>
          <p class="intro-description">
            {{ siteConfig.siteDescription || 'A platform dedicated to community event organization, helping you discover exciting events around you and meet like-minded friends' }}
          </p>
          <div class="intro-actions">
            <el-button type="primary" size="large" class="intro-button primary" @click="goToActivities()">
              <span>Browse All Events</span>
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button v-if="!userStore.isLoggedIn" size="large" class="intro-button secondary" @click="goToRegister">
              <span>Register Now</span>
              <el-icon><UserFilled /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <div class="footer">
      <div class="footer-background"></div>
      <div class="container">
        <div class="footer-content">
          <div class="footer-info">
            <h3>{{ siteConfig.siteName || 'Community Event Platform' }}</h3>
            <p>{{ siteConfig.siteDescription || 'A platform dedicated to community event organization' }}</p>
          </div>
          <div class="footer-contact">
            <h4>Contact Us</h4>
            <p v-if="siteConfig.contactEmail">
              <el-icon><Message /></el-icon>
              {{ siteConfig.contactEmail }}
            </p>
            <p v-if="siteConfig.contactPhone">
              <el-icon><Phone /></el-icon>
              {{ siteConfig.contactPhone }}
            </p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2025 {{ siteConfig.siteName || 'Community Event Platform' }}. All rights reserved.</p>
        </div>
      </div>
    </div>

    <!-- 通知抽屉 -->
    <NotificationDrawer
        v-model="notificationDrawerVisible"
        @update-unread-count="loadUnreadCount"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar, User, Plus, UserFilled, Picture, Grid, Clock, Location, Star, Bell,
  ArrowRight, Money, Setting, SwitchButton, Message, Phone
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import NotificationDrawer from '@/components/NotificationDrawer.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(true)
const banners = ref([])
const latestActivities = ref([])
const featuredActivities = ref([])
const categories = ref([])
const stats = ref({
  totalActivities: 0,
  totalUsers: 0,
  thisMonthActivities: 0,
  thisMonthUsers: 0
})
const siteConfig = ref({
  siteName: 'Community Event Platform',
  siteDescription: 'A platform dedicated to community event organization',
  contactEmail: '',
  contactPhone: ''
})

// 通知相关数据
const notificationDrawerVisible = ref(false)
const unreadCount = ref(0)

// 方法
const loadHomePageData = async () => {
  try {
    loading.value = true
    const response = await request.get('/api/public/homepage')

    if (response.code === 200) {
      const data = response.data
      banners.value = data.banners || []
      latestActivities.value = data.latestActivities || []
      featuredActivities.value = data.featuredActivities || []
      categories.value = data.categories || []
      stats.value = data.stats || stats.value
      siteConfig.value = { ...siteConfig.value, ...data.siteConfig }
    } else {
      ElMessage.error(response.message || 'Failed to load homepage data')
    }
  } catch (error) {
    console.error('Failed to load homepage data:', error)
    ElMessage.error('Failed to load homepage data')
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  if (!userStore.isLoggedIn) {
    unreadCount.value = 0
    return
  }

  try {
    const response = await request.get('/api/user/messages/unread/count')
    if (response.code === 200) {
      unreadCount.value = response.data
    }
  } catch (error) {
    console.error('Failed to load unread message count:', error)
  }
}

const toggleNotificationDrawer = () => {
  notificationDrawerVisible.value = !notificationDrawerVisible.value
}

// 轮播图相关方法
const handleBannerClick = async (banner) => {
  try {
    // 记录点击统计
    await request.post(`/api/public/homepage/banners/${banner.id}/click`)

    // 如果有链接，则跳转
    if (banner.linkUrl) {
      if (banner.linkUrl.startsWith('http')) {
        // 外部链接
        window.open(banner.linkUrl, '_blank')
      } else {
        // 内部链接
        router.push(banner.linkUrl)
      }
    }
  } catch (error) {
    console.error('Failed to handle banner click:', error)
  }
}

const handleImageError = (event) => {
  console.error('Banner image failed to load:', event.target.src)
  event.target.style.display = 'none'
}

const handleActivityImageError = (event) => {
  event.target.style.display = 'none'
  const placeholder = event.target.parentNode.querySelector('.placeholder-image')
  if (placeholder) {
    placeholder.style.display = 'flex'
  }
}

// 活动相关方法
const getActivityImage = (activity) => {
  // 优先返回封面图片
  if (activity.coverImage) {
    return activity.coverImage
  }

  // 如果没有封面图片，则尝试从images中获取第一张图片
  if (activity.images) {
    return getFirstImage(activity.images)
  }

  return null
}

const getFirstImage = (images) => {
  if (!images) return null

  try {
    if (typeof images === 'string') {
      // 尝试解析JSON格式的图片数组
      if (images.startsWith('[') && images.endsWith(']')) {
        const imageArray = JSON.parse(images)
        return Array.isArray(imageArray) && imageArray.length > 0 ? imageArray[0] : null
      } else if (images.startsWith('{') && images.endsWith('}')) {
        // 如果是JSON对象格式，跳过
        return null
      } else {
        // 如果是逗号分隔的字符串
        const imageArray = images.split(',').map(img => img.trim()).filter(img => img.length > 0)
        return imageArray.length > 0 ? imageArray[0] : null
      }
    } else if (Array.isArray(images)) {
      return images.length > 0 ? images[0] : null
    }
  } catch (error) {
    console.error('Failed to parse activity images:', error, images)
  }

  return null
}

const getStatusClass = (status) => {
  const statusMap = {
    'published': 'status-published',
    'pending': 'status-pending',
    'cancelled': 'status-cancelled',
    'completed': 'status-completed'
  }
  return statusMap[status] || 'status-default'
}

const getStatusText = (status) => {
  const statusMap = {
    'published': 'Active',
    'pending': 'Pending',
    'cancelled': 'Cancelled',
    'completed': 'Completed'
  }
  return statusMap[status] || status
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''

  const date = new Date(dateTime)
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const activityDate = new Date(date.getFullYear(), date.getMonth(), date.getDate())

  if (activityDate.getTime() === today.getTime()) {
    return `Today ${date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })}`
  } else if (activityDate.getTime() === today.getTime() + 24 * 60 * 60 * 1000) {
    return `Tomorrow ${date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })}`
  } else {
    return date.toLocaleString('en-US', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }
}

const getTagArray = (tags) => {
  if (!tags) return []

  try {
    if (typeof tags === 'string') {
      // 如果是逗号分隔的字符串
      return tags.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0)
    } else if (Array.isArray(tags)) {
      return tags
    }
  } catch (error) {
    console.error('Failed to parse tags:', error)
  }

  return []
}

const getParticipationRate = (activity) => {
  if (!activity.maxParticipants || activity.maxParticipants === 0) return 0
  return Math.round((activity.currentParticipants / activity.maxParticipants) * 100)
}

const getProgressColor = (activity) => {
  const rate = getParticipationRate(activity)
  if (rate >= 80) return '#f56c6c'
  if (rate >= 60) return '#e6a23c'
  return '#67c23a'
}

// 导航方法
const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToAdmin = () => {
  router.push('/admin/dashboard')
}

const goToActivities = (categoryId = null) => {
  const query = categoryId ? { categoryId } : {}
  router.push({ path: '/activities', query })
}

const goToActivityDetail = (activityId) => {
  router.push(`/activities/${activityId}`)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('Are you sure you want to logout?', 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    await userStore.logout()
    ElMessage.success('Logged out successfully')

    // 清空未读数量
    unreadCount.value = 0
  } catch (error) {
    // 用户取消
  }
}

// 生命周期
onMounted(() => {
  loadHomePageData()
  loadUnreadCount()

  // 定时更新未读数量
  setInterval(() => {
    if (userStore.isLoggedIn) {
      loadUnreadCount()
    }
  }, 30000) // 每30秒更新一次
})
</script>

<style scoped>
/* 全局样式 */
.homepage {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow-x: hidden;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部样式 */
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
}

.site-title {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
}

.title-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 50px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border: 1px solid rgba(148, 163, 184, 0.2);
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
}

.welcome-text {
  color: #475569;
  font-weight: 500;
  font-size: 14px;
}

.notification-wrapper {
  position: relative;
}

.notification-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  border: 2px solid white;
  font-weight: 600;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-3px);
  }
  60% {
    transform: translateY(-2px);
  }
}

.notification-button {
  position: relative;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.notification-button:hover {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.notification-icon {
  font-size: 20px;
}

.notification-pulse {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(239, 68, 68, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
  }
}

.nav-button {
  border-radius: 50px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.nav-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.admin-button {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  border-color: #8b5cf6;
}

.logout-button {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border-color: #ef4444;
  color: white;
}

.login-button, .register-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
}

/* 轮播图样式 */
.banner-section {
  position: relative;
  padding: 40px 0;
}

.banner-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.background-decoration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.banner-carousel {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
}

/* 修复轮播图指示器样式，隐藏原生指示器并自定义样式 */
.modern-carousel :deep(.el-carousel__indicators) {
  /* 隐藏原生指示器容器，只显示自定义的 */
  opacity: 0;
  pointer-events: none;
}

.modern-carousel :deep(.el-carousel__indicators--outside) {
  /* 如果是外部指示器也隐藏 */
  display: none;
}

/* 创建自定义指示器 */
.modern-carousel {
  position: relative;
}

.modern-carousel::after {
  content: '';
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  /* 这里可以添加自定义指示器的样式，如果需要的话 */
}

/* 如果需要保留指示器，可以使用以下样式替代上面的隐藏样式 */
.modern-carousel :deep(.el-carousel__indicator) {
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 6px;
  width: 12px;
  height: 4px;
  border: none;
}

.modern-carousel :deep(.el-carousel__indicator.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  width: 24px;
}

.modern-carousel :deep(.el-carousel__indicator button) {
  background-color: transparent;
  border: none;
  outline: none;
  width: 100%;
  height: 100%;
  border-radius: inherit;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 500px;
  cursor: pointer;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.banner-item:hover .banner-image {
  transform: scale(1.05);
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(
      to top,
      rgba(0, 0, 0, 0.8) 0%,
      rgba(0, 0, 0, 0.4) 50%,
      transparent 100%
  );
  color: white;
  padding: 60px;
}

.banner-content {
  max-width: 600px;
}

.banner-title {
  font-size: 42px;
  font-weight: 800;
  margin: 0 0 20px 0;
  line-height: 1.2;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
}

.banner-description {
  font-size: 20px;
  margin: 0 0 32px 0;
  line-height: 1.6;
  opacity: 0.9;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
}

.banner-action {
  margin-top: 32px;
}

.action-button {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 32px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  color: white;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
}

.no-banners {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 24px;
}

.no-banners-content {
  text-align: center;
  color: #64748b;
}

.no-banners-icon {
  font-size: 80px;
  margin-bottom: 24px;
  opacity: 0.6;
}

.no-banners-text {
  font-size: 20px;
  margin: 0;
  font-weight: 500;
}

/* 活动区域样式 */
.latest-activities-section,
.featured-activities-section {
  position: relative;
  padding: 80px 0;
}

.latest-activities-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.section-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at top, rgba(102, 126, 234, 0.05) 0%, transparent 50%);
}

.featured-activities-section {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 60px;
}

.section-title-wrapper {
  flex: 1;
}

.section-title-wrapper.center {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 42px;
  font-weight: 800;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-subtitle {
  font-size: 18px;
  color: #64748b;
  margin: 0;
  font-weight: 400;
}

.section-button {
  border-radius: 50px;
  padding: 12px 28px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.section-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 32px;
}

/* 活动卡片样式 */
.modern-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.modern-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.featured-card {
  border: 2px solid transparent;
  background: linear-gradient(white, white) padding-box,
  linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%) border-box;
}

.card-image {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-card:hover .card-image img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.1) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-card:hover .image-overlay {
  opacity: 1;
}

.placeholder-image {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}

.placeholder-icon {
  font-size: 60px;
}

.modern-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 6px 16px;
  border-radius: 50px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: 6px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-published {
  background: rgba(34, 197, 94, 0.9);
}

.status-pending {
  background: rgba(245, 158, 11, 0.9);
}

.status-cancelled {
  background: rgba(239, 68, 68, 0.9);
}

.status-completed {
  background: rgba(107, 114, 128, 0.9);
}

.modern-fee {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 8px 16px;
  border-radius: 50px;
  font-size: 13px;
  font-weight: 700;
  background: rgba(59, 130, 246, 0.9);
  color: white;
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: 4px;
}

.modern-fee.free {
  background: rgba(34, 197, 94, 0.9);
}

.hot-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 8px 16px;
  border-radius: 50px;
  font-size: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 4px 12px rgba(251, 191, 36, 0.3);
}

.hot-icon {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.card-content {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.activity-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  line-height: 1.3;
  flex: 1;
  margin-right: 16px;
}

.modern-tag {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  color: #475569;
  padding: 6px 12px;
  border-radius: 50px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.activity-description {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.meta-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #667eea;
  font-size: 16px;
}

.progress-section {
  margin-bottom: 20px;
}

.progress-wrapper {
  margin-bottom: 8px;
}

.modern-progress :deep(.el-progress-bar__outer) {
  border-radius: 10px;
  background: #f1f5f9;
}

.modern-progress :deep(.el-progress-bar__inner) {
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.progress-text {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.card-footer {
  padding: 0 24px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
}

.modern-tag-item {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border: 1px solid rgba(148, 163, 184, 0.2);
  color: #475569;
  font-size: 11px;
  font-weight: 500;
  border-radius: 6px;
}

.card-action {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-card:hover .card-action {
  opacity: 1;
  transform: translateX(4px);
}

.action-icon {
  font-size: 16px;
}

/* 分类样式 */
.categories-section {
  position: relative;
  padding: 80px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.categories-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="50" r="0.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
}

.categories-section .section-title,
.categories-section .section-subtitle {
  color: white;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
}

.modern-category {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px 32px;
  border-radius: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

.category-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-category:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.modern-category:hover .category-background {
  opacity: 1;
}

.category-icon {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: white;
  font-size: 36px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-category:hover .category-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.3);
}

.category-name {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #1e293b;
}

.category-description {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 24px 0;
  line-height: 1.5;
}

.category-arrow {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  color: #667eea;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-category:hover .category-arrow {
  opacity: 1;
  transform: translateX(4px);
}

/* 统计数据样式 - 修改为一排显示 */
.stats-section {
  position: relative;
  padding: 80px 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.modern-stat {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 32px 24px;
  background: white;
  border-radius: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.1);
  overflow: hidden;
}

.stat-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-stat:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.12);
}

.modern-stat:hover .stat-background {
  opacity: 1;
}

.stat-icon {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  color: white;
  font-size: 28px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.calendar-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-icon {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.plus-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.users-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.modern-stat:hover .stat-icon {
  transform: scale(1.1) rotate(-5deg);
}

.stat-content {
  position: relative;
  flex: 1;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 8px 0;
  color: #1e293b;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

.stat-decoration {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  opacity: 0;
  transition: all 0.3s ease;
}

.modern-stat:hover .stat-decoration {
  opacity: 1;
  transform: scale(1.2);
}

/* 介绍区域样式 */
.intro-section {
  position: relative;
  padding: 100px 0;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  text-align: center;
  overflow: hidden;
}

.intro-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at center, rgba(102, 126, 234, 0.1) 0%, transparent 70%);
}

.intro-decoration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1000px;
  height: 1000px;
  background: conic-gradient(from 0deg, transparent, rgba(255, 255, 255, 0.03), transparent);
  border-radius: 50%;
  animation: rotate-slow 20s linear infinite;
}

@keyframes rotate-slow {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

.intro-content {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
}

.intro-title {
  font-size: 56px;
  font-weight: 900;
  margin: 0 0 24px 0;
  background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.intro-description {
  font-size: 22px;
  line-height: 1.6;
  margin: 0 0 48px 0;
  opacity: 0.9;
  font-weight: 400;
}

.intro-actions {
  display: flex;
  justify-content: center;
  gap: 24px;
  flex-wrap: wrap;
}

.intro-button {
  border-radius: 50px;
  padding: 16px 32px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.intro-button.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.intro-button.secondary {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

.intro-button:hover {
  transform: translateY(-2px);
}

.intro-button.primary:hover {
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.intro-button.secondary:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 页脚样式 */
.footer {
  position: relative;
  background: #0f172a;
  color: white;
  padding: 60px 0 30px;
}

.footer-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95) 0%, rgba(30, 41, 59, 0.95) 100%);
}

.footer-content {
  position: relative;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 48px;
  margin-bottom: 40px;
}

.footer-info h3 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 16px 0;
  background: linear-gradient(135deg, #ffffff 0%, #cbd5e1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-info p {
  font-size: 16px;
  line-height: 1.6;
  margin: 0;
  opacity: 0.8;
  color: #cbd5e1;
}

.footer-contact h4 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #e2e8f0;
}

.footer-contact p {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  margin: 0 0 12px 0;
  color: #94a3b8;
  transition: color 0.3s ease;
}

.footer-contact p:hover {
  color: #e2e8f0;
}

.footer-bottom {
  position: relative;
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.footer-bottom p {
  margin: 0;
  font-size: 14px;
  opacity: 0.6;
  color: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }

  .header-content {
    flex-direction: column;
    height: auto;
    padding: 20px 0;
    gap: 20px;
  }

  .header-actions {
    flex-wrap: wrap;
    justify-content: center;
  }

  .welcome-section {
    order: -1;
  }

  .banner-item {
    height: 300px;
  }

  .banner-overlay {
    padding: 32px;
  }

  .banner-title {
    font-size: 28px;
  }

  .banner-description {
    font-size: 16px;
  }

  .section-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }

  .section-title {
    font-size: 32px;
  }

  .activities-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .categories-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 24px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .modern-stat {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }

  .stat-icon {
    margin-bottom: 16px;
  }

  .intro-title {
    font-size: 40px;
  }

  .intro-description {
    font-size: 18px;
  }

  .intro-actions {
    flex-direction: column;
    align-items: center;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
    text-align: center;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-button) {
  border-radius: 12px;
}

:deep(.el-progress-bar__outer) {
  border-radius: 10px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 10px;
}

:deep(.el-tag) {
  border-radius: 8px;
}
</style>