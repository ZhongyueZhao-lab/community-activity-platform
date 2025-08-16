<template>
  <div class="activity-detail-page">
    <!-- 页面背景装饰 -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-content">
        <div class="loading-animation">
          <div class="loading-spinner"></div>
        </div>
        <p class="loading-text">Loading event details...</p>
      </div>
    </div>

    <!-- 活动详情内容 -->
    <div v-else-if="activity" class="activity-content">
      <!-- 返回按钮 -->
      <div class="back-section">
        <div class="container">
          <el-button @click="goBack" class="back-button">
            <el-icon><ArrowLeft /></el-icon>
            <span>Back</span>
          </el-button>
        </div>
      </div>

      <!-- 活动头部信息 -->
      <div class="activity-header">
        <div class="header-background"></div>
        <div class="container">
          <div class="header-content">
            <div class="header-left">
              <div class="activity-meta">
                <el-tag v-if="activity.categoryName" class="category-tag modern-tag">
                  {{ activity.categoryName }}
                </el-tag>
                <span class="activity-id">#{{ activity.id }}</span>
              </div>
              <h1 class="activity-title">{{ activity.title }}</h1>
              <div class="activity-basic-info">
                <div class="info-item">
                  <div class="info-icon">
                    <el-icon><Clock /></el-icon>
                  </div>
                  <span>{{ formatDateTimeRange(activity.startTime, activity.endTime) }}</span>
                </div>
                <div v-if="activity.location" class="info-item">
                  <div class="info-icon">
                    <el-icon><Location /></el-icon>
                  </div>
                  <span>{{ activity.location }}</span>
                </div>
                <div v-if="activity.creatorName" class="info-item">
                  <div class="info-icon">
                    <el-icon><UserFilled /></el-icon>
                  </div>
                  <span>Organized by {{ activity.creatorName }}</span>
                </div>
              </div>
            </div>
            <div class="header-right">
              <div class="status-badges">
                <div class="status-badge modern-badge" :class="getStatusClass(activity.status)">
                  <div class="badge-dot"></div>
                  {{ getStatusText(activity.status) }}
                </div>
                <div class="fee-badge modern-fee" :class="{ 'free': activity.fee <= 0 }">
                  <el-icon><Money /></el-icon>
                  {{ activity.fee > 0 ? `$${activity.fee}` : 'Free' }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="container">
        <div class="main-content">
          <!-- 左侧主要内容 -->
          <div class="content-left">
            <!-- 活动图片 -->
            <div v-if="hasActivityImages()" class="image-section modern-card">
              <el-carousel
                  v-if="getActivityImages().length > 1"
                  height="400px"
                  indicator-position="inside"
                  arrow="hover"
                  class="modern-carousel"
              >
                <el-carousel-item v-for="(image, index) in getActivityImages()" :key="index">
                  <img :src="image" :alt="`Event image ${index + 1}`" class="activity-image" @error="handleImageError" />
                </el-carousel-item>
              </el-carousel>
              <div v-else class="single-image">
                <img :src="getActivityImages()[0]" alt="Event image" class="activity-image" @error="handleImageError" />
              </div>
            </div>

            <!-- 活动描述 -->
            <div class="description-section modern-card">
              <h2 class="section-title">Event Details</h2>
              <div class="activity-description" v-html="formatDescription(activity.description)"></div>
            </div>

            <!-- 活动标签 -->
            <div v-if="activity.tagList && activity.tagList.length > 0" class="tags-section modern-card">
              <h3 class="section-subtitle">Tags</h3>
              <div class="tags-container">
                <el-tag
                    v-for="tag in activity.tagList"
                    :key="tag"
                    class="activity-tag modern-tag-item"
                    effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <!-- 联系方式 -->
            <div v-if="activity.contactInfo" class="contact-section modern-card">
              <h3 class="section-subtitle">Contact Information</h3>
              <p class="contact-info">{{ activity.contactInfo }}</p>
            </div>

            <!-- 参与者列表 -->
            <div class="participants-section modern-card">
              <h3 class="section-subtitle">
                Participants ({{ activity.participants ? activity.participants.length : 0 }} people)
              </h3>
              <div v-if="activity.participants && activity.participants.length > 0" class="participants-list">
                <div
                    v-for="participant in activity.participants"
                    :key="participant.userId"
                    class="participant-item"
                >
                  <el-avatar
                      :src="participant.avatar"
                      :size="40"
                      class="participant-avatar"
                  >
                    {{ participant.nickname.charAt(0) }}
                  </el-avatar>
                  <div class="participant-info">
                    <span class="participant-name">{{ participant.nickname }}</span>
                    <span class="participant-time">
                      Registered {{ formatDateTime(participant.registrationTime) }}
                    </span>
                  </div>
                </div>
              </div>
              <div v-else class="no-participants">
                <div class="no-participants-content">
                  <div class="no-participants-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <p class="no-participants-text">No participants yet</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧信息栏 -->
          <div class="content-right">
            <!-- 报名信息卡片 -->
            <div class="registration-card modern-card">
              <div class="card-header">
                <h3>Registration Information</h3>
              </div>
              <div class="card-content">
                <!-- 名额信息 -->
                <div class="quota-info">
                  <div class="quota-item">
                    <span class="quota-label">Current Registrations</span>
                    <span class="quota-value">{{ activity.currentParticipants || 0 }} people</span>
                  </div>
                  <div v-if="activity.maxParticipants > 0" class="quota-item">
                    <span class="quota-label">Maximum Capacity</span>
                    <span class="quota-value">{{ activity.maxParticipants }} people</span>
                  </div>
                  <div v-if="activity.remainingSeats !== null" class="quota-item">
                    <span class="quota-label">Available Spots</span>
                    <span class="quota-value">{{ activity.remainingSeats }} people</span>
                  </div>
                </div>

                <!-- 进度条 -->
                <div v-if="activity.maxParticipants > 0" class="progress-info">
                  <el-progress
                      :percentage="Math.round(activity.participationRate || 0)"
                      :color="getProgressColor(activity.participationRate)"
                      :stroke-width="8"
                      class="modern-progress"
                  />
                  <span class="progress-text">
                    {{ Math.round(activity.participationRate || 0) }}% Filled
                  </span>
                </div>

                <!-- 时间信息 -->
                <div class="time-info">
                  <div v-if="activity.remainingDays !== null" class="time-item">
                    <span class="time-label">Time Until Start</span>
                    <span class="time-value">
                      {{ activity.remainingDays > 0 ? `${activity.remainingDays} days` : 'Started' }}
                    </span>
                  </div>
                  <div v-if="activity.registrationEndTime" class="time-item">
                    <span class="time-label">Registration Ends</span>
                    <span class="time-value">
                      {{ formatDateTime(activity.registrationEndTime) }}
                    </span>
                  </div>
                </div>

                <!-- 报名按钮 -->
                <div class="registration-actions">
                  <!-- 已报名状态 -->
                  <div v-if="activity.isRegistered" class="registered-status">
                    <el-alert
                        title="You have successfully registered for this event"
                        type="success"
                        :closable="false"
                        show-icon
                        class="registration-alert"
                    />
                    <div v-if="activity.userRegistrationTime" class="registration-time">
                      Registration time: {{ formatDateTime(activity.userRegistrationTime) }}
                    </div>
                    <el-button
                        type="danger"
                        :loading="cancelLoading"
                        @click="handleCancelRegistration"
                        :disabled="!canCancel"
                        class="action-button cancel-button"
                        size="large"
                    >
                      {{ canCancel ? 'Cancel Registration' : 'Cannot Cancel' }}
                    </el-button>
                  </div>

                  <!-- 未报名状态 -->
                  <div v-else class="unregistered-status">
                    <el-button
                        v-if="activity.canRegister"
                        type="primary"
                        size="large"
                        :loading="registerLoading"
                        @click="handleRegister"
                        class="action-button register-button"
                    >
                      <el-icon><Plus /></el-icon>
                      <span>Register Now</span>
                    </el-button>
                    <el-alert
                        v-else
                        :title="activity.cannotRegisterReason || 'Registration not available'"
                        type="warning"
                        :closable="false"
                        show-icon
                        class="registration-alert"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- 创建者信息卡片 -->
            <div v-if="activity.creatorName" class="creator-card modern-card">
              <div class="card-header">
                <h3>Event Organizer</h3>
              </div>
              <div class="card-content">
                <div class="creator-info">
                  <el-avatar
                      :src="activity.creatorAvatar"
                      :size="60"
                      class="creator-avatar"
                  >
                    {{ activity.creatorName.charAt(0) }}
                  </el-avatar>
                  <div class="creator-details">
                    <h4 class="creator-name">{{ activity.creatorName }}</h4>
                    <p class="creator-meta">
                      Published {{ formatDateTime(activity.createdAt) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 活动统计信息 -->
            <div class="stats-card modern-card">
              <div class="card-header">
                <h3>Event Statistics</h3>
              </div>
              <div class="card-content">
                <div class="stats-grid">
                  <div class="stat-item">
                    <div class="stat-icon">
                      <el-icon><User /></el-icon>
                    </div>
                    <span class="stat-value">{{ activity.currentParticipants || 0 }}</span>
                    <span class="stat-label">Registered</span>
                  </div>
                  <div v-if="activity.maxParticipants > 0" class="stat-item">
                    <div class="stat-icon">
                      <el-icon><UserFilled /></el-icon>
                    </div>
                    <span class="stat-value">{{ activity.maxParticipants }}</span>
                    <span class="stat-label">Max Capacity</span>
                  </div>
                  <div v-if="activity.remainingDays !== null" class="stat-item">
                    <div class="stat-icon">
                      <el-icon><Clock /></el-icon>
                    </div>
                    <span class="stat-value">{{ Math.max(0, activity.remainingDays) }}</span>
                    <span class="stat-label">Days Left</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-container">
      <div class="error-content modern-card">
        <div class="error-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <h3 class="error-title">Failed to Load</h3>
        <p class="error-text">Unable to load event details. Please try again later.</p>
        <div class="error-actions">
          <el-button type="primary" @click="loadActivityDetail" class="retry-button">
            <el-icon><Refresh /></el-icon>
            <span>Retry</span>
          </el-button>
          <el-button @click="goBack" class="back-button">
            <el-icon><ArrowLeft /></el-icon>
            <span>Go Back</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 报名确认对话框 -->
    <el-dialog
        v-model="registerDialogVisible"
        title="Confirm Registration"
        width="500px"
        :close-on-click-modal="false"
        class="modern-dialog"
    >
      <div class="register-dialog-content">
        <p>Are you sure you want to register for this event?</p>
        <el-form :model="registerForm" label-width="100px">
          <el-form-item label="Notes">
            <el-input
                v-model="registerForm.notes"
                type="textarea"
                :rows="3"
                placeholder="Optional notes..."
                maxlength="200"
                show-word-limit
                class="modern-input"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="registerDialogVisible = false" class="dialog-button">Cancel</el-button>
        <el-button
            type="primary"
            :loading="registerLoading"
            @click="confirmRegister"
            class="dialog-button primary"
        >
          Confirm Registration
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Location, UserFilled, User, Money, Plus, ArrowLeft, Warning, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(true)
const activity = ref(null)
const registerLoading = ref(false)
const cancelLoading = ref(false)
const registerDialogVisible = ref(false)
const registerForm = ref({
  notes: ''
})

// 计算属性
const canCancel = computed(() => {
  if (!activity.value) return false
  const now = new Date()
  const startTime = new Date(activity.value.startTime)
  return startTime > now // 活动未开始才能取消
})

// 方法
const loadActivityDetail = async () => {
  try {
    loading.value = true
    const activityId = route.params.id
    const response = await request.get(`/api/public/activities/${activityId}/detail`)

    if (response.code === 200) {
      activity.value = response.data
    } else {
      ElMessage.error(response.message || 'Failed to load event details')
    }
  } catch (error) {
    console.error('Failed to load event details:', error)
    ElMessage.error('Failed to load event details')
  } finally {
    loading.value = false
  }
}

// 新增：获取活动图片的方法，参考首页代码
const getActivityImages = () => {
  if (!activity.value) return []

  const images = []

  // 优先添加封面图片
  if (activity.value.coverImage) {
    images.push(activity.value.coverImage)
  }

  // 添加imageList中的其他图片（如果存在且不重复）
  if (activity.value.imageList && activity.value.imageList.length > 0) {
    activity.value.imageList.forEach(image => {
      if (image && !images.includes(image)) {
        images.push(image)
      }
    })
  }

  // 如果没有封面图片，尝试从images字段解析
  if (images.length === 0 && activity.value.images) {
    const parsedImages = parseImageString(activity.value.images)
    parsedImages.forEach(image => {
      if (image && !images.includes(image)) {
        images.push(image)
      }
    })
  }

  return images
}

// 新增：检查是否有活动图片
const hasActivityImages = () => {
  return getActivityImages().length > 0
}

// 新增：解析图片字符串的方法，参考首页代码
const parseImageString = (images) => {
  if (!images) return []

  try {
    if (typeof images === 'string') {
      // 尝试解析JSON格式的图片数组
      if (images.startsWith('[') && images.endsWith(']')) {
        const imageArray = JSON.parse(images)
        return Array.isArray(imageArray) ? imageArray : []
      } else if (images.startsWith('{') && images.endsWith('}')) {
        // 如果是JSON对象格式，跳过
        return []
      } else {
        // 如果是逗号分隔的字符串
        const imageArray = images.split(',').map(img => img.trim()).filter(img => img.length > 0)
        return imageArray
      }
    } else if (Array.isArray(images)) {
      return images
    }
  } catch (error) {
    console.error('Failed to parse event images:', error, images)
  }

  return []
}

// 新增：图片加载失败处理
const handleImageError = (event) => {
  console.error('Event image failed to load:', event.target.src)
  event.target.style.display = 'none'
}

const handleRegister = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }

  registerDialogVisible.value = true
}

const confirmRegister = async () => {
  try {
    registerLoading.value = true
    const activityId = route.params.id

    const requestData = {
      notes: registerForm.value.notes.trim() || null
    }

    const response = await request.post(`/api/public/activities/${activityId}/register`, requestData)

    if (response.code === 200) {
      ElMessage.success('Registration successful')
      registerDialogVisible.value = false
      registerForm.value.notes = ''
      await loadActivityDetail() // 重新加载数据
    } else {
      ElMessage.error(response.message || 'Registration failed')
    }
  } catch (error) {
    console.error('Registration failed:', error)
    ElMessage.error('Registration failed, please try again')
  } finally {
    registerLoading.value = false
  }
}

const handleCancelRegistration = async () => {
  try {
    await ElMessageBox.confirm('Are you sure you want to cancel your registration?', 'Confirm Cancellation', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    cancelLoading.value = true
    const activityId = route.params.id

    const response = await request.post(`/api/public/activities/${activityId}/cancel`)

    if (response.code === 200) {
      ElMessage.success('Registration cancelled successfully')
      await loadActivityDetail() // 重新加载数据
    } else {
      ElMessage.error(response.message || 'Failed to cancel registration')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel registration:', error)
      ElMessage.error('Failed to cancel registration, please try again')
    }
  } finally {
    cancelLoading.value = false
  }
}

const goBack = () => {
  router.back()
}

// 格式化方法
const formatDateTimeRange = (startTime, endTime) => {
  if (!startTime || !endTime) return ''

  const start = new Date(startTime)
  const end = new Date(endTime)

  const startStr = start.toLocaleString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })

  const endStr = end.toLocaleString('en-US', {
    hour: '2-digit',
    minute: '2-digit'
  })

  return `${startStr} - ${endStr}`
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''

  const date = new Date(dateTime)
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDescription = (description) => {
  if (!description) return ''

  // 简单的换行处理
  return description.replace(/\n/g, '<br>')
}

const getStatusClass = (status) => {
  const classMap = {
    'published': 'status-published',
    'pending': 'status-pending',
    'cancelled': 'status-cancelled',
    'completed': 'status-completed'
  }
  return classMap[status] || 'status-default'
}

const getStatusText = (status) => {
  const textMap = {
    'published': 'Active',
    'pending': 'Pending',
    'cancelled': 'Cancelled',
    'completed': 'Completed'
  }
  return textMap[status] || status
}

const getProgressColor = (rate) => {
  if (rate >= 80) return '#f56c6c'
  if (rate >= 60) return '#e6a23c'
  return '#67c23a'
}

// 生命周期
onMounted(() => {
  loadActivityDetail()
})
</script>

<style scoped>
/* 全局样式 */
.activity-detail-page {
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
  width: 1000px;
  height: 1000px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0px); }
  50% { transform: translate(-50%, -50%) translateY(-30px); }
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
}

