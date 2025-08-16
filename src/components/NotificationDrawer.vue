<template>
  <el-drawer
      v-model="drawerVisible"
      title="Message notifications"
      direction="rtl"
      size="400px"
      :before-close="handleClose"
      class="notification-drawer"
  >
    <template #header>
      <div class="drawer-header">
        <h3 class="drawer-title">
          Message notifications
          <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
        </h3>
        <div class="header-actions">
          <el-button
              size="small"
              @click="toggleFilter"
              :type="showOnlyUnread ? 'primary' : 'default'"
              :icon="Filter"
          >
            {{ showOnlyUnread ? 'Show all' : 'Just not read' }}
          </el-button>
          <el-button
              v-if="unreadCount > 0"
              type="primary"
              size="small"
              @click="handleMarkAllAsRead"
              :loading="markAllLoading"
              :icon="Check"
          >
            Read All
          </el-button>
        </div>
      </div>
    </template>

    <div class="notification-content">
      <!-- 加载状态 -->
      <div v-if="loading && messages.length === 0" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 消息列表 -->
      <div v-else-if="filteredMessages.length > 0" class="message-list">
        <div
            v-for="message in filteredMessages"
            :key="message.id"
            class="message-item"
            :class="{
            'message-unread': message.isRead === 0,
            'message-read': message.isRead === 1
          }"
            @click="handleMessageClick(message)"
        >
          <!-- 消息状态指示器 -->
          <div class="message-status-indicator">
            <div v-if="message.isRead === 0" class="status-unread">
              <div class="unread-dot"></div>
            </div>
            <div v-else class="status-read">
              <el-icon class="read-icon"><CircleCheck /></el-icon>
            </div>
          </div>

          <div class="message-content">
            <div class="message-header">
              <div class="message-info">
                <span class="message-type" :class="getTypeClass(message.type)">
                  {{ getTypeText(message.type) }}
                </span>
                <span class="message-time" :class="{ 'time-read': message.isRead === 1 }">
                  {{ formatTime(message.createdAt) }}
                </span>
              </div>
              <div class="message-actions" @click.stop>
                <el-button
                    v-if="message.isRead === 0"
                    type="primary"
                    size="small"
                    text
                    @click="handleMarkAsRead(message.id)"
                    :loading="markReadLoading[message.id]"
                    :icon="Check"
                >
                  Read
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    text
                    @click="handleDeleteMessage(message.id)"
                    :loading="deleteLoading[message.id]"
                    :icon="Delete"
                >
                  Delete
                </el-button>
              </div>
            </div>

            <h4 class="message-title" :class="{ 'title-read': message.isRead === 1 }">
              {{ message.title }}
            </h4>

            <p class="message-preview" :class="{ 'preview-read': message.isRead === 1 }">
              {{ truncateText(message.content, 100) }}
            </p>

            <!-- 已读时间显示 -->
            <div v-if="message.isRead === 1 && message.readTime" class="read-time">
              <el-icon class="read-time-icon"><Clock /></el-icon>
              <span>{{ formatDateTime(message.readTime) }} Read</span>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore && !showOnlyUnread" class="load-more">
          <el-button
              @click="loadMore"
              :loading="loadingMore"
              text
              type="primary"
          >
            Load more
          </el-button>
        </div>

        <!-- 没有更多数据 -->
        <div v-else-if="messages.length > 0 && !showOnlyUnread" class="no-more">
          <span>No more news</span>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty :description="getEmptyDescription()">
          <el-button type="primary" @click="refreshMessages">flushed</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 消息详情对话框 -->
    <el-dialog
        v-model="detailDialogVisible"
        :title="selectedMessage?.title"
        width="600px"
        :close-on-click-modal="false"
    >
      <div v-if="selectedMessage" class="message-detail">
        <div class="detail-meta">
          <div class="detail-info">
            <span class="detail-type" :class="getTypeClass(selectedMessage.type)">
              {{ getTypeText(selectedMessage.type) }}
            </span>
            <span class="detail-time">{{ formatDateTime(selectedMessage.createdAt) }}</span>
          </div>
          <div class="detail-status">
            <el-tag
                :type="selectedMessage.isRead === 1 ? 'success' : 'warning'"
                size="small"
            >
              {{ selectedMessage.isRead === 1 ? 'Read' : 'Not read' }}
            </el-tag>
          </div>
        </div>

        <div class="detail-content" v-html="formatContent(selectedMessage.content)"></div>

        <div class="detail-footer">
          <div v-if="selectedMessage.senderName" class="detail-sender">
            <el-icon><User /></el-icon>
            <span>sender：{{ selectedMessage.senderName }}</span>
          </div>

          <div v-if="selectedMessage.isRead === 1 && selectedMessage.readTime" class="detail-read-time">
            <el-icon><Clock /></el-icon>
            <span>{{ formatDateTime(selectedMessage.readTime) }} Read</span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">Shut down</el-button>
          <el-button
              v-if="selectedMessage?.isRead === 0"
              type="primary"
              @click="handleMarkAsReadInDetail"
              :loading="markReadLoading[selectedMessage?.id]"
              :icon="Check"
          >
            mark read
          </el-button>
        </div>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  Delete,
  Filter,
  CircleCheck,
  Clock,
  User
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'update-unread-count'])

