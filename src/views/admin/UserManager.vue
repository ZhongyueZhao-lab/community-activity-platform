<template>
  <div class="user-manager">
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
            <span class="title-gradient">User Management</span>
          </h1>
          <p class="page-subtitle">Manage and oversee user accounts</p>
        </div>
      </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <div class="filter-background"></div>
      <div class="filter-content">
        <div class="filter-header">
          <h3 class="filter-title">Filter Users</h3>
          <p class="filter-subtitle">Search and filter user accounts</p>
        </div>

        <el-form :model="filterForm" :inline="true" class="filter-form">
          <div class="form-row">
            <el-form-item label="Keyword" class="form-item">
              <el-input
                  v-model="filterForm.keyword"
                  placeholder="Search by username or nickname..."
                  clearable
                  class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="Role" class="form-item">
              <el-select v-model="filterForm.role" placeholder="Select role..." clearable class="modern-select">
                <el-option label="User" value="user" />
                <el-option label="Admin" value="admin" />
              </el-select>
            </el-form-item>

            <el-form-item label="Status" class="form-item">
              <el-select v-model="filterForm.status" placeholder="Select status..." clearable class="modern-select">
                <el-option label="Active" :value="1" />
                <el-option label="Frozen" :value="0" />
              </el-select>
            </el-form-item>

            <el-form-item class="form-actions">
              <el-button type="primary" @click="loadUsers" class="search-button">
                <el-icon><Search /></el-icon>
                <span>Search</span>
              </el-button>
              <el-button @click="resetFilter" class="reset-button">
                <el-icon><Refresh /></el-icon>
                <span>Reset</span>
              </el-button>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>

    <!-- User Table Section -->
    <div class="table-section">
      <div class="table-background"></div>
      <div class="table-content">
        <div class="table-header">
          <div class="results-info">
            <h3 class="results-title">User List</h3>
            <p class="results-subtitle">{{ pagination.total }} users found</p>
          </div>
        </div>

        <div class="table-container">
          <el-table
              :data="users"
              v-loading="loading"
              class="modern-table"
              :border="false"
              :stripe="true"
              empty-text="No users found"
          >
            <el-table-column prop="id" label="ID" width="80" />

            <el-table-column prop="username" label="Username" width="100" show-overflow-tooltip>
              <template #default="scope">
                <div class="user-info">
                  <div class="username">{{ scope.row.username }}</div>
                  <div class="nickname">{{ scope.row.nickname }}</div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="phone" label="Phone" width="100" class-name="hidden-sm" show-overflow-tooltip>
              <template #default="scope">
                <div class="contact-info">
                  <el-icon class="contact-icon"><Phone /></el-icon>
                  <span>{{ scope.row.phone || '-' }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="email" label="Email" width="140" class-name="hidden-md" show-overflow-tooltip>
              <template #default="scope">
                <div class="contact-info">
                  <el-icon class="contact-icon"><Message /></el-icon>
                  <span>{{ scope.row.email || '-' }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Role" width="100">
              <template #default="scope">
                <div class="role-wrapper">
                  <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'primary'" class="modern-tag">
                    <div class="tag-dot"></div>
                    {{ scope.row.role === 'admin' ? 'Admin' : 'User' }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Status" width="100">
              <template #default="scope">
                <div class="status-wrapper">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="modern-status-tag">
                    <div class="status-dot"></div>
                    {{ scope.row.status === 1 ? 'Active' : 'Frozen' }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Last Login" width="120" class-name="hidden-xs">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Clock /></el-icon>
                  <span>{{ formatDateTime(scope.row.lastLoginTime) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Registered" width="120" class-name="hidden-lg">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Calendar /></el-icon>
                  <span>{{ formatDateTime(scope.row.createdAt) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Actions" width="120" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button size="small" type="primary" @click="viewUser(scope.row)" class="action-btn">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-dropdown @command="(command) => handleCommand(command, scope.row)" size="small">
                    <el-button size="small" class="action-btn more-btn">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu class="modern-dropdown">
                        <el-dropdown-item :command="scope.row.status === 1 ? 'freeze' : 'unfreeze'">
                          <el-icon>
                            <CircleClose v-if="scope.row.status === 1" />
                            <CircleCheck v-else />
                          </el-icon>
                          {{ scope.row.status === 1 ? 'Freeze Account' : 'Unfreeze Account' }}
                        </el-dropdown-item>
                        <el-dropdown-item command="changeRole" v-if="scope.row.role !== 'admin'">
                          <el-icon><UserFilled /></el-icon>
                          Set as Admin
                        </el-dropdown-item>
                        <el-dropdown-item command="resetPassword">
                          <el-icon><Key /></el-icon>
                          Reset Password
                        </el-dropdown-item>
                        <el-dropdown-item command="delete" divided v-if="scope.row.role !== 'admin'" class="danger-item">
                          <el-icon><Delete /></el-icon>
                          Delete User
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- Pagination -->
        <div class="pagination-section">
          <div class="pagination-wrapper">
            <el-config-provider :locale="enLocale">
              <el-pagination
                  v-model:current-page="pagination.page"
                  v-model:page-size="pagination.size"
                  :page-sizes="[10, 20, 50, 100]"
                  :total="pagination.total"
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

    <!-- User Detail Dialog -->
    <el-dialog
        v-model="dialogVisible"
        title="User Details"
        width="70%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleDialogClose"
        @open="handleDialogOpen"
        @opened="handleDialogOpened"
        @close="handleDialogClosed"
    >
      <div v-if="currentUser" class="dialog-content">
        <div class="user-detail">
          <el-descriptions :column="2" border size="large" class="modern-descriptions">
            <el-descriptions-item label="User ID">{{ currentUser.id }}</el-descriptions-item>
            <el-descriptions-item label="Username">{{ currentUser.username }}</el-descriptions-item>
            <el-descriptions-item label="Nickname">{{ currentUser.nickname }}</el-descriptions-item>
            <el-descriptions-item label="Phone">{{ currentUser.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="Email">{{ currentUser.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="Gender">
              {{ currentUser.gender === 1 ? 'Male' : currentUser.gender === 2 ? 'Female' : 'Not Set' }}
            </el-descriptions-item>
            <el-descriptions-item label="Age">{{ currentUser.age || '-' }}</el-descriptions-item>
            <el-descriptions-item label="Role">
              <el-tag :type="currentUser.role === 'admin' ? 'danger' : 'primary'" class="detail-tag">
                {{ currentUser.role === 'admin' ? 'Administrator' : 'Regular User' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Status">
              <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" class="detail-tag">
                {{ currentUser.status === 1 ? 'Active' : 'Frozen' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Phone Verified">
              <el-tag :type="currentUser.phoneVerified === 1 ? 'success' : 'info'" class="detail-tag">
                {{ currentUser.phoneVerified === 1 ? 'Verified' : 'Not Verified' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Email Verified">
              <el-tag :type="currentUser.emailVerified === 1 ? 'success' : 'info'" class="detail-tag">
                {{ currentUser.emailVerified === 1 ? 'Verified' : 'Not Verified' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Last Login IP">{{ currentUser.lastLoginIp || '-' }}</el-descriptions-item>
            <el-descriptions-item label="Last Login Time">
              {{ formatDateTime(currentUser.lastLoginTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="Registration Time">
              {{ formatDateTime(currentUser.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="Bio" :span="2">
              {{ currentUser.bio || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-button">Close</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Reset Password Dialog -->
    <el-dialog
        v-model="passwordDialogVisible"
        title="Reset Password"
        width="450px"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog password-dialog"
        :before-close="handlePasswordDialogClose"
        @open="handlePasswordDialogOpen"
        @opened="handlePasswordDialogOpened"
        @close="handlePasswordDialogClosed"
    >
      <div class="dialog-content">
        <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" class="modern-form">
          <el-form-item label="New Password" prop="newPassword">
            <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="Enter new password..."
                show-password
                class="modern-input"
            />
          </el-form-item>
          <el-form-item label="Confirm Password" prop="confirmPassword">
            <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="Confirm new password..."
                show-password
                class="modern-input"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button type="primary" @click="resetPassword" :loading="passwordLoading" class="save-button">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElConfigProvider } from 'element-plus'
import {
  Search, Refresh, Phone, Message, Clock, Calendar, View, MoreFilled,
  CircleClose, CircleCheck, UserFilled, Key, Delete
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import request from '@/utils/request'
import dayjs from 'dayjs'

// English locale configuration
const enLocale = en

// Responsive data
const loading = ref(false)
const dialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const passwordLoading = ref(false)

const users = ref([])
const currentUser = ref(null)

const filterForm = reactive({
  keyword: '',
  role: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const passwordForm = reactive({
  userId: null,
  newPassword: '',
  confirmPassword: ''
})

const passwordFormRef = ref()

// Store original body overflow for dialogs
let originalBodyOverflow = ''
let originalHtmlOverflow = ''

// Computed property for responsive pagination layout
const paginationLayout = computed(() => {
  if (window.innerWidth < 768) {
    return 'prev, pager, next'
  } else if (window.innerWidth < 1024) {
    return 'total, prev, pager, next'
  } else {
    return 'total, sizes, prev, pager, next, jumper'
  }
})

// Password form validation rules
const passwordRules = {
  newPassword: [
    { required: true, message: 'Please enter new password', trigger: 'blur' },
    { min: 6, max: 100, message: 'Password length must be between 6-100 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('The two passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// Methods
const loadUsers = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...filterForm
    }

    // Filter empty values
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key]
      }
    })

    const response = await request.get('/api/admin/users', { params })
    if (response.code === 200) {
      users.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    console.error('Failed to fetch users:', error)
    ElMessage.error('Failed to fetch users')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.keyword = ''
  filterForm.role = ''
  filterForm.status = null
  pagination.page = 1
  loadUsers()
}

const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadUsers()
}

const handleCurrentChange = (newPage) => {
  pagination.page = newPage
  loadUsers()
}

const viewUser = (user) => {
  currentUser.value = user
  dialogVisible.value = true
}

const handleCommand = async (command, user) => {
  switch (command) {
    case 'freeze':
      await updateUserStatus(user.id, 0)
      break
    case 'unfreeze':
      await updateUserStatus(user.id, 1)
      break
    case 'changeRole':
      await updateUserRole(user.id, 'admin')
      break
    case 'resetPassword':
      showResetPasswordDialog(user)
      break
    case 'delete':
      await deleteUser(user)
      break
  }
}

const updateUserStatus = async (userId, status) => {
  try {
    const statusText = status === 1 ? 'unfreeze' : 'freeze'
    await ElMessageBox.confirm(`Are you sure to ${statusText} this user?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.put(`/api/admin/users/${userId}/status`, null, {
      params: { status }
    })

    if (response.code === 200) {
      ElMessage.success(`User ${statusText}d successfully`)
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update user status:', error)
      ElMessage.error('Failed to update user status')
    }
  }
}

const updateUserRole = async (userId, role) => {
  try {
    await ElMessageBox.confirm('Are you sure to set this user as administrator?', 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.put(`/api/admin/users/${userId}/role`, null, {
      params: { role }
    })

    if (response.code === 200) {
      ElMessage.success('User role updated successfully')
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update user role:', error)
      ElMessage.error('Failed to update user role')
    }
  }
}

const showResetPasswordDialog = (user) => {
  passwordForm.userId = user.id
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const resetPassword = async () => {
  if (!passwordFormRef.value) return

  try {
    const valid = await passwordFormRef.value.validate()
    if (!valid) return

    passwordLoading.value = true

    const response = await request.put(`/api/admin/users/${passwordForm.userId}/password`, null, {
      params: { newPassword: passwordForm.newPassword }
    })

    if (response.code === 200) {
      ElMessage.success('Password reset successfully')
      passwordDialogVisible.value = false
    }
  } catch (error) {
    console.error('Failed to reset password:', error)
    ElMessage.error('Failed to reset password')
  } finally {
    passwordLoading.value = false
  }
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(`Are you sure to delete user "${user.username}"? This action cannot be undone!`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/users/${user.id}`)

    if (response.code === 200) {
      ElMessage.success('User deleted successfully')
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete user:', error)
      ElMessage.error('Failed to delete user')
    }
  }
}

// Dialog scroll control methods
const handleDialogOpen = () => {
  originalBodyOverflow = document.body.style.overflow || ''
  originalHtmlOverflow = document.documentElement.style.overflow || ''

  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
  })
}

const handleDialogOpened = () => {
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

const handleDialogClose = (done) => {
  done()
}

const handleDialogClosed = () => {
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

// Password dialog scroll control methods
const handlePasswordDialogOpen = () => {
  originalBodyOverflow = document.body.style.overflow || ''
  originalHtmlOverflow = document.documentElement.style.overflow || ''

  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
  })
}

const handlePasswordDialogOpened = () => {
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

const handlePasswordDialogClose = (done) => {
  if (passwordLoading.value) {
    ElMessage.warning('Please wait for the operation to complete')
    return
  }
  done()
}

const handlePasswordDialogClosed = () => {
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('MM-DD HH:mm') : '-'
}

// Lifecycle
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
/* Global Styles */
.user-manager {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
  padding-bottom: 40px;
}

/* Ensure all elements have transparent background */
.user-manager,
.user-manager .page-header,
.user-manager .header-content,
.user-manager .header-left,
.user-manager .page-title,
.user-manager .page-subtitle,
.user-manager .filter-section,
.user-manager .table-section {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
}

.page-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  min-height: 100vh;
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

/* Page Header */
.page-header {
  position: relative;
  z-index: 1;
  padding: 40px 24px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  max-width: 1400px;
  margin: 0 auto;
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
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.95) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3)) drop-shadow(0 0 20px rgba(0, 0, 0, 0.2));
}

.page-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 400;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

/* Filter Section */
.filter-section {
  position: relative;
  margin: 0 24px 24px;
  z-index: 1;
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
  margin-bottom: 24px;
}

.filter-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.filter-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.filter-form {
  width: 100%;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: end;
}

.form-item {
  flex: 1;
  min-width: 200px;
  margin-bottom: 0 !important;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 0 !important;
}

/* Form Controls */
.modern-input :deep(.el-input__wrapper),
.modern-select :deep(.el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-input :deep(.el-input__wrapper:hover),
.modern-select :deep(.el-select__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-input :deep(.el-input__wrapper.is-focus),
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
  padding: 0 20px;
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
  padding: 0 20px;
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

/* Table Section */
.table-section {
  position: relative;
  margin: 0 24px 24px;
  z-index: 1;
}

.table-background {
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

.table-content {
  position: relative;
  padding: 32px;
}

.table-header {
  margin-bottom: 24px;
}

.results-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.results-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.table-container {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
}

/* Modern Table Styles */
.modern-table {
  border-radius: 0;
}

.modern-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.modern-table :deep(.el-table__header th) {
  background: transparent;
  border: none;
  padding: 16px 12px;
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.modern-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
}

.modern-table :deep(.el-table__body tr:hover) {
  background: rgba(102, 126, 234, 0.05);
}

.modern-table :deep(.el-table__body td) {
  border: none;
  padding: 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.nickname {
  font-size: 12px;
  color: #64748b;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 13px;
}

.contact-icon {
  font-size: 14px;
  color: #94a3b8;
}

.role-wrapper, .status-wrapper {
  display: flex;
  align-items: center;
}

.modern-tag, .modern-status-tag {
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
}

.tag-dot, .status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 13px;
}

.date-icon {
  font-size: 14px;
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
  transition: all 0.3s ease;
}

.more-btn {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.more-btn:hover {
  background: #e2e8f0;
  color: #475569;
}

/* Dropdown Styles */
.modern-dropdown {
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
  border: 1px solid #e2e8f0;
}

.modern-dropdown :deep(.el-dropdown-menu__item) {
  border-radius: 8px;
  padding: 8px 12px;
  margin: 2px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.modern-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.modern-dropdown :deep(.danger-item:hover) {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

/* Pagination */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
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

/* Dialog Styles */
.modern-dialog {
  z-index: 2001;
}

.modern-dialog :deep(.el-overlay) {
  z-index: 2001;
  overflow: auto !important;
  padding: 20px;
  box-sizing: border-box;
}

.modern-dialog :deep(.el-overlay-dialog) {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-height: 100%;
  overflow: auto !important;
  padding: 20px 0;
}

.modern-dialog :deep(.el-dialog) {
  margin: 0 !important;
  max-height: none !important;
  height: auto !important;
  max-width: calc(100vw - 40px);
  width: 70%;
  min-width: 600px;
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.password-dialog :deep(.el-dialog) {
  width: 450px;
  min-width: 400px;
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 24px 32px;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.modern-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.modern-dialog :deep(.el-dialog__body) {
  flex: 1;
  overflow: hidden;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
}

.modern-dialog :deep(.el-dialog__footer) {
  flex-shrink: 0;
}

.dialog-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 32px;
  max-height: 60vh;
  min-height: 200px;
}

.user-detail {
  width: 100%;
}

.modern-descriptions :deep(.el-descriptions__header) {
  margin-bottom: 16px;
}

.modern-descriptions :deep(.el-descriptions__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.modern-descriptions :deep(.el-descriptions__body) {
  background: #f8fafc;
  border-radius: 12px;
}

.modern-descriptions :deep(.el-descriptions__table) {
  border-radius: 12px;
}

.modern-descriptions :deep(.el-descriptions__cell) {
  border-color: #e2e8f0;
}

.modern-descriptions :deep(.el-descriptions__label) {
  background: #f1f5f9;
  font-weight: 600;
  color: #475569;
}

.detail-tag {
  border-radius: 6px;
  font-weight: 500;
}

.modern-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
}

.dialog-footer {
  padding: 24px 32px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.save-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.save-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.cancel-button {
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.cancel-button:hover {
  border-color: #94a3b8;
  color: #475569;
  transform: translateY(-1px);
}

/* Responsive Design */
@media (max-width: 1400px) {
  :deep(.hidden-lg) {
    display: none !important;
  }
}

@media (max-width: 1200px) {
  :deep(.hidden-md) {
    display: none !important;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 85%;
    min-width: 500px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .page-title {
    font-size: 32px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .filter-section,
  .table-section {
    margin: 0 16px 16px;
  }

  .filter-content,
  .table-content {
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

  :deep(.hidden-sm) {
    display: none !important;
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 10px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 95%;
    min-width: 320px;
    max-width: calc(100vw - 20px);
  }

  .password-dialog :deep(.el-dialog) {
    width: 95%;
    min-width: 300px;
  }

  .dialog-content {
    padding: 20px;
    max-height: 50vh;
  }

  .pagination-wrapper {
    padding: 12px 16px;
  }

  .modern-pagination :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .page-title {
    font-size: 24px;
  }

  :deep(.hidden-xs) {
    display: none !important;
  }

  .table-actions {
    flex-direction: column;
    gap: 4px;
  }

  .action-btn {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 5px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 98%;
    min-width: 300px;
    max-width: calc(100vw - 10px);
  }

  .password-dialog :deep(.el-dialog) {
    width: 98%;
    min-width: 280px;
  }

  .dialog-content {
    padding: 16px;
    max-height: 45vh;
  }

  .dialog-footer {
    padding: 16px 20px;
    flex-direction: column;
  }

  .dialog-footer .el-button {
    width: 100%;
  }
}

/* Scrollbar Styles */
.table-container::-webkit-scrollbar {
  height: 8px;
}

.table-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}

.dialog-content::-webkit-scrollbar {
  width: 6px;
}

.dialog-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-radius: 3px;
}

/* Global scroll control */
body.dialog-open {
  overflow: auto !important;
}

html.dialog-open {
  overflow: auto !important;
}

.el-popup-parent--hidden {
  overflow: auto !important;
}

/* Loading and Animation States */
.user-manager {
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

.filter-section,
.table-section {
  animation: slideUp 0.6s ease-out;
  animation-fill-mode: both;
}

.filter-section {
  animation-delay: 0.1s;
}

.table-section {
  animation-delay: 0.2s;
}

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

/* Hover Effects */
.filter-section:hover .filter-background,
.table-section:hover .table-background {
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.filter-section .filter-background,
.table-section .table-background {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>