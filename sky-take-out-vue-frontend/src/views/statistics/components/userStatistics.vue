<template>
  <div class="container">
    <h2 class="homeTitle">用户统计</h2>
    <div class="charBox">
      <div ref="chartRef" style="width: 100%; height: 320px"></div>
      <ul class="orderListLine user">
        <li class="one">
          <span></span>
          用户总量（个）
        </li>
        <li class="three">
          <span></span>
          新增用户（个）
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

defineOptions({
  name: 'UserStatistics',
})

const props = withDefaults(
  defineProps<{
    userdata?: any
  }>(),
  {
    userdata: () => ({}), // 对象必须用工厂函数
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
      top: '5%',
      left: '20',
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
      data: props.userdata?.dateList ?? [],
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
        name: '用户总量',
        type: 'line',
        smooth: false,
        showSymbol: false,
        symbolSize: 10,
        itemStyle: {
          normal: {
            color: '#FFD000',
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
        data: props.userdata?.totalUserList ?? [],
      },
      {
        name: '新增用户',
        type: 'line',
        smooth: false,
        showSymbol: false,
        symbolSize: 10,
        itemStyle: {
          normal: {
            color: '#FD7F7F',
            lineStyle: {
              color: '#FD7F7F',
            },
          },
          emphasis: {
            color: '#fff',
            borderWidth: 5,
            borderColor: '#FD7F7F',
          },
        },
        data: props.userdata?.newUserList ?? [],
      },
    ],
  }

  myChart.setOption(option, true)
}

// ---- watch：userdata 变化后重新渲染 ----
watch(
  () => props.userdata,
  () => {
    nextTick(() => initChart())
  },
  { deep: true },
)

// ---- 生命周期 ----
onMounted(() => {
  if (props.userdata?.dateList?.length) {
    nextTick(() => initChart())
  }
})

onBeforeUnmount(() => {
  myChart?.dispose()
  myChart = null
})
</script>
