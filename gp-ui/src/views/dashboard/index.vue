<template>
  <div class="dashboard">
    <!-- 动画背景墙 -->
    <div class="hero-wall">
      <!-- 动态渐变背景 -->
      <div class="wall-bg"></div>
      <div class="wall-grid"></div>

      <!-- 浮动粒子 -->
      <span v-for="i in 20" :key="'p'+i" class="float-particle" :style="particleStyle(i)"></span>

      <!-- 动态光圈 -->
      <div class="glow-ring ring-1"></div>
      <div class="glow-ring ring-2"></div>
      <div class="glow-ring ring-3"></div>

      <!-- 内容 -->
      <div class="wall-content">
        <div class="wall-text">
          <div class="greeting-box">
            <div class="avatar-wrap">
              <el-avatar :size="72" :src="avatarUrl" :icon="UserFilled" />
              <div class="status-dot"></div>
            </div>
            <div class="greeting-text">
              <h1>{{ greeting }}，{{ userName }} 👋</h1>
              <p class="sub-text">{{ todayStr }} · {{ welcomeTip }}</p>
              <div class="tags">
                <el-tag v-for="t in quickTags" :key="t" effect="dark" :type="t.type" round>
                  {{ t.text }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 统计数字 -->
        <div class="stats-grid">
          <div
            v-for="(s, idx) in statCards"
            :key="s.title"
            class="stat-item"
            :style="{ '--delay': idx * 0.1 + 's', '--c': s.color }"
          >
            <div class="stat-icon-bg">
              <el-icon :size="26"><component :is="s.icon" /></el-icon>
            </div>
            <div class="stat-number">
              <span ref="el => setCount(el, s.value)">0</span>
              <i class="trend" :class="s.trend > 0 ? 'up' : 'down'">
                <el-icon :size="10"><component :is="s.trend > 0 ? CaretTop : CaretBottom" /></el-icon>
                {{ Math.abs(s.trend) }}%
              </i>
            </div>
            <div class="stat-label">{{ s.title }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 下方快捷操作区 -->
    <el-row :gutter="16" class="action-row">
      <!-- 快捷入口 -->
      <el-col :xs="24" :md="12">
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
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><InfoFilled /></el-icon>
                系统状态
              </span>
            </div>
          </template>
          <div class="info-list">
            <div class="info-item" v-for="info in systemInfo" :key="info.label">
              <span class="info-label">{{ info.label }}</span>
              <div class="info-bar" v-if="info.bar !== undefined">
                <div class="bar-fill" :style="{ width: info.bar + '%', background: info.color || '#409EFF' }"></div>
              </div>
              <span class="info-value" :style="{ color: info.color || '#303133' }">
                {{ info.value }}
              </span>
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
  UserFilled, CaretTop, CaretBottom, Grid, InfoFilled,
  Menu, User, Reading, Monitor, Lock, Setting, Document
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const userName = computed(() => userStore.userInfo?.nickName || userStore.userInfo?.userName || '管理员')
const avatarUrl = computed(() => userStore.userInfo?.avatar || '')

const hour = new Date().getHours()
const greeting = hour < 6 ? '凌晨好' : hour < 9 ? '早上好' : hour < 12 ? '上午好' : hour < 14 ? '中午好' : hour < 17 ? '下午好' : hour < 19 ? '傍晚好' : '晚上好'

const todayStr = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

const welcomeTips = [
  '今天也要元气满满哦',
  '愿代码零 Bug，愿部署一次成功',
  '保持热爱，奔赴山海',
  '优秀是一种习惯',
  '今天也是奋斗的一天',
  '代码改变世界，学习成就未来',
  '站得更高，看得更远',
  '努力成为更好的自己'
]
const welcomeTip = computed(() => welcomeTips[new Date().getDate() % welcomeTips.length])

const quickTags = ref([
  { text: '超级管理员', type: 'danger' },
  { text: '系统运行正常', type: 'success' },
  { text: '祝你开发顺利', type: 'warning' }
])

const statCards = ref([
  { title: '用户总数', value: 2, icon: 'User', color: '#409EFF', trend: 10 },
  { title: '学生总数', value: 5, icon: 'Reading', color: '#67C23A', trend: 5 },
  { title: '菜单数量', value: 6, icon: 'Menu', color: '#E6A23C', trend: 0 },
  { title: '角色数量', value: 2, icon: 'UserFilled', color: '#F56C6C', trend: -2 }
])

const quickLinks = ref([
  { title: '用户管理', path: '/system/user', icon: 'User', color: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { title: '角色管理', path: '/system/role', icon: 'UserFilled', color: 'linear-gradient(135deg, #11998e, #38ef7d)' },
  { title: '菜单管理', path: '/system/menu', icon: 'Menu', color: 'linear-gradient(135deg, #f093fb, #f5576c)' },
  { title: '部门管理', path: '/system/dept', icon: 'Setting', color: 'linear-gradient(135deg, #4facfe, #00f2fe)' },
  { title: '字典管理', path: '/system/dict/type', icon: 'Document', color: 'linear-gradient(135deg, #fa709a, #fee140)' },
  { title: '学生管理', path: '/business/student', icon: 'Reading', color: 'linear-gradient(135deg, #30cfd0, #330867)' },
  { title: '操作日志', path: '/monitor/operlog', icon: 'Monitor', color: 'linear-gradient(135deg, #a8edea, #fed6e3)' },
  { title: '登录日志', path: '/monitor/logininfor', icon: 'Lock', color: 'linear-gradient(135deg, #ff9a9e, #fad0c4)' }
])

const systemInfo = ref([
  { label: 'CPU 使用率', value: '12%', bar: 12, color: '#67C23A' },
  { label: '内存使用率', value: '56%', bar: 56, color: '#E6A23C' },
  { label: '磁盘使用', value: '42%', bar: 42, color: '#409EFF' },
  { label: 'JVM 内存', value: '512MB / 2GB', bar: 25, color: '#9C27B0' },
  { label: '在线用户', value: '1 人', bar: 10, color: '#00BCD4' },
  { label: '服务器运行', value: '30 天', bar: 85, color: '#67C23A' }
])

// 粒子样式
const particleStyle = (i) => {
  const size = Math.random() * 6 + 2
  return {
    left: Math.random() * 100 + '%',
    top: Math.random() * 100 + '%',
    width: size + 'px',
    height: size + 'px',
    animationDuration: (Math.random() * 10 + 10) + 's',
    animationDelay: (Math.random() * 10) + 's'
  }
}

// 数字滚动动画
const setCount = (el, target) => {
  if (!el) return
  const duration = 1200
  const start = performance.now()
  const from = 0
  const animate = (now) => {
    const progress = Math.min((now - start) / duration, 1)
    const ease = 1 - Math.pow(1 - progress, 3)
    el.textContent = Math.round(from + (target - from) * ease)
    if (progress < 1) requestAnimationFrame(animate)
  }
  requestAnimationFrame(animate)
}
</script>

<style scoped lang="scss">
.dashboard {
  padding: 0;
}

/* ============ 动画背景墙 ============ */
.hero-wall {
  position: relative;
  border-radius: 16px;
  padding: 40px 48px;
  margin-bottom: 16px;
  overflow: hidden;
  min-height: 360px;
  background: #0f172a;
  color: #fff;
}

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
    linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(circle at center, black 40%, transparent 70%);
  -webkit-mask-image: radial-gradient(circle at center, black 40%, transparent 70%);
  z-index: 1;
}

/* 光圈 */
.glow-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.15);
  z-index: 1;
  pointer-events: none;

  &::before, &::after {
    content: '';
    position: absolute;
    inset: -2px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.08);
  }
}

