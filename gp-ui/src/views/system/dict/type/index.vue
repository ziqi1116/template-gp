<template>
  <div>
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="字典名称">
          <el-input v-model="queryParams.dictName" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input v-model="queryParams.dictType" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <el-row class="mb8">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" plain :icon="Delete" :disabled="ids.length===0" @click="handleDelete()">删除</el-button>
      </el-row>
      <el-table v-loading="loading" :data="tableData" @selection-change="sel => ids = sel.map(i=>i.id)" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="55" align="center" />
        <el-table-column label="字典名称" prop="dictName" width="150" />
        <el-table-column label="字典类型" prop="dictType" width="150" />
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleUpdate(row)">修改</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="字典名称" prop="dictName"><el-input v-model="form.dictName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="字典类型" prop="dictType"><el-input v-model="form.dictType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="0">正常</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogVisible=false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { listDictType, getDictType, addDictType, updateDictType, delDictType } from '@/api/system/dict/type'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const tableData = ref([])
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const queryParams = reactive({ dictName: '', dictType: '' })
const form = ref({})
const rules = reactive({
  dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }],
  dictType: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }]
})

onMounted(() => getList())

function getList() {
  loading.value = true
  listDictType(queryParams).then(res => {
    tableData.value = res.data
  }).finally(() => loading.value = false)
}

function handleQuery() { getList() }
function resetQuery() { queryParams.dictName = ''; queryParams.dictType = ''; handleQuery() }
function reset() { form.value = { dictName: '', dictType: '', status: '0', remark: '' } }

function handleAdd() { reset(); proxy.$refs.formRef?.resetFields(); dialogTitle.value = '新增字典类型'; dialogVisible.value = true }
function handleUpdate(row) { reset(); getDictType(row.id).then(res => { form.value = res.data; dialogTitle.value = '修改字典类型'; dialogVisible.value = true }) }

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      updateDictType(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); dialogVisible.value = false; getList() })
    } else {
      addDictType(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); dialogVisible.value = false; getList() })
    }
  })
}

function handleDelete(row) {
  const deleteIds = row ? [row.id] : ids.value
  proxy.$modal.confirm('确认删除？').then(() => delDictType(deleteIds.join(',')))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}
</script>