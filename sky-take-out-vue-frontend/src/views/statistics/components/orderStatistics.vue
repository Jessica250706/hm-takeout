<template>
  <div class="container">
    <h2 class="homeTitle">订单统计</h2>
    <div class="charBox">
      <div class="orderProportion">
        <div>
          <p>订单完成率</p>
          <p>{{ completionRateText }}%</p>
        </div>
        <div class="symbol">=</div>
        <div>
          <p>有效订单</p>
          <p>{{ orderdata.validOrderCount }}</p>
        </div>
        <div class="symbol">/</div>
        <div>
          <p>订单总数</p>
          <p>{{ orderdata.totalOrderCount }}</p>
        </div>
      </div>
      <div ref="chartRef" style="width: 100%; height: 300px"></div>
      <ul class="orderListLine">
        <li class="one">
          <span></span>
          订单总数（个）
        </li>
        <li class="three">
          <span></span>
          有效订单（个）
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

defineOptions({
  name: 'OrderStatistics',
})

const props = withDefaults(
  defineProps<{
    orderdata?: any
    overviewData?: any
  }>(),
  {
    orderdata: () => ({}),
    overviewData: () => ({}),
  },
)

// ---- 完成率展示（避免模板里算术类型报错） ----
const completionRateText = computed(() => {
  const rate = Number(props.orderdata?.orderCompletionRate)
  return Number.isFinite(rate) ? (rate * 100).toFixed(1) : '0.0'
})

// ---- echarts ----
const chartRef = ref<HTMLDivElement | null>(null)
let myChart: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return

  // 同一个 DOM 只 init 一次，避免 ECharts 重复 init 警告
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
      data: props.orderdata?.data?.dateList ?? [],
    },
    yAxis: [
      {
        type: 'value',
        min: 0,
        interval: 50,
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
        name: '订单总数',
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
        data: props.orderdata?.data?.orderCountList ?? [],
      },
      {
        name: '有效订单',
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
        data: props.orderdata?.data?.validOrderCountList ?? [],
      },
    ],
  }

  myChart.setOption(option, true) // true = 不合并，直接替换，适合重新渲染
}

// ---- watch：orderdata 变化后重新渲染 ----
watch(
  () => props.orderdata,
  () => {
    nextTick(() => initChart())
  },
  { deep: true },
)

// ---- 生命周期 ----
onMounted(() => {
  if (props.orderdata?.data?.dateList?.length) {
    nextTick(() => initChart())
  }
})

onBeforeUnmount(() => {
  myChart?.dispose()
  myChart = null
})
</script>
