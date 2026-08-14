import request from '@/utils/request'

export function listDept() {
  return request({ url: '/system/dept/list', method: 'get' })
}

export function listDeptTree() {
  return request({ url: '/system/dept/tree', method: 'get' })
}

export function getDept(id) {
  return request({ url: '/system/dept/' + id, method: 'get' })
}

export function addDept(data) {
  return request({ url: '/system/dept', method: 'post', data })
}

export function updateDept(data) {
  return request({ url: '/system/dept', method: 'put', data })
}

export function delDept(id) {
  return request({ url: '/system/dept/' + id, method: 'delete' })
}