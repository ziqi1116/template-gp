import { ref } from 'vue'
import { listDictDataByType } from '@/api/system/dict/data'

const dictCache = ref({})

export function useDict(...types) {
  const result = {}
  types.forEach(type => {
    result[type] = ref([])
    if (!dictCache.value[type]) {
      listDictDataByType(type).then(res => {
        dictCache.value[type] = res.data
        result[type].value = res.data
      })
    } else {
      result[type].value = dictCache.value[type]
    }
  })
  return result
}