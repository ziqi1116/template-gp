import request from '@/utils/request'

export function listDictData(query) {
  return request({ url: '/system/dict/data/list', method: 'get', params: query })
}

export function listDictDataByType(dictType) {
  return request({ url: '/system/dict/data/type/' + dictType, method: 'get' })
}

export function getDictData(id) {
  return request({ url: '/system/dict/data/' + id, method: 'get' })
}

export function addDictData(data) {
  return request({ url: '/system/dict/data', method: 'post', data })
}

export function updateDictData(data) {
  return request({ url: '/system/dict/data', method: 'put', data })
}

export function delDictData(ids) {
  return request({ url: '/system/dict/data/' + ids, method: 'delete' })
}