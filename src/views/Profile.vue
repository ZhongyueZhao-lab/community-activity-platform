<template>
  <div class="profile-container">
    <!-- 页面背景装饰 -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- 头部 -->
    <div class="header">
      <div class="header-background"></div>
      <div class="container">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-gradient">My Profile</span>
          </h1>
          <el-button @click="goBack" class="back-button">
            <el-icon><House /></el-icon>
            <span>Back to Home</span>
          </el-button>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="container">
        <el-tabs v-model="activeTab" type="card" class="profile-tabs modern-tabs">
          <!-- Personal Information Tab -->
          <el-tab-pane label="Personal Info" name="profile">
            <div class="profile-info modern-card">
              <div class="avatar-section">
                <div class="avatar-container">
                  <div class="avatar-wrapper">
                    <el-avatar :size="120" :src="userForm.avatar" class="user-avatar">
                      <el-icon><User /></el-icon>
                    </el-avatar>
                    <div class="avatar-badge">
                      <el-icon><Camera /></el-icon>
                    </div>
                  </div>
                  <div class="avatar-upload">
                    <ImageUpload
                        v-model="userForm.avatar"
                        :limit="1"
                        :show-file-list="false"
                        @success="handleAvatarSuccess"
                    >
                      <el-button size="small" type="primary" class="upload-button">
                        <el-icon><Upload /></el-icon>
                        <span>Change Avatar</span>
                      </el-button>
                    </ImageUpload>
                  </div>
                </div>
                <div class="user-basic-info">
                  <h3 class="user-name">{{ userStore.userInfo?.nickname }}</h3>
                  <p class="user-role">
                    <el-tag :type="userStore.userInfo?.role === 'admin' ? 'success' : 'primary'" class="role-tag">
                      {{ userStore.userInfo?.role === 'admin' ? 'Administrator' : 'Member' }}
                    </el-tag>
                  </p>
                  <p class="join-time">
                    <el-icon><Calendar /></el-icon>
                    <span>Joined {{ formatDate(userStore.userInfo?.createdAt) }}</span>
                  </p>
                </div>
              </div>

              <div class="section-divider"></div>

              <el-form
                  ref="profileFormRef"
                  :model="userForm"
                  :rules="profileRules"
                  label-width="120px"
                  class="profile-form modern-form"
              >
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="Username">
                      <el-input v-model="userForm.username" disabled class="modern-input" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="Nickname" prop="nickname">
                      <el-input v-model="userForm.nickname" placeholder="Enter your nickname" class="modern-input" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="Phone" prop="phone">
                      <el-input v-model="userForm.phone" placeholder="Enter your phone number" class="modern-input" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="Email" prop="email">
                      <el-input v-model="userForm.email" placeholder="Enter your email" class="modern-input" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="Gender">
                      <el-radio-group v-model="userForm.gender" class="modern-radio-group">
                        <el-radio :label="0" class="modern-radio">Not Set</el-radio>
                        <el-radio :label="1" class="modern-radio">Male</el-radio>
                        <el-radio :label="2" class="modern-radio">Female</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="Age">
                      <el-input-number v-model="userForm.age" :min="1" :max="120" placeholder="Enter your age" class="modern-input-number" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="Bio">
                  <el-input
                      v-model="userForm.bio"
                      type="textarea"
                      :rows="4"
                      placeholder="Tell us about yourself..."
                      maxlength="500"
                      show-word-limit
                      class="modern-textarea"
                  />
                </el-form-item>

                <el-form-item class="form-actions">
                  <el-button type="primary" @click="updateProfile" :loading="updating" class="action-button primary">
                    <el-icon><Check /></el-icon>
                    <span>Save Changes</span>
                  </el-button>
                  <el-button @click="resetForm" class="action-button secondary">
                    <el-icon><Refresh /></el-icon>
                    <span>Reset</span>
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>

          <!-- Change Password Tab -->
          <el-tab-pane label="Security" name="password">
            <div class="password-section modern-card">
              <div class="section-header">
                <div class="section-icon">
                  <el-icon><Lock /></el-icon>
                </div>
                <div class="section-info">
                  <h3 class="section-title">Change Password</h3>
                  <p class="section-subtitle">Update your password to keep your account secure</p>
                </div>
              </div>

              <el-form
                  ref="passwordFormRef"
                  :model="passwordForm"
                  :rules="passwordRules"
                  label-width="140px"
                  class="password-form modern-form"
              >
                <el-form-item label="Current Password" prop="currentPassword">
                  <el-input
                      v-model="passwordForm.currentPassword"
                      type="password"
                      placeholder="Enter current password"
                      show-password
                      class="modern-input"
                  />
                </el-form-item>

                <el-form-item label="New Password" prop="newPassword">
                  <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      placeholder="Enter new password"
                      show-password
                      class="modern-input"
                  />
                </el-form-item>

                <el-form-item label="Confirm Password" prop="confirmPassword">
                  <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      placeholder="Confirm new password"
                      show-password
                      class="modern-input"
                  />
                </el-form-item>

                <el-form-item class="form-actions">
                  <el-button type="primary" @click="changePassword" :loading="changingPassword" class="action-button primary">
                    <el-icon><Lock /></el-icon>
                    <span>Update Password</span>
                  </el-button>
                  <el-button @click="resetPasswordForm" class="action-button secondary">
                    <el-icon><Refresh /></el-icon>
                    <span>Reset</span>
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>

          <!-- My Registrations Tab -->
          <el-tab-pane label="My Events" name="registrations">
            <div class="registrations-section modern-card">
              <div class="registrations-header">
                <div class="section-info">
                  <h3 class="section-title">My Event Registrations</h3>
                  <p class="section-subtitle">Manage your event registrations and view history</p>
                </div>
                <div class="filter-section">
                  <el-radio-group v-model="registrationFilter" @change="handleFilterChange" class="modern-filter">
                    <el-radio-button label="">All</el-radio-button>
                    <el-radio-button label="registered">Registered</el-radio-button>
                    <el-radio-button label="attended">Attended</el-radio-button>
                    <el-radio-button label="cancelled">Cancelled</el-radio-button>
                  </el-radio-group>
                </div>
              </div>

              <div class="statistics-cards">
                <el-row :gutter="24">
                  <el-col :span="6">
                    <div class="stat-card modern-stat">
                      <div class="stat-icon total">
                        <el-icon><Calendar /></el-icon>
                      </div>
                      <div class="stat-content">
                        <div class="stat-number">{{ statistics.totalRegistrations }}</div>
                        <div class="stat-label">Total Registrations</div>
                      </div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-card modern-stat">
                      <div class="stat-icon attended">
                        <el-icon><Check /></el-icon>
                      </div>
                      <div class="stat-content">
                        <div class="stat-number">{{ statistics.attendedActivities }}</div>
                        <div class="stat-label">Attended</div>
                      </div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-card modern-stat">
                      <div class="stat-icon upcoming">
                        <el-icon><Clock /></el-icon>
                      </div>
                      <div class="stat-content">
                        <div class="stat-number">{{ statistics.upcomingActivities }}</div>
                        <div class="stat-label">Upcoming</div>
                      </div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-card modern-stat">
                      <div class="stat-icon cancelled">
                        <el-icon><Close /></el-icon>
                      </div>
                      <div class="stat-content">
                        <div class="stat-number">{{ statistics.cancelledRegistrations }}</div>
                        <div class="stat-label">Cancelled</div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="registrations-list" v-loading="loadingRegistrations">
                <div
                    v-for="registration in registrations"
                    :key="registration.registrationId"
                    class="registration-item modern-registration-card"
                >
                  <div class="activity-info">
                    <div class="activity-image">
                      <el-image
                          :src="registration.activityCoverImage"
                          fit="cover"
                          class="cover-image"
                      >
                        <template #error>
                          <div class="image-slot">
                            <el-icon><Picture /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </div>
                    <div class="activity-details">
                      <h4 class="activity-title">{{ registration.activityTitle }}</h4>
                      <div class="activity-meta">
                        <div class="meta-item">
                          <div class="meta-icon">
                            <el-icon><Clock /></el-icon>
                          </div>
                          <span>{{ formatDateTime(registration.activityStartTime) }} - {{ formatDateTime(registration.activityEndTime) }}</span>
                        </div>
                        <div class="meta-item" v-if="registration.activityLocation">
                          <div class="meta-icon">
                            <el-icon><Location /></el-icon>
                          </div>
                          <span>{{ registration.activityLocation }}</span>
                        </div>
                        <div class="meta-item" v-if="registration.categoryName">
                          <el-tag size="small" class="category-tag">{{ registration.categoryName }}</el-tag>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="registration-info">
                    <div class="registration-status">
                      <div class="status-badge modern-badge" :class="getStatusClass(registration.registrationStatus)">
                        <div class="badge-dot"></div>
                        {{ getStatusText(registration.registrationStatus) }}
                      </div>
                    </div>
                    <div class="registration-details">
                      <div class="registration-time">
                        <el-icon><Calendar /></el-icon>
                        <span>Registered {{ formatDateTime(registration.registrationTime) }}</span>
                      </div>
                      <div class="registration-notes" v-if="registration.registrationNotes">
                        <el-icon><Document /></el-icon>
                        <span>{{ registration.registrationNotes }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="activity-actions">
                    <el-button
                        type="primary"
                        size="small"
                        @click="viewActivity(registration.activityId)"
                        class="action-btn primary"
                    >
                      <el-icon><View /></el-icon>
                      <span>View Details</span>
                    </el-button>
                    <el-button
                        v-if="registration.canCancel"
                        type="danger"
                        size="small"
                        @click="cancelRegistration(registration)"
                        class="action-btn danger"
                    >
                      <el-icon><Close /></el-icon>
                      <span>Cancel</span>
                    </el-button>
                  </div>
                </div>

                <div v-if="registrations.length === 0" class="empty-state">
                  <div class="empty-content">
                    <div class="empty-icon">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <h3 class="empty-title">No Registrations Yet</h3>
                    <p class="empty-text">You haven't registered for any events yet. Start exploring and join exciting events!</p>
                    <el-button type="primary" @click="goToEvents" class="empty-action">
                      <el-icon><Plus /></el-icon>
                      <span>Browse Events</span>
                    </el-button>
                  </div>
                </div>
              </div>

              <div class="pagination-section" v-if="registrationTotal > 0">
                <div class="pagination-wrapper">
                  <!-- 配置英文分页器 -->
                  <el-config-provider :locale="enLocale">
                    <el-pagination
                        v-model:current-page="registrationPage"
                        v-model:page-size="registrationPageSize"
                        :total="registrationTotal"
                        :page-sizes="[10, 20, 50]"
                        layout="total, sizes, prev, pager, next, jumper"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        class="modern-pagination"
                    />
                  </el-config-provider>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox, ElConfigProvider } from 'element-plus'