// 响应式数据
const messages = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const markAllLoading = ref(false)
const markReadLoading = reactive({})
const deleteLoading = reactive({})
const showOnlyUnread = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const hasMore = computed(() => messages.value.length < total.value)
const unreadCount = computed(() => messages.value.filter(msg => msg.isRead === 0).length)

// 过滤后的消息列表
const filteredMessages = computed(() => {
  if (showOnlyUnread.value) {
    return messages.value.filter(msg => msg.isRead === 0)
  }
  return messages.value
})

// 消息详情相关
const detailDialogVisible = ref(false)
const selectedMessage = ref(null)

// 计算属性
const drawerVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 方法
const loadMessages = async (page = 1, append = false) => {
  try {
    if (!append) {
      loading.value = true
    } else {
      loadingMore.value = true
    }

    const response = await request.get('/api/user/messages', {
      params: {
        page,
        size: pageSize.value
      }
    })

    if (response.code === 200) {
      const data = response.data
      if (append) {
        messages.value = [...messages.value, ...data.records]
      } else {
        messages.value = data.records
      }
      total.value = data.total
      currentPage.value = page
    } else {
      ElMessage.error(response.message || 'Failed to get messages')
    }
  } catch (error) {
    console.error('Failed to get messages:', error)
    ElMessage.error('Failed to get messages')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  if (hasMore.value && !loadingMore.value) {
    loadMessages(currentPage.value + 1, true)
  }
}

const refreshMessages = () => {
  currentPage.value = 1
  showOnlyUnread.value = false
  loadMessages(1, false)
}

const toggleFilter = () => {
  showOnlyUnread.value = !showOnlyUnread.value
}

const getEmptyDescription = () => {
  if (showOnlyUnread.value) {
    return 'There is no unread news yet'
  }
  return 'No news notifications yet'
}

const handleMarkAsRead = async (messageId) => {
  try {
    markReadLoading[messageId] = true

    const response = await request.post(`/api/user/messages/${messageId}/read`)

    if (response.code === 200) {
      // 更新本地状态
      const message = messages.value.find(msg => msg.id === messageId)
      if (message) {
        message.isRead = 1
        message.readTime = new Date().toISOString()
      }

      // 更新选中的消息状态（如果在详情页）
      if (selectedMessage.value && selectedMessage.value.id === messageId) {
        selectedMessage.value.isRead = 1
        selectedMessage.value.readTime = new Date().toISOString()
      }

      ElMessage.success('Mark successfully read')
      emit('update-unread-count')
    } else {
      ElMessage.error(response.message || 'Mark read failure')
    }
  } catch (error) {
    console.error('Mark read failure:', error)
    ElMessage.error('Mark read failure')
  } finally {
    markReadLoading[messageId] = false
  }
}

const handleMarkAsReadInDetail = () => {
  if (selectedMessage.value) {
    handleMarkAsRead(selectedMessage.value.id)
  }
}

const handleMarkAllAsRead = async () => {
  try {
    await ElMessageBox.confirm('Determine if you want to mark all messages as read？', 'Confirm the action', {
      confirmButtonText: 'Are you sure',
      cancelButtonText: 'Cancel',
      type: 'info'
    })

    markAllLoading.value = true

    const response = await request.post('/api/user/messages/read-all')

    if (response.code === 200) {
      // 更新本地状态
      messages.value.forEach(message => {
        message.isRead = 1
        message.readTime = new Date().toISOString()
      })

      ElMessage.success('All marks are read successfully')
      emit('update-unread-count')
    } else {
      ElMessage.error(response.message || 'Bulk marking failed to read')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Bulk marking failed to read:', error)
      ElMessage.error('Bulk marking failed to read')
    }
  } finally {
    markAllLoading.value = false
  }
}

const handleDeleteMessage = async (messageId) => {
  try {
    await ElMessageBox.confirm('Are you sure you want to delete this message?？', 'Confirm the deletion', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    deleteLoading[messageId] = true

    const response = await request.post(`/api/user/messages/${messageId}/delete`)

    if (response.code === 200) {
      // 从列表中移除消息
      const index = messages.value.findIndex(msg => msg.id === messageId)
      if (index > -1) {
        messages.value.splice(index, 1)
        total.value--
      }

      ElMessage.success('Delete successful')
      emit('update-unread-count')
    } else {
      ElMessage.error(response.message || 'Delete failed')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Deleting messages failed:', error)
      ElMessage.error('Delete failed')
    }
  } finally {
    deleteLoading[messageId] = false
  }
}

const handleMessageClick = async (message) => {
  selectedMessage.value = message
  detailDialogVisible.value = true

  // 如果消息未读，自动标记为已读
  if (message.isRead === 0) {
    await handleMarkAsRead(message.id)
  }
}

const handleClose = () => {
  drawerVisible.value = false
}

// 工具方法
const getTypeClass = (type) => {
  const typeMap = {
    'system': 'type-system',
    'activity': 'type-activity',
    'notice': 'type-notice'
  }
  return typeMap[type] || 'type-default'
}

const getTypeText = (type) => {
  const typeMap = {
    'system': 'system',
    'activity': 'activity',
    'notice': 'notice'
  }
  return typeMap[type] || type
}

const formatTime = (dateTime) => {
  if (!dateTime) return ''

  const date = new Date(dateTime)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) {
    return 'just'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}minutes ago`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}hours ago`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}days ago`
  } else {
    return date.toLocaleDateString('zh-CN', {
      month: '2-digit',
      day: '2-digit'
    })
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''

  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const formatContent = (content) => {
  if (!content) return ''
  // 简单的换行处理
  return content.replace(/\n/g, '<br>')
}

// 监听抽屉显示状态
watch(drawerVisible, (newVal) => {
  if (newVal) {
    refreshMessages()
  }
})
</script>

<style scoped>
.notification-drawer {
  --el-drawer-padding-primary: 0;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #fafafa;
}

.drawer-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 10px;
}

.unread-badge {
  background: #ef4444;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.notification-content {
  height: calc(100vh - 80px);
  overflow-y: auto;
  background: #f9fafb;
}

.loading-container {
  padding: 20px;
}

.message-list {
  padding: 0;
}

.message-item {
  display: flex;
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

/* 未读消息样式 */
.message-item.message-unread {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  border-left: 4px solid #3b82f6;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.message-item.message-unread:hover {
  background: linear-gradient(135deg, #bfdbfe 0%, #93c5fd 100%);
  transform: translateX(2px);
}

/* 已读消息样式 */
.message-item.message-read {
  background: #f8f9fa;
  border-left: 4px solid #e5e7eb;
  opacity: 0.75;
}

.message-item.message-read:hover {
  background: #f1f3f4;
  opacity: 0.9;
}

.message-status-indicator {
  width: 24px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 4px;
  margin-right: 12px;
}

.status-unread {
  position: relative;
}

.unread-dot {
  width: 12px;
  height: 12px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse 2s infinite;
  box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
  }
  70% {
    box-shadow: 0 0 0 8px rgba(239, 68, 68, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
  }
}

.status-read {
  color: #10b981;
  font-size: 16px;
}

.read-icon {
  opacity: 0.8;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.message-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-type {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.type-system {
  background: #6b7280;
}

.type-activity {
  background: #10b981;
}

.type-notice {
  background: #f59e0b;
}

.type-default {
  background: #8b5cf6;
}

.message-time {
  font-size: 12px;
  color: #374151;
  font-weight: 500;
}

.message-time.time-read {
  color: #9ca3af;
  font-weight: 400;
}

.message-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.message-item:hover .message-actions {
  opacity: 1;
}

.message-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
  line-height: 1.4;
  transition: all 0.3s ease;
}

/* 已读消息标题添加删除线效果 */
.message-title.title-read {
  text-decoration: line-through;
  text-decoration-color: #9ca3af;
  text-decoration-thickness: 1px;
  color: #6b7280;
  font-weight: 400;
}

.message-preview {
  font-size: 13px;
  color: #374151;
  line-height: 1.4;
  margin: 0 0 8px 0;
  transition: all 0.3s ease;
}

.message-preview.preview-read {
  color: #9ca3af;
  font-style: italic;
}

.read-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #10b981;
  margin-top: 4px;
}

.read-time-icon {
  font-size: 12px;
}

.load-more,
.no-more {
  padding: 16px 20px;
  text-align: center;
}

.no-more span {
  font-size: 14px;
  color: #9ca3af;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

/* 消息详情样式 */
.message-detail {
  max-height: 500px;
  overflow-y: auto;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.detail-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-type {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.detail-time {
  font-size: 14px;
  color: #6b7280;
}

.detail-status {
  display: flex;
  align-items: center;
}

.detail-content {
  font-size: 14px;
  line-height: 1.6;
  color: #374151;
  margin-bottom: 16px;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
  font-size: 14px;
  color: #6b7280;
}

.detail-sender,
.detail-read-time {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-read-time {
  color: #10b981;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.el-drawer) {
    width: 100% !important;
  }

  .message-actions {
    opacity: 1;
  }

  .message-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .message-actions {
    align-self: flex-end;
  }

  .header-actions {
    flex-direction: column;
    gap: 6px;
  }

  .detail-footer {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 0;
}

:deep(.el-drawer__body) {
  padding: 0;
}

:deep(.el-empty) {
  padding: 40px 20px;
}

:deep(.el-skeleton) {
  padding: 0;
}

/* 增强滚动条样式 */
.notification-content::-webkit-scrollbar {
  width: 6px;
}

.notification-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.notification-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.notification-content::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>