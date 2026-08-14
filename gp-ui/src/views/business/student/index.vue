<template>
  <div>
    <!-- 搜索栏 -->
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable style="width:180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入姓名" clearable style="width:180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
            <el-option label="启用" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 + 表格 -->
    <el-card style="margin-top:16px">
      <el-row :gutter="10" class="mb8">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" plain :icon="Delete" :disabled="ids.length===0" @click="handleDelete()">批量删除</el-button>
      </el-row>

      <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="55" align="center" />
        <el-table-column label="学号" prop="studentNo" width="100" />
        <el-table-column label="姓名" prop="studentName" width="80" />
        <el-table-column label="性别" prop="gender" width="60" align="center">
          <template #default="{ row }">
            <span>{{ row.gender === '0' ? '男' : row.gender === '1' ? '女' : '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="电话" prop="phone" width="130" />
        <el-table-column label="班级" prop="className" width="120" />
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleUpdate(row)">修改</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/修改弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择" style="width:100%">
                <el-option label="男" value="0" />
                <el-option label="女" value="1" />
                <el-option label="未知" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
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
import { pageStudent, getStudent, addStudent, updateStudent, delStudent, changeStudentStatus } from '@/api/business/student'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, studentNo: '', studentName: '', status: '' })

const form = ref({})
const rules = reactive({
  studentNo: [{ required: true, message: '学号不能为空', trigger: 'blur' }],
  studentName: [{ required: true, message: '姓名不能为空', trigger: 'blur' }]
})

onMounted(() => getList())

function getList() {
  loading.value = true
  pageStudent(queryParams).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
  }).finally(() => loading.value = false)
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryParams.studentNo=''; queryParams.studentName=''; queryParams.status=''; handleQuery() }

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
}

function reset() {
  form.value = { studentNo: '', studentName: '', gender: '0', phone: '', className: '', email: '', status: '0' }
}

function handleAdd() {
  reset()
  proxy.$refs.formRef?.resetFields()
  dialogTitle.value = '新增学生'
  dialogVisible.value = true
}

function handleUpdate(row) {
  reset()
  getStudent(row.id).then(res => {
    form.value = res.data
    dialogTitle.value = '修改学生'
    dialogVisible.value = true
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      updateStudent(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        dialogVisible.value = false
        getList()
      })
    } else {
      addStudent(form.value).then(() => {
        proxy.$modal.msgSuccess('新增成功')
        dialogVisible.value = false
        getList()
      })
    }
  })
}

function handleDelete(row) {
  const deleteIds = row ? [row.id] : ids.value
  proxy.$modal.confirm('确认删除选中的' + deleteIds.length + '条数据？').then(() => {
    return delStudent(deleteIds.join(','))
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function handleStatusChange(row) {
  const text = row.status === '0' ? '启用' : '停用'
  proxy.$modal.confirm('确认' + text + '该学生？').then(() => {
    return changeStudentStatus({ id: row.id, status: row.status })
  }).then(() => proxy.$modal.msgSuccess(text + '成功')).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}
</script>
