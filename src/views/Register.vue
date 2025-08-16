<template>
  <div class="register-container">
    <!-- 页面背景装饰 -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <div class="register-layout modern-card">
      <!-- 左侧介绍区域 -->
      <div class="register-left">
        <div class="brand-section">
          <div class="brand-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <h2 class="brand-title">
            <span class="title-gradient">Join Our Community</span>
          </h2>
          <p class="brand-subtitle">Discover amazing events, meet new people, and create unforgettable memories</p>
        </div>

        <div class="features-list">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="feature-content">
              <h4>Discover Events</h4>
              <p>Find exciting events happening around you</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="feature-content">
              <h4>Meet People</h4>
              <p>Connect with like-minded individuals</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="feature-content">
              <h4>Create Memories</h4>
              <p>Build lasting experiences and friendships</p>
            </div>
          </div>
        </div>

        <div class="testimonial">
          <div class="testimonial-content">
            <p>"This platform has helped me discover so many amazing events and meet wonderful people in my community!"</p>
            <div class="testimonial-author">
              <div class="author-avatar">
                <el-icon><User /></el-icon>
              </div>
              <div class="author-info">
                <span class="author-name">Sarah Johnson</span>
                <span class="author-role">Community Member</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="register-right">
        <div class="register-header">
          <div class="register-icon">
            <el-icon><Plus /></el-icon>
          </div>
          <h2 class="register-title">Create Account</h2>
          <p class="register-subtitle">Start your journey with us today</p>
        </div>

        <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form modern-form"
            @submit.prevent="handleRegister"
        >
          <div class="form-row">
            <el-form-item prop="username" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><User /></el-icon>
                </div>
                <el-input
                    v-model="registerForm.username"
                    placeholder="Username"
                    size="large"
                    clearable
                    class="modern-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="nickname" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Avatar /></el-icon>
                </div>
                <el-input
                    v-model="registerForm.nickname"
                    placeholder="Display Name"
                    size="large"
                    clearable
                    class="modern-input"
                />
              </div>
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item prop="phone" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Phone /></el-icon>
                </div>
                <el-input
                    v-model="registerForm.phone"
                    placeholder="Phone (optional)"
                    size="large"
                    clearable
                    class="modern-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="email" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Message /></el-icon>
                </div>
                <el-input
                    v-model="registerForm.email"
                    placeholder="Email (optional)"
                    size="large"
                    clearable
                    class="modern-input"
                />
              </div>
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item prop="password" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Lock /></el-icon>
                </div>
                <el-input
                    v-model="registerForm.password"
                    type="password"
                    placeholder="Password"
                    size="large"
                    show-password
                    clearable
                    class="modern-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="confirmPassword" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Lock /></el-icon>
                </div>
                <el-input
                    v-model="registerForm.confirmPassword"
                    type="password"
                    placeholder="Confirm Password"
                    size="large"
                    show-password
                    clearable
                    class="modern-input"
                />
              </div>
            </el-form-item>
          </div>

          <div class="form-notice">
            <div class="notice-icon">
              <el-icon><InfoFilled /></el-icon>
            </div>
            <span class="notice-text">Please provide at least phone number or email address</span>
          </div>

          <el-form-item class="register-actions">
            <el-button
                type="primary"
                size="large"
                class="register-button"
                :loading="loading"
                @click="handleRegister"
            >
              <el-icon v-if="!loading"><Check /></el-icon>
              <span>{{ loading ? 'Creating Account...' : 'Create Account' }}</span>
            </el-button>
          </el-form-item>

          <div class="register-footer">
            <span class="footer-text">Already have an account?</span>
            <el-button type="primary" link class="login-link" @click="goToLogin">
              <span>Sign In</span>
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>

          <div class="back-to-home">
            <el-button @click="goToHome" class="home-button">
              <el-icon><House /></el-icon>
              <span>Back to Home</span>
            </el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 装饰性元素 -->
    <div class="floating-elements">
      <div class="floating-circle circle-1"></div>
      <div class="floating-circle circle-2"></div>
      <div class="floating-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import {
  User, Lock, Plus, Check, ArrowRight, House, Avatar, Phone, Message, InfoFilled,
  Calendar, Star
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref()

// 加载状态
const loading = ref(false)

