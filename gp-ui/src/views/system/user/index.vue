<template>
  <div>
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userName" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
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
        <el-table-column label="用户名" prop="userName" width="100" />
        <el-table-column label="昵称" prop="nickName" width="100" />
        <el-table-column label="手机号" prop="phone" width="130" />
        <el-table-column label="邮箱" prop="email" width="180" />
        <el-table-column label="性别" prop="sex" width="60" align="center">
          <template #default="{ row }">{{ row.sex === '0' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleUpdate(row)">修改</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)" v-if="row.id!==1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10,20,50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        class="pagination" @size-change="getList" @current-change="getList" />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="用户名" prop="userName"><el-input v-model="form.userName" :disabled="form.id!=null" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="昵称" prop="nickName"><el-input v-model="form.nickName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="form.sex" style="width:100%"><el-option label="男" value="0" /><el-option label="女" value="1" /></el-select></el-form-item></el-col>
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
import { pageUser, getUser, addUser, updateUser, delUser, changeUserStatus } from '@/api/system/user'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const queryParams = reactive({ pageNum:1, pageSize:10, userName:'', phone:'' })
const form = ref({})
const rules = reactive({
  userName: [{ required:true, message:'用户名不能为空', trigger:'blur' }],
  nickName: [{ required:true, message:'昵称不能为空', trigger:'blur' }]
})

onMounted(() => getList())

function getList() {
  loading.value = true
  pageUser(queryParams).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
  }).finally(() => loading.value = false)
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryParams.userName=''; queryParams.phone=''; handleQuery() }
function reset() { form.value = { userName:'', nickName:'', phone:'', email:'', sex:'0', status:'0' } }

function handleAdd() { reset(); proxy.$refs.formRef?.resetFields(); dialogTitle.value='新增用户'; dialogVisible.value=true }
function handleUpdate(row) { reset(); getUser(row.id).then(res => { form.value = res.data; dialogTitle.value='修改用户'; dialogVisible.value=true }) }

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      updateUser(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); dialogVisible.value=false; getList() })
    } else {
      addUser(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); dialogVisible.value=false; getList() })
    }
  })
}

function handleDelete(row) {
  const deleteIds = row ? [row.id] : ids.value
  proxy.$modal.confirm('确认删除？').then(() => delUser(deleteIds.join(',')))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{})
}

function handleStatusChange(row) {
  changeUserStatus({ id: row.id, status: row.status }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}
</script>
