<template>
  <div class="screen-page">
    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <div class="header-side left">
        <span class="date-text">{{ data.date }}</span>
      </div>
      <div class="header-title">{{ title }}</div>
      <div class="header-side right">
        <span class="clock">{{ clock }}</span>
      </div>
    </div>    <!-- 统计卡片行 -->
    <div class="stat-row">
      <div v-for="s in statCards" :key="s.label" class="stat-card">
        <div class="stat-value">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-grid">
      <div class="chart-card large">
        <div class="chart-title">近 7 天登录趋势</div>
        <div ref="loginTrendRef" class="chart-body" />
      </div>
      <div class="chart-card">
        <div class="chart-title">角色用户分布</div>
        <div ref="roleDistRef" class="chart-body" />
      </div>
      <div class="chart-card">
        <div class="chart-title">班级人数排行</div>
        <div ref="classDistRef" class="chart-body" />
      </div>
      <div class="chart-card">
        <div class="chart-title">近 7 天新增用户</div>
        <div ref="userTrendRef" class="chart-body" />
      </div>
      <div class="chart-card">
        <div class="chart-title">操作类型统计</div>
        <div ref="operTypeRef" class="chart-body" />
      </div>
      <div class="chart-card">
        <div class="chart-title">学生性别分布</div>
        <div ref="genderDistRef" class="chart-body" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, getCurrentInstance } from 'vue'
import * as echarts from 'echarts'
import { getScreenData } from '@/api/dashboard'

const { proxy } = getCurrentInstance()

const data = ref({})
const clock = ref('')
// 大屏标题：可改成「XX系统 · 数据大屏」（不要在模板里直接写 {{xxx}} 字样，会被 Vue 当成插值）
const title = 'GP 数据可视化大屏'
const statCards = computed(() => [
  { label: '注册用户', value: data.value.userCount ?? '-' },
  { label: '学生总数', value: data.value.studentCount ?? '-' },
  { label: '今日登录', value: data.value.todayLogin ?? '-' },
  { label: '今日操作', value: data.value.todayOper ?? '-' }
])

const loginTrendRef = ref(null)
const roleDistRef = ref(null)
const classDistRef = ref(null)
const userTrendRef = ref(null)
const operTypeRef = ref(null)
const genderDistRef = ref(null)

const charts = []
let clockTimer = null

onMounted(() => {
  startClock()
  loadData()
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  charts.forEach(c => c.dispose())
  clearInterval(clockTimer)
})

function startClock() {
  const tick = () => { clock.value = new Date().toLocaleTimeString('zh-CN', { hour12: false }) }
  tick()
  clockTimer = setInterval(tick, 1000)
}

function loadData() {
  getScreenData().then(res => {
    data.value = res.data || {}
    renderCharts()
  }).catch(() => {
    proxy.$modal.msgError('大屏数据加载失败')
  })
}

function renderCharts() {
  const d = data.value
  // 近 7 天登录趋势（大面积折线）
  initChart(loginTrendRef.value, {
    grid: { left: 50, right: 24, top: 36, bottom: 30 },
    xAxis: { type: 'category', data: (d.loginTrend || []).map(i => i.date), ...axisStyle() },
    yAxis: { type: 'value', minInterval: 1, ...axisStyle(true) },
    series: [lineSeries('登录次数', (d.loginTrend || []).map(i => i.value), '#38BDF8', true)]
  })
  // 角色用户分布（环形饼图）
  initChart(roleDistRef.value, {
    series: [{
      type: 'pie', radius: ['42%', '68%'], center: ['50%', '52%'],
      label: { color: 'rgba(255,255,255,.75)', fontSize: 11 },
      itemStyle: { borderColor: '#0E1A3C', borderWidth: 2 },
      data: (d.roleDist || []).map(i => ({ name: i.name, value: i.value }))
    }]
  })
  // 班级人数排行（横向条形图）
  initChart(classDistRef.value, {
    grid: { left: 80, right: 30, top: 16, bottom: 26 },
    xAxis: { type: 'value', ...axisStyle(true) },
    yAxis: { type: 'category', inverse: true, data: (d.classDist || []).slice(0, 6).map(i => i.name), ...axisStyle() },
    series: [{
      type: 'bar', barWidth: 12,
      itemStyle: { borderRadius: [0, 6, 6, 0], color: gradient('#22D3EE', '#3B82F6') },
      label: { show: true, position: 'right', color: 'rgba(255,255,255,.8)', fontSize: 11 },
      data: (d.classDist || []).slice(0, 6).map(i => i.value)
    }]
  })
  // 近 7 天新增用户（渐变柱）
  initChart(userTrendRef.value, {
    grid: { left: 40, right: 20, top: 30, bottom: 26 },
    xAxis: { type: 'category', data: (d.userTrend || []).map(i => i.date), ...axisStyle() },
    yAxis: { type: 'value', minInterval: 1, ...axisStyle(true) },
    series: [{
      type: 'bar', barWidth: 14,
      itemStyle: { borderRadius: [6, 6, 0, 0], color: gradient('#818CF8', '#38BDF8') },
      data: (d.userTrend || []).map(i => i.value)
    }]
  })
  // 操作类型统计（饼图）
  initChart(operTypeRef.value, {
    series: [{
      type: 'pie', radius: '62%', center: ['50%', '52%'], roseType: 'radius',
      label: { color: 'rgba(255,255,255,.75)', fontSize: 11 },
      data: (d.operTypeDist || []).map(i => ({ name: i.name, value: i.value }))
    }]
  })
  // 学生性别分布（环形饼图，男蓝女粉）
  const genderColors = { '男': '#3B82F6', '女': '#EC4899', '未知': '#94A3B8' }
  initChart(genderDistRef.value, {
    series: [{
      type: 'pie', radius: ['44%', '68%'], center: ['50%', '52%'],
      label: { color: 'rgba(255,255,255,.75)', fontSize: 11, formatter: '{b}: {c}' },
      itemStyle: { borderColor: '#0E1A3C', borderWidth: 2 },
      data: (d.genderDist || []).map(i => ({
        name: i.name,
        value: i.value,
        itemStyle: { color: genderColors[i.name] || '#94A3B8' }
      }))
    }]
  })
}

