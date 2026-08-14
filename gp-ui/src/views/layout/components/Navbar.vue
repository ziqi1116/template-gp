<template>
  <div class="navbar">
    <div class="navbar-left">
      <el-icon class="hamburger" @click="toggleSidebar"><Fold /></el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path">
          <el-icon v-if="index === breadcrumbList.length - 1 && resolveIcon(item)" class="breadcrumb-icon">
            <component :is="resolveIcon(item)" />
          </el-icon>
          {{ item.meta?.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" :src="avatarUrl" :icon="UserFilled" />
          <span class="username">{{ userStore.userInfo?.nickName || userStore.userInfo?.userName }}</span>
          <el-icon class="caret-icon"><CaretBottom /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="pwd">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="440px" @close="resetPwdForm">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码（6-20位）" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="submitPwd">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, CaretBottom, SwitchButton, Lock } from '@element-plus/icons-vue'
import useUserStore from '@/store/modules/user'
import { updateUserPwd } from '@/api/system/user'

const { proxy } = getCurrentInstance()

const ICON_MAP = {
  dashboard: 'Dashboard', system: 'Setting', user: 'User', userfilled: 'UserFilled',
  peoples: 'UserFilled', people: 'User', tree: 'OfficeBuilding',
  'tree-table': 'Menu', menu: 'Menu', dict: 'Collection', list: 'List',
  monitor: 'Monitor', log: 'Document', document: 'Document',
  education: 'Reading', business: 'OfficeBuilding', reading: 'Reading',
  officebuilding: 'OfficeBuilding', collection: 'Collection',
  ai: 'ChatDotRound', chat: 'ChatLineRound'
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const avatarUrl = computed(() => userStore.userInfo?.avatar || '')

const breadcrumbList = computed(() => {
  return route.matched.filter(item => item.meta && item.meta.title)
})

function resolveIcon(item) {
  const icon = (item.meta?.icon || '').trim().toLowerCase()
  if (!icon || icon === '#') return null
  if (ICON_MAP[icon]) return ICON_MAP[icon]
  const PascalCase = icon.charAt(0).toUpperCase() + icon.slice(1)
  return PascalCase
}

function toggleSidebar() {
  window.toggleSidebar()
}

function handleCommand(cmd) {
  if (cmd === 'logout') handleLogout()
  if (cmd === 'pwd') pwdDialogVisible.value = true
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.resetState()
    router.push('/login')
  }).catch(() => {})
}

/* -------- 修改密码 -------- */
const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

function resetPwdForm() {
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  proxy?.$refs?.pwdFormRef?.clearValidate?.()
}

function submitPwd() {
  pwdFormRef.value.validate(valid => {
    if (!valid) return
    pwdLoading.value = true
    updateUserPwd({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    }).then(() => {
      ElMessage.success('密码修改成功，请重新登录')
      pwdDialogVisible.value = false
      userStore.logout().then(() => router.push('/login'))
    }).catch(() => {
      pwdLoading.value = false
    })
  })
}
</script>

<style scoped lang="scss">
.navbar {
  height: 60px; display: flex; align-items: center; justify-content: space-between;
  background: #fff; box-shadow: 0 1px 4px rgba(0,21,41,0.08); padding: 0 20px;
}
.navbar-left { display: flex; align-items: center; gap: 15px; }
.hamburger { font-size: 20px; cursor: pointer; }
.breadcrumb-icon { margin-right: 4px; font-size: 14px; vertical-align: middle; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px; border-radius: 6px; transition: background 0.2s; }
.user-info:hover { background: #f5f7fa; }
.username { font-size: 14px; color: #303133; }
.caret-icon { font-size: 12px; color: #909399; }
:deep(.el-avatar) { border: 2px solid #e4e7ed; }
:deep(.el-dropdown-menu__item) { display: flex; align-items: center; gap: 6px; }
</style>
