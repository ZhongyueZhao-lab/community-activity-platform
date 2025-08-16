<template>
  <div class="notice-manager">
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
            <span class="title-gradient">Announcement Management</span>
          </h1>
          <p class="page-subtitle">Manage and publish system announcements</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showAddNoticeDialog" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Publish Announcement</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <div class="filter-background"></div>
      <div class="filter-content">
        <div class="filter-header">
          <h3 class="filter-title">Filter Announcements</h3>
          <p class="filter-subtitle">Search and filter announcements</p>
        </div>

        <el-form :model="filterForm" :inline="true" class="filter-form">
          <div class="form-row">
            <el-form-item label="Title" class="form-item">
              <el-input
                  v-model="filterForm.title"
                  placeholder="Search by title..."
                  clearable
                  class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="Type" class="form-item">
              <el-select v-model="filterForm.type" placeholder="Select type..." clearable class="modern-select">
                <el-option label="System" value="system" />
                <el-option label="Activity" value="activity" />
                <el-option label="Notice" value="notice" />
              </el-select>
            </el-form-item>

            <el-form-item label="Target" class="form-item">
              <el-select v-model="filterForm.targetType" placeholder="Select target..." clearable class="modern-select">
                <el-option label="All Users" value="all" />
                <el-option label="Regular Users" value="user" />
                <el-option label="Admins" value="admin" />
              </el-select>
            </el-form-item>

            <el-form-item label="Status" class="form-item">
              <el-select v-model="filterForm.status" placeholder="Select status..." clearable class="modern-select">
                <el-option label="Enabled" :value="1" />
                <el-option label="Disabled" :value="0" />
              </el-select>
            </el-form-item>

            <el-form-item class="form-actions">
              <el-button type="primary" @click="loadNotices" class="search-button">
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

    <!-- Notice Table Section -->
    <div class="table-section">
      <div class="table-background"></div>
      <div class="table-content">
        <div class="table-header">
          <div class="results-info">
            <h3 class="results-title">Announcement List</h3>
            <p class="results-subtitle">{{ pagination.total }} announcements found</p>
          </div>
        </div>

        <div class="table-container">
          <el-table
              :data="notices"
              v-loading="loading"
              class="modern-table"
              :border="false"
              :stripe="true"
              empty-text="No announcements found"
          >
            <el-table-column prop="id" label="ID" width="80" />

            <el-table-column prop="title" label="Title" min-width="140" show-overflow-tooltip>
              <template #default="scope">
                <div class="title-cell">
                  <div class="title-text">{{ scope.row.title }}</div>
                  <div class="title-type" v-if="scope.row.type">
                    <el-tag :type="getNoticeTypeColor(scope.row.type)" size="small" class="mini-tag">
                      {{ getNoticeTypeText(scope.row.type) }}
                    </el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Content" min-width="160" show-overflow-tooltip class-name="hidden-sm">
              <template #default="scope">
                <div class="content-preview">
                  <el-icon class="content-icon"><Document /></el-icon>
                  <span>{{ scope.row.content.substring(0, 40) }}{{ scope.row.content.length > 40 ? '...' : '' }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Target" width="100" class-name="hidden-md">
              <template #default="scope">
                <div class="target-info">
                  <el-icon class="target-icon"><UserFilled /></el-icon>
                  <span class="target-text">{{ getTargetTypeText(scope.row.targetType) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Status" width="90">
              <template #default="scope">
                <div class="status-wrapper">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="modern-status-tag">
                    <div class="status-dot"></div>
                    {{ scope.row.status === 1 ? 'Active' : 'Disabled' }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Published" width="120" class-name="hidden-xs">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Clock /></el-icon>
                  <span>{{ formatDateTime(scope.row.createdAt) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Actions" width="100" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button size="small" type="primary" @click="viewNotice(scope.row)" class="action-btn">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-dropdown @command="(command) => handleCommand(command, scope.row)" size="small">
                    <el-button size="small" class="action-btn more-btn">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu class="modern-dropdown">
                        <el-dropdown-item command="edit">
                          <el-icon><Edit /></el-icon>
                          Edit
                        </el-dropdown-item>
                        <el-dropdown-item :command="scope.row.status === 1 ? 'disable' : 'enable'">
                          <el-icon>
                            <CircleClose v-if="scope.row.status === 1" />
                            <CircleCheck v-else />
                          </el-icon>
                          {{ scope.row.status === 1 ? 'Disable' : 'Enable' }}
                        </el-dropdown-item>
                        <el-dropdown-item command="stats">
                          <el-icon><DataAnalysis /></el-icon>
                          View Statistics
                        </el-dropdown-item>
                        <el-dropdown-item command="delete" divided class="danger-item">
                          <el-icon><Delete /></el-icon>
                          Delete
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

    <!-- Notice Edit Dialog -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
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
      <div class="dialog-content">
        <el-form
            ref="formRef"
            :model="currentNotice"
            :rules="formRules"
            label-width="120px"
            class="modern-form"
        >
          <el-form-item label="Title" prop="title">
            <el-input v-model="currentNotice.title" placeholder="Enter announcement title..." class="modern-input" />
          </el-form-item>

          <el-form-item label="Content" prop="content">
            <el-input
                v-model="currentNotice.content"
                type="textarea"
                :rows="6"
                placeholder="Enter announcement content..."
                class="modern-textarea"
            />
          </el-form-item>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Type" prop="type">
                <el-select v-model="currentNotice.type" placeholder="Select type..." class="modern-select" style="width: 100%">
                  <el-option label="System Message" value="system" />
                  <el-option label="Activity Message" value="activity" />
                  <el-option label="General Notice" value="notice" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Target Users" prop="targetType">
                <el-select v-model="currentNotice.targetType" placeholder="Select target users..." class="modern-select" style="width: 100%">
                  <el-option label="All Users" value="all" />
                  <el-option label="Regular Users" value="user" />
                  <el-option label="Administrators" value="admin" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="Related Activity" prop="relatedActivityId" v-if="currentNotice.type === 'activity'">
            <el-select
                v-model="currentNotice.relatedActivityId"
                placeholder="Select related activity..."
                class="modern-select"
                style="width: 100%"
                clearable
            >
              <el-option
                  v-for="activity in activities"
                  :key="activity.id"
                  :label="activity.title"
                  :value="activity.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="Specific User" prop="targetUserId" v-if="currentNotice.targetType === 'user'">
            <el-input
                v-model="currentNotice.targetUserId"
                placeholder="Enter user ID (optional, leave empty for all regular users)"
                type="number"
                class="modern-input"
            />
          </el-form-item>

          <el-form-item label="Status" prop="status">
            <el-radio-group v-model="currentNotice.status" class="modern-radio-group">
              <el-radio :label="1" class="modern-radio">Enabled</el-radio>
              <el-radio :label="0" class="modern-radio">Disabled</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button
              type="primary"
              @click="saveNotice"
              :loading="dialogLoading"
              class="save-button"
          >
            {{ isEdit ? 'Update' : 'Publish' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Notice View Dialog -->
    <el-dialog
        v-model="viewDialogVisible"
        title="View Announcement"
        width="70%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleViewDialogClose"
        @open="handleViewDialogOpen"
        @opened="handleViewDialogOpened"
        @close="handleViewDialogClosed"
    >
      <div v-if="viewNoticeData" class="dialog-content">
        <div class="notice-detail">
          <el-descriptions :column="2" border size="large" class="modern-descriptions">
            <el-descriptions-item label="Title" :span="2">{{ viewNoticeData.title }}</el-descriptions-item>
            <el-descriptions-item label="Type">
              <el-tag :type="getNoticeTypeColor(viewNoticeData.type)" class="detail-tag">
                {{ getNoticeTypeText(viewNoticeData.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Target Users">
              <el-tag class="detail-tag">{{ getTargetTypeText(viewNoticeData.targetType) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Status">
              <el-tag :type="viewNoticeData.status === 1 ? 'success' : 'danger'" class="detail-tag">
                {{ viewNoticeData.status === 1 ? 'Enabled' : 'Disabled' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Publisher">
              {{ viewNoticeData.senderName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="Published Time">
              {{ formatDateTime(viewNoticeData.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="Updated Time">
              {{ formatDateTime(viewNoticeData.updatedAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="Content" :span="2">
              <div class="notice-content">{{ viewNoticeData.content }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewDialogVisible = false" class="cancel-button">Close</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Statistics Dialog -->
    <el-dialog
        v-model="statsDialogVisible"
        title="Message Statistics"
        width="60%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog stats-dialog"
        :before-close="handleStatsDialogClose"
        @open="handleStatsDialogOpen"
        @opened="handleStatsDialogOpened"
        @close="handleStatsDialogClosed"
    >
      <div v-if="statsData" class="dialog-content">
        <div class="stats-content">
          <el-row :gutter="24" class="stats-cards">
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-icon total">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-number">{{ statsData.totalUsers || 0 }}</div>
                <div class="stat-label">Target Users</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-icon read">
                  <el-icon><View /></el-icon>
                </div>
                <div class="stat-number">{{ statsData.readCount || 0 }}</div>
                <div class="stat-label">Users Read</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-icon rate">
                  <el-icon><TrendCharts /></el-icon>
                </div>
                <div class="stat-number">{{ statsData.readRate || 0 }}%</div>
                <div class="stat-label">Read Rate</div>
              </div>
            </el-col>
          </el-row>

          <div class="stats-divider">
            <h4>Reading Details</h4>
          </div>

          <div class="stats-table">
            <el-table :data="statsData.readDetails || []" class="modern-table" max-height="300">
              <el-table-column prop="userNickname" label="User" width="120" />
              <el-table-column label="Status" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.isRead ? 'success' : 'info'" size="small" class="modern-tag">
                    {{ scope.row.isRead ? 'Read' : 'Unread' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="readTime" label="Read Time" min-width="150">
                <template #default="scope">
                  {{ scope.row.readTime ? formatDateTime(scope.row.readTime) : '-' }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
      <div v-else class="dialog-content">
        <div class="loading-stats">
          <el-skeleton :rows="3" animated />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="statsDialogVisible = false" class="cancel-button">Close</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElConfigProvider } from 'element-plus'
import {
  Plus, Search, Refresh, Document, UserFilled, Clock, View, MoreFilled, Edit,
  CircleClose, CircleCheck, DataAnalysis, Delete, User, TrendCharts
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import request from '@/utils/request'
import dayjs from 'dayjs'

// English locale configuration
const enLocale = en

// Responsive data
const loading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const statsDialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

const notices = ref([])
const activities = ref([])
const viewNoticeData = ref(null)
const statsData = ref(null)

const filterForm = reactive({
  title: '',
  type: '',
  targetType: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const currentNotice = ref({
  title: '',
  content: '',
  type: 'notice',
  targetType: 'all',
  targetUserId: null,
  relatedActivityId: null,
  status: 1
})

const formRef = ref()

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

// Form validation rules
const formRules = {
  title: [{ required: true, message: 'Please enter announcement title', trigger: 'blur' }],
  content: [{ required: true, message: 'Please enter announcement content', trigger: 'blur' }],
  type: [{ required: true, message: 'Please select announcement type', trigger: 'change' }],
  targetType: [{ required: true, message: 'Please select target users', trigger: 'change' }]
}

// Methods
const loadNotices = async () => {
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

    const response = await request.get('/api/admin/messages', { params })
    if (response.code === 200) {
      notices.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('Failed to fetch announcements:', error)
    ElMessage.error('Failed to fetch announcements')
  } finally {
    loading.value = false
  }
}

const loadActivities = async () => {
  try {
    const response = await request.get('/api/admin/activities')
    if (response.code === 200) {
      activities.value = response.data.records || response.data || []
    }
  } catch (error) {
    console.error('Failed to fetch activities:', error)
  }
}

const resetFilter = () => {
  filterForm.title = ''
  filterForm.type = ''
  filterForm.targetType = ''
  filterForm.status = null
  pagination.page = 1
  loadNotices()
}

const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadNotices()
}

const handleCurrentChange = (newPage) => {
  pagination.page = newPage
  loadNotices()
}

const showAddNoticeDialog = () => {
  isEdit.value = false
  dialogTitle.value = 'Publish Announcement'
  currentNotice.value = {
    title: '',
    content: '',
    type: 'notice',
    targetType: 'all',
    targetUserId: null,
    relatedActivityId: null,
    status: 1
  }
  dialogVisible.value = true
}

const viewNotice = (notice) => {
  viewNoticeData.value = notice
  viewDialogVisible.value = true
}

const editNotice = (notice) => {
  isEdit.value = true
  dialogTitle.value = 'Edit Announcement'
  currentNotice.value = { ...notice }
  dialogVisible.value = true
}

const saveNotice = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    dialogLoading.value = true

    const data = { ...currentNotice.value }

    let response
    if (isEdit.value) {
      response = await request.put(`/api/admin/messages/${data.id}`, data)
    } else {
      response = await request.post('/api/admin/messages', data)
    }

    if (response.code === 200) {
      ElMessage.success(isEdit.value ? 'Announcement updated successfully' : 'Announcement published successfully')
      dialogVisible.value = false
      loadNotices()
    }
  } catch (error) {
    console.error('Failed to save announcement:', error)
    ElMessage.error('Failed to save announcement')
  } finally {
    dialogLoading.value = false
  }
}

const handleCommand = async (command, notice) => {
  switch (command) {
    case 'edit':
      editNotice(notice)
      break
    case 'enable':
      await updateNoticeStatus(notice.id, 1)
      break
    case 'disable':
      await updateNoticeStatus(notice.id, 0)
      break
    case 'stats':
      await showNoticeStats(notice.id)
      break
    case 'delete':
      await deleteNotice(notice)
      break
  }
}

const updateNoticeStatus = async (id, status) => {
  try {
    const response = await request.put(`/api/admin/messages/${id}/status`, null, {
      params: { status }
    })

    if (response.code === 200) {
      ElMessage.success('Status updated successfully')
      loadNotices()
    }
  } catch (error) {
    console.error('Failed to update status:', error)
    ElMessage.error('Failed to update status')
  }
}

const showNoticeStats = async (id) => {
  try {
    statsData.value = null
    statsDialogVisible.value = true

    const response = await request.get(`/api/admin/messages/${id}/stats`)
    if (response.code === 200) {
      statsData.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
    ElMessage.error('Failed to fetch statistics')
  }
}

const deleteNotice = async (notice) => {
  try {
    await ElMessageBox.confirm(`Are you sure to delete announcement "${notice.title}"?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/messages/${notice.id}`)

    if (response.code === 200) {
      ElMessage.success('Announcement deleted successfully')
      loadNotices()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete announcement:', error)
      ElMessage.error('Failed to delete announcement')
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
  if (dialogLoading.value) {
    ElMessage.warning('Please wait for the operation to complete')
    return
  }
  done()
}

const handleDialogClosed = () => {
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

// View dialog scroll control
const handleViewDialogOpen = () => {
  originalBodyOverflow = document.body.style.overflow || ''
  originalHtmlOverflow = document.documentElement.style.overflow || ''

  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
  })
}

const handleViewDialogOpened = () => {
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

const handleViewDialogClose = (done) => {
  done()
}

const handleViewDialogClosed = () => {
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

// Stats dialog scroll control
const handleStatsDialogOpen = () => {
  originalBodyOverflow = document.body.style.overflow || ''
  originalHtmlOverflow = document.documentElement.style.overflow || ''

  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
  })
}

const handleStatsDialogOpened = () => {
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

const handleStatsDialogClose = (done) => {
  done()
}

const handleStatsDialogClosed = () => {
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

// Utility methods
const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('MM-DD HH:mm') : '-'
}

const getNoticeTypeColor = (type) => {
  const colorMap = {
    system: 'info',
    activity: 'warning',
    notice: 'success'
  }
  return colorMap[type] || 'info'
}

const getNoticeTypeText = (type) => {
  const textMap = {
    system: 'System',
    activity: 'Activity',
    notice: 'Notice'
  }
  return textMap[type] || type
}

const getTargetTypeText = (type) => {
  const textMap = {
    all: 'All',
    user: 'Users',
    admin: 'Admins'
  }
  return textMap[type] || type
}

// Lifecycle
onMounted(() => {
  loadNotices()
  loadActivities()
})
</script>

<style scoped>
/* Global Styles */
.notice-manager {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
  padding-bottom: 40px;
}

/* Ensure all elements have transparent background */
.notice-manager,
.notice-manager .page-header,
.notice-manager .header-content,
.notice-manager .header-left,
.notice-manager .page-title,
.notice-manager .page-subtitle,
.notice-manager .header-actions,
.notice-manager .filter-section,
.notice-manager .table-section {
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

.header-actions {
  display: flex;
  gap: 16px;
}

.create-button {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.create-button:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.35);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.25);
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
  min-width: 180px;
  margin-bottom: 0 !important;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 0 !important;
}

/* Form Controls */
.modern-input :deep(.el-input__wrapper),
.modern-select :deep(.el-select__wrapper),
.modern-textarea :deep(.el-textarea__inner) {
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

.modern-textarea :deep(.el-textarea__inner:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-textarea :deep(.el-textarea__inner:focus) {
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
  overflow-x: auto;
}

/* Modern Table Styles - 确保表格在小屏幕上不溢出 */
.modern-table {
  border-radius: 0;
  min-width: 480px; /* 减小最小宽度 */
}

.modern-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.modern-table :deep(.el-table__header th) {
  background: transparent;
  border: none;
  padding: 16px 8px; /* 减小左右padding */
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
  padding: 12px 8px; /* 减小左右padding */
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
}

.title-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-text {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.3;
}

.title-type {
  display: flex;
  align-items: center;
}

.mini-tag {
  font-size: 11px;
  padding: 2px 6px;
  height: auto;
  line-height: 1.2;
}

.content-preview {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.4;
}

.content-icon {
  font-size: 14px;
  color: #94a3b8;
  margin-top: 2px;
  flex-shrink: 0;
}

.target-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
}

.target-icon {
  font-size: 14px;
  color: #94a3b8;
}

.target-text {
  font-size: 12px;
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
  padding: 4px 8px;
  font-size: 12px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 12px;
}

.date-icon {
  font-size: 14px;
  color: #94a3b8;
}

.table-actions {
  display: flex;
  gap: 6px;
}

.action-btn {
  border-radius: 8px;
  padding: 6px;
  min-width: auto;
  width: 28px;
  height: 28px;
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

.stats-dialog :deep(.el-dialog) {
  width: 60%;
  min-width: 500px;
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

.notice-detail {
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

.notice-content {
  max-height: 200px;
  overflow-y: auto;
  line-height: 1.6;
  white-space: pre-wrap;
  color: #374151;
  background: #f9fafb;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.modern-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
}

.modern-radio-group {
  display: flex;
  gap: 16px;
}

.modern-radio :deep(.el-radio__label) {
  font-weight: 500;
}

/* Statistics Content */
.stats-content {
  width: 100%;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  font-size: 20px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.stat-icon.read {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.rate {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.stats-divider {
  margin: 24px 0;
  padding: 16px 0;
  border-bottom: 1px solid #e2e8f0;
}

.stats-divider h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.stats-table {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.loading-stats {
  padding: 20px;
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
@media (max-width: 1200px) {
  :deep(.hidden-md) {
    display: none !important;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 85%;
    min-width: 500px;
  }

  .stats-dialog :deep(.el-dialog) {
    width: 80%;
    min-width: 450px;
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

  .modern-table {
    min-width: 400px; /* 进一步减小移动端表格最小宽度 */
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 10px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 95%;
    min-width: 320px;
    max-width: calc(100vw - 20px);
  }

  .stats-dialog :deep(.el-dialog) {
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

  .stats-cards .el-col {
    margin-bottom: 15px;
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
    width: 26px;
    height: 26px;
    font-size: 12px;
  }

  .modern-table {
    min-width: 350px; /* 小屏幕最小宽度 */
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 5px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 98%;
    min-width: 300px;
    max-width: calc(100vw - 10px);
  }

  .stats-dialog :deep(.el-dialog) {
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

  .stat-number {
    font-size: 24px;
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

.dialog-content::-webkit-scrollbar,
.notice-content::-webkit-scrollbar {
  width: 6px;
}

.dialog-content::-webkit-scrollbar-track,
.notice-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-thumb,
.notice-content::-webkit-scrollbar-thumb {
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
.notice-manager {
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