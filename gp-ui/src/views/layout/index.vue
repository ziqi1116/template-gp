<template>
  <div class="app-wrapper">
    <!-- Sidebar -->
    <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo">
        <el-icon class="logo-icon"><Platform /></el-icon>
        <span v-if="!isCollapse">GP-Framework</span>
        <span v-else>GP</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <sidebar-item
          v-for="route in sidebarRoutes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
          @navigate="handleMenuSelect"
        />
      </el-menu>
    </div>
    <!-- Main Content -->
    <div class="main-container" :class="{ 'is-collapse': isCollapse }">
      <navbar />
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SidebarItem from './components/SidebarItem.vue'
import Navbar from './components/Navbar.vue'
import { usePermissionStore } from '@/store/modules/permission'

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()
const isCollapse = ref(false)

const sidebarRoutes = computed(() => permissionStore.sidebarRoutes)
const activeMenu = computed(() => route.path)

function handleMenuSelect(index) {
  router.push(index)
}

window.toggleSidebar = () => { isCollapse.value = !isCollapse.value }
</script>

<style scoped lang="scss">
.app-wrapper { display: flex; height: 100vh; }
.sidebar-container {
  width: 210px; background: #304156; transition: width 0.3s;
  display: flex; flex-direction: column; overflow: hidden;
  &.is-collapse { width: 64px; }
}
.logo {
  height: 60px; display: flex; align-items: center; justify-content: center; gap: 8px;
  color: #fff; font-size: 18px; font-weight: bold; background: #2b3a4d;
}
.main-container {
  flex: 1; display: flex; flex-direction: column; overflow: hidden;
  margin-left: 0;
}
.app-main { flex: 1; overflow-y: auto; background: #f0f2f5; padding: 16px; }
:deep(.el-menu) { border-right: none; }
:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  margin-right: 8px;
  font-size: 16px;
  width: 16px;
  text-align: center;
}
:deep(.el-menu--collapse .el-menu-item .el-icon),
:deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin-right: 0;
}
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
}
.logo .logo-icon {
  font-size: 24px; color: #409EFF;
}
</style>
