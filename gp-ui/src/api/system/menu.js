import request from '@/utils/request'

export function listMenu() {
  return request({ url: '/system/menu/list', method: 'get' })
}

export function listMenuTree() {
  return request({ url: '/system/menu/tree', method: 'get' })
}

export function getMenu(id) {
  return request({ url: '/system/menu/' + id, method: 'get' })
}

export function addMenu(data) {
  return request({ url: '/system/menu', method: 'post', data })
}

export function updateMenu(data) {
  return request({ url: '/system/menu', method: 'put', data })
}

export function delMenu(id) {
  return request({ url: '/system/menu/' + id, method: 'delete' })
}

export function roleMenu(roleId) {
  return request({ url: '/system/menu/roleMenu/' + roleId, method: 'get' })
}

export function updateRoleMenu(data) {
  return request({ url: '/system/menu/roleMenu', method: 'put', data })
}