import {
  User, Clock, Location, Picture, House, Camera, Upload, Calendar, Check, Refresh,
  Lock, Close, Plus, View, Document
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import ImageUpload from '@/components/ImageUpload.vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

// 英文语言配置
const enLocale = en

// 响应式数据
const activeTab = ref('profile')
const updating = ref(false)
const changingPassword = ref(false)
const loadingRegistrations = ref(false)

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 用户信息表单
const userForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  gender: 0,
  age: null,
  bio: ''
})

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 报名相关数据
const registrationFilter = ref('')
const registrationPage = ref(1)
const registrationPageSize = ref(10)
const registrationTotal = ref(0)
const registrations = ref([])

// 统计数据
const statistics = reactive({
  totalRegistrations: 0,
  attendedActivities: 0,
  upcomingActivities: 0,
  cancelledRegistrations: 0
})

// 表单验证规则
const profileRules = {
  nickname: [
    { required: true, message: 'Please enter your nickname', trigger: 'blur' },
    { max: 50, message: 'Nickname cannot exceed 50 characters', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: 'Invalid phone number format', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: 'Invalid email format', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: 'Please enter current password', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: 'Please enter new password', trigger: 'blur' },
    { min: 6, max: 100, message: 'Password must be 6-100 characters long', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm new password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('Passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 生命周期
onMounted(async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }

  await loadUserProfile()
  await loadUserStatistics()
  await loadUserRegistrations()
})

// 方法
const goBack = () => {
  router.push('/home')
}

const goToEvents = () => {
  router.push('/activities')
}

const loadUserProfile = async () => {
  try {
    const response = await request.get('/api/profile/info')
    const userInfo = response.data

    // 更新表单数据
    Object.assign(userForm, {
      username: userInfo.username,
      nickname: userInfo.nickname || '',
      phone: userInfo.phone || '',
      email: userInfo.email || '',
      avatar: userInfo.avatar || '',
      gender: userInfo.gender || 0,
      age: userInfo.age,
      bio: userInfo.bio || ''
    })

    // 更新store中的用户信息
    userStore.setUserInfo(userInfo)
  } catch (error) {
    console.error('Failed to load user profile:', error)
    ElMessage.error('Failed to load user profile')
  }
}

const loadUserStatistics = async () => {
  try {
    const response = await request.get('/api/profile/statistics')
    Object.assign(statistics, response.data)
  } catch (error) {
    console.error('Failed to load statistics:', error)
  }
}

const loadUserRegistrations = async () => {
  loadingRegistrations.value = true
  try {
    const response = await request.get('/api/profile/registrations', {
      params: {
        page: registrationPage.value,
        size: registrationPageSize.value,
        status: registrationFilter.value || undefined
      }
    })

    registrations.value = response.data.records || []
    registrationTotal.value = response.data.total || 0
  } catch (error) {
    console.error('Failed to load registrations:', error)
    ElMessage.error('Failed to load registrations')
  } finally {
    loadingRegistrations.value = false
  }
}

const updateProfile = async () => {
  if (!profileFormRef.value) return

  try {
    await profileFormRef.value.validate()
    updating.value = true

    const updateData = {
      nickname: userForm.nickname,
      phone: userForm.phone || null,
      email: userForm.email || null,
      avatar: userForm.avatar,
      gender: userForm.gender,
      age: userForm.age,
      bio: userForm.bio
    }

    const response = await request.put('/api/profile/update', updateData)
    ElMessage.success('Profile updated successfully')

    // 更新store中的用户信息
    userStore.updateUserInfo(response.data)
  } catch (error) {
    console.error('Failed to update profile:', error)
  } finally {
    updating.value = false
  }
}

const changePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true

    await request.put('/api/profile/change-password', passwordForm)
    ElMessage.success('Password changed successfully')
    resetPasswordForm()
  } catch (error) {
    console.error('Failed to change password:', error)
  } finally {
    changingPassword.value = false
  }
}

