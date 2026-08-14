import request from '@/utils/request'

// ${functionName}分页查询
export function page${ClassName}(query) {
  return request({ url: '/${module}/page', method: 'get', params: query })
}

// ${functionName}详情
export function get${ClassName}(id) {
  return request({ url: '/${module}/' + id, method: 'get' })
}

// 新增${functionName}
export function add${ClassName}(data) {
  return request({ url: '/${module}', method: 'post', data })
}

// 修改${functionName}
export function update${ClassName}(data) {
  return request({ url: '/${module}', method: 'put', data })
}

// 删除${functionName}
export function del${ClassName}(ids) {
  return request({ url: '/${module}/' + ids, method: 'delete' })
}
