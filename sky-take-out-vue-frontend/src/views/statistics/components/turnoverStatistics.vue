<template>
  <div class="container">
    <h2 class="homeTitle">营业额统计</h2>
    <div class="charBox">
      <div ref="chartRef" style="width: 100%; height: 320px"></div>
      <ul class="orderListLine turnover">
        <li>营业额(元)</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

defineOptions({
  name: 'TurnoverStatistics',
})

const props = withDefaults(
  defineProps<{
    turnoverdata?: any
  }>(),
  {
    turnoverdata: () => ({}), // 对象必须用工厂函数
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
    },
    grid: {
      top: '5%',
      left: '10',
      right: '50',
      bottom: '12%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      axisLabel: {
        textStyle: {
          color: '#666',
          fontSize: '12px',
        },
      },
      axisLine: {
        lineStyle: {
          color: '#E5E4E4',
          width: 1,
        },
      },
      data: props.turnoverdata?.dateList ?? [],
    },
    yAxis: [
      {
        type: 'value',
        min: 0,
        axisLabel: {
          textStyle: {
            color: '#666',
            fontSize: '12px',
          },
        },
      },
    ],
    series: [
      {
        name: '营业额',
        type: 'line',
        smooth: false,
        showSymbol: false,
        symbolSize: 10,
        itemStyle: {
          normal: {
            color: '#F29C1B',
            lineStyle: {
              color: '#FFD000',
            },
          },
          emphasis: {
            color: '#fff',
            borderWidth: 5,
            borderColor: '#FFC100',
          },
        },
        data: props.turnoverdata?.turnoverList ?? [],
      },
    ],
  }

  myChart.setOption(option, true)
}

// ---- watch：turnoverdata 变化后重新渲染 ----
watch(
  () => props.turnoverdata,
  () => {
    nextTick(() => initChart())
  },
  { deep: true },
)

// ---- 生命周期 ----
onMounted(() => {
  if (props.turnoverdata?.dateList?.length) {
    nextTick(() => initChart())
  }
})

onBeforeUnmount(() => {
  myChart?.dispose()
  myChart = null
})
</script>
