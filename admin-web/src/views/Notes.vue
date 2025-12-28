<template>
  <div>
    <h2>Note Management</h2>
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="fetchNotes">
        <el-radio-button label="">All</el-radio-button>
        <el-radio-button :label="2">Pending</el-radio-button>
        <el-radio-button :label="1">Published</el-radio-button>
        <el-radio-button :label="3">Rejected</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="notes" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="Cover" width="100">
        <template #default="scope">
          <el-image 
            style="width: 80px; height: 80px"
            :src="scope.row.coverUrl" 
            fit="cover"
            :preview-src-list="[scope.row.coverUrl]"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="Title" show-overflow-tooltip />
      <el-table-column prop="user.username" label="Author" width="120" />
      <el-table-column prop="status" label="Status" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="200">
        <template #default="scope">
          <div v-if="scope.row.status === 2">
            <el-button type="success" size="small" @click="auditNote(scope.row, 1)">Pass</el-button>
            <el-button type="danger" size="small" @click="openRejectDialog(scope.row)">Reject</el-button>
          </div>
          <div v-else>
            <el-button type="text" disabled>Audited</el-button>
          </div>
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

    <el-dialog v-model="rejectDialogVisible" title="Reject Reason">
      <el-input
        v-model="rejectReason"
        type="textarea"
        placeholder="Please enter the reason for rejection"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="confirmReject">Confirm</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const notes = ref([])
const loading = ref(false)
const page = ref(1)
const statusFilter = ref(2) // Default to Pending
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentNote = ref(null)

const fetchNotes = async () => {
  loading.value = true
  try {
    const params = { page: page.value, limit: 10 }
    if (statusFilter.value !== '') {
      params.status = statusFilter.value
    }
    const res = await api.get('/admin/notes', { params })
    notes.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: 'Draft', 1: 'Published', 2: 'Pending', 3: 'Rejected' }
  return map[status] || 'Unknown'
}

const auditNote = async (note, status, reason = '') => {
  try {
    await api.patch(`/admin/notes/${note.id}/audit`, { status, reason })
    ElMessage.success('Audit successful')
    fetchNotes()
  } catch (e) {
    console.error(e)
  }
}

const openRejectDialog = (note) => {
  currentNote.value = note
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('Please enter a reason')
    return
  }
  await auditNote(currentNote.value, 3, rejectReason.value)
  rejectDialogVisible.value = false
}

const handlePageChange = (val) => {
  page.value = val
  fetchNotes()
}

onMounted(() => {
  fetchNotes()
})
</script>

<style scoped>
.filter-bar {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
