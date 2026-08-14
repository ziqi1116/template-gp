import request from '@/utils/request'

export function listRole() {
  return request({ url: '/system/role/list', method: 'get' })
}

export function getRole(id) {
  return request({ url: '/system/role/' + id, method: 'get' })
}

export function addRole(data) {
  return request({ url: '/system/role', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/system/role', method: 'put', data })
}

export function delRole(ids) {
  return request({ url: '/system/role/' + ids, method: 'delete' })
}