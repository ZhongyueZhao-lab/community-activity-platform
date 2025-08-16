<template>
  <div class="login-container">
    <!-- 页面背景装饰 -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <div class="login-form-container modern-card">
      <div class="login-header">
        <div class="login-icon">
          <el-icon><UserFilled /></el-icon>
        </div>
        <h2 class="login-title">
          <span class="title-gradient">Welcome Back</span>
        </h2>
        <p class="login-subtitle">Sign in to your account to continue</p>
      </div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form modern-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <div class="input-wrapper">
            <div class="input-icon">
              <el-icon><User /></el-icon>
            </div>
            <el-input
                v-model="loginForm.username"
                placeholder="Username / Phone / Email"
                size="large"
                clearable
                class="modern-input"
            />
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <div class="input-wrapper">
            <div class="input-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="Enter your password"
                size="large"
                show-password
                clearable
                class="modern-input"
            />
          </div>
        </el-form-item>

        <el-form-item class="login-actions">
          <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
          >
            <el-icon v-if="!loading"><Right /></el-icon>
            <span>{{ loading ? 'Signing In...' : 'Sign In' }}</span>
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span class="footer-text">Don't have an account?</span>
          <el-button type="primary" link class="register-link" @click="goToRegister">
            <span>Create Account</span>
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
import { User, Lock, UserFilled, Right, ArrowRight, House } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref()

// 加载状态
const loading = ref(false)

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: 'Please enter username/phone/email', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    const result = await userStore.login({
      username: loginForm.username.trim(),
      password: loginForm.password
    })

    if (result.success) {
      ElMessage.success('Login successful')

      // 根据用户角色跳转到相应页面
      if (userStore.isAdmin) {
        router.push('/admin/dashboard')
      } else {
        router.push('/home')
      }
    } else {
      ElMessage.error(result.message || 'Login failed')
    }
  } catch (error) {
    console.error('Login failed:', error)
    ElMessage.error('Login failed, please try again')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}

// 返回首页
const goToHome = () => {
  router.push('/home')
}
</script>

<style scoped>
/* 全局样式 */
.login-container {
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

/* 现代化卡片 */
.modern-card {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 48px;
  width: 100%;
  max-width: 440px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: white;
  font-size: 36px;
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.3);
  animation: pulse-icon 3s ease-in-out infinite;
}

@keyframes pulse-icon {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.login-title {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 12px 0;
  line-height: 1.2;
}

.title-gradient {
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.login-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  font-weight: 400;
  line-height: 1.5;
}

/* 现代化表单 */
.modern-form {
  width: 100%;
}

.modern-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.modern-form :deep(.el-form-item__error) {
  margin-top: 8px;
  color: #ef4444;
  font-size: 13px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 16px;
  z-index: 2;
  color: #94a3b8;
  font-size: 18px;
  transition: all 0.3s ease;
}

.modern-input {
  width: 100%;
}

.modern-input :deep(.el-input__wrapper) {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 2px solid #e2e8f0;
  padding-left: 48px;
  height: 56px;
  width: 100%;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.8);
}

.modern-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.15);
  transform: translateY(-1px);
}

.modern-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.modern-input :deep(.el-input__wrapper.is-focus) + .input-icon,
.modern-input :deep(.el-input__wrapper:hover) + .input-icon {
  color: #667eea;
  transform: scale(1.1);
}

.modern-input :deep(.el-input__inner) {
  padding-left: 16px;
  font-size: 16px;
  color: #1e293b;
  font-weight: 500;
}

.modern-input :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
  font-weight: 400;
}

/* 登录按钮 */
.login-actions {
  margin-bottom: 24px;
}

.login-button {
  width: 100%;
  height: 56px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
}

.login-button:active {
  transform: translateY(0px);
}

.login-button:before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-button:hover:before {
  left: 100%;
}

/* 页脚链接 */
.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
  padding: 20px 0;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.footer-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.register-link {
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.register-link:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  transform: translateY(-1px);
}

/* 返回首页按钮 */
.back-to-home {
  text-align: center;
}

.home-button {
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.2);
  color: #64748b;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.home-button:hover {
  background: rgba(148, 163, 184, 0.2);
  border-color: rgba(148, 163, 184, 0.3);
  transform: translateY(-1px);
  color: #475569;
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
  left: 20%;
  animation-delay: 10s;
}

@keyframes float-circle {
  0%, 100% {
    transform: translateY(0px) translateX(0px) rotate(0deg);
    opacity: 0.3;
  }
  25% {
    transform: translateY(-20px) translateX(10px) rotate(90deg);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-10px) translateX(-15px) rotate(180deg);
    opacity: 0.3;
  }
  75% {
    transform: translateY(15px) translateX(5px) rotate(270deg);
    opacity: 0.4;
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }

  .modern-card {
    padding: 32px 24px;
    border-radius: 20px;
  }

  .login-icon {
    width: 64px;
    height: 64px;
    font-size: 28px;
    margin-bottom: 20px;
  }

  .login-title {
    font-size: 28px;
  }

  .login-subtitle {
    font-size: 14px;
  }

  .modern-input :deep(.el-input__wrapper) {
    height: 48px;
    padding-left: 44px;
  }

  .input-icon {
    left: 14px;
    font-size: 16px;
  }

  .login-button {
    height: 48px;
    font-size: 15px;
  }

  .login-footer {
    flex-direction: column;
    gap: 12px;
  }

  .floating-circle {
    display: none;
  }
}

@media (max-width: 360px) {
  .modern-card {
    padding: 24px 16px;
  }

  .login-title {
    font-size: 24px;
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

/* 加载状态动画 */
.login-button.is-loading {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
}

/* 增强的悬停效果 */
.modern-card:hover .login-icon {
  transform: scale(1.05);
  box-shadow: 0 16px 32px rgba(102, 126, 234, 0.4);
}

/* 输入焦点时的动画 */
.modern-input:focus-within .input-icon {
  color: #667eea;
  transform: scale(1.1);
}

/* 自定义滚动条（如果需要） */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(148, 163, 184, 0.1);
}

::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}
</style>