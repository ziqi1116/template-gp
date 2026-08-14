import request from '@/utils/request'

export function uploadFile(data) {
  return request({
    url: '/common/upload',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}