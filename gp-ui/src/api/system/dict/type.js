import request from '@/utils/request'

export function listDictType() {
  return request({ url: '/system/dict/type/list', method: 'get' })
}

export function getDictType(id) {
  return request({ url: '/system/dict/type/' + id, method: 'get' })
}

export function addDictType(data) {
  return request({ url: '/system/dict/type', method: 'post', data })
}

export function updateDictType(data) {
  return request({ url: '/system/dict/type', method: 'put', data })
}

export function delDictType(ids) {
  return request({ url: '/system/dict/type/' + ids, method: 'delete' })
}