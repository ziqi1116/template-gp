<template>
  <div>
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="部门名称">
          <el-input v-model="queryParams.deptName" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <el-row class="mb8">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd()">新增</el-button>
        <el-button type="danger" plain :icon="Delete" :disabled="ids.length===0" @click="handleDelete()">删除</el-button>
      </el-row>
      <el-table v-loading="loading" :data="tableData" row-key="id" :tree-props="{ children: 'children' }" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="部门名称" prop="deptName" min-width="160" />
        <el-table-column label="排序" prop="orderNum" width="70" align="center" />
        <el-table-column label="负责人" prop="leader" width="100" />
        <el-table-column label="联系电话" prop="phone" width="130" />
        <el-table-column label="邮箱" prop="email" width="180" />
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Plus" @click="handleAdd(row)">新增</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleUpdate(row)">修改</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select v-model="form.parentId" :data="deptTree" :props="{ label: 'deptName', value: 'id' }" check-strictly :render-after-expand="false" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="部门名称" prop="deptName"><el-input v-model="form.deptName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序" prop="orderNum"><el-input-number v-model="form.orderNum" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="0">正常</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="负责人"><el-input v-model="form.leader" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
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
import { listDept, listDeptTree, getDept, addDept, updateDept, delDept } from '@/api/system/dept'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const tableData = ref([])
const deptTree = ref([])
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const queryParams = reactive({ deptName: '' })
const form = ref({})
const rules = reactive({
  deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }]
})

onMounted(() => { getList(); getDeptTree() })

function getList() {
  loading.value = true
  listDept(queryParams).then(res => {
    tableData.value = res.data
  }).finally(() => loading.value = false)
}

function getDeptTree() {
  listDeptTree().then(res => {
    deptTree.value = res.data
  })
}

function handleQuery() { getList() }
function resetQuery() { queryParams.deptName = ''; handleQuery() }
function reset() { form.value = { parentId: 0, deptName: '', orderNum: 0, status: '0', leader: '', phone: '', email: '' } }

function handleAdd(row) {
  reset(); proxy.$refs.formRef?.resetFields()
  if (row) { form.value.parentId = row.id }
  dialogTitle.value = '新增部门'; dialogVisible.value = true
}
function handleUpdate(row) {
  reset(); getDept(row.id).then(res => { form.value = res.data; dialogTitle.value = '修改部门'; dialogVisible.value = true })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      updateDept(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); dialogVisible.value = false; getList() })
    } else {
      addDept(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); dialogVisible.value = false; getList() })
    }
  })
}

function handleDelete(row) {
  const deleteIds = row ? row.id : ids.value.join(',')
  proxy.$modal.confirm('确认删除？').then(() => delDept(deleteIds))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}
</script>