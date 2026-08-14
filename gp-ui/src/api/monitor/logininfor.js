import request from '@/utils/request'

export function listLogininfor(query) {
  return request({ url: '/monitor/logininfor/page', method: 'get', params: query })
}

export function delLogininfor(ids) {
  return request({ url: '/monitor/logininfor/' + ids, method: 'delete' })
}

export function cleanLogininfor() {
  return request({ url: '/monitor/logininfor/clean', method: 'delete' })
}

export function unlockUser(userId) {
  return request({ url: '/monitor/logininfor/unlock/' + userId, method: 'put' })
}