<template>
  <div>
    <el-card>
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入" clearable style="width:160px" @keyup.enter="handleQuery" />
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
        <el-table-column label="菜单名称" prop="menuName" min-width="160" />
        <el-table-column label="排序" prop="orderNum" width="70" align="center" />
        <el-table-column label="路径" prop="path" width="140" />
        <el-table-column label="组件" prop="component" width="160" />
        <el-table-column label="类型" prop="menuType" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 'M'" type="primary">目录</el-tag>
            <el-tag v-else-if="row.menuType === 'C'" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示" prop="visible" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visible === '0' ? 'success' : 'danger'">{{ row.visible === '0' ? '显示' : '隐藏' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" />
          </template>
        </el-table-column>
        <el-table-column label="权限标识" prop="perms" width="160" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Plus" @click="handleAdd(row)">新增</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleUpdate(row)">修改</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级菜单" prop="parentId">
              <el-tree-select v-model="form.parentId" :data="menuTree" :props="{ label: 'menuName', value: 'id' }" check-strictly :render-after-expand="false" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">菜单</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="菜单名称" prop="menuName"><el-input v-model="form.menuName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序" prop="orderNum"><el-input-number v-model="form.orderNum" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="路径" prop="path"><el-input v-model="form.path" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="组件" prop="component"><el-input v-model="form.component" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="显示"><el-radio-group v-model="form.visible"><el-radio value="0">显示</el-radio><el-radio value="1">隐藏</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="0">正常</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="权限标识" prop="perms"><el-input v-model="form.perms" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="图标" prop="icon"><el-input v-model="form.icon" /></el-form-item></el-col>
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
import { listMenu, listMenuTree, getMenu, addMenu, updateMenu, delMenu } from '@/api/system/menu'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const tableData = ref([])
const menuTree = ref([])
const ids = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const queryParams = reactive({ menuName: '' })
const form = ref({})
const rules = reactive({
  menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  menuType: [{ required: true, message: '菜单类型不能为空', trigger: 'change' }]
})

onMounted(() => { getList(); getMenuTree() })

function getList() {
  loading.value = true
  listMenu(queryParams).then(res => {
    tableData.value = res.data
  }).finally(() => loading.value = false)
}

function getMenuTree() {
  listMenuTree().then(res => {
    menuTree.value = res.data
  })
}

function handleQuery() { getList() }
function resetQuery() { queryParams.menuName = ''; handleQuery() }
function reset() { form.value = { parentId: 0, menuType: 'C', menuName: '', orderNum: 0, path: '', component: '', visible: '0', status: '0', perms: '', icon: '' } }

function handleAdd(row) {
  reset(); proxy.$refs.formRef?.resetFields()
  if (row) { form.value.parentId = row.id }
  dialogTitle.value = '新增菜单'; dialogVisible.value = true
}
function handleUpdate(row) {
  reset(); getMenu(row.id).then(res => { form.value = res.data; dialogTitle.value = '修改菜单'; dialogVisible.value = true })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    if (form.value.id != null) {
      updateMenu(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); dialogVisible.value = false; getList() })
    } else {
      addMenu(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); dialogVisible.value = false; getList() })
    }
  })
}

function handleDelete(row) {
  const deleteIds = row ? row.id : ids.value.join(',')
  proxy.$modal.confirm('确认删除？').then(() => delMenu(deleteIds))
    .then(() => { getList(); proxy.$modal.msgSuccess('删除成功') }).catch(() => {})
}
</script>