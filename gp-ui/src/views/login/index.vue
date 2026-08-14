<template>
  <div class="login-container">
    <!-- 动态渐变背景 -->
    <div class="wall-bg"></div>
    <div class="wall-grid"></div>

    <!-- 浮动粒子 -->
    <span v-for="i in 20" :key="'p'+i" class="float-particle" :style="particleStyle(i)"></span>

    <!-- 旋转光圈 -->
    <div class="glow-ring ring-1"></div>
    <div class="glow-ring ring-2"></div>

    <!-- 居中登录卡片 -->
    <div class="login-wrapper">
      <!-- 顶部品牌 -->
      <div class="brand-header">
        <div class="brand-logo">
          <el-icon :size="42"><Platform /></el-icon>
        </div>
        <h1 class="brand-title">GP-Framework</h1>
        <p class="brand-subtitle">毕业设计统一开发框架</p>
      </div>

      <div class="login-card">
        <!-- Tab切换 -->
        <div class="form-tabs">
          <div
            class="tab-item"
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >
            登 录
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >
            注 册
          </div>
        </div>

        <!-- 登录表单 -->
        <div v-show="activeTab === 'login'" class="form-body">
          <div class="card-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号和密码</p>
          </div>
          <el-form ref="loginRef" :model="loginForm" :rules="loginRules" size="large">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item>
              <div class="login-options">
                <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
                <el-link type="primary" :underline="false">忘记密码？</el-link>
              </div>
            </el-form-item>
            <el-button
              :loading="loginLoading"
              type="primary"
              size="large"
              class="login-btn"
              @click="handleLogin"
            >
              <span v-if="!loginLoading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form>
        </div>

        <!-- 注册表单 -->
        <div v-show="activeTab === 'register'" class="form-body">
          <div class="card-header">
            <h2>创建账号</h2>
            <p>填写以下信息完成注册</p>
          </div>
          <el-form ref="registerRef" :model="registerForm" :rules="registerRules" size="large">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名（3-20位）"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="nickName">
              <el-input
                v-model="registerForm.nickName"
                placeholder="请输入昵称（选填）"
                :prefix-icon="UserFilled"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码（6-20位）"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleRegister"
              />
            </el-form-item>
            <el-form-item prop="code">
              <div class="captcha-row">
                <el-input
                  v-model="registerForm.code"
                  placeholder="请输入验证码"
                  :prefix-icon="Key"
                  clearable
                  style="flex:1"
                />
                <img
                  class="captcha-img"
                  :src="captchaImg"
                  @click="refreshCaptcha"
                  alt="验证码"
                  title="点击刷新验证码"
                />
              </div>
            </el-form-item>
            <el-button
              :loading="registerLoading"
              type="primary"
              size="large"
              class="login-btn"
              @click="handleRegister"
            >
              <span v-if="!registerLoading">注 册</span>
              <span v-else>注 册 中...</span>
            </el-button>
          </el-form>
        </div>

        <div class="login-tip" v-if="activeTab === 'login'">
          <el-icon><InfoFilled /></el-icon>
          <span>默认账号：<b>admin</b> &nbsp; 密码：<b>admin123</b></span>
        </div>
      </div>
    </div>

    <div class="copyright">
      Copyright &copy; {{ new Date().getFullYear() }} GP-Framework · 毕业设计开发框架
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Lock, Platform, InfoFilled,
  UserFilled, Key
} from '@element-plus/icons-vue'
import useUserStore from '@/store/modules/user'
import { register, getCaptcha } from '@/api/login'

const router = useRouter()
const userStore = useUserStore()
const { proxy } = getCurrentInstance()

const activeTab = ref('login')

/* -------- 登录 -------- */
const loginLoading = ref(false)
const loginForm = ref({ username: 'admin', password: 'admin123', rememberMe: false })
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (!valid) return
    loginLoading.value = true
    userStore.login(loginForm.value).then(() => {
      router.push('/')
    }).catch(() => {
      loginLoading.value = false
    })
  })
}

/* -------- 注册 -------- */
const registerLoading = ref(false)
const captchaImg = ref('')
const registerForm = reactive({
  username: '',
  nickName: '',
  password: '',
  confirmPassword: '',
  code: '',
  uuid: ''
})

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== registerForm.password) callback(new Error('两次密码输入不一致'))
  else callback()
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

function refreshCaptcha() {
  getCaptcha().then(res => {
    captchaImg.value = res.data.img
    registerForm.uuid = res.data.uuid
  })
}

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (!valid) return
    registerLoading.value = true
    register({
      username: registerForm.username,
      nickName: registerForm.nickName,
      password: registerForm.password,
      code: registerForm.code,
      uuid: registerForm.uuid
    }).then(() => {
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
      loginForm.value.username = registerForm.username
      loginForm.value.password = registerForm.password
      registerLoading.value = false
    }).catch(() => {
      registerLoading.value = false
      refreshCaptcha()
    })
  })
}

