import request from '@/utils/request'

export function listOperLog(query) {
  return request({ url: '/monitor/operlog/page', method: 'get', params: query })
}

export function delOperLog(ids) {
  return request({ url: '/monitor/operlog/' + ids, method: 'delete' })
}

export function cleanOperLog() {
  return request({ url: '/monitor/operlog/clean', method: 'delete' })
}