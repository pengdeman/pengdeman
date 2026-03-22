<template>
  <el-card>
    <h2>提现审核</h2>
    <el-table :data="withdrawalList" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="amount" label="提现金额" width="120">
        <template #default="{ row }">
          <span class="amount">¥{{ row.amount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="bankName" label="银行" width="100" />
      <el-table-column prop="bankCardNumber" label="银行卡号" width="180" />
      <el-table-column prop="bankCardHolder" label="持卡人" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING'"
            type="success"
            size="small"
            @click="handleApprove(row, 'APPROVED')"
          >
            通过
          </el-button>
          <el-button
            v-if="row.status === 'PENDING'"
            type="danger"
            size="small"
            @click="handleApprove(row, 'REJECTED')"
          >
            拒绝
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @change="loadData"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWithdrawals, approveWithdrawal } from '@/api/admin'

const loading = ref(false)
const withdrawalList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getWithdrawals({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.success) {
      withdrawalList.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('加载提现列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row, status) => {
  const action = status === 'APPROVED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(
      `确认${action}这笔提现申请吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await approveWithdrawal(row.id, status)
    if (res.success) {
      ElMessage.success(`已${action}`)
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}

.amount {
  color: #e4393c;
  font-weight: bold;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