onMounted(() => {
  refreshCaptcha()
})

/* -------- 背景粒子 -------- */
function particleStyle(i) {
  const size = 3 + Math.random() * 6
  return {
    left: Math.random() * 100 + '%',
    top: Math.random() * 100 + '%',
    width: size + 'px',
    height: size + 'px',
    animationDuration: (Math.random() * 10 + 15) + 's',
    animationDelay: (Math.random() * 10) + 's'
  }
}
</script>

<style scoped lang="scss">
.login-container {
  position: relative;
  height: 100vh;
  overflow: hidden;
  background: #0f172a;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 动态渐变背景 */
.wall-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(-45deg, #667eea, #764ba2, #6B8DD6, #8E37D7);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  z-index: 0;
}

@keyframes gradientShift {
  0%   { background-position: 0% 50%; }
  50%  { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.wall-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(circle at center, black 30%, transparent 70%);
  -webkit-mask-image: radial-gradient(circle at center, black 30%, transparent 70%);
  z-index: 1;
}

/* 旋转光圈 */
.glow-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.12);
  z-index: 1;
  pointer-events: none;
}

.ring-1 {
  width: 600px; height: 600px;
  top: -200px; right: -150px;
  animation: ringRotate 40s linear infinite;
}
.ring-2 {
  width: 400px; height: 400px;
  bottom: -120px; left: -80px;
  animation: ringRotate 60s linear infinite reverse;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

/* 浮动粒子 */
.float-particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  z-index: 2;
  animation: float linear infinite;
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.4);
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); opacity: 0.4; }
  25%      { transform: translate(20px, -30px); opacity: 0.8; }
  50%      { transform: translate(-15px, -60px); opacity: 0.6; }
  75%      { transform: translate(25px, -90px); opacity: 0.3; }
}

/* 居中内容 */
.login-wrapper {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 品牌头部 */
.brand-header {
  text-align: center;
  margin-bottom: 32px;
  color: #fff;
}

.brand-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
  color: #409EFF;
  filter: drop-shadow(0 0 16px rgba(64, 158, 255, 0.6));
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0 0 6px 0;
  background: linear-gradient(135deg, #fff 0%, rgba(255,255,255,0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  letter-spacing: 3px;
}

/* 登录卡片 */
.login-card {
  width: 400px;
  padding: 36px 40px 30px 40px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.3);
}

/* Tabs */
.form-tabs {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 12px 0;
    color: rgba(255, 255, 255, 0.45);
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    position: relative;
    transition: color 0.2s;
    letter-spacing: 2px;

    &.active {
      color: #409EFF;
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: -1px;
        left: 20%;
        right: 20%;
        height: 2px;
        background: linear-gradient(90deg, #409EFF, #67C23A);
        border-radius: 2px;
      }
    }

    &:hover:not(.active) {
      color: rgba(255, 255, 255, 0.75);
    }
  }
}

.card-header {
  text-align: center;
  margin-bottom: 24px;

  h2 {
    font-size: 22px;
    font-weight: 600;
    color: #fff;
    margin: 0 0 6px 0;
  }

  p {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
    margin: 0;
  }
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border-radius: 8px;
  height: 44px;
  transition: all 0.3s;

  &:hover { border-color: rgba(64, 158, 255, 0.4); }
  &.is-focus {
    border-color: #409EFF;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.15);
  }
}

:deep(.el-input__inner) {
  color: #fff;
  &::placeholder { color: rgba(255, 255, 255, 0.35); }
}

:deep(.el-input__prefix-inner) { color: rgba(255, 255, 255, 0.4); }

.captcha-row {
  display: flex;
  gap: 10px;
  align-items: center;
  width: 100%;
}

.captcha-img {
  height: 44px;
  width: 120px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.1);
  object-fit: cover;
  flex-shrink: 0;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  :deep(.el-checkbox__label) {
    color: rgba(255, 255, 255, 0.6);
    font-size: 13px;
  }
  :deep(.el-checkbox__inner) {
    background: rgba(255, 255, 255, 0.08);
    border-color: rgba(255, 255, 255, 0.2);
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #409EFF 0%, #2b7fd6 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
  }
  &:active { transform: translateY(0); }
}

.login-tip {
  margin-top: 20px;
  padding: 12px 16px;
  border-radius: 8px;
  background: rgba(64, 158, 255, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.15);
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);

  .el-icon { color: #409EFF; flex-shrink: 0; }
  b { color: #67C23A; }
}

.copyright {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
  text-align: center;
  z-index: 10;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card { width: 90%; padding: 28px 24px 24px; }
  .brand-title { font-size: 28px; }
}
</style>
