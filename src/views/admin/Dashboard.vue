<template>
  <div class="dashboard-container">
    <!-- Page Background -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <span class="title-gradient">Dashboard Overview</span>
          </h1>
          <p class="page-subtitle">Welcome back! Here's what's happening with your platform.</p>
        </div>
        <div class="header-actions">
          <el-button @click="refreshStats" class="refresh-button" size="large">
            <el-icon class="refresh-icon"><Refresh /></el-icon>
            <span>Refresh Data</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card modern-stat">
          <div class="stat-background"></div>
          <div class="stat-icon total-users">
            <el-icon size="28"><User /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-number">{{ stats.totalUsers }}</h3>
            <p class="stat-label">Total Users</p>
            <div class="stat-trend">
              <el-icon class="trend-icon"><TrendCharts /></el-icon>
              <span class="trend-text">Active Community</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>

        <div class="stat-card modern-stat">
          <div class="stat-background"></div>
          <div class="stat-icon total-activities">
            <el-icon size="28"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-number">{{ stats.totalActivities }}</h3>
            <p class="stat-label">Total Events</p>
            <div class="stat-trend">
              <el-icon class="trend-icon"><TrendCharts /></el-icon>
              <span class="trend-text">Growing Collection</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>

        <div class="stat-card modern-stat">
          <div class="stat-background"></div>
          <div class="stat-icon pending-activities">
            <el-icon size="28"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-number">{{ stats.pendingActivities }}</h3>
            <p class="stat-label">Pending Review</p>
            <div class="stat-trend">
              <el-icon class="trend-icon"><Warning /></el-icon>
              <span class="trend-text">Need Attention</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>

        <div class="stat-card modern-stat">
          <div class="stat-background"></div>
          <div class="stat-icon published-activities">
            <el-icon size="28"><Check /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-number">{{ stats.publishedActivities }}</h3>
            <p class="stat-label">Published Events</p>
            <div class="stat-trend">
              <el-icon class="trend-icon"><SuccessFilled /></el-icon>
              <span class="trend-text">Live & Active</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
      </div>
    </div>

    <!-- Growth Statistics -->
    <div class="growth-section">
      <div class="section-header">
        <h2 class="section-title">Monthly Growth</h2>
        <p class="section-subtitle">Track your platform's growth metrics</p>
      </div>
      <div class="growth-grid">
        <div class="growth-card modern-card">
          <div class="card-background"></div>
          <div class="card-header">
            <div class="card-icon new-users">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="card-title">New Users This Month</div>
          </div>
          <div class="card-content">
            <div class="growth-number">{{ stats.newUsersThisMonth }}</div>
            <div class="growth-indicator positive">
              <el-icon><Top /></el-icon>
              <span>vs last month</span>
            </div>
          </div>
          <div class="card-decoration"></div>
        </div>

        <div class="growth-card modern-card">
          <div class="card-background"></div>
          <div class="card-header">
            <div class="card-icon new-events">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="card-title">New Events This Month</div>
          </div>
          <div class="card-content">
            <div class="growth-number">{{ stats.newActivitiesThisMonth }}</div>
            <div class="growth-indicator positive">
              <el-icon><Top /></el-icon>
              <span>vs last month</span>
            </div>
          </div>
          <div class="card-decoration"></div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="actions-section">
      <div class="section-header">
        <h2 class="section-title">Quick Actions</h2>
        <p class="section-subtitle">Manage your platform efficiently</p>
      </div>
      <div class="actions-grid">
        <div class="action-card modern-action" @click="goToActivities">
          <div class="action-background"></div>
          <div class="action-icon events">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="action-content">
            <h3 class="action-title">Event Management</h3>
            <p class="action-description">Create, edit and manage events</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="action-card modern-action" @click="goToUsers">
          <div class="action-background"></div>
          <div class="action-icon users">
            <el-icon><User /></el-icon>
          </div>
          <div class="action-content">
            <h3 class="action-title">User Management</h3>
            <p class="action-description">Manage user accounts and permissions</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="action-card modern-action" @click="goToSystem">
          <div class="action-background"></div>
          <div class="action-icon system">
            <el-icon><Setting /></el-icon>
          </div>
          <div class="action-content">
            <h3 class="action-title">System Configuration</h3>
            <p class="action-description">Configure platform settings</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="action-card modern-action" @click="refreshStats">
          <div class="action-background"></div>
          <div class="action-icon refresh">
            <el-icon><Refresh /></el-icon>
          </div>
          <div class="action-content">
            <h3 class="action-title">Data Refresh</h3>
            <p class="action-description">Update dashboard statistics</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activities -->
    <div class="recent-section">
      <div class="section-header">
        <h2 class="section-title">Recent Events</h2>
        <el-button type="primary" class="view-all-button" @click="goToActivities">
          <span>View All Events</span>
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="recent-card modern-card">
        <div class="card-background"></div>
        <div class="table-container">
          <el-table
              :data="recentActivities"
              v-loading="activitiesLoading"
              class="modern-table"
              :border="false"
              :show-header="true"
              empty-text="No recent events found"
          >
            <el-table-column prop="title" label="Event Title" width="220">
              <template #default="scope">
                <div class="table-title">
                  <div class="title-text">{{ scope.row.title }}</div>
                  <div class="title-meta">{{ scope.row.categoryName || 'General' }}</div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Status" width="220">
              <template #default="scope">
                <div class="status-wrapper">
                  <el-tag :type="getStatusType(scope.row.status)" class="modern-status-tag">
                    <div class="status-dot"></div>
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Participants" width="180">
              <template #default="scope">
                <div class="participants-info">
                  <el-icon class="participants-icon"><User /></el-icon>
                  <span>{{ scope.row.currentParticipants }}/{{ scope.row.maxParticipants || '∞' }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Created" width="220">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Clock /></el-icon>
                  <span>{{ formatDateTime(scope.row.createdAt) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Actions" width="180">
              <template #default="scope">
                <div class="table-actions">
                  <el-button
                      type="primary"
                      size="small"
                      class="action-btn"
                      @click="viewEvent(scope.row.id)"
                  >
                    <el-icon><View /></el-icon>
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Calendar, Clock, Check, Plus, UserFilled, Setting, Refresh,
  ArrowRight, Top, TrendCharts, Warning, SuccessFilled, View
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()

// Reactive data
const stats = ref({
  totalUsers: 0,
  totalActivities: 0,
  pendingActivities: 0,
  publishedActivities: 0,
  newUsersThisMonth: 0,
  newActivitiesThisMonth: 0
})

const recentActivities = ref([])
const loading = ref(false)
const activitiesLoading = ref(false)

// Methods
const loadStats = async () => {
  try {
    loading.value = true
    const response = await request.get('/api/admin/dashboard/stats')
    if (response.code === 200) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
    ElMessage.error('Failed to fetch statistics')
  } finally {
    loading.value = false
  }
}

const loadRecentActivities = async () => {
  try {
    activitiesLoading.value = true
    const response = await request.get('/api/admin/activities', {
      params: { page: 1, size: 5 }
    })
    if (response.code === 200) {
      recentActivities.value = response.data.records
    }
  } catch (error) {
    console.error('Failed to fetch recent events:', error)
  } finally {
    activitiesLoading.value = false
  }
}

const refreshStats = () => {
  loadStats()
  loadRecentActivities()
  ElMessage.success('Data refreshed successfully')
}

const goToActivities = () => {
  router.push('/admin/activities')
}

const goToUsers = () => {
  router.push('/admin/users')
}

const goToSystem = () => {
  router.push('/admin/system')
}

const viewEvent = (eventId) => {
  router.push(`/activities/${eventId}`)
}

const getStatusType = (status) => {
  const typeMap = {
    draft: 'info',
    pending: 'warning',
    published: 'success',
    cancelled: 'danger',
    completed: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    draft: 'Draft',
    pending: 'Pending',
    published: 'Active',
    cancelled: 'Cancelled',
    completed: 'Completed'
  }
  return textMap[status] || status
}

const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('MMM DD, HH:mm') : '-'
}

// Lifecycle
onMounted(() => {
  loadStats()
  loadRecentActivities()
})
</script>

<style scoped>
/* Global Styles */
.dashboard-container {
  position: relative;
  min-height: 100vh;
  padding: 0;
}

/* 确保所有元素都是透明背景，移除多余的白色背景 */
.dashboard-container,
.dashboard-container .page-header,
.dashboard-container .header-content,
.dashboard-container .header-left,
.dashboard-container .page-title,
.dashboard-container .page-subtitle,
.dashboard-container .header-actions,
.dashboard-container .stats-section,
.dashboard-container .growth-section,
.dashboard-container .actions-section,
.dashboard-container .recent-section {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
}

.page-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
}

.background-decoration {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1200px;
  height: 1200px;
  background: radial-gradient(circle, rgba(5, 5, 5, 0.1) 0%, transparent 70%);
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
      rgba(0, 0, 0, 0.02) 30%,
      rgba(0, 0, 0, 0.05) 100%
  );
}

