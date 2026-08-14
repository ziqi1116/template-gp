import request from '@/utils/request'

export function pageUser(query) {
  return request({ url: '/system/user/page', method: 'get', params: query })
}
export function getUser(id) {
  return request({ url: '/system/user/' + id, method: 'get' })
}
export function addUser(data) {
  return request({ url: '/system/user', method: 'post', data })
}
export function updateUser(data) {
  return request({ url: '/system/user', method: 'put', data })
}
export function delUser(ids) {
  return request({ url: '/system/user/' + ids, method: 'delete' })
}
export function changeUserStatus(data) {
  return request({ url: '/system/user/status', method: 'put', data })
}
export function updateUserPwd(data) {
  return request({ url: '/system/user/profile/updatePwd', method: 'put', data })
}
