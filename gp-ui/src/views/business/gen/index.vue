<template>
  <div>
    <!-- 说明 -->
    <el-alert type="info" :closable="false" class="mb8">
      <template #title>
        三步生成一个业务模块：选表 → 填写配置 → 下载 ZIP。解压到 gp-admin 根目录合并，执行生成的 SQL，重启后端即可看到新菜单。
        建表需遵循框架规范：包含 id / create_time / update_time / del_flag 等基础字段（参考 biz_student）。
      </template>
    </el-alert>

    <!-- 表列表 -->
    <el-card>
      <el-table v-loading="loading" :data="tables" border stripe>
        <el-table-column label="表名" prop="name" width="220" />
        <el-table-column label="表说明" prop="comment" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" plain size="small" :icon="MagicStick" @click="openConfig(row)">生 成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 配置弹窗 -->
    <el-dialog title="生成配置" v-model="configVisible" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="数据表">
          <el-input v-model="form.tableName" disabled />
        </el-form-item>
        <el-form-item label="模块名">
          <el-input v-model="form.module" placeholder="小写，如 teacher" />
        </el-form-item>
        <el-form-item label="类名">
          <el-input v-model="form.className" placeholder="大驼峰，如 Teacher" />
        </el-form-item>
        <el-form-item label="功能名">
          <el-input v-model="form.functionName" placeholder="中文，如 教师管理" />
        </el-form-item>
        <el-form-item label="菜单ID">
          <el-input-number v-model="form.menuId" :min="100" :max="9999" />
          <span class="form-tip">菜单ID段（默认 2000，冲突时调整）</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configVisible = false">取 消</el-button>
        <el-button type="primary" :loading="generating" :icon="View" @click="handlePreview">预览生成</el-button>
        <el-button type="success" :icon="Download" @click="handleDownload">下载 ZIP</el-button>
      </template>
    </el-dialog>

    <!-- 代码预览 -->
    <el-drawer v-model="previewVisible" size="62%" :title="'生成代码预览（' + Object.keys(files).length + ' 个文件）'">
      <div class="drawer-actions">
        <el-button type="success" :icon="Download" @click="handleDownload">下载 ZIP</el-button>
        <span class="form-tip">解压到 gp-admin 根目录（覆盖合并），执行 sql/ 下的菜单脚本并重启后端</span>
      </div>
      <el-tabs>
        <el-tab-pane v-for="(content, path) in files" :key="path" :label="shortName(path)" :name="path">
          <pre class="code-view">{{ content }}</pre>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MagicStick, View, Download } from '@element-plus/icons-vue'
import { listGenTables, previewCode } from '@/api/business/gen'
import { getToken } from '@/utils/auth'

const loading = ref(false)
const tables = ref([])
const configVisible = ref(false)
const previewVisible = ref(false)
const generating = ref(false)
const files = ref({})

const form = ref({ tableName: '', module: '', className: '', functionName: '', menuId: 2000 })

onMounted(() => load())

function load() {
  loading.value = true
  listGenTables().then(res => {
    tables.value = res.data || []
  }).finally(() => loading.value = false)
}

/** 打开配置：按表名预填默认值（biz_teacher → module=teacher, className=Teacher） */
function openConfig(row) {
  const pure = String(row.name).replace(/^biz_/, '')
  const camel = pure.split('_').map(p => p.charAt(0).toUpperCase() + p.slice(1)).join('')
  form.value = {
    tableName: row.name,
    module: pure.replace(/_/g, ''),
    className: camel,
    functionName: row.comment || camel,
    menuId: 2000
  }
  configVisible.value = true
}

/** 预览生成代码 */
function handlePreview() {
  generating.value = true
  previewCode(form.value).then(res => {
    files.value = res.data || {}
    configVisible.value = false
    previewVisible.value = true
  }).finally(() => generating.value = false)
}

/** 下载 ZIP（文件流用 fetch 接收，绕过 axios 对二进制的统一处理） */
async function handleDownload() {
  const resp = await fetch(import.meta.env.VITE_APP_BASE_API + '/gen/download', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + getToken()
    },
    body: JSON.stringify(form.value)
  })
  if (!resp.ok) {
    return
  }
  const blob = await resp.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'gen-' + form.value.module + '.zip'
  a.click()
  URL.revokeObjectURL(url)
}

/** 文件路径 → 短名（tab 标签用） */
function shortName(path) {
  return path.substring(path.lastIndexOf('/') + 1)
}
</script>

<style scoped>
.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

.drawer-actions {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.code-view {
  background: #282c34;
  color: #abb2bf;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 12.5px;
  line-height: 1.6;
  padding: 14px 16px;
  border-radius: 6px;
  max-height: calc(100vh - 260px);
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
