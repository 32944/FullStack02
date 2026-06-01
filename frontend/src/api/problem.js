import request from '../utils/request'

export function getProblemPage(params) {
  return request.post('/admin/problem/page', params)
}

export function getProblemById(id) {
  return request.get(`/admin/problem/get/${id}`)
}

export function addProblem(data) {
  return request.post('/admin/problem/add', data)
}

export function updateProblem(data) {
  return request.post('/admin/problem/update', data)
}

export function deleteProblem(id) {
  return request.delete(`/admin/problem/delete/${id}`)
}

export function updateProblemStatus(id, status) {
  return request.post('/admin/problem/status', null, {
    params: { id, status }
  })
}

export function batchAddProblems(text) {
  return request.post('/admin/problem/batchAdd', null, {
    params: { text }
  })
}

export function getCategoryList() {
  return request.get('/admin/problem/category/list')
}
