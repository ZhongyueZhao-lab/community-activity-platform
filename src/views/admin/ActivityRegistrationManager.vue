<template>
  <div class="registration-manager">
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
            <span class="title-gradient">Registration Management</span>
          </h1>
          <p class="page-subtitle">Manage event registrations and participants</p>
        </div>
        <div class="header-actions">
          <el-button type="success" @click="showAddDialog" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Manual Add</span>
          </el-button>
          <el-button type="warning" @click="exportData" class="create-button" :loading="exportLoading">
            <el-icon><Download /></el-icon>
            <span>Export</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Statistics Section -->
    <div class="statistics-section">
      <div class="stats-background"></div>
      <div class="stats-content">
        <div class="stats-grid">
          <div class="stat-card modern-card">
            <div class="stat-icon total">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalRegistrations }}</div>
              <div class="stat-label">Total Registrations</div>
            </div>
          </div>
          <div class="stat-card modern-card">
            <div class="stat-icon registered">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.registeredCount }}</div>
              <div class="stat-label">Active Registrations</div>
            </div>
          </div>
          <div class="stat-card modern-card">
            <div class="stat-icon attended">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.attendedCount }}</div>
              <div class="stat-label">Attended</div>
            </div>
          </div>
          <div class="stat-card modern-card">
            <div class="stat-icon cancelled">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.cancelledCount }}</div>
              <div class="stat-label">Cancelled</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <div class="filter-background"></div>
      <div class="filter-content">
        <div class="filter-header">
          <h3 class="filter-title">Filter Registrations</h3>
          <p class="filter-subtitle">Search and filter registration records</p>
        </div>

        <el-form :model="filterForm" :inline="true" class="filter-form">
          <div class="form-row">
            <el-form-item label="Activity ID" class="form-item">
              <el-input
                  v-model="filterForm.activityId"
                  placeholder="Enter activity ID..."
                  clearable
                  class="modern-input"
                  type="number"
              >
                <template #prefix>
                  <el-icon class="input-icon"><DocumentChecked /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="Activity Title" class="form-item">
              <el-input
                  v-model="filterForm.activityTitle"
                  placeholder="Search by activity title..."
                  clearable
                  class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="User Name" class="form-item">
              <el-input
                  v-model="filterForm.userName"
                  placeholder="Search by user name..."
                  clearable
                  class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="Status" class="form-item">
              <el-select v-model="filterForm.status" placeholder="Select status..." clearable class="modern-select">
                <el-option label="Registered" value="registered" />
                <el-option label="Cancelled" value="cancelled" />
                <el-option label="Attended" value="attended" />
              </el-select>
            </el-form-item>

            <el-form-item class="form-actions">
              <el-button type="primary" @click="loadRegistrations" class="search-button">
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

    <!-- Registration Table Section -->
    <div class="table-section">
      <div class="table-background"></div>
      <div class="table-content">
        <div class="table-header">
          <div class="results-info">
            <h3 class="results-title">Registration Records</h3>
            <p class="results-subtitle">{{ pagination.total }} records found</p>
          </div>
          <div class="table-actions">
            <el-button
                type="danger"
                :disabled="!hasSelectedItems"
                @click="batchCancel"
                class="batch-button"
            >
              <el-icon><Delete /></el-icon>
              <span>Batch Cancel</span>
            </el-button>
            <el-button
                type="success"
                :disabled="!hasSelectedItems"
                @click="batchMarkAttended"
                class="batch-button"
            >
              <el-icon><Check /></el-icon>
              <span>Mark Attended</span>
            </el-button>
          </div>
        </div>

        <div class="table-container">
          <el-table
              :data="registrations"
              v-loading="loading"
              class="modern-table"
              :border="false"
              :stripe="true"
              empty-text="No registration records found"
              @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />

            <el-table-column prop="registrationId" label="ID" width="80" />

            <el-table-column label="Activity" width="250" show-overflow-tooltip>
              <template #default="scope">
                <div class="activity-info">
                  <div class="activity-image">
                    <img
                        v-if="scope.row.activityCoverImage"
                        :src="scope.row.activityCoverImage"
                        alt="Activity Cover"
                        class="cover-image"
                        @error="handleImageError"
                    />
                    <div v-else class="no-cover">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </div>
                  <div class="activity-details">
                    <div class="activity-title">{{ scope.row.activityTitle }}</div>
                    <div class="activity-meta">
                      ID: {{ scope.row.activityId }} |
                      {{ scope.row.activityFeeText || 'Free' }}
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="User" width="200" show-overflow-tooltip>
              <template #default="scope">
                <div class="user-info">
                  <el-avatar
                      :src="scope.row.userAvatar"
                      :size="32"
                      class="user-avatar"
                  >
                    {{ scope.row.userNickname?.charAt(0) || 'U' }}
                  </el-avatar>
                  <div class="user-details">
                    <div class="user-name">{{ scope.row.userNickname }}</div>
                    <div class="user-meta">ID: {{ scope.row.userId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Registration Time" width="140" class-name="hidden-lg">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Clock /></el-icon>
                  <span>{{ formatDateTime(scope.row.registrationTime) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Activity Time" width="140" class-name="hidden-lg">
              <template #default="scope">
                <div class="date-info">
                  <el-icon class="date-icon"><Calendar /></el-icon>
                  <span>{{ formatDateTime(scope.row.activityStartTime) }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Status" width="120">
              <template #default="scope">
                <div class="status-wrapper">
                  <el-tag :type="getStatusType(scope.row.registrationStatus)" class="modern-status-tag">
                    <div class="status-dot"></div>
                    {{ getStatusText(scope.row.registrationStatus) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Contact" width="200" class-name="hidden-lg" show-overflow-tooltip>
              <template #default="scope">
                <div class="contact-info">
                  <div v-if="scope.row.userEmail" class="contact-item">
                    <el-icon><Message /></el-icon>
                    <span>{{ scope.row.userEmail }}</span>
                  </div>
                  <div v-if="scope.row.userPhone" class="contact-item">
                    <el-icon><Phone /></el-icon>
                    <span>{{ scope.row.userPhone }}</span>
                  </div>
                  <div v-if="!scope.row.userEmail && !scope.row.userPhone" class="no-contact">
                    No contact info
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Actions" width="140" fixed="right">
              <template #default="scope">
                <div class="table-actions">
                  <el-button size="small" type="primary" @click="viewRegistration(scope.row)" class="action-btn">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-dropdown @command="(command) => handleCommand(command, scope.row)" size="small">
                    <el-button size="small" class="action-btn more-btn">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu class="modern-dropdown">
                        <el-dropdown-item command="markAttended" v-if="scope.row.registrationStatus === 'registered'">
                          <el-icon><Check /></el-icon>
                          Mark Attended
                        </el-dropdown-item>
                        <el-dropdown-item command="markRegistered" v-if="scope.row.registrationStatus === 'cancelled'">
                          <el-icon><RefreshRight /></el-icon>
                          Mark Registered
                        </el-dropdown-item>
                        <el-dropdown-item command="cancel" v-if="scope.row.registrationStatus === 'registered'">
                          <el-icon><Close /></el-icon>
                          Cancel Registration
                        </el-dropdown-item>
                        <el-dropdown-item command="delete" divided class="danger-item">
                          <el-icon><Delete /></el-icon>
                          Delete Record
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

    <!-- Registration Detail Dialog -->
    <el-dialog
        v-model="detailDialogVisible"
        title="Registration Details"
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
      <div v-if="currentRegistration" class="dialog-content">
        <div class="detail-section">
          <h4 class="section-title">Registration Information</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">Registration ID:</span>
              <span class="info-value">{{ currentRegistration.registrationId }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Registration Time:</span>
              <span class="info-value">{{ formatDateTime(currentRegistration.registrationTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Status:</span>
              <el-tag :type="getStatusType(currentRegistration.registrationStatus)" class="modern-status-tag">
                {{ getStatusText(currentRegistration.registrationStatus) }}
              </el-tag>
            </div>
            <div class="info-item" v-if="currentRegistration.registrationNotes">
              <span class="info-label">Notes:</span>
              <span class="info-value">{{ currentRegistration.registrationNotes }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">Activity Information</h4>
          <div class="activity-detail">
            <div class="activity-image-large" v-if="currentRegistration.activityCoverImage">
              <img :src="currentRegistration.activityCoverImage" alt="Activity Cover" />
            </div>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Activity ID:</span>
                <span class="info-value">{{ currentRegistration.activityId }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Title:</span>
                <span class="info-value">{{ currentRegistration.activityTitle }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Location:</span>
                <span class="info-value">{{ currentRegistration.activityLocation || 'Not specified' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Start Time:</span>
                <span class="info-value">{{ formatDateTime(currentRegistration.activityStartTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">End Time:</span>
                <span class="info-value">{{ formatDateTime(currentRegistration.activityEndTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Fee:</span>
                <span class="info-value">{{ currentRegistration.activityFeeText || 'Free' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Participants:</span>
                <span class="info-value">{{ currentRegistration.participationRateText }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">User Information</h4>
          <div class="user-detail">
            <el-avatar
                :src="currentRegistration.userAvatar"
                :size="60"
                class="user-avatar-large"
            >
              {{ currentRegistration.userNickname?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">User ID:</span>
                <span class="info-value">{{ currentRegistration.userId }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Nickname:</span>
                <span class="info-value">{{ currentRegistration.userNickname }}</span>
              </div>
              <div class="info-item" v-if="currentRegistration.userEmail">
                <span class="info-label">Email:</span>
                <span class="info-value">{{ currentRegistration.userEmail }}</span>
              </div>
              <div class="info-item" v-if="currentRegistration.userPhone">
                <span class="info-label">Phone:</span>
                <span class="info-value">{{ currentRegistration.userPhone }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false" class="cancel-button">Close</el-button>
          <el-button
              v-if="currentRegistration && currentRegistration.registrationStatus === 'registered'"
              type="success"
              @click="markAsAttended(currentRegistration.registrationId)"
              class="save-button"
          >
            Mark Attended
          </el-button>
          <el-button
              v-if="currentRegistration && currentRegistration.registrationStatus === 'registered'"
              type="danger"
              @click="cancelRegistration(currentRegistration.registrationId)"
              class="save-button"
          >
            Cancel Registration
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Manual Add Registration Dialog -->
    <el-dialog
        v-model="addDialogVisible"
        title="Manual Add Registration"
        width="600px"
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
            ref="addFormRef"
            :model="addForm"
            :rules="addFormRules"
            label-width="120px"
            class="modern-form"
        >
          <el-form-item label="Activity ID" prop="activityId">
            <el-input-number
                v-model="addForm.activityId"
                :min="1"
                placeholder="Enter activity ID"
                style="width: 100%"
                class="modern-number-input"
            />
          </el-form-item>

          <el-form-item label="User ID" prop="userId">
            <el-input-number
                v-model="addForm.userId"
                :min="1"
                placeholder="Enter user ID"
                style="width: 100%"
                class="modern-number-input"
            />
          </el-form-item>

          <el-form-item label="Notes" prop="notes">
            <el-input
                v-model="addForm.notes"
                type="textarea"
                :rows="3"
                placeholder="Optional notes..."
                maxlength="200"
                show-word-limit
                class="modern-textarea"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addDialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button
              type="primary"
              @click="submitAdd"
              :loading="addLoading"
              class="save-button"
          >
            Add Registration
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
  Plus, Download, User, Check, Trophy, Close, DocumentChecked, Search, Refresh,
  Picture, Clock, Calendar, Message, Phone, View, MoreFilled, Delete,
  RefreshRight
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import request from '@/utils/request'
import dayjs from 'dayjs'

// English locale configuration
const enLocale = en

// Responsive data
const loading = ref(false)
const exportLoading = ref(false)
const addLoading = ref(false)
const detailDialogVisible = ref(false)
const addDialogVisible = ref(false)
const dialogLoading = ref(false)

const registrations = ref([])
const selectedRegistrations = ref([])
const currentRegistration = ref(null)

const statistics = reactive({
  totalRegistrations: 0,
  registeredCount: 0,
  cancelledCount: 0,
  attendedCount: 0,
  attendanceRate: 0,
  cancellationRate: 0
})

const filterForm = reactive({
  activityId: '',
  activityTitle: '',
  userName: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const addForm = ref({
  activityId: null,
  userId: null,
  notes: ''
})

const addFormRef = ref()

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

const hasSelectedItems = computed(() => {
  return selectedRegistrations.value.length > 0
})

// Form validation rules
const addFormRules = {
  activityId: [{ required: true, message: 'Please enter activity ID', trigger: 'blur' }],
  userId: [{ required: true, message: 'Please enter user ID', trigger: 'blur' }]
}

// Methods
const loadRegistrations = async () => {
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

    const response = await request.get('/api/admin/activity-registrations', { params })
    if (response.code === 200) {
      registrations.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    console.error('Failed to fetch registrations:', error)
    ElMessage.error('Failed to fetch registrations')
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const response = await request.get('/api/admin/activity-registrations/statistics')
    if (response.code === 200) {
      Object.assign(statistics, response.data)
    }
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
  }
}

const resetFilter = () => {
  filterForm.activityId = ''
  filterForm.activityTitle = ''
  filterForm.userName = ''
  filterForm.status = ''
  pagination.page = 1
  loadRegistrations()
}

const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadRegistrations()
}

const handleCurrentChange = (newPage) => {
  pagination.page = newPage
  loadRegistrations()
}

const handleSelectionChange = (selection) => {
  selectedRegistrations.value = selection
}

const viewRegistration = async (registration) => {
  try {
    const response = await request.get(`/api/admin/activity-registrations/${registration.registrationId}`)
    if (response.code === 200) {
      currentRegistration.value = response.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    console.error('Failed to fetch registration detail:', error)
    ElMessage.error('Failed to fetch registration detail')
  }
}

const handleCommand = async (command, registration) => {
  switch (command) {
    case 'markAttended':
      await markAsAttended(registration.registrationId)
      break
    case 'markRegistered':
      await updateStatus(registration.registrationId, 'registered')
      break
    case 'cancel':
      await cancelRegistration(registration.registrationId)
      break
    case 'delete':
      await deleteRegistration(registration)
      break
  }
}

const markAsAttended = async (registrationId) => {
  try {
    await ElMessageBox.confirm('Are you sure to mark this registration as attended?', 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'success'
    })

    const response = await request.put(`/api/admin/activity-registrations/${registrationId}/status`, null, {
      params: { status: 'attended' }
    })

    if (response.code === 200) {
      ElMessage.success('Status updated successfully')
      loadRegistrations()
      loadStatistics()
      if (detailDialogVisible.value) {
        detailDialogVisible.value = false
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update status:', error)
      ElMessage.error('Failed to update status')
    }
  }
}

const updateStatus = async (registrationId, status) => {
  try {
    const response = await request.put(`/api/admin/activity-registrations/${registrationId}/status`, null, {
      params: { status }
    })

    if (response.code === 200) {
      ElMessage.success('Status updated successfully')
      loadRegistrations()
      loadStatistics()
    }
  } catch (error) {
    console.error('Failed to update status:', error)
    ElMessage.error('Failed to update status')
  }
}

const cancelRegistration = async (registrationId) => {
  try {
    await ElMessageBox.confirm('Are you sure to cancel this registration?', 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/activity-registrations/${registrationId}`)

    if (response.code === 200) {
      ElMessage.success('Registration cancelled successfully')
      loadRegistrations()
      loadStatistics()
      if (detailDialogVisible.value) {
        detailDialogVisible.value = false
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel registration:', error)
      ElMessage.error('Failed to cancel registration')
    }
  }
}

const deleteRegistration = async (registration) => {
  try {
    await ElMessageBox.confirm(
        `Are you sure to delete registration for "${registration.activityTitle}"?`,
        'Confirm',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'danger'
        }
    )

    const response = await request.delete(`/api/admin/activity-registrations/${registration.registrationId}`)

    if (response.code === 200) {
      ElMessage.success('Registration deleted successfully')
      loadRegistrations()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete registration:', error)
      ElMessage.error('Failed to delete registration')
    }
  }
}

const batchCancel = async () => {
  try {
    await ElMessageBox.confirm(
        `Are you sure to cancel ${selectedRegistrations.value.length} registrations?`,
        'Confirm',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
    )

    const registrationIds = selectedRegistrations.value.map(r => r.registrationId)
    const response = await request.delete('/api/admin/activity-registrations/batch', {
      data: { registrationIds }
    })

    if (response.code === 200) {
      ElMessage.success('Batch cancellation successful')
      loadRegistrations()
      loadStatistics()
      selectedRegistrations.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to batch cancel:', error)
      ElMessage.error('Failed to batch cancel')
    }
  }
}

const batchMarkAttended = async () => {
  try {
    await ElMessageBox.confirm(
        `Are you sure to mark ${selectedRegistrations.value.length} registrations as attended?`,
        'Confirm',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'success'
        }
    )

    const registrationIds = selectedRegistrations.value.map(r => r.registrationId)
    const response = await request.put('/api/admin/activity-registrations/batch/status', {
      registrationIds,
      status: 'attended'
    })

    if (response.code === 200) {
      ElMessage.success('Batch update successful')
      loadRegistrations()
      loadStatistics()
      selectedRegistrations.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to batch update:', error)
      ElMessage.error('Failed to batch update')
    }
  }
}

const showAddDialog = () => {
  addForm.value = {
    activityId: null,
    userId: null,
    notes: ''
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  if (!addFormRef.value) return

  try {
    const valid = await addFormRef.value.validate()
    if (!valid) return

    addLoading.value = true

    const response = await request.post('/api/admin/activity-registrations/manual', addForm.value)

    if (response.code === 200) {
      ElMessage.success('Registration added successfully')
      addDialogVisible.value = false
      loadRegistrations()
      loadStatistics()
    }
  } catch (error) {
    console.error('Failed to add registration:', error)
    ElMessage.error('Failed to add registration')
  } finally {
    addLoading.value = false
  }
}

const exportData = async () => {
  try {
    exportLoading.value = true
    const params = { ...filterForm }

    // Filter empty values
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key]
      }
    })

    const response = await request.get('/api/admin/activity-registrations/export', { params })

    if (response.code === 200) {
      ElMessage.success('Export successful')
      // Here you would typically trigger a download
      console.log('Download URL:', response.data)
    }
  } catch (error) {
    console.error('Failed to export data:', error)
    ElMessage.error('Failed to export data')
  } finally {
    exportLoading.value = false
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

const handleImageError = (event) => {
  event.target.style.display = 'none'
  const nocover = event.target.parentNode.querySelector('.no-cover')
  if (nocover) {
    nocover.style.display = 'flex'
  }
}

const getStatusType = (status) => {
  const typeMap = {
    registered: 'success',
    cancelled: 'danger',
    attended: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    registered: 'Registered',
    cancelled: 'Cancelled',
    attended: 'Attended'
  }
  return textMap[status] || status
}

const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('MM-DD HH:mm') : '-'
}

// Lifecycle
onMounted(() => {
  loadRegistrations()
  loadStatistics()
})
</script>

<style scoped>
/* Global Styles */
.registration-manager {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
  padding-bottom: 40px;
  width: 100%;
  max-width: 100vw;
  box-sizing: border-box;
}

/* 确保所有元素都是透明背景，移除多余的白色背景 */
.registration-manager,
.registration-manager .page-header,
.registration-manager .header-content,
.registration-manager .header-left,
.registration-manager .page-title,
.registration-manager .page-subtitle,
.registration-manager .header-actions,
.registration-manager .statistics-section,
.registration-manager .filter-section,
.registration-manager .table-section {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
}

/* 确保所有section不会超出视口 */
.statistics-section,
.filter-section,
.table-section {
  position: relative;
  margin: 0 24px 24px;
  z-index: 1;
  width: calc(78vw - 48px);
  overflow: hidden;
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
  max-width: 100vw;
  box-sizing: border-box;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
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

/* Statistics Section */

.stats-background {
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

.stats-content {
  position: relative;
  padding: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.stat-card {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid rgba(148, 163, 184, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 30px;
  height: 30px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.registered {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.attended {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon.cancelled {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Filter Section */

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
.modern-number-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-input :deep(.el-input__wrapper:hover),
.modern-select :deep(.el-select__wrapper:hover),
.modern-number-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-input :deep(.el-input__wrapper.is-focus),
.modern-select :deep(.el-select__wrapper.is-focus),
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.results-info {
  flex: 1;
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

.table-actions {
  display: flex;
  gap: 12px;
}

.batch-button {
  border-radius: 12px;
  padding: 8px 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.table-container {
  border-radius: 12px;
  overflow: hidden;
  overflow-x: hidden;
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  width: 100%;
  max-width: 100%;
}

/* Modern Table Styles */
.modern-table {
  border-radius: 0;
  width: 100%;
  table-layout: fixed;
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

/* Table Cell Content */
.activity-info,
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.activity-image {
  width: 50px;
  height: 30px;
  border-radius: 6px;
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
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.activity-details,
.user-details {
  flex: 1;
}

.activity-title,
.user-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
  margin-bottom: 4px;
}

.activity-meta,
.user-meta {
  font-size: 12px;
  color: #64748b;
}

.user-avatar {
  border: 2px solid rgba(102, 126, 234, 0.1);
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

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}

.no-contact {
  font-size: 12px;
  color: #94a3b8;
  font-style: italic;
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

.detail-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #1e293b;
  padding-bottom: 8px;
  border-bottom: 2px solid #e2e8f0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 10px;
}

.info-label {
  font-weight: 600;
  color: #64748b;
  min-width: 120px;
}

.info-value {
  color: #1e293b;
  font-weight: 500;
}

.activity-detail,
.user-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-image-large {
  max-width: 300px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.activity-image-large img {
  width: 100%;
  height: auto;
  display: block;
}

.user-avatar-large {
  border: 3px solid rgba(102, 126, 234, 0.2);
  margin-bottom: 16px;
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

/* Form Styles */
.modern-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
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

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
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

  .statistics-section,
  .filter-section,
  .table-section {
    margin: 0 16px 16px;
    max-width: calc(100vw - 32px);
  }

  .stats-content,
  .filter-content,
  .table-content {
    padding: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
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

  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
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

  .info-grid {
    grid-template-columns: 1fr;
  }

  .dialog-footer {
    padding: 16px 20px;
    flex-direction: column;
  }

  .dialog-footer .el-button {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .page-title {
    font-size: 24px;
  }

  .statistics-section,
  .filter-section,
  .table-section {
    margin: 0 8px 16px;
    max-width: calc(100vw - 16px);
  }

  .stats-content,
  .filter-content,
  .table-content {
    padding: 16px;
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

  .activity-image {
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

/* Animation */
.registration-manager {
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

.statistics-section,
.filter-section,
.table-section {
  animation: slideUp 0.6s ease-out;
  animation-fill-mode: both;
}

.statistics-section {
  animation-delay: 0.1s;
}

.filter-section {
  animation-delay: 0.2s;
}

.table-section {
  animation-delay: 0.3s;
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
.statistics-section:hover .stats-background,
.filter-section:hover .filter-background,
.table-section:hover .table-background {
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stats-background,
.filter-background,
.table-background {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>