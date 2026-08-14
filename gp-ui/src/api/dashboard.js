import request from '@/utils/request'

// 数据大屏全量统计
export function getScreenData() {
  return request({ url: '/dashboard/screen', method: 'get' })
}
