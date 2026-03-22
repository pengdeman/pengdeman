<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
          <el-icon class="stat-icon" color="#409eff"><User /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalOrders }}</div>
            <div class="stat-label">总订单数</div>
          </div>
          <el-icon class="stat-icon" color="#67c23a"><Document /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.pendingWithdrawals }}</div>
            <div class="stat-label">待审核提现</div>
          </div>
          <el-icon class="stat-icon" color="#e6a23c"><Clock /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">¥{{ stats.totalRebate.toFixed(2) }}</div>
            <div class="stat-label">累计返利</div>
          </div>
          <el-icon class="stat-icon" color="#f56c6c"><Wallet /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20">
      <h3>欢迎使用运营管理后台</h3>
      <p>使用左侧菜单进行功能操作：</p>
      <ul>
        <li>👉 <strong>提现审核</strong> - 查看并审核用户提现申请</li>
        <li>👉 <strong>产品管理</strong> - 管理商城产品，添加/编辑/删除</li>
        <li>👉 <strong>用户管理</strong> - 查看注册用户列表</li>
        <li>👉 <strong>订单管理</strong> - 查看用户订单记录</li>
      </ul>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, Clock, Wallet } from '@element-plus/icons-vue'
import { getStats } from '@/api/admin'

const stats = ref({
  totalUsers: 0,
  totalOrders: 0,
  pendingWithdrawals: 0,
  totalRebate: 0
})

const loadStats = async () => {
  try {
    const res = await getStats()
    if (res.success) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.stat-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-content .stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-content .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-icon {
  font-size: 48px;
}

.mt-20 {
  margin-top: 20px;
}

h3 {
  margin-bottom: 16px;
}

ul {
  padding-left: 20px;
}

li {
  margin-bottom: 8px;
  line-height: 2;
  color: #606266;
}
</style>
