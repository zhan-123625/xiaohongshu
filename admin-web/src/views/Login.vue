<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>Xiaohongshu Admin</h2>
        </div>
      </template>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.phone" placeholder="Phone Number" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="Password" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">Login</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({
  phone: '',
  password: ''
})
const loading = ref(false)

const handleLogin = async () => {
  if (!form.value.phone || !form.value.password) {
    ElMessage.warning('Please enter phone and password')
    return
  }
  
  loading.value = true
  try {
    // Using the same login endpoint as the app
    const res = await api.post('/auth/login', form.value)
    const token = res.data.access_token
    localStorage.setItem('admin_token', token)
    ElMessage.success('Login successful')
    router.push('/')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
}
</style>
