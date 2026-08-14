<template>
  <div>
    <!-- 搜索栏 -->
    <el-card>
      <el-form :model="queryParams" :inline="true">
${searchItems}
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
${tableColumns}
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
${formItems}
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
import { ref, onMounted } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getCurrentInstance } from 'vue'
import { page${ClassName}, get${ClassName}, add${ClassName}, update${ClassName}, del${ClassName} } from '@/api/business/${module}'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = ref({ pageNum: 1, pageSize: 10${queryFields} })

const form = ref({})
const rules = {
${formRules}
}

onMounted(() => getList())

function getList() {
  loading.value = true
  page${ClassName}(queryParams.value).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
  }).finally(() => loading.value = false)
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() {${resetFields} handleQuery() }

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
}

function reset() {
  form.value = {${formReset} }
}

function handleAdd() {
  reset()
  dialogTitle.value = '新增${functionName}'
  dialogVisible.value = true
}

function handleUpdate(row) {
  reset()
  get${ClassName}(row.id).then(res => {
    form.value = res.data
    dialogTitle.value = '修改${functionName}'
    dialogVisible.value = true
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      update${ClassName}(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        dialogVisible.value = false
        getList()
      })
    } else {
      add${ClassName}(form.value).then(() => {
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
    return del${ClassName}(deleteIds.join(','))
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}
</script>

<!-- 生成于 ${date}：遵循 GP-Framework 业务模块开发规范，可自由修改 -->
