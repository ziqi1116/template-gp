import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

let loadingInstance = null

export default {
  msgSuccess(msg) { ElMessage.success(msg) },
  msgError(msg) { ElMessage.error(msg) },
  msgWarning(msg) { ElMessage.warning(msg) },
  confirm(msg) {
    return ElMessageBox.confirm(msg, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  },
  loading(msg) { loadingInstance = ElLoading.service({ text: msg, background: 'rgba(0,0,0,0.5)' }) },
  closeLoading() { if (loadingInstance) loadingInstance.close() }
}
