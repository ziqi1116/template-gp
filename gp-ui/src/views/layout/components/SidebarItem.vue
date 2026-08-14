<template>
  <template v-if="!item.hidden">
    <!-- Single child or no children → render as single link -->
    <el-menu-item
      v-if="visibleChildren.length === 1 && !item.alwaysShow"
      :index="resolvePath(visibleChildren[0].path)"
      @click="handleClick($event, resolvePath(visibleChildren[0].path))"
    >
      <el-icon><component :is="iconName" /></el-icon>
      <template #title>{{ visibleChildren[0].meta?.title || item.meta?.title }}</template>
    </el-menu-item>

    <!-- No visible children → single item -->
    <el-menu-item
      v-else-if="visibleChildren.length === 0"
      :index="resolvePath(item.path)"
      @click="handleClick($event, resolvePath(item.path))"
    >
      <el-icon><component :is="iconName" /></el-icon>
      <template #title>{{ item.meta?.title }}</template>
    </el-menu-item>

    <!-- Multiple children → submenu -->
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template #title>
        <el-icon><component :is="iconName" /></el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <sidebar-item
        v-for="child in visibleChildren"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(item.path)"
        @navigate="handleNav"
      />
    </el-sub-menu>
  </template>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: { type: Object, required: true },
  basePath: { type: String, default: '' }
})

const emit = defineEmits(['navigate'])

const ICON_MAP = {
  dashboard: 'Dashboard',
  home: 'HomeFilled',
  homefilled: 'HomeFilled',
  system: 'Setting',
  setting: 'Setting',
  user: 'User',
  userfilled: 'UserFilled',
  peoples: 'UserFilled',
  people: 'User',
  avatar: 'UserFilled',
  tree: 'OfficeBuilding',
  officebuilding: 'OfficeBuilding',
  treeTable: 'Menu',
  'tree-table': 'Menu',
  menu: 'Menu',
  dict: 'Collection',
  collection: 'Collection',
  list: 'List',
  monitor: 'Monitor',
  log: 'Document',
  document: 'Document',
  education: 'Reading',
  reading: 'Reading',
  business: 'OfficeBuilding',
  office: 'OfficeBuilding',
  classroom: 'School',
  school: 'School',
  graph: 'Share',
  share: 'Share',
  edit: 'Edit',
  view: 'View',
  add: 'Plus',
  plus: 'Plus',
  delete: 'Delete',
  search: 'Search',
  import: 'Upload',
  export: 'Download',
  upload: 'Upload',
  download: 'Download',
  reset: 'Refresh',
  refresh: 'Refresh',
  clean: 'Delete',
  unlock: 'Unlock',
  lock: 'Lock',
  login: 'Right',
  logout: 'SwitchButton',
  ai: 'ChatDotRound',
  chatdotround: 'ChatDotRound',
  chatlineround: 'ChatLineRound',
  chat: 'ChatDotRound',
  dataline: 'DataLine',
  magicstick: 'MagicStick',
  gen: 'MagicStick'
}

const DEFAULT_ICON = 'Menu'

const visibleChildren = computed(() => {
  return (props.item.children || []).filter(c => !c.hidden)
})

const iconName = computed(() => {
  const icon = (props.item.meta?.icon || '').trim().toLowerCase()
  if (!icon || icon === '#') return DEFAULT_ICON
  if (ICON_MAP[icon]) return ICON_MAP[icon]
  if (ICON_MAP[icon.toLowerCase()]) return ICON_MAP[icon.toLowerCase()]
  const PascalCase = icon.charAt(0).toUpperCase() + icon.slice(1)
  return PascalCase
})

function resolvePath(routePath) {
  if (routePath.startsWith('/')) return routePath
  const base = props.basePath.endsWith('/') ? props.basePath.slice(0, -1) : props.basePath
  return base + '/' + routePath
}

function handleClick(event, path) {
  emit('navigate', path)
}

function handleNav(path) {
  emit('navigate', path)
}
</script>
