<template>
  <div>
    <h2>User Management</h2>
    <el-table :data="users" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="Username" />
      <el-table-column prop="phone" label="Phone" />
      <el-table-column prop="role" label="Role">
        <template #default="scope">
          <el-tag :type="scope.row.role === 1 ? 'danger' : 'info'">
            {{ scope.row.role === 1 ? 'Admin' : 'User' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="Status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? 'Active' : 'Banned' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="150">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 1"
            type="danger"
            size="small"
            @click="toggleStatus(scope.row, 0)"
          >
            Ban
          </el-button>
          <el-button
            v-else
            type="success"
            size="small"
            @click="toggleStatus(scope.row, 1)"
          >
            Unban
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="100" 
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const page = ref(1)

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await api.get('/admin/users', {
      params: { page: page.value, limit: 10 }
    })
    users.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (user, status) => {
  try {
    await api.patch(`/admin/users/${user.id}/status`, { status })
    ElMessage.success('Status updated')
    fetchUsers()
  } catch (e) {
    console.error(e)
  }
}

const handlePageChange = (val) => {
  page.value = val
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
