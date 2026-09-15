<template>
  <div class="container top10">
    <h2 class="homeTitle">销量排名TOP10</h2>
    <div class="charBox">
      <div ref="chartRef" style="width: 100%; height: 380px"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

defineOptions({
  name: 'Top',
})

const props = withDefaults(
  defineProps<{
    top10data?: any
  }>(),
  {
    top10data: () => ({}),
  },
)

// ---- echarts ----
const chartRef = ref<HTMLDivElement | null>(null)
let myChart: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return

  // 缓存实例，避免重复 init 报警告
  if (!myChart) {
    myChart = echarts.init(chartRef.value)
  }

  const option: any = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderRadius: 2,
      textStyle: {
        color: '#333',
        fontSize: 12,
        fontWeight: 300,
      },
    },
    grid: {
      top: '-10px',
      left: '0',
      right: '0',
      bottom: '0',
      containLabel: true,
    },
    xAxis: {
      show: false,
    },
    yAxis: {
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
        alignWithLabel: true,
      },
      type: 'category',
      axisLabel: {
        textStyle: {
          color: '#666',
          fontSize: '12px',
        },
      },
      data: props.top10data?.nameList ?? [],
    },
    series: [
      {
        data: props.top10data?.numberList ?? [],
        type: 'bar',
        showBackground: true,
        backgroundStyle: {
          color: '#F3F4F7',
        },
        barWidth: 20,
        barGap: '80%',
        barCategoryGap: '80%',
        itemStyle: {
          emphasis: {
            barBorderRadius: 30,
          },
          normal: {
            barBorderRadius: [0, 10, 10, 0],
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#FFBD00' },
              { offset: 1, color: '#FFD000' },
            ]),
            label: {
              show: true,
              formatter: '{@score}',
              color: '#333',
              position: ['8', '5'],
            },
          },
        },
      },
    ],
  }

  myChart.setOption(option, true) // true = 不合并，直接替换
}

// ---- watch：top10data 变化后重新渲染 ----
watch(
  () => props.top10data,
  () => {
    nextTick(() => initChart())
  },
  { deep: true },
)

// ---- 生命周期 ----
onMounted(() => {
  if (props.top10data?.nameList?.length) {
    nextTick(() => initChart())
  }
})

onBeforeUnmount(() => {
  myChart?.dispose()
  myChart = null
})
</script>