/* 加载状态 */
.loading-container {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.loading-content {
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 60px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
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

/* 返回按钮 */
.back-section {
  position: relative;
  z-index: 1;
  padding: 20px 0;
}

.back-button {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  border-radius: 50px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.back-button:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

/* 活动头部 */
.activity-header {
  position: relative;
  margin-bottom: 40px;
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 30px;
  padding: 40px;
}

.header-left {
  flex: 1;
}

.activity-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.modern-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  font-weight: 600;
  border-radius: 50px;
  padding: 6px 16px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.activity-id {
  color: #94a3b8;
  font-size: 14px;
  font-weight: 500;
  background: rgba(148, 163, 184, 0.1);
  padding: 4px 12px;
  border-radius: 20px;
}

.activity-title {
  font-size: 42px;
  font-weight: 800;
  margin: 0 0 24px 0;
  color: #1e293b;
  line-height: 1.2;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.activity-basic-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
}

.info-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  font-size: 18px;
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.status-badges {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.modern-badge {
  padding: 8px 20px;
  border-radius: 50px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.badge-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-published {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.status-pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.status-cancelled {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.status-completed {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
}

.modern-fee {
  padding: 12px 24px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.modern-fee.free {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

/* 主要内容 */
.main-content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 40px;
  padding: 0 0 60px 0;
}

.content-left,
.content-right {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 现代化卡片样式 */
.modern-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

/* 图片区域 */
.image-section {
  overflow: hidden;
}

.modern-carousel :deep(.el-carousel__indicator) {
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 6px;
  width: 12px;
  height: 4px;
}

.modern-carousel :deep(.el-carousel__indicator.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  width: 24px;
}

.activity-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-image:hover {
  transform: scale(1.05);
}

/* 描述区域 */
.description-section {
  padding: 32px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 24px 0;
  color: #1e293b;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-subtitle {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #374151;
}

.activity-description {
  font-size: 16px;
  line-height: 1.8;
  color: #4b5563;
}

/* 标签区域 */
.tags-section {
  padding: 32px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.modern-tag-item {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border: 1px solid rgba(148, 163, 184, 0.2);
  color: #475569;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  padding: 6px 12px;
}

/* 联系方式 */
.contact-section {
  padding: 32px;
}

.contact-info {
  font-size: 16px;
  color: #4b5563;
  margin: 0;
  line-height: 1.6;
}

/* 参与者列表 */
.participants-section {
  padding: 32px;
}

.participants-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.participant-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  transition: all 0.3s ease;
}

.participant-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.participant-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.participant-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 15px;
}

.participant-time {
  font-size: 13px;
  color: #64748b;
}

.no-participants {
  text-align: center;
  padding: 40px 20px;
}

.no-participants-content {
  color: #94a3b8;
}

.no-participants-icon {
  font-size: 80px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.no-participants-text {
  font-size: 16px;
  margin: 0;
  font-weight: 500;
}

/* 右侧卡片 */
.registration-card,
.creator-card,
.stats-card {
  padding: 0;
}

.card-header {
  padding: 24px 24px 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  margin-bottom: 24px;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  padding-bottom: 16px;
}

.card-content {
  padding: 0 24px 24px;
}

/* 报名信息 */
.quota-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.quota-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 10px;
}

.quota-label {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.quota-value {
  color: #1e293b;
  font-weight: 700;
  font-size: 15px;
}

.progress-info {
  margin-bottom: 24px;
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
  display: block;
  text-align: center;
  margin-top: 12px;
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.time-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 10px;
}

.time-label {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.time-value {
  color: #1e293b;
  font-weight: 600;
  font-size: 14px;
}

.registration-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.registration-alert {
  border-radius: 12px;
  border: none;
}

.registration-time {
  font-size: 13px;
  color: #64748b;
  margin-top: 12px;
  text-align: center;
}

.action-button {
  border-radius: 50px;
  padding: 14px 28px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.cancel-button {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border: none;
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.3);
}

.cancel-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(239, 68, 68, 0.4);
}

/* 创建者信息 */
.creator-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.creator-details {
  flex: 1;
}

.creator-name {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.creator-meta {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 统计信息 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 20px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  color: white;
  font-size: 18px;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 6px;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 错误状态 */
.error-container {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40px 20px;
}

.error-content {
  text-align: center;
  padding: 60px 40px;
  max-width: 500px;
}

.error-icon {
  font-size: 100px;
  color: #f59e0b;
  margin-bottom: 24px;
}

.error-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #1e293b;
}

.error-text {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.retry-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50px;
  padding: 12px 28px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.retry-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

/* 对话框 */
.modern-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 24px 24px 16px;
}

.modern-dialog :deep(.el-dialog__title) {
  font-weight: 700;
  color: #1e293b;
}

.register-dialog-content p {
  margin: 0 0 24px 0;
  font-size: 16px;
  color: #4b5563;
  line-height: 1.6;
}

.modern-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.modern-input :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.dialog-button {
  border-radius: 50px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.dialog-button.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.dialog-button.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 24px;
    padding: 24px;
  }

  .header-right {
    align-items: flex-start;
    width: 100%;
  }

  .activity-title {
    font-size: 32px;
  }

  .activity-image {
    height: 250px;
  }

  .modern-card {
    padding: 0;
  }

  .description-section,
  .tags-section,
  .contact-section,
  .participants-section {
    padding: 24px;
  }

  .card-header {
    padding: 20px 20px 0;
  }

  .card-content {
    padding: 0 20px 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .error-actions {
    flex-direction: column;
    align-items: center;
  }
}

@media (max-width: 480px) {
  .activity-title {
    font-size: 28px;
  }

  .section-title {
    font-size: 24px;
  }

  .participant-item {
    padding: 12px;
  }

  .quota-item,
  .time-item {
    padding: 10px 12px;
  }

  .stat-item {
    padding: 16px 12px;
  }

  .stat-value {
    font-size: 24px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-button) {
  font-weight: 600;
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

:deep(.el-alert) {
  border-radius: 12px;
}
</style>