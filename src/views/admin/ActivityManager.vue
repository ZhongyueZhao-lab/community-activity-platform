<template>
  <div class="activity-manager">
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
            <span class="title-gradient">Event Management</span>
          </h1>
          <p class="page-subtitle">Manage and organize community events</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showCreateDialog" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Create Event</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <div class="filter-background"></div>
      <div class="filter-content">
        <div class="filter-header">
          <h3 class="filter-title">Filter Events</h3>
          <p class="filter-subtitle">Search and filter your events</p>
        </div>

        <el-form :model="filterForm" :inline="true" class="filter-form">
          <div class="form-row">
            <el-form-item label="Event Title" class="form-item">
              <el-input
                  v-model="filterForm.title"
                  placeholder="Search by event title..."
                  clearable
                  class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="Status" class="form-item">
              <el-select v-model="filterForm.status" placeholder="Select status..." clearable class="modern-select">
                <el-option label="Draft" value="draft" />
                <el-option label="Pending" value="pending" />
                <el-option label="Published" value="published" />
                <el-option label="Cancelled" value="cancelled" />
                <el-option label="Completed" value="completed" />
              </el-select>
            </el-form-item>

            <el-form-item label="Category" class="form-item">
              <el-select v-model="filterForm.categoryId" placeholder="Select category..." clearable class="modern-select">
                <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item class="form-actions">
              <el-button type="primary" @click="loadActivities" class="search-button">
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

    <!-- Event Table Section -->
    <div class="table-section">
      <div class="table-background"></div>
      <div class="table-content">
        <div class="table-header">
          <div class="results-info">
            <h3 class="results-title">Event List</h3>
            <p class="results-subtitle">{{ pagination.total }} events found</p>
          </div>
        </div>

        <div class="table-container">
          <el-table
              :data="activities"
              v-loading="loading"
              class="modern-table"
              :border="false"
              :stripe="true"
              empty-text="No events found"
          >
            <el-table-column prop="id" label="ID" width="80" />

            <el-table-column label="Cover" width="100" class-name="hidden-xs">
              <template #default="scope">
                <div class="cover-preview">
                  <img
                      v-if="scope.row.coverImage"
                      :src="scope.row.coverImage"
                      alt="Event Cover"
                      class="cover-image"
                      @error="handleImageError"
                  />
                  <div v-else class="no-cover">
                    <el-icon><Picture /></el-icon>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="title" label="Event Title" width="100" show-overflow-tooltip>
              <template #default="scope">
                <div class="table-title">
                  <div class="title-text">{{ scope.row.title }}</div>
                  <div class="title-meta">{{ scope.row.categoryName || 'General' }}</div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="location" label="Location" width="120" show-overflow-tooltip class-name="hidden-sm" />

            <el-table-column label="Start Time" width="140" class-name="hidden-xs">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Clock /></el-icon>
                  <span>{{ formatDateTime(scope.row.startTime) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Participants" width="100" class-name="hidden-sm">
              <template #default="scope">
                <div class="participants-info">
                  <el-icon class="participants-icon"><User /></el-icon>
                  <span>{{ scope.row.currentParticipants }}/{{ scope.row.maxParticipants || '∞' }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Status" width="120">
              <template #default="scope">
                <div class="status-wrapper">
                  <el-tag :type="getStatusType(scope.row.status)" class="modern-status-tag">
                    <div class="status-dot"></div>
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="creatorName" label="Creator" width="100" class-name="hidden-md" />

            <el-table-column label="Actions" width="120" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button size="small" type="primary" @click="viewActivity(scope.row)" class="action-btn">
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
                        <el-dropdown-item command="publish" v-if="scope.row.status === 'pending'">
                          <el-icon><Check /></el-icon>
                          Publish
                        </el-dropdown-item>
                        <el-dropdown-item command="cancel" v-if="scope.row.status === 'published'">
                          <el-icon><Close /></el-icon>
                          Cancel
                        </el-dropdown-item>
                        <el-dropdown-item command="complete" v-if="scope.row.status === 'published'">
                          <el-icon><CircleCheck /></el-icon>
                          Complete
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

    <!-- Event Dialog -->
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
            :model="currentActivity"
            :rules="formRules"
            label-width="120px"
            v-loading="dialogLoading"
            :disabled="dialogMode === 'view'"
            class="modern-form"
        >
          <!-- Cover Upload -->
          <el-form-item label="Event Cover" prop="coverImage" class="cover-form-item">
            <div class="cover-upload-section">
              <ImageUpload
                  v-model="currentActivity.coverImage"
                  :limit="1"
                  :show-tips="dialogMode !== 'view'"
                  :disabled="dialogMode === 'view'"
                  tips="Recommended size: 1200x600 pixels, supports jpg, png, gif, bmp, webp formats, max 10MB"
                  @success="handleCoverUploadSuccess"
                  @error="handleCoverUploadError"
                  @remove="handleCoverRemove"
              />
              <div v-if="currentActivity.coverImage" class="cover-preview-large">
                <img :src="currentActivity.coverImage" alt="Event cover preview" />
              </div>
            </div>
          </el-form-item>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Event Title" prop="title">
                <el-input v-model="currentActivity.title" placeholder="Enter event title..." class="modern-input" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Category" prop="categoryId">
                <el-select v-model="currentActivity.categoryId" placeholder="Select category..." class="modern-select" style="width: 100%">
                  <el-option
                      v-for="category in categories"
                      :key="category.id"
                      :label="category.name"
                      :value="category.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="Description" prop="description">
            <el-input
                v-model="currentActivity.description"
                type="textarea"
                :rows="4"
                placeholder="Enter event description..."
                class="modern-textarea"
            />
          </el-form-item>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Location" prop="location">
                <el-input v-model="currentActivity.location" placeholder="Enter event location..." class="modern-input" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Contact Info" prop="contactInfo">
                <el-input v-model="currentActivity.contactInfo" placeholder="Enter contact information..." class="modern-input" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Start Time" prop="startTime">
                <el-date-picker
                    v-model="currentActivity.startTime"
                    type="datetime"
                    placeholder="Select start time..."
                    style="width: 100%"
                    class="modern-datepicker"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="End Time" prop="endTime">
                <el-date-picker
                    v-model="currentActivity.endTime"
                    type="datetime"
                    placeholder="Select end time..."
                    style="width: 100%"
                    class="modern-datepicker"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Registration Start" prop="registrationStartTime">
                <el-date-picker
                    v-model="currentActivity.registrationStartTime"
                    type="datetime"
                    placeholder="Select registration start time..."
                    style="width: 100%"
                    class="modern-datepicker"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Registration End" prop="registrationEndTime">
                <el-date-picker
                    v-model="currentActivity.registrationEndTime"
                    type="datetime"
                    placeholder="Select registration end time..."
                    style="width: 100%"
                    class="modern-datepicker"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Max Participants" prop="maxParticipants">
                <el-input-number
                    v-model="currentActivity.maxParticipants"
                    :min="0"
                    placeholder="0 for unlimited"
                    style="width: 100%"
                    class="modern-number-input"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Event Fee" prop="fee">
                <el-input-number
                    v-model="currentActivity.fee"
                    :min="0"
                    :precision="2"
                    placeholder="Enter event fee..."
                    style="width: 100%"
                    class="modern-number-input"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="Tags" prop="tags">
            <el-input
                v-model="currentActivity.tags"
                placeholder="Enter tags, separated by commas..."
                class="modern-input"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-button">
            {{ dialogMode === 'view' ? 'Close' : 'Cancel' }}
          </el-button>
          <el-button
              v-if="dialogMode !== 'view'"
              type="primary"
              @click="saveActivity"
              :loading="dialogLoading"
              class="save-button"
          >
            {{ dialogMode === 'edit' ? 'Update' : 'Create' }}
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
  Plus, Picture, Search, Refresh, Clock, User, View, MoreFilled, Edit, Check,
  Close, CircleCheck, Delete
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import request from '@/utils/request'
import dayjs from 'dayjs'
import ImageUpload from '@/components/ImageUpload.vue'

// English locale configuration
const enLocale = en

// Responsive data
const loading = ref(false)
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogTitle = ref('')
const dialogMode = ref('create') // 'create', 'edit', 'view'

const activities = ref([])
const categories = ref([])

const filterForm = reactive({
  title: '',
  status: '',
  categoryId: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const currentActivity = ref({
  title: '',
  description: '',
  categoryId: null,
  location: '',
  contactInfo: '',
  startTime: null,
  endTime: null,
  registrationStartTime: null,
  registrationEndTime: null,
  maxParticipants: 0,
  fee: 0,
  tags: '',
  coverImage: ''
})

const formRef = ref()

// Store original body overflow
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
  title: [{ required: true, message: 'Please enter event title', trigger: 'blur' }],
  categoryId: [{ required: true, message: 'Please select event category', trigger: 'change' }],
  startTime: [{ required: true, message: 'Please select start time', trigger: 'change' }],
  endTime: [{ required: true, message: 'Please select end time', trigger: 'change' }]
}

// Methods
const loadActivities = async () => {
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

    const response = await request.get('/api/admin/activities', { params })
    if (response.code === 200) {
      activities.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    console.error('Failed to fetch activities:', error)
    ElMessage.error('Failed to fetch activities')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await request.get('/api/admin/categories')
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const resetFilter = () => {
  filterForm.title = ''
  filterForm.status = ''
  filterForm.categoryId = null
  pagination.page = 1
  loadActivities()
}

const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadActivities()
}

const handleCurrentChange = (newPage) => {
  pagination.page = newPage
  loadActivities()
}

const showCreateDialog = () => {
  dialogMode.value = 'create'
  dialogTitle.value = 'Create Event'
  currentActivity.value = {
    title: '',
    description: '',
    categoryId: null,
    location: '',
    contactInfo: '',
    startTime: null,
    endTime: null,
    registrationStartTime: null,
    registrationEndTime: null,
    maxParticipants: 0,
    fee: 0,
    tags: '',
    coverImage: ''
  }
  dialogVisible.value = true
}

const viewActivity = (activity) => {
  dialogMode.value = 'view'
  dialogTitle.value = 'View Event'
  currentActivity.value = { ...activity }
  if (currentActivity.value.startTime) {
    currentActivity.value.startTime = new Date(currentActivity.value.startTime)
  }
  if (currentActivity.value.endTime) {
    currentActivity.value.endTime = new Date(currentActivity.value.endTime)
  }
  if (currentActivity.value.registrationStartTime) {
    currentActivity.value.registrationStartTime = new Date(currentActivity.value.registrationStartTime)
  }
  if (currentActivity.value.registrationEndTime) {
    currentActivity.value.registrationEndTime = new Date(currentActivity.value.registrationEndTime)
  }
  dialogVisible.value = true
}

const editActivity = (activity) => {
  dialogMode.value = 'edit'
  dialogTitle.value = 'Edit Event'
  currentActivity.value = { ...activity }
  if (currentActivity.value.startTime) {
    currentActivity.value.startTime = new Date(currentActivity.value.startTime)
  }
  if (currentActivity.value.endTime) {
    currentActivity.value.endTime = new Date(currentActivity.value.endTime)
  }
  if (currentActivity.value.registrationStartTime) {
    currentActivity.value.registrationStartTime = new Date(currentActivity.value.registrationStartTime)
  }
  if (currentActivity.value.registrationEndTime) {
    currentActivity.value.registrationEndTime = new Date(currentActivity.value.registrationEndTime)
  }
  dialogVisible.value = true
}

const saveActivity = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    dialogLoading.value = true

    const data = { ...currentActivity.value }

    let response
    if (dialogMode.value === 'edit') {
      response = await request.put(`/api/admin/activities/${data.id}`, data)
    } else if (dialogMode.value === 'create') {
      response = await request.post('/api/admin/activities', data)
    } else {
      return
    }

    if (response.code === 200) {
      ElMessage.success(dialogMode.value === 'edit' ? 'Event updated successfully' : 'Event created successfully')
      dialogVisible.value = false
      loadActivities()
    }
  } catch (error) {
    console.error('Failed to save event:', error)
    ElMessage.error('Failed to save event')
  } finally {
    dialogLoading.value = false
  }
}

const handleCommand = async (command, activity) => {
  switch (command) {
    case 'edit':
      editActivity(activity)
      break
    case 'publish':
      await updateActivityStatus(activity.id, 'published')
      break
    case 'cancel':
      await updateActivityStatus(activity.id, 'cancelled')
      break
    case 'complete':
      await updateActivityStatus(activity.id, 'completed')
      break
    case 'delete':
      await deleteActivity(activity)
      break
  }
}

const updateActivityStatus = async (id, status) => {
  try {
    await ElMessageBox.confirm(`Are you sure to ${getStatusText(status).toLowerCase()} this event?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.put(`/api/admin/activities/${id}/status`, null, {
      params: { status }
    })

    if (response.code === 200) {
      ElMessage.success('Status updated successfully')
      loadActivities()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update status:', error)
      ElMessage.error('Failed to update status')
    }
  }
}

const deleteActivity = async (activity) => {
  try {
    await ElMessageBox.confirm(`Are you sure to delete event "${activity.title}"?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/activities/${activity.id}`)

    if (response.code === 200) {
      ElMessage.success('Event deleted successfully')
      loadActivities()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete event:', error)
      ElMessage.error('Failed to delete event')
    }
  }
}

// 优化的弹窗滚动控制方法
const handleDialogOpen = () => {
  // 存储原始的 overflow 值
  originalBodyOverflow = document.body.style.overflow || ''
  originalHtmlOverflow = document.documentElement.style.overflow || ''

  // 强制允许页面滚动
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
    // 确保dialog容器也允许滚动
    const dialogElement = document.querySelector('.el-dialog__wrapper')
    if (dialogElement) {
      dialogElement.style.overflow = 'auto'
    }
  })
}

const handleDialogOpened = () => {
  // 弹窗完全打开后再次确保滚动正常
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'

    // 移除可能存在的滚动锁定类
    document.body.classList.remove('el-popup-parent--hidden')

    // 确保弹窗容器滚动正常
    const overlayElement = document.querySelector('.el-overlay')
    if (overlayElement) {
      overlayElement.style.overflow = 'auto'
    }
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
  // 弹窗关闭后恢复原始滚动状态
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow

    // 移除可能存在的滚动锁定类
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

// Cover upload methods
const handleCoverUploadSuccess = (response) => {
  console.log('Cover upload success:', response)
  ElMessage.success('Cover uploaded successfully')
}

const handleCoverUploadError = (error) => {
  console.error('Cover upload failed:', error)
  ElMessage.error('Cover upload failed')
}

const handleCoverRemove = () => {
  currentActivity.value.coverImage = ''
  ElMessage.success('Cover removed successfully')
}

const handleImageError = (event) => {
  event.target.style.display = 'none'
  const nocover = event.target.parentNode.querySelector('.no-cover')
  if (nocover) {
    nocover.style.display = 'flex'
  }
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
    published: 'Published',
    cancelled: 'Cancelled',
    completed: 'Completed'
  }
  return textMap[status] || status
}

const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('MM-DD HH:mm') : '-'
}

// Lifecycle
onMounted(() => {
  loadActivities()
  loadCategories()
})
</script>

<style scoped>
/* Global Styles */
.activity-manager {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
  padding-bottom: 40px;
}

/* 确保所有元素都是透明背景，移除多余的白色背景 */
.activity-manager,
.activity-manager .page-header,
.activity-manager .header-content,
.activity-manager .header-left,
.activity-manager .page-title,
.activity-manager .page-subtitle,
.activity-manager .header-actions,
.activity-manager .filter-section,
.activity-manager .table-section {
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

.container {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
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
.modern-select :deep(.el-select__wrapper),
.modern-textarea :deep(.el-textarea__inner),
.modern-datepicker :deep(.el-input__wrapper),
.modern-number-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-input :deep(.el-input__wrapper:hover),
.modern-select :deep(.el-select__wrapper:hover),
.modern-datepicker :deep(.el-input__wrapper:hover),
.modern-number-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-input :deep(.el-input__wrapper.is-focus),
.modern-select :deep(.el-select__wrapper.is-focus),
.modern-datepicker :deep(.el-input__wrapper.is-focus),
.modern-number-input :deep(.el-input__wrapper.is-focus) {
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

.cover-preview {
  width: 60px;
  height: 36px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  color: #94a3b8;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
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
  font-size: 13px;
}

.participants-icon, .date-icon {
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

/* 优化后的 Dialog 样式 */
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

.modern-dialog :deep(.el-dialog__headerbtn) {
  top: 24px;
  right: 24px;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(148, 163, 184, 0.1);
  transition: all 0.3s ease;
}

.modern-dialog :deep(.el-dialog__headerbtn:hover) {
  background: rgba(148, 163, 184, 0.2);
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

.modern-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
}

.cover-form-item {
  margin-bottom: 32px;
}

.cover-upload-section {
  width: 100%;
}

.cover-preview-large {
  margin-top: 16px;
  max-width: 400px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.cover-preview-large img {
  width: 100%;
  height: auto;
  display: block;
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

  .cover-preview {
    width: 40px;
    height: 24px;
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 5px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 98%;
    min-width: 300px;
    max-width: calc(100vw - 10px);
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

/* 全局滚动控制样式 */
body.dialog-open {
  overflow: auto !important;
}

html.dialog-open {
  overflow: auto !important;
}

/* 防止 Element Plus 添加滚动锁定类 */
.el-popup-parent--hidden {
  overflow: auto !important;
}

/* Loading and Animation States */
.activity-manager {
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