<template>
  <div>
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="系统模块">
          <el-input v-model="queryParams.title" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="queryParams.operName" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="操作状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width:120px">
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <el-row class="mb8">
        <el-button type="danger" plain :icon="Delete" :disabled="ids.length===0" @click="handleDelete()">删除</el-button>
        <el-button type="warning" plain :icon="Delete" @click="handleClean()">清空</el-button>
      </el-row>
      <el-table v-loading="loading" :data="tableData" @selection-change="sel => ids = sel.map(i=>i.id)" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="55" align="center" />
        <el-table-column label="系统模块" prop="title" width="150" />
        <el-table-column label="操作人员" prop="operName" width="100" />
        <el-table-column label="操作类型" prop="businessType" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.businessType === '0'" type="primary">新增</el-tag>
            <el-tag v-else-if="row.businessType === '1'" type="success">修改</el-tag>
            <el-tag v-else-if="row.businessType === '2'" type="warning">删除</el-tag>
            <el-tag v-else-if="row.businessType === '3'" type="info">授权</el-tag>
            <el-tag v-else type="danger">导出</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请求方式" prop="requestMethod" width="100" />
        <el-table-column label="操作IP" prop="operIp" width="130" />
        <el-table-column label="操作地点" prop="operLocation" width="130" />
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作时间" prop="operTime" width="160" />
        <el-table-column label="消耗时间" prop="costTime" width="100" align="center">
          <template #default="{ row }">{{ row.costTime }} ms</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10,20,50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        class="pagination" @size-change="getList" @current-change="getList" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { listOperLog, delOperLog, cleanOperLog } from '@/api/monitor/operlog'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const ids = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, title: '', operName: '', status: '' })

onMounted(() => getList())

function getList() {
  loading.value = true
  listOperLog(queryParams).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
  }).finally(() => loading.value = false)
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryParams.title = ''; queryParams.operName = ''; queryParams.status = ''; handleQuery() }

function handleDelete(row) {
  const deleteIds = row ? [row.id] : ids.value
  proxy.$modal.confirm('确认删除？').then(() => delOperLog(deleteIds.join(',')))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}

function handleClean() {
  proxy.$modal.confirm('确认清空所有操作日志？').then(() => cleanOperLog())
    .then(() => { getList(); proxy.$modal.msgSuccess('清空成功') }).catch(() => {})
}
</script>