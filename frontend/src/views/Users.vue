<template>
  <el-card>
    <h2>用户管理</h2>
    <el-table :data="userList" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nickname" label="昵称" width="150" />
      <el-table-column prop="openid" label="OpenID" min-width="200" />
      <el-table-column prop="balance" label="余额" width="100">
        <template #default="{ row }">
          ¥{{ row.balance.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalRebate" label="累计返利" width="100">
        <template #default="{ row }">
          ¥{{ row.totalRebate.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="isAdmin" label="管理员" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isAdmin ? 'danger' : 'info'">
            {{ row.isAdmin ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="180" />
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
import { getUsers } from '@/api/admin'

const loading = ref(false)
const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUsers({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.success) {
      userList.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
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

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