const handleAvatarSuccess = async (response) => {
  try {
    userForm.avatar = response.imageUrl
    await request.put('/api/profile/avatar', { avatarUrl: response.imageUrl })
    ElMessage.success('Avatar updated successfully')

    // 更新store中的用户头像
    userStore.updateAvatar(response.imageUrl)
  } catch (error) {
    console.error('Failed to update avatar:', error)
    ElMessage.error('Failed to update avatar')
  }
}

const resetForm = () => {
  loadUserProfile()
}

const resetPasswordForm = () => {
  Object.assign(passwordForm, {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  passwordFormRef.value?.clearValidate()
}

const handleFilterChange = () => {
  registrationPage.value = 1
  loadUserRegistrations()
}

const handleSizeChange = () => {
  registrationPage.value = 1
  loadUserRegistrations()
}

const handleCurrentChange = () => {
  loadUserRegistrations()
}

const viewActivity = (activityId) => {
  router.push(`/activities/${activityId}`)
}

const cancelRegistration = async (registration) => {
  try {
    await ElMessageBox.confirm(
        `Are you sure you want to cancel registration for "${registration.activityTitle}"?`,
        'Confirm Cancellation',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
    )

    await request.post(`/api/public/activities/${registration.activityId}/cancel`)
    ElMessage.success('Registration cancelled successfully')

    // 重新加载数据
    await loadUserRegistrations()
    await loadUserStatistics()
  } catch (error) {
    if (error === 'cancel') return
    console.error('Failed to cancel registration:', error)
  }
}

const getStatusClass = (status) => {
  const classMap = {
    registered: 'status-registered',
    attended: 'status-attended',
    cancelled: 'status-cancelled'
  }
  return classMap[status] || 'status-default'
}

const getStatusText = (status) => {
  const textMap = {
    registered: 'Registered',
    attended: 'Attended',
    cancelled: 'Cancelled'
  }
  return textMap[status] || 'Unknown'
}

const formatDate = (dateString) => {
  if (!dateString) return 'Unknown'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatDateTime = (dateString) => {
  if (!dateString) return 'Unknown'
  return new Date(dateString).toLocaleString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
/* 全局样式 */
.profile-container {
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

/* 头部样式 */
.header {
  position: relative;
  padding: 40px 0;
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
}

.page-title {
  font-size: 48px;
  font-weight: 900;
  margin: 0;
  line-height: 1.1;
}

.title-gradient {
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.95) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3)) drop-shadow(0 0 20px rgba(0, 0, 0, 0.2));
}

.back-button {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  border-radius: 50px;
  padding: 14px 32px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.back-button:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.35);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.25);
}