// 表单数据
const registerForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 自定义验证函数
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('Invalid phone number format'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('Invalid email format'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 50, message: 'Username must be 3-50 characters long', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: 'Please enter display name', trigger: 'blur' },
    { max: 50, message: 'Display name cannot exceed 50 characters', trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, max: 100, message: 'Password must be 6-100 characters long', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    // 检查手机号和邮箱至少填写一个
    if (!registerForm.phone.trim() && !registerForm.email.trim()) {
      ElMessage.warning('Please provide at least phone number or email address')
      return
    }

    loading.value = true

    const result = await userStore.register({
      username: registerForm.username.trim(),
      nickname: registerForm.nickname.trim(),
      phone: registerForm.phone.trim() || null,
      email: registerForm.email.trim() || null,
      password: registerForm.password
    })

    if (result.success) {
      ElMessage.success('Registration successful, please sign in')
      router.push('/login')
    } else {
      ElMessage.error(result.message || 'Registration failed')
    }
  } catch (error) {
    console.error('Registration failed:', error)
    ElMessage.error('Registration failed, please try again')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}

// 返回首页
const goToHome = () => {
  router.push('/home')
}
</script>

<style scoped>
/* 全局样式 */
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  overflow: hidden;
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
  width: 1200px;
  height: 1200px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0px) rotate(0deg); }
  50% { transform: translate(-50%, -50%) translateY(-30px) rotate(180deg); }
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
      135deg,
      rgba(255, 255, 255, 0.1) 0%,
      transparent 50%,
      rgba(255, 255, 255, 0.05) 100%
  );
}

/* 主布局 */
.register-layout {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  width: 100%;
  max-width: 1100px;
  min-height: 600px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-layout:hover {
  transform: translateY(-4px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

/* 左侧介绍区域 */
.register-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.register-left::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="0.5" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="0.5" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.brand-section {
  position: relative;
  margin-bottom: 48px;
  text-align: center;
}

.brand-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  font-size: 36px;
  backdrop-filter: blur(10px);
  animation: pulse-icon 3s ease-in-out infinite;
}

@keyframes pulse-icon {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.brand-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.title-gradient {
  color: white;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 18px;
  opacity: 0.9;
  line-height: 1.6;
  margin: 0;
}

.features-list {
  position: relative;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(4px);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.feature-content h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: white;
}

.feature-content p {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
  line-height: 1.4;
}

.testimonial {
  position: relative;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  backdrop-filter: blur(10px);
}

.testimonial-content p {
  font-size: 16px;
  font-style: italic;
  line-height: 1.6;
  margin: 0 0 20px 0;
  opacity: 0.9;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.author-role {
  font-size: 12px;
  opacity: 0.7;
}

/* 右侧表单区域 */
.register-right {
  flex: 1;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  font-size: 24px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.register-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

/* 表单样式 */
.modern-form {
  width: 100%;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-item {
  flex: 1;
  margin-bottom: 0 !important;
}

.modern-form :deep(.el-form-item__error) {
  margin-top: 4px;
  color: #ef4444;
  font-size: 12px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 12px;
  z-index: 2;
  color: #94a3b8;
  font-size: 16px;
  transition: all 0.3s ease;
}

.modern-input {
  width: 100%;
}

.modern-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  padding-left: 40px;
  height: 44px;
  width: 100%;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.8);
}

.modern-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modern-input :deep(.el-input__inner) {
  padding-left: 12px;
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.modern-input :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
  font-weight: 400;
}

/* 表单提示 */
.form-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: rgba(59, 130, 246, 0.05);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 8px;
  margin-bottom: 20px;
}

.notice-icon {
  color: #3b82f6;
  font-size: 14px;
  flex-shrink: 0;
}

.notice-text {
  color: #1e40af;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.4;
}

/* 注册按钮 */
.register-actions {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 页脚链接 */
.register-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
  padding: 16px 0;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.footer-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.login-link {
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

/* 返回首页按钮 */
.back-to-home {
  text-align: center;
}

.home-button {
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.2);
  color: #64748b;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.home-button:hover {
  background: rgba(148, 163, 184, 0.2);
  transform: translateY(-1px);
}

/* 装饰性浮动元素 */
.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.floating-circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: float-circle 15s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 5s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 30%;
  animation-delay: 10s;
}

@keyframes float-circle {
  0%, 100% {
    transform: translateY(0px) translateX(0px) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px) translateX(10px) rotate(180deg);
    opacity: 0.5;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .register-layout {
    flex-direction: column;
    max-width: 500px;
  }

  .register-left {
    order: 2;
    padding: 32px;
  }

  .register-right {
    order: 1;
    padding: 32px;
  }

  .brand-section {
    margin-bottom: 32px;
  }

  .features-list {
    margin-bottom: 24px;
  }

  .feature-item {
    margin-bottom: 16px;
    padding: 12px;
  }
}

@media (max-width: 768px) {
  .register-container {
    padding: 16px;
  }

  .register-layout {
    border-radius: 20px;
    min-height: auto;
  }

  .register-left,
  .register-right {
    padding: 24px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-item {
    margin-bottom: 16px !important;
  }

  .brand-title {
    font-size: 28px;
  }

  .brand-subtitle {
    font-size: 16px;
  }

  .feature-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .testimonial-author {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .register-left,
  .register-right {
    padding: 20px;
  }

  .register-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }

  .register-title {
    font-size: 24px;
  }

  .modern-input :deep(.el-input__wrapper) {
    height: 40px;
    padding-left: 36px;
  }

  .input-icon {
    left: 10px;
    font-size: 14px;
  }

  .register-button {
    height: 44px;
    font-size: 15px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-button) {
  font-weight: 600;
}

:deep(.el-input) {
  font-weight: 500;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
}
</style>