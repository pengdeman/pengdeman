<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>小满小世界</h2>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#2d3748"
        text-color="#fff"
        active-text-color="#e4393c"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>
        <el-menu-item index="/withdrawals">
          <el-icon><Wallet /></el-icon>
          <template #title>提现审核</template>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <template #title>产品管理</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        <el-menu-item @click="handleLogout" class="logout-item">
          <el-icon><SwitchButton /></el-icon>
          <template #title>退出登录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="breadcrumb">
          <span>{{ currentTitle }}</span>
        </div>
        <div class="user-info">
          <span>管理员</span>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { Odometer, Wallet, Goods, User, Document, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const adminStore = useAdminStore()

const currentTitle = computed(() => {
  return route.meta.title || '运营管理后台'
})

const handleLogout = () => {
  adminStore.logout()
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #2d3748;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1a202c;
}

.logo h2 {
  color: #fff;
  font-size: 16px;
  margin: 0;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.breadcrumb {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.user-info {
  color: #606266;
}

.main-content {
  background-color: #f5f5f5;
  padding: 20px;
}

.logout-item {
  position: absolute;
  bottom: 20px;
  width: calc(100% - 20px);
}
</style>
