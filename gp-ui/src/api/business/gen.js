import request from '@/utils/request'

// 可生成的业务表列表
export function listGenTables() {
  return request({ url: '/gen/tables', method: 'get' })
}

// 预览生成代码
export function previewCode(data) {
  return request({ url: '/gen/preview', method: 'post', data })
}