function initChart(el, option) {
  if (!el) return
  const chart = echarts.init(el)
  chart.setOption(option)
  charts.push(chart)
}

function resizeCharts() {
  charts.forEach(c => c.resize())
}

/** 深色主题下的坐标轴通用样式 */
function axisStyle(isValue) {
  const base = {
    axisLine: { lineStyle: { color: 'rgba(255,255,255,.25)' } },
    axisLabel: { color: 'rgba(255,255,255,.65)', fontSize: 11 },
    splitLine: { show: false }
  }
  if (isValue) {
    base.splitLine = { show: true, lineStyle: { color: 'rgba(255,255,255,.08)' } }
  }
  return base
}

function lineSeries(name, values, color, area) {
  return {
    name, type: 'line', smooth: true, symbolSize: 6,
    lineStyle: { width: 2.5, color },
    itemStyle: { color },
    areaStyle: area ? { color: gradient(color, 'rgba(14,26,60,0)') } : undefined,
    data: values
  }
}

function gradient(from, to) {
  return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: from },
    { offset: 1, color: to }
  ])
}
</script>

<style scoped lang="scss">
$bg: #0B1638;
$card: rgba(255, 255, 255, 0.045);
$border: rgba(120, 160, 255, 0.18);

.screen-page {
  min-height: calc(100vh - 130px);
  background: linear-gradient(160deg, #0B1638 0%, #0E1F4E 55%, #0B1638 100%);
  border-radius: 10px;
  padding: 18px 20px 22px;
  color: #e8eefc;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.screen-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px 6px 12px;

  .header-title {
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 4px;
    background: linear-gradient(90deg, #7DD3FC, #A5B4FC, #7DD3FC);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }

  .header-side {
    font-size: 13px;
    color: rgba(232, 238, 252, 0.65);
    font-variant-numeric: tabular-nums;
  }
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.stat-card {
  background: $card;
  border: 1px solid $border;
  border-radius: 8px;
  padding: 14px 0;
  text-align: center;

  .stat-value {
    font-size: 34px;
    font-weight: 700;
    font-variant-numeric: tabular-nums;
    background: linear-gradient(180deg, #ffffff, #93C5FD);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }

  .stat-label {
    margin-top: 4px;
    font-size: 13px;
    color: rgba(232, 238, 252, 0.6);
    letter-spacing: 2px;
  }
}

.chart-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: minmax(215px, auto);
  gap: 14px;
}

.chart-card {
  background: $card;
  border: 1px solid $border;
  border-radius: 8px;
  padding: 12px 12px 6px;
  display: flex;
  flex-direction: column;

  &.large {
    grid-column: span 2;
  }

  .chart-title {
    font-size: 14px;
    font-weight: 600;
    color: #cfe0ff;
    padding-left: 10px;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 3px;
      bottom: 3px;
      width: 3px;
      border-radius: 2px;
      background: linear-gradient(180deg, #38BDF8, #6366F1);
    }
  }

  .chart-body {
    flex: 1;
    min-height: 180px;
  }
}
</style>
