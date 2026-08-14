import { ref } from 'vue'
import { uploadFile } from '@/api/common/upload'

export function useUpload() {
  const uploading = ref(false)
  const url = ref('')

  function upload(file) {
    uploading.value = true
    const formData = new FormData()
    formData.append('file', file)
    return uploadFile(formData).then(res => {
      url.value = res.data.url
      return res.data
    }).finally(() => {
      uploading.value = false
    })
  }

  return { uploading, url, upload }
}