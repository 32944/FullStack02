import request from '../utils/request'

export function getUserPage(params) {
  return request.post('/admin/user/page', params)
}

export function getUserById(id) {
  return request.get(`/admin/user/get/${id}`)
}

export function getUserDetail(id) {
  return request.get(`/admin/user/detail/${id}`)
}

export function updateUser(data) {
  return request.post('/admin/user/update', data)
}

export function deleteUser(id) {
  return request.delete(`/admin/user/delete/${id}`)
}

export function updateUserStatus(id, status) {
  return request.post('/admin/user/status', null, {
    params: { id, status }
  })
}

export function updateUserLevel(id, currentLevel) {
  return request.post('/admin/user/level', null, {
    params: { id, currentLevel }
  })
}
