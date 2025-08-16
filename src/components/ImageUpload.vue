<template>
  <div class="image-upload">
    <el-upload
        ref="uploadRef"
        class="upload-demo"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="handleProgress"
        :file-list="fileList"
        :limit="limit"
        :accept="accept"
        :show-file-list="showFileList"
        :auto-upload="autoUpload"
        :disabled="disabled"
        list-type="picture-card"
    >
      <template #default>
        <div class="upload-trigger">
          <el-icon v-if="!uploading"><Plus /></el-icon>
          <el-progress
              v-if="uploading"
              type="circle"
              :percentage="uploadProgress"
              :width="50"
          />
        </div>
      </template>
      <template #file="{ file }">
        <div class="upload-file">
          <img class="upload-file-img" :src="file.url" alt="" />
          <div class="upload-file-actions">
            <el-icon class="upload-file-action" @click="handlePreview(file)">
              <ZoomIn />
            </el-icon>
            <el-icon class="upload-file-action" @click="handleRemove(file)">
              <Delete />
            </el-icon>
          </div>
        </div>
      </template>
    </el-upload>

    <el-dialog v-model="previewVisible" title="Image Preview" width="60%">
      <img w-full :src="previewImageUrl" alt="Image Preview" style="width: 100%;" />
    </el-dialog>

    <div v-if="showTips" class="upload-tips">
      <p>{{ tips }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, inject } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  limit: {
    type: Number,
    default: 1
  },
  accept: {
    type: String,
    default: '.jpg,.jpeg,.png,.gif,.bmp,.webp'
  },
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024 // 10MB
  },
  showFileList: {
    type: Boolean,
    default: true
  },
  autoUpload: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  tips: {
    type: String,
    default: 'Only JPG, PNG, GIF, BMP, and WEBP formats are supported, and the file size must not exceed 10MB'
  },
  showTips: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'error', 'remove'])

const userStore = useUserStore()
const uploadRef = ref()
const uploading = ref(false)
const uploadProgress = ref(0)
const previewVisible = ref(false)
const previewImageUrl = ref('')

const uploadUrl = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL || ''}/api/admin/upload/image`
})

const uploadHeaders = computed(() => {
  const token = userStore.token
  return token ? { 'Authorization': `Bearer ${token}` } : {}
})

const fileList = ref([])

// 监听 modelValue 变化，更新文件列表
watch(() => props.modelValue, (newValue) => {
  if (newValue && newValue !== getCurrentUrl()) {
    fileList.value = [{
      name: 'image',
      url: newValue,
      status: 'success'
    }]
  } else if (!newValue) {
    fileList.value = []
  }
}, { immediate: true })

const getCurrentUrl = () => {
  return fileList.value.length > 0 ? fileList.value[0].url : ''
}

const beforeUpload = (file) => {
  console.log('开始上传文件:', file.name, '大小:', file.size)

  // 检查用户权限
  if (!userStore.isLoggedIn) {
    ElMessage.error('Please log in first！')
    return false
  }

  if (!userStore.isAdmin) {
    ElMessage.error('权限不足，仅管理员可上传图片！')
    return false
  }

  // 文件类型检查
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('文件格式不支持，请选择图片文件！')
    return false
  }

  // 文件大小检查
  if (file.size > props.maxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize / 1024 / 1024}MB！`)
    return false
  }

  uploading.value = true
  uploadProgress.value = 0

  return true
}

const handleSuccess = (response, file) => {
  console.log('上传成功响应:', response)
  uploading.value = false
  uploadProgress.value = 0

  if (response && response.code === 200) {
    const imageUrl = response.data.imageUrl

    // 更新文件列表
    fileList.value = [{
      name: file.name,
      url: imageUrl,
      status: 'success'
    }]

    // 更新 v-model
    emit('update:modelValue', imageUrl)
    emit('success', response.data)

    ElMessage.success('上传成功')
  } else {
    console.error('上传失败:', response)
    ElMessage.error(response?.message || '上传失败')
    emit('error', response)
  }
}

const handleError = (error, file, fileList) => {
  console.error('上传失败:', error)
  uploading.value = false
  uploadProgress.value = 0

  let errorMessage = '上传失败，请重试'

  try {
    if (error.response && error.response.data) {
      errorMessage = error.response.data.message || errorMessage
    } else if (error.message) {
      errorMessage = error.message
    }
  } catch (e) {
    console.error('解析错误信息失败:', e)
  }

  ElMessage.error(errorMessage)
  emit('error', error)
}

const handleProgress = (event) => {
  uploadProgress.value = Math.round(event.percent)
}

const handlePreview = (file) => {
  previewImageUrl.value = file.url
  previewVisible.value = true
}

const handleRemove = (file) => {
  fileList.value = []
  emit('update:modelValue', '')
  emit('remove', file)
}

const clearFiles = () => {
  fileList.value = []
  emit('update:modelValue', '')
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const submitUpload = () => {
  if (uploadRef.value) {
    uploadRef.value.submit()
  }
}

// 暴露方法给父组件
defineExpose({
  clearFiles,
  submitUpload
})
</script>

<style scoped>
.image-upload {
  display: inline-block;
}

.upload-demo :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.upload-demo :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.upload-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 28px;
  color: #8c939d;
  background-color: #fbfdff;
}

.upload-file {
  position: relative;
  width: 100%;
  height: 100%;
}

.upload-file-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-file-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-file:hover .upload-file-actions {
  opacity: 1;
}

.upload-file-action {
  color: #fff;
  font-size: 20px;
  margin: 0 10px;
  cursor: pointer;
  transition: transform 0.3s;
}

.upload-file-action:hover {
  transform: scale(1.2);
}

.upload-tips {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.upload-tips p {
  margin: 0;
}
</style>