import { createRouter, createWebHashHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import Withdrawals from '@/views/Withdrawals.vue'
import Products from '@/views/Products.vue'
import Users from '@/views/Users.vue'
import Orders from '@/views/Orders.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    redirect: '/dashboard',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '数据看板' }
      },
      {
        path: 'withdrawals',
        name: 'Withdrawals',
        component: Withdrawals,
        meta: { title: '提现审核' }
      },
      {
        path: 'products',
        name: 'Products',
        component: Products,
        meta: { title: '产品管理' }
      },
      {
        path: 'users',
        name: 'Users',
        component: Users,
        meta: { title: '用户管理' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: Orders,
        meta: { title: '订单管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const adminStore = useAdminStore()
  if (to.meta.requiresAuth && !adminStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && adminStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
