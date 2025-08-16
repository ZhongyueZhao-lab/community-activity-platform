<template>
  <div class="activities-page">
    <!-- 页面背景装饰 -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <div class="container">
      <!-- 页面头部 - 简洁设计，直接在渐变背景上显示 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">
              <span class="title-gradient">Discover Events</span>
            </h1>
            <p class="page-subtitle">Find exciting events around you and join the community</p>
          </div>
          <div class="header-actions">
            <el-button class="nav-button home-button" @click="goToHome">
              <el-icon><House /></el-icon>
              <span>Back to Home</span>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 筛选和搜索 -->
      <div class="filter-section">
        <div class="filter-background"></div>
        <div class="filter-content">
          <div class="filter-header">
            <h3 class="filter-title">Filter Events</h3>
            <p class="filter-subtitle">Find exactly what you're looking for</p>
          </div>

          <el-form :model="searchForm" :inline="true" @submit.prevent="searchActivities" class="filter-form">
            <div class="form-row">
              <el-form-item label="Event Name" class="form-item">
                <el-input
                    v-model="searchForm.title"
                    placeholder="Search by event name..."
                    clearable
                    class="modern-input"
                >
                  <template #prefix>
                    <el-icon class="input-icon"><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="Category" class="form-item">
                <el-select
                    v-model="searchForm.categoryId"
                    placeholder="Select category..."
                    clearable
                    class="modern-select"
                >
                  <el-option
                      v-for="category in categories"
                      :key="category.id"
                      :label="category.name"
                      :value="category.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item class="form-actions">
                <el-button type="primary" @click="searchActivities" class="search-button">
                  <el-icon><Search /></el-icon>
                  <span>Search</span>
                </el-button>
                <el-button @click="resetSearch" class="reset-button">
                  <el-icon><Refresh /></el-icon>
                  <span>Reset</span>
                </el-button>
              </el-form-item>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 活动列表 -->
      <div class="activities-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-section">
          <div class="loading-content">
            <div class="loading-animation">
              <div class="loading-spinner"></div>
            </div>
            <p class="loading-text">Loading amazing events...</p>
          </div>
        </div>

        <!-- 活动内容 -->
        <div v-else class="activities-content">
          <!-- 结果统计 -->
          <div v-if="activities.length > 0" class="results-header">
            <div class="results-info">
              <h3 class="results-title">{{ total }} Events Found</h3>
              <p class="results-subtitle">Showing {{ activities.length }} events on this page</p>
            </div>
          </div>

          <!-- 活动网格 -->
          <div v-if="activities.length > 0" class="activities-grid">
            <div
                v-for="activity in activities"
                :key="activity.id"
                class="activity-card modern-card"
                @click="goToActivityDetail(activity.id)"
            >
              <div class="card-image">
                <img
                    v-if="getActivityImage(activity)"
                    :src="getActivityImage(activity)"
                    :alt="activity.title"
                    @error="handleImageError"
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
                    <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }} joined</span>
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
                <div class="card-action">
                  <span class="action-text">View Details</span>
                  <el-icon class="action-icon"><ArrowRight /></el-icon>
                </div>
              </div>
            </div>
          </div>

          <!-- 无活动状态 -->
          <div v-else class="no-activities">
            <div class="no-activities-content">
              <div class="no-activities-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <h3 class="no-activities-title">No Events Found</h3>
              <p class="no-activities-text">
                We couldn't find any events matching your criteria. Try adjusting your filters or check back later.
              </p>
              <el-button type="primary" @click="resetSearch" class="no-activities-button">
                <el-icon><Refresh /></el-icon>
                <span>Clear Filters</span>
              </el-button>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination-section">
            <div class="pagination-wrapper">
              <!-- 配置英文分页器 -->
              <el-config-provider :locale="enLocale">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :total="total"
                    :page-sizes="[12, 24, 36, 48]"
                    :layout="paginationLayout"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    class="modern-pagination"
                />
              </el-config-provider>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElConfigProvider } from 'element-plus'
