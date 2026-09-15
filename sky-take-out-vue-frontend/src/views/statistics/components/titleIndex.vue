<template>
  <div class="title-index">
    <div class="month">
      <ul class="tabs">
        <li
          v-for="(item, index) in tabsParam"
          :key="index"
          class="li-tab"
          :class="{ active: index === nowIndex }"
          @click="toggleTabs(index)"
        >
          {{ item }}
          <span></span>
        </li>
      </ul>
    </div>
    <div class="get-time">
      <p>
        已选时间：{{ tateData[0] }} 至
        {{ tateData[tateData.length - 1] }}
      </p>
    </div>
    <el-button icon="iconfont icon-download" class="right-el-button" @click="handleExport">
      数据导出
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import { exportInfor } from '@/api/index'

defineOptions({
  name: 'TitleIndex',
})

const props = withDefaults(
  defineProps<{
    flag?: any
    tateData?: any
    turnoverData?: any
  }>(),
  {
    flag: 2,
    tateData: () => [],
    turnoverData: () => ({}),
  },
)

const emit = defineEmits<{
  (e: 'sendTitleInd', index: number): void
}>()

// ---- state ----
const nowIndex = ref(2 - 1)
const value = ref<any[]>([])
const tabsParam = ['昨日', '近7日', '近30日', '本周', '本月']

// ---- watch：flag 变化时同步选中的 tab ----
watch(
  () => props.flag,
  (val) => {
    nowIndex.value = Number(val) || 0
  },
)

// ---- 方法 ----
// tab 切换
const toggleTabs = (index: number) => {
  nowIndex.value = index
  value.value = []
  emit('sendTitleInd', index + 1)
}

// 数据导出
const handleExport = async () => {
  try {
    await ElMessageBox.confirm('是否确认导出最近30天运营数据?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const { data } = await exportInfor()
    const url = window.URL.createObjectURL(data)
    const a = document.createElement('a')
    document.body.appendChild(a)
    a.href = url
    a.download = '运营数据统计报表.xlsx'
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
  } catch {
    // 用户取消，无需处理
  }
}
</script>