.ring-1 {
  width: 500px; height: 500px;
  top: -150px; right: -100px;
  animation: ringRotate 40s linear infinite;
}
.ring-2 {
  width: 300px; height: 300px;
  bottom: -80px; left: -60px;
  animation: ringRotate 60s linear infinite reverse;
}
.ring-3 {
  width: 800px; height: 800px;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  border-style: dashed;
  opacity: 0.4;
  animation: ringRotate 80s linear infinite;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

.ring-3 {
  animation-name: ringRotate3;
}
@keyframes ringRotate3 {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to   { transform: translate(-50%, -50%) rotate(360deg); }
}

/* 浮动粒子 */
.float-particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  z-index: 2;
  animation: float linear infinite;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.5; }
  25%      { transform: translate(30px, -40px) scale(1.2); opacity: 0.9; }
  50%      { transform: translate(-20px, -80px) scale(0.8); opacity: 0.7; }
  75%      { transform: translate(40px, -120px) scale(1.1); opacity: 0.4; }
}

.wall-content {
  position: relative;
  z-index: 10;
}

/* 问候区 */
.greeting-box {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 40px;
}

.avatar-wrap {
  position: relative;

  .el-avatar {
    border: 3px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  }
}

.status-dot {
  position: absolute;
  right: 4px;
  bottom: 4px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #67C23A;
  border: 2px solid #fff;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.6); }
  50%      { box-shadow: 0 0 0 8px rgba(103, 194, 58, 0); }
}

.greeting-text {
  h1 {
    margin: 0 0 8px 0;
    font-size: 32px;
    font-weight: 700;
    background: linear-gradient(90deg, #fff, #f0f0f0);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  .sub-text {
    margin: 0 0 12px 0;
    font-size: 15px;
    opacity: 0.85;
  }
  .tags {
    display: flex;
    gap: 8px;
  }
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
}

.stat-item {
  position: relative;
  padding: 24px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: statFadeIn 0.6s ease backwards;
  animation-delay: var(--delay);

  &:hover {
    transform: translateY(-6px) scale(1.02);
    background: rgba(255, 255, 255, 0.15);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  }
}

@keyframes statFadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.stat-icon-bg {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: var(--c);
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.stat-number {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 6px;

  span {
    font-size: 36px;
    font-weight: 700;
    line-height: 1;
  }
  .trend {
    font-size: 12px;
    display: flex;
    align-items: center;
    &.up { color: #67C23A; }
    &.down { color: #F56C6C; }
  }
}

.stat-label {
  font-size: 13px;
  opacity: 0.8;
}

/* ============ 下方面板 ============ */
.action-row {
  margin-bottom: 0;
}

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
  padding: 14px 8px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-4px);
    background: #f5f7fa;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  }

  span {
    font-size: 12px;
    color: #606266;
  }
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

/* 系统信息 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-item {
  display: grid;
  grid-template-columns: 80px 1fr 80px;
  gap: 12px;
  align-items: center;
  font-size: 13px;

  .info-label { color: #909399; }
  .info-value { font-weight: 600; text-align: right; }
}

.info-bar {
  height: 6px;
  background: #f0f2f5;
  border-radius: 3px;
  overflow: hidden;

  .bar-fill {
    height: 100%;
    border-radius: 3px;
    transition: width 1s ease;
  }
}
</style>
