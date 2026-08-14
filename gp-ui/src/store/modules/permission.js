import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRouters } from '@/api/login'

const modules = import.meta.glob('@/views/**/*.vue')

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref([])
  const sidebarRoutes = ref([])

  function generateRoutes() {
    return getRouters().then(res => {
      sidebarRoutes.value = res.data
      routes.value = wrapDynamicRoutes(res.data)
      return routes.value
    })
  }

  function wrapDynamicRoutes(data) {
    return data.map(item => {
      const route = {
        path: item.path,
        meta: { title: item.meta?.title, icon: item.meta?.icon }
      }

      if (item.name) {
        route.name = item.name
      }

      if (item.children && item.children.length > 0) {
        if (item.component === 'Layout') {
          route.component = () => import('@/views/layout/index.vue')
          const childRoutes = wrapDynamicRoutes(item.children)
          const firstChildPath = item.children[0].path
          route.redirect = `${item.path}/${firstChildPath}`
          route.children = childRoutes
        } else if (item.component === 'ParentView') {
          route.component = () => import('@/views/layout/components/ParentView.vue')
          const childRoutes = wrapDynamicRoutes(item.children)
          const firstChildPath = item.children[0].path
          route.redirect = firstChildPath
          route.children = childRoutes
        } else if (item.component) {
          const compPath = `/src/views/${item.component}.vue`
          route.component = modules[compPath] || (() => import('@/views/error/404.vue'))
          route.children = wrapDynamicRoutes(item.children)
        }
      } else {
        if (item.component === 'Layout') {
          route.component = () => import('@/views/layout/index.vue')
        } else if (item.component === 'ParentView') {
          route.component = () => import('@/views/layout/components/ParentView.vue')
        } else if (item.component) {
          const compPath = `/src/views/${item.component}.vue`
          route.component = modules[compPath] || (() => import('@/views/error/404.vue'))
        }
        route.children = undefined
      }

      return route
    })
  }

  return { routes, sidebarRoutes, generateRoutes }
})

export default usePermissionStore
