import request from '@/utils/request'

// 管理员登录
export function adminLogin(username, password) {
  return request({
    url: '/api/admin/auth/login',
    method: 'post',
    data: { username, password }
  })
}

// 获取提现列表
export function getWithdrawals(params) {
  return request({
    url: '/api/admin/withdrawals',
    method: 'get',
    params
  })
}

// 审核提现
export function approveWithdrawal(id, status) {
  return request({
    url: `/api/admin/withdrawals/${id}/approve`,
    method: 'post',
    params: { status }
  })
}

// 获取产品列表
export function getProducts(params) {
  return request({
    url: '/api/admin/products',
    method: 'get',
    params
  })
}

// 创建产品
export function createProduct(data) {
  return request({
    url: '/api/admin/products',
    method: 'post',
    data
  })
}

// 更新产品
export function updateProduct(id, data) {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'put',
    data
  })
}

// 删除产品
export function deleteProduct(id) {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'delete'
  })
}

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

// 获取统计数据
export function getStats() {
  return request({
    url: '/api/admin/stats',
    method: 'get'
  })
}

// 获取订单列表
export function getOrders(params) {
  return request({
    url: '/api/admin/orders',
    method: 'get',
    params
  })
}