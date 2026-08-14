<template>
  <div>
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="字典标签">
          <el-input v-model="queryParams.dictLabel" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width:120px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" plain :icon="Delete" :disabled="ids.length===0" @click="handleDelete()">删除</el-button>
      </el-row>
      <el-table v-loading="loading" :data="tableData" @selection-change="sel => ids = sel.map(i=>i.id)" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="55" align="center" />
        <el-table-column label="字典标签" prop="dictLabel" width="120" />
        <el-table-column label="字典键值" prop="dictValue" width="100" />
        <el-table-column label="样式属性" prop="listClass" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.listClass" :type="row.listClass === 'primary' ? '' : row.listClass === 'success' ? 'success' : row.listClass === 'warning' ? 'warning' : row.listClass === 'info' ? 'info' : 'danger'">{{ row.listClass }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="回显样式" prop="listClass" width="100" />
        <el-table-column label="默认" prop="isDefault" width="60" align="center">
          <template #default="{ row }">{{ row.isDefault === 'Y' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" />
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
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="字典标签" prop="dictLabel"><el-input v-model="form.dictLabel" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="字典键值" prop="dictValue"><el-input v-model="form.dictValue" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="显示排序" prop="dictSort"><el-input-number v-model="form.dictSort" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="回显样式" prop="listClass"><el-select v-model="form.listClass" placeholder="请选择" style="width:100%"><el-option label="默认" value="default" /><el-option label="主要" value="primary" /><el-option label="成功" value="success" /><el-option label="警告" value="warning" /><el-option label="信息" value="info" /><el-option label="危险" value="danger" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="是否默认"><el-radio-group v-model="form.isDefault"><el-radio value="Y">是</el-radio><el-radio value="N">否</el-radio></el-radio-group></el-form-item></el-col>
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
import { listDictData, getDictData, addDictData, updateDictData, delDictData } from '@/api/system/dict/data'

const props = defineProps({
  type: { type: String, default: '' }
})

const { proxy } = getCurrentInstance()
const loading = ref(false)
const tableData = ref([])
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const queryParams = reactive({ dictType: '', dictLabel: '', status: '' })
const form = ref({})
const rules = reactive({
  dictLabel: [{ required: true, message: '字典标签不能为空', trigger: 'blur' }],
  dictValue: [{ required: true, message: '字典键值不能为空', trigger: 'blur' }]
})

onMounted(() => {
  if (props.type) { queryParams.dictType = props.type }
  getList()
})

function getList() {
  loading.value = true
  listDictData(queryParams).then(res => {
    tableData.value = res.data
  }).finally(() => loading.value = false)
}

function handleQuery() { getList() }
function resetQuery() { queryParams.dictLabel = ''; queryParams.status = ''; handleQuery() }
function reset() { form.value = { dictType: props.type || '', dictLabel: '', dictValue: '', dictSort: 0, listClass: 'default', isDefault: 'N', status: '0', remark: '' } }

function handleAdd() { reset(); proxy.$refs.formRef?.resetFields(); dialogTitle.value = '新增字典数据'; dialogVisible.value = true }
function handleUpdate(row) { reset(); getDictData(row.id).then(res => { form.value = res.data; dialogTitle.value = '修改字典数据'; dialogVisible.value = true }) }

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      updateDictData(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); dialogVisible.value = false; getList() })
    } else {
      addDictData(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); dialogVisible.value = false; getList() })
    }
  })
}

function handleDelete(row) {
  const deleteIds = row ? [row.id] : ids.value
  proxy.$modal.confirm('确认删除？').then(() => delDictData(deleteIds.join(',')))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}
</script>