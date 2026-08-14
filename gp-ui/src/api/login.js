import request from '@/utils/request'

export function login(data) {
  return request({ url: '/login', method: 'post', data })
}

export function register(data) {
  return request({ url: '/register', method: 'post', data })
}

export function getCaptcha() {
  return request({ url: '/captcha/captchaImage', method: 'get' })
}

export function logout() {
  return request({ url: '/logout', method: 'post' })
}

export function getInfo() {
  return request({ url: '/getInfo', method: 'get' })
}

export function getRouters() {
  return request({ url: '/getRouters', method: 'get' })
}