/* Page Header */
.page-header {
  position: relative;
  z-index: 1;
  margin-bottom: 40px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 0 0 20px 0;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 48px;
  font-weight: 900;
  margin: 0 0 12px 0;
  line-height: 1.1;
}

.title-gradient {
  background: linear-gradient(135deg, #ffffff 0%, #ffffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 18px;
  color: #ffffff;
  margin: 0;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.refresh-button {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.refresh-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.refresh-icon {
  animation: none;
  transition: transform 0.3s ease;
}

.refresh-button:hover .refresh-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Statistics Section */
.stats-section {
  position: relative;
  z-index: 1;
  margin-bottom: 60px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.modern-stat {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px 28px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.stat-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.05) 0%, rgba(0, 0, 0, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-stat:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.12);
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
  color: white;
  font-size: 28px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.total-users {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.total-activities {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.pending-activities {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.published-activities {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.modern-stat:hover .stat-icon {
  transform: scale(1.1) rotate(-5deg);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.stat-content {
  position: relative;
  flex: 1;
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 4px 0;
  color: #1e293b;
  line-height: 1;
}

.stat-label {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #10b981;
  font-size: 13px;
  font-weight: 500;
}

.trend-icon {
  font-size: 14px;
}

.stat-decoration {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.1) 100%);
  opacity: 0;
  transition: all 0.3s ease;
}

.modern-stat:hover .stat-decoration {
  opacity: 1;
  transform: scale(1.2);
}

/* Growth Section */
.growth-section {
  position: relative;
  z-index: 1;
  margin-bottom: 60px;
}

.section-header {
  margin-bottom: 32px;
}

.section-title {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.section-subtitle {
  font-size: 16px;
  color: #000000;
  margin: 0;
  font-weight: 400;
}

.growth-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.modern-card {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.05) 0%, rgba(0, 0, 0, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.12);
}

.modern-card:hover .card-background {
  opacity: 1;
}

.card-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.new-users {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.new-events {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.card-content {
  position: relative;
  text-align: center;
}

.growth-number {
  font-size: 42px;
  font-weight: 800;
  color: #667eea;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.growth-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.growth-indicator.positive {
  color: #10b981;
}

.card-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  opacity: 0;
  transition: all 0.3s ease;
}

.modern-card:hover .card-decoration {
  opacity: 1;
  transform: scale(1.2);
}

/* Actions Section */
.actions-section {
  position: relative;
  z-index: 1;
  margin-bottom: 60px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.modern-action {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 28px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.2);
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-action:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.12);
}

.modern-action:hover .action-background {
  opacity: 1;
}

.action-icon {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-icon.events {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.action-icon.users {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.action-icon.system {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
}

.action-icon.refresh {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.modern-action:hover .action-icon {
  transform: scale(1.1) rotate(-5deg);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.action-content {
  position: relative;
  flex: 1;
}

.action-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 6px 0;
  color: #1e293b;
}

.action-description {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.4;
}

.action-arrow {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-action:hover .action-arrow {
  opacity: 1;
  transform: translateX(4px);
}

/* Recent Section */
.recent-section {
  position: relative;
  z-index: 1;
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.view-all-button {
  border-radius: 12px;
  padding: 10px 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.view-all-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.recent-card {
  padding: 0;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.2);
}

.table-container {
  position: relative;
}

/* Modern Table Styles */
.modern-table {
  border-radius: 0;
}

.modern-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.modern-table :deep(.el-table__header) {
  background: transparent;
}

.modern-table :deep(.el-table__header th) {
  background: transparent;
  border: none;
  padding: 20px 16px;
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.modern-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
}

.modern-table :deep(.el-table__body tr:hover) {
  background: rgba(0, 0, 0, 0.05);
}

.modern-table :deep(.el-table__body td) {
  border: none;
  padding: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.table-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-text {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.title-meta {
  font-size: 12px;
  color: #64748b;
}

.status-wrapper {
  display: flex;
  align-items: center;
}

.modern-status-tag {
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.participants-info, .date-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
}

.participants-icon, .date-icon {
  font-size: 16px;
  color: #94a3b8;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  border-radius: 8px;
  padding: 8px;
  min-width: auto;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Responsive Design */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .growth-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }

  .page-title {
    font-size: 36px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .actions-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .modern-stat {
    padding: 24px 20px;
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    font-size: 24px;
  }

  .stat-number {
    font-size: 28px;
  }

  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .section-title {
    font-size: 24px;
  }

  .modern-action {
    padding: 20px;
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .action-content {
    text-align: center;
  }

  .modern-table :deep(.el-table__header th),
  .modern-table :deep(.el-table__body td) {
    padding: 12px 8px;
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    padding: 0;
  }

  .page-title {
    font-size: 28px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .stats-grid, .growth-grid, .actions-grid {
    gap: 12px;
  }

  .modern-stat, .modern-card, .modern-action {
    padding: 16px;
  }

  .section-title {
    font-size: 20px;
  }

  .growth-number {
    font-size: 32px;
  }
}

/* Loading States */
.loading-skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Enhanced Animations */
.dashboard-container {
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modern-stat, .modern-card, .modern-action {
  animation: slideUp 0.6s ease-out;
  animation-fill-mode: both;
}

.modern-stat:nth-child(1) { animation-delay: 0.1s; }
.modern-stat:nth-child(2) { animation-delay: 0.2s; }
.modern-stat:nth-child(3) { animation-delay: 0.3s; }
.modern-stat:nth-child(4) { animation-delay: 0.4s; }

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>