import {
  Calendar, Clock, Location, User, Money, ArrowRight, Search, Refresh, House
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

// 英文语言配置
const enLocale = en

// 响应式数据
const loading = ref(true)
const activities = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const searchForm = ref({
  title: '',
  categoryId: null
})

// 计算属性：根据屏幕大小调整分页器布局
const paginationLayout = computed(() => {
  if (window.innerWidth < 768) {
    return 'prev, pager, next'
  } else if (window.innerWidth < 1024) {
    return 'total, prev, pager, next'
  } else {
    return 'total, sizes, prev, pager, next, jumper'
  }
})

// 方法
const loadActivities = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      title: searchForm.value.title || undefined,
      categoryId: searchForm.value.categoryId || undefined
    }

    const response = await request.get('/api/public/activities', { params })

    if (response.code === 200) {
      activities.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || 'Failed to load activities')
    }
  } catch (error) {
    console.error('Failed to load activities:', error)
    ElMessage.error('Failed to load activities')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await request.get('/api/public/categories')

    if (response.code === 200) {
      categories.value = response.data || []
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const searchActivities = () => {
  currentPage.value = 1
  loadActivities()
}

const resetSearch = () => {
  searchForm.value = {
    title: '',
    categoryId: null
  }
  currentPage.value = 1
  loadActivities()
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadActivities()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  loadActivities()
}

const goToHome = () => {
  router.push('/home')
}

const goToActivityDetail = (activityId) => {
  router.push(`/activities/${activityId}`)
}

// 获取活动图片的方法，参考首页代码
const getActivityImage = (activity) => {
  // 优先返回封面图片
  if (activity.coverImage) {
    return activity.coverImage
  }

  // 如果没有封面图片，则尝试从imageList中获取第一张图片
  if (activity.imageList && activity.imageList.length > 0) {
    return activity.imageList[0]
  }

  // 如果没有imageList，则尝试从images中获取第一张图片
  if (activity.images) {
    return getFirstImage(activity.images)
  }

  return null
}

// 工具方法
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

const handleImageError = (event) => {
  event.target.style.display = 'none'
  const placeholder = event.target.parentNode.querySelector('.placeholder-image')
  if (placeholder) {
    placeholder.style.display = 'flex'
  }
}

// 监听路由查询参数
watch(() => route.query, (newQuery) => {
  if (newQuery.categoryId) {
    searchForm.value.categoryId = parseInt(newQuery.categoryId)
    searchActivities()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  loadCategories()

  // 如果有查询参数，设置对应的筛选条件
  if (route.query.categoryId) {
    searchForm.value.categoryId = parseInt(route.query.categoryId)
  }

  loadActivities()
})
</script>

<style scoped>
/* 全局样式 */
.activities-page {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
}

/* 确保所有头部相关元素都是透明背景 */
.activities-page .container,
.activities-page .page-header,
.activities-page .header-content,
.activities-page .header-text,
.activities-page .page-title,
.activities-page .page-subtitle,
.activities-page .header-actions {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
  box-shadow: none !important;
  border: none !important;
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
  top: 20%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1000px;
  height: 1000px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0px); }
  50% { transform: translate(-50%, -50%) translateY(-20px); }
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

.container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  background: transparent !important;
}

/* 页面头部样式 - 简洁设计，直接在渐变背景上显示 */
.page-header {
  padding: 60px 0 40px;
  margin-bottom: 40px;
  background: transparent !important;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  background: transparent !important;
}

.header-text {
  flex: 1;
  background: transparent !important;
}

.header-text {
  flex: 1;
}

.page-title {
  font-size: 64px;
  font-weight: 900;
  margin: 0 0 16px 0;
  line-height: 1.1;
  background: transparent !important;
}

.title-gradient {
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.95) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3)) drop-shadow(0 0 20px rgba(0, 0, 0, 0.2));
}

.page-subtitle {
  font-size: 22px;
  color: rgba(255, 255, 255, 0.95);
  margin: 0;
  font-weight: 400;
  line-height: 1.5;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4), 0 0 16px rgba(0, 0, 0, 0.3);
  background: transparent !important;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.nav-button {
  border-radius: 50px;
  padding: 14px 32px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 16px;
}

.home-button {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2), 0 2px 8px rgba(0, 0, 0, 0.1);
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.nav-button:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.35);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.25), 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 筛选区域样式 */
.filter-section {
  position: relative;
  margin-bottom: 40px;
}

