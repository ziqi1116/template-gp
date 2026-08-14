<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-left">
        <div class="welcome-avatar">
          <el-avatar :size="56" :src="avatarUrl" :icon="UserFilled" />
        </div>
        <div class="welcome-text">
          <h2>{{ greeting }}，{{ userName }}</h2>
          <p>{{ todayStr }} · 祝您工作愉快！</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="weather-card">
          <el-icon :size="28" color="#fff"><Sunny /></el-icon>
          <div>
            <div class="temp">22°C</div>
            <div class="desc">晴 · 适合开发</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="card in statCards" :key="card.title">
        <div class="stat-card" :style="{ '--card-color': card.color }">
          <div class="stat-icon">
            <el-icon :size="28"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
          <div class="stat-trend" :class="card.trend > 0 ? 'up' : 'down'">
            <el-icon :size="12">
              <CaretTop v-if="card.trend > 0" />
              <CaretBottom v-else />
            </el-icon>
            <span>{{ Math.abs(card.trend) }}%</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 中间区域 -->
    <el-row :gutter="16" class="middle-row">
      <!-- 快捷入口 -->
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Grid /></el-icon>
                快捷入口
              </span>
            </div>
          </template>
          <div class="quick-grid">
            <div
              v-for="item in quickLinks"
              :key="item.title"
              class="quick-item"
              @click="$router.push(item.path)"
            >
              <div class="quick-icon" :style="{ background: item.color }">
                <el-icon :size="20"><component :is="item.icon" /></el-icon>
              </div>
              <span>{{ item.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统信息 -->
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><InfoFilled /></el-icon>
                系统信息
              </span>
            </div>
          </template>
          <div class="info-list">
            <div class="info-item" v-for="info in systemInfo" :key="info.label">
              <span class="info-label">{{ info.label }}</span>
              <span class="info-value" :style="{ color: info.color || '#303133' }">
                {{ info.value }}
              </span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 框架特性 -->
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Star /></el-icon>
                框架特性
              </span>
            </div>
          </template>
          <div class="feature-list">
            <div class="feature-row" v-for="feat in features" :key="feat.title">
              <div class="feature-icon" :style="{ background: feat.color }">
                <el-icon :size="16"><component :is="feat.icon" /></el-icon>
              </div>
              <div class="feature-body">
                <div class="feature-name">{{ feat.title }}</div>
                <div class="feature-desc">{{ feat.desc }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部 -->
    <el-row :gutter="16" class="bottom-row">
      <!-- 最近操作 -->
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Clock /></el-icon>
                开发指南
              </span>
            </div>
          </template>
          <div class="guide-content">
            <el-timeline>
              <el-timeline-item
                v-for="(step, i) in devGuide"
                :key="i"
                :type="step.type"
                :hollow="i !== 0"
                :timestamp="step.time"
              >
                <h4>{{ step.title }}</h4>
                <p>{{ step.desc }}</p>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>

      <!-- 版本信息 -->
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card version-card">
          <div class="version-badge">
            <el-icon :size="40" color="#409EFF"><Platform /></el-icon>
          </div>
          <h3 class="version-name">GP-Framework</h3>
          <p class="version-desc">毕业设计统一开发框架</p>
          <el-divider />
          <div class="version-list">
            <div class="version-item">
              <span>框架版本</span>
              <el-tag size="small" type="success">v1.0.0</el-tag>
            </div>
            <div class="version-item">
              <span>后端技术</span>
              <el-tag size="small">Spring Boot 2.7</el-tag>
            </div>
            <div class="version-item">
              <span>前端技术</span>
              <el-tag size="small">Vue 3 + Vite</el-tag>
            </div>
            <div class="version-item">
              <span>UI 框架</span>
              <el-tag size="small">Element Plus</el-tag>
            </div>
            <div class="version-item">
              <span>ORM 框架</span>
              <el-tag size="small">MyBatis-Plus</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import useUserStore from '@/store/modules/user'
import {
  UserFilled, Sunny, CaretTop, CaretBottom, Grid, InfoFilled, Star,
  Clock, Platform, Setting, Menu, Monitor, User, Reading, Document,
  Promotion, Lock, Refresh, Download
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const userName = computed(() => userStore.userInfo?.nickName || userStore.userInfo?.userName || '管理员')
const avatarUrl = computed(() => userStore.userInfo?.avatar || '')

const hour = new Date().getHours()
const greeting = hour < 6 ? '凌晨好' : hour < 9 ? '早上好' : hour < 12 ? '上午好' : hour < 14 ? '中午好' : hour < 17 ? '下午好' : hour < 19 ? '傍晚好' : '晚上好'

const todayStr = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

const statCards = ref([
  { title: '用户总数', value: 2, icon: 'User', color: '#409EFF', trend: 10 },
  { title: '学生总数', value: 5, icon: 'Reading', color: '#67C23A', trend: 5 },
  { title: '菜单数量', value: 6, icon: 'Menu', color: '#E6A23C', trend: 0 },
  { title: '角色数量', value: 2, icon: 'UserFilled', color: '#F56C6C', trend: -2 }
])

const quickLinks = ref([
  { title: '用户管理', path: '/system/user', icon: 'User', color: '#409EFF' },
  { title: '角色管理', path: '/system/role', icon: 'UserFilled', color: '#67C23A' },
  { title: '菜单管理', path: '/system/menu', icon: 'Menu', color: '#E6A23C' },
  { title: '部门管理', path: '/system/dept', icon: 'Setting', color: '#F56C6C' },
  { title: '字典管理', path: '/system/dict/type', icon: 'Document', color: '#909399' },
  { title: '学生管理', path: '/business/student', icon: 'Reading', color: '#9C27B0' },
  { title: '操作日志', path: '/monitor/operlog', icon: 'Monitor', color: '#00BCD4' },
  { title: '登录日志', path: '/monitor/logininfor', icon: 'Lock', color: '#FF9800' }
])

const systemInfo = ref([
  { label: '服务器IP', value: '127.0.0.1' },
  { label: '系统架构', value: 'x86_64' },
  { label: '操作系统', value: 'macOS' },
  { label: '运行环境', value: 'Java 1.8' },
  { label: 'JVM 内存', value: '512MB' },
  { label: '磁盘使用', value: '42.3%' },
  { label: 'CPU 使用率', value: '12%', color: '#67C23A' },
  { label: '内存使用率', value: '56%', color: '#E6A23C' }
])

const features = ref([
  { title: 'RBAC 权限', desc: '角色-菜单-按钮三级权限控制', icon: 'Lock', color: '#409EFF' },
  { title: '动态路由', desc: '后端配置菜单，前端动态加载', icon: 'Menu', color: '#67C23A' },
  { title: '数据字典', desc: '统一管理枚举数据，维护方便', icon: 'Document', color: '#E6A23C' },
  { title: '操作日志', desc: 'AOP自动记录操作行为', icon: 'Monitor', color: '#F56C6C' },
  { title: '代码生成', desc: '一键生成CRUD代码，快速开发', icon: 'Refresh', color: '#909399' },
  { title: '接口文档', desc: 'Swagger3自动生成API文档', icon: 'Promotion', color: '#9C27B0' }
])

const devGuide = ref([
  { type: 'primary', time: '步骤 1', title: '创建数据库表', desc: '在MySQL中创建业务表，建议包含 create_by, create_time, update_by, update_time, del_flag 等基础字段。' },
  { type: 'success', time: '步骤 2', title: '生成后端代码', desc: '创建 Entity、Mapper、Service、Controller，继承基础类即可实现 CRUD。' },
  { type: 'warning', time: '步骤 3', title: '配置菜单路由', desc: '在菜单管理中添加菜单项，设置路径和组件，分配给对应角色。' },
  { type: 'danger', time: '步骤 4', title: '开发前端页面', desc: '在 views 目录下创建对应组件，参考已有页面实现列表、新增、修改、删除功能。' },
  { type: 'info', time: '步骤 5', title: '测试部署', desc: '前后端联调测试，确保功能正常后打包部署。' }
])
</script>

<style scoped lang="scss">
.dashboard {
  padding: 0;
}

/* 欢迎横幅 */
.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 32px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin-bottom: 16px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: 20%;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
  }
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 20px;
  z-index: 1;
}

.welcome-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.welcome-text {
  color: #fff;

  h2 {
    margin: 0 0 6px 0;
    font-size: 22px;
    font-weight: 600;
  }

  p {
    margin: 0;
    font-size: 14px;
    opacity: 0.85;
  }
}

.welcome-right {
  z-index: 1;
}

.weather-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: #fff;

  .temp {
    font-size: 22px;
    font-weight: 700;
  }

  .desc {
    font-size: 12px;
    opacity: 0.8;
  }
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  margin-bottom: 8px;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 4px;
    background: var(--card-color);
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: var(--card-color);
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-title {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 600;

  &.up { color: #67C23A; }
  &.down { color: #F56C6C; }
}

/* 面板卡片 */
.panel-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 16px;

  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
  }

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;

  .el-icon { color: #409EFF; }
}

/* 快捷入口 */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 12px 8px;
  border-radius: 10px;
  transition: all 0.3s;

  &:hover {
    background: #f5f7fa;
    transform: scale(1.05);
  }

  span {
    font-size: 12px;
    color: #606266;
  }
}

.quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

/* 系统信息 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #f0f0f0;

  &:last-child { border-bottom: none; }

  .info-label {
    font-size: 13px;
    color: #909399;
  }

  .info-value {
    font-size: 13px;
    font-weight: 600;
  }
}

/* 框架特性 */
.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.feature-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.feature-body {
  flex: 1;
}

.feature-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.feature-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

/* 开发指南 */
.guide-content {
  padding: 10px 0;

  h4 {
    margin: 0 0 6px 0;
    font-size: 14px;
    color: #303133;
  }

  p {
    margin: 0;
    font-size: 13px;
    color: #909399;
    line-height: 1.6;
  }
}

/* 版本卡片 */
.version-card {
  text-align: center;

  :deep(.el-card__body) {
    padding: 24px 20px;
  }
}

.version-badge {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}

.version-name {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}

.version-desc {
  font-size: 13px;
  color: #909399;
  margin: 4px 0 0 0;
}

.version-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.version-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;

  span:first-child {
    color: #909399;
  }
}

.middle-row, .bottom-row {
  margin-bottom: 0;
}
</style>
