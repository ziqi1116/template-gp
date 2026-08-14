import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi, getInfo as getInfoApi } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref({})
  const roles = ref([])
  const permissions = ref([])

  function login(userInfo) {
    return loginApi(userInfo).then(res => {
      setToken(res.data.token)
      token.value = res.data.token
    })
  }

  function getInfo() {
    return getInfoApi().then(res => {
      userInfo.value = res.data.user
      roles.value = res.data.roles
      permissions.value = res.data.permissions
      return res.data
    })
  }

  function logout() {
    return logoutApi().then(() => {
      resetState()
    }).catch(() => {
      resetState()
    })
  }

  function resetState() {
    token.value = ''
    userInfo.value = {}
    roles.value = []
    permissions.value = []
    removeToken()
  }

  return { token, userInfo, roles, permissions, login, getInfo, logout, resetState }
})

export default useUserStore
