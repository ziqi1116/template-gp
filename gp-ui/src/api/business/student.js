import request from '@/utils/request'

export function pageStudent(query) {
  return request({ url: '/business/student/page', method: 'get', params: query })
}

export function getStudent(id) {
  return request({ url: '/business/student/' + id, method: 'get' })
}

export function addStudent(data) {
  return request({ url: '/business/student', method: 'post', data })
}

export function updateStudent(data) {
  return request({ url: '/business/student', method: 'put', data })
}

export function delStudent(ids) {
  return request({ url: '/business/student/' + ids, method: 'delete' })
}

export function changeStudentStatus(data) {
  return request({ url: '/business/student/status', method: 'put', data })
}
