<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>Total Users</template>
          <div class="stat-value">{{ stats.userCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>Total Notes</template>
          <div class="stat-value">{{ stats.noteCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>Pending Audit</template>
          <div class="stat-value warning">{{ stats.pendingNotes }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const stats = ref({
  userCount: 0,
  noteCount: 0,
  pendingNotes: 0
})

const fetchStats = async () => {
  try {
    const res = await api.get('/admin/stats/overview')
    stats.value = res.data
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.stat-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}
.warning {
  color: #e6a23c;
}
</style>