.filter-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.filter-content {
  position: relative;
  padding: 32px;
}

.filter-header {
  margin-bottom: 32px;
}

.filter-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.filter-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

.filter-form {
  width: 100%;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: end;
}

.form-item {
  flex: 1;
  min-width: 250px;
  margin-bottom: 0 !important;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 0 !important;
}

/* 表单控件样式 */
.modern-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modern-select :deep(.el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-select :deep(.el-select__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-select :deep(.el-select__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-icon {
  color: #94a3b8;
}

.search-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 0 24px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.reset-button {
  border-radius: 12px;
  padding: 0 24px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.reset-button:hover {
  border-color: #94a3b8;
  color: #475569;
  transform: translateY(-1px);
}

/* 活动区域样式 */
.activities-section {
  position: relative;
  min-height: 500px;
}

/* 加载状态 */
.loading-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
}

.loading-content {
  text-align: center;
}

.loading-animation {
  margin-bottom: 24px;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 18px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

/* 活动内容 */
.activities-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.results-header {
  margin-bottom: 32px;
}

.results-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.results-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

/* 活动网格 */
.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 32px;
  margin-bottom: 40px;
}

/* 活动卡片样式 - 复用首页样式 */
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
  justify-content: flex-end;
  align-items: center;
}

.card-action {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  font-weight: 600;
  font-size: 14px;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-card:hover .card-action {
  opacity: 1;
  transform: translateX(4px);
}

.action-text {
  font-weight: 600;
}

.action-icon {
  font-size: 16px;
}

/* 无活动状态 */
.no-activities {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  text-align: center;
}

.no-activities-content {
  max-width: 400px;
}

.no-activities-icon {
  font-size: 120px;
  color: #cbd5e1;
  margin-bottom: 32px;
}

.no-activities-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #475569;
}

.no-activities-text {
  font-size: 16px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 32px 0;
}

.no-activities-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50px;
  padding: 12px 28px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.no-activities-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 分页样式 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid #e2e8f0;
}

.pagination-wrapper {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 50px;
  padding: 16px 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.modern-pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.modern-pagination :deep(.el-pager li) {
  border-radius: 8px;
  min-width: 36px;
  height: 36px;
  line-height: 36px;
  margin: 0 2px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-pagination :deep(.el-pager li:hover) {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
}

.modern-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.modern-pagination :deep(.btn-prev),
.modern-pagination :deep(.btn-next) {
  border-radius: 8px;
  width: 36px;
  height: 36px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-pagination :deep(.btn-prev:hover),
.modern-pagination :deep(.btn-next:hover) {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
}

.modern-pagination :deep(.el-select) {
  margin: 0 8px;
}

.modern-pagination :deep(.el-pagination__jump) {
  margin-left: 16px;
}

.modern-pagination :deep(.el-pagination__total) {
  margin-right: 16px;
  font-weight: 500;
  color: #475569;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .activities-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }

  .page-header {
    padding: 40px 0 30px;
  }

  .header-content {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }

  .page-title {
    font-size: 42px;
  }

  .page-subtitle {
    font-size: 18px;
  }

  .filter-content {
    padding: 20px;
  }

  .form-row {
    flex-direction: column;
    gap: 16px;
  }

  .form-item {
    min-width: 100%;
  }

  .form-actions {
    flex-direction: column;
    width: 100%;
  }

  .form-actions .el-button {
    width: 100%;
  }

  .activities-content {
    padding: 20px;
  }

  .activities-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .results-title {
    font-size: 20px;
  }

  .no-activities-icon {
    font-size: 80px;
  }

  .no-activities-title {
    font-size: 24px;
  }

  .pagination-wrapper {
    padding: 12px 16px;
  }

  .modern-pagination :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 32px;
  }

  .activities-grid {
    grid-template-columns: 1fr;
  }

  .card-content {
    padding: 16px;
  }

  .activity-meta {
    gap: 8px;
  }

  .meta-icon {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
}

:deep(.el-button) {
  font-weight: 600;
}

:deep(.el-progress-bar__outer) {
  border-radius: 10px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 10px;
}
</style>