.content {
  position: relative;
  z-index: 1;
  padding: 40px 0 80px;
}

/* 现代化标签页 */
.modern-tabs :deep(.el-tabs__header) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px 20px 0 0;
  padding: 20px 20px 0;
  margin: 0;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.modern-tabs :deep(.el-tabs__nav) {
  border: none;
}

.modern-tabs :deep(.el-tabs__item) {
  border: none;
  border-radius: 12px;
  margin-right: 8px;
  padding: 12px 24px;
  font-weight: 600;
  color: #64748b;
  transition: all 0.3s ease;
  background: transparent;
}

.modern-tabs :deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.modern-tabs :deep(.el-tabs__item:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.modern-tabs :deep(.el-tabs__content) {
  padding: 0;
}

/* 现代化卡片 */
.modern-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 0 0 20px 20px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 40px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
  gap: 40px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border: 4px solid rgba(255, 255, 255, 0.8);
}

.avatar-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.upload-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50px;
  padding: 8px 20px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.upload-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.user-basic-info {
  flex: 1;
}

.user-name {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 16px 0;
  color: #1e293b;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.role-tag {
  font-weight: 600;
  border-radius: 50px;
  padding: 8px 16px;
  border: none;
  margin-bottom: 16px;
}

.join-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.section-divider {
  border: none;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(148, 163, 184, 0.3), transparent);
  margin: 40px 0;
}

/* 现代化表单 */
.modern-form {
  max-width: 800px;
}

.modern-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
}

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

.modern-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.modern-textarea :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modern-input-number :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.modern-radio-group {
  display: flex;
  gap: 16px;
}

.modern-radio :deep(.el-radio__label) {
  font-weight: 500;
}

.form-actions {
  margin-top: 32px;
}

.action-button {
  border-radius: 50px;
  padding: 12px 28px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-right: 16px;
}

.action-button.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.action-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.action-button.secondary {
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.3);
  color: #64748b;
}

.action-button.secondary:hover {
  background: rgba(148, 163, 184, 0.2);
  transform: translateY(-1px);
}

/* 密码区域 */
.section-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.section-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.section-info {
  flex: 1;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.section-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

/* 报名区域 */
.registrations-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  gap: 20px;
}

.modern-filter :deep(.el-radio-button__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modern-filter :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 统计卡片 */
.statistics-cards {
  margin-bottom: 40px;
}

.modern-stat {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.modern-stat:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.attended {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.upcoming {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon.cancelled {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 4px;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 报名列表 */
.modern-registration-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 24px;
  transition: all 0.3s ease;
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.modern-registration-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.activity-info {
  display: flex;
  flex: 1;
  align-items: center;
  gap: 20px;
}

.activity-image {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #94a3b8;
  font-size: 24px;
}

.activity-details {
  flex: 1;
}

.activity-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #1e293b;
  line-height: 1.3;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.meta-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
}

.category-tag {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  color: #475569;
  border: none;
  font-weight: 500;
  border-radius: 6px;
}

.registration-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  min-width: 180px;
}

.modern-badge {
  padding: 8px 16px;
  border-radius: 50px;
  font-size: 12px;
  font-weight: 600;
  color: white;
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

.status-registered {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.status-attended {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.status-cancelled {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.registration-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.registration-time,
.registration-notes {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}

.activity-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  font-size: 12px;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.action-btn.danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border: none;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 40px;
}

.empty-content {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 100px;
  color: #cbd5e1;
  margin-bottom: 24px;
}

.empty-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #475569;
}

.empty-text {
  font-size: 16px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 32px 0;
}

.empty-action {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50px;
  padding: 12px 28px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.empty-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

/* 分页 */
.pagination-section {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.pagination-wrapper {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 50px;
  padding: 16px 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.modern-pagination :deep(.el-pager li) {
  border-radius: 8px;
  min-width: 36px;
  height: 36px;
  margin: 0 2px;
  transition: all 0.3s ease;
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

/* 响应式设计 */
@media (max-width: 1024px) {
  .registrations-header {
    flex-direction: column;
    gap: 16px;
  }

  .statistics-cards :deep(.el-col) {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 24px;
  }

  .page-title {
    font-size: 36px;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 24px;
  }

  .modern-registration-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .activity-info {
    width: 100%;
  }

  .registration-info {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    min-width: auto;
  }

  .activity-actions {
    width: 100%;
    flex-direction: row;
  }

  .statistics-cards :deep(.el-row) {
    flex-direction: column;
  }

  .statistics-cards :deep(.el-col) {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 28px;
  }

  .modern-card {
    padding: 24px;
  }

  .activity-info {
    flex-direction: column;
    align-items: flex-start;
  }

  .registration-info {
    flex-direction: column;
    align-items: flex-start;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-button) {
  font-weight: 600;
}

:deep(.el-tag) {
  border-radius: 8px;
}

:deep(.el-avatar) {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
}
</style>