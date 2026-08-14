import { createRouter, createWebHistory } from 'vue-router'
import useUserStore from '@/store/modules/user'
import usePermissionStore from '@/store/modules/permission'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

export const constantRoutes = [
  {
    path: '/',
    redirect: '/dashboard',
    hidden: true
  },
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

const whiteList = ['/login', '/404']
let routeLoaded = false

router.beforeEach(async (to, from, next) => {
  const hasToken = getToken()
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      if (!routeLoaded) {
        try {
          await userStore.getInfo()
          const permissionStore = usePermissionStore()
          const dynamicRoutes = await permissionStore.generateRoutes()
          dynamicRoutes.forEach(route => {
            router.addRoute(route)
          })
          router.addRoute({ path: '/:pathMatch(.*)*', redirect: '/404' })
          routeLoaded = true
          next({ ...to, replace: true })
        } catch (err) {
          await userStore.logout()
          routeLoaded = false
          ElMessage.error(err.message || '获取用户信息失败')
          next('/login')
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      if (to.path === '/login') {
        routeLoaded = false
      }
      next()
    } else {
      next('/login')
    }
  }
})

export default router
