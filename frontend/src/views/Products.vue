<template>
  <el-card>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加产品
      </el-button>
    </div>
    <el-table :data="productList" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="产品名称" min-width="200" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="originalPrice" label="原价" width="100">
        <template #default="{ row }">
          ¥{{ row.originalPrice.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="rebateRate" label="返利比例" width="100">
        <template #default="{ row }">
          {{ row.rebateRate }}%
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="active" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.active ? 'success' : 'info'">
            {{ row.active ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @change="loadData"
      />
    </div>
  </el-card>

  <!-- 添加/编辑对话框 -->
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑产品' : '添加产品'"
    width="500px"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="产品名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入产品名称" />
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="formData.price" :precision="2" :min="0" placeholder="价格" />
      </el-form-item>
      <el-form-item label="原价" prop="originalPrice">
        <el-input-number v-model="formData.originalPrice" :precision="2" :min="0" placeholder="原价" />
      </el-form-item>
      <el-form-item label="返利比例" prop="rebateRate">
        <el-input-number v-model="formData.rebateRate" :min="0" :max="100" placeholder="返利比例%" />
      </el-form-item>
      <el-form-item label="库存" prop="stock">
        <el-input-number v-model="formData.stock" :min="0" placeholder="库存" />
      </el-form-item>
      <el-form-item label="图片URL" prop="imageUrl">
        <el-input v-model="formData.imageUrl" placeholder="产品图片URL" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="产品描述"
        />
      </el-form-item>
      <el-form-item label="是否上架" prop="active">
        <el-switch v-model="formData.active" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getProducts, createProduct, updateProduct, deleteProduct } from '@/api/admin'

const loading = ref(false)
const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  name: '',
  price: 0,
  originalPrice: 0,
  rebateRate: 0,
  stock: 0,
  imageUrl: '',
  description: '',
  active: true
})

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getProducts({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.success) {
      productList.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('加载产品列表失败', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.id = null
  formData.name = ''
  formData.price = 0
  formData.originalPrice = 0
  formData.rebateRate = 0
  formData.stock = 0
  formData.imageUrl = ''
  formData.description = ''
  formData.active = true
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateProduct(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createProduct(formData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('保存失败', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除产品 "${row.name}" 吗？此操作不可撤销。`,
      '确认删除',
      { type: 'warning' }
    )
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}

h2 {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
