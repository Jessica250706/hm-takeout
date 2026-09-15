<template>
  <div class="dashboard-container home">
    <!-- 标题 -->
    <TitleIndex :flag="flag" :tate-data="tateData" @sendTitleInd="getTitleNum" />
    <div class="homeMain">
      <!-- 营业额统计 -->
      <TurnoverStatistics :turnover-data="turnoverData" />
      <!-- 用户统计 -->
      <UserStatistics :user-data="userData" />
    </div>
    <div class="homeMain homecon">
      <!-- 订单统计 -->
      <OrderStatistics :order-data="orderData" :overview-data="overviewData" />
      <!-- 销量排名 TOP10 -->
      <Top :top10-data="top10Data" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get1stAndToday, past7Day, past30Day, pastWeek, pastMonth } from '@/utils/formValidate'
import { getTurnoverStatistics, getUserStatistics, getOrderStatistics, getTop } from '@/api/index'

import TitleIndex from './components/titleIndex.vue'
import TurnoverStatistics from './components/turnoverStatistics.vue'
import UserStatistics from './components/userStatistics.vue'
import OrderStatistics from './components/orderStatistics.vue'
import Top from './components/top10.vue'

defineOptions({
  name: 'Statistics',
})

// ---- state ----
const overviewData = ref<any>({})
const flag = ref(2)
const tateData = ref<string[]>([])
const turnoverData = ref<any>({})
const userData = ref<any>({})
const orderData = ref<any>({ data: {} })
const top10Data = ref<any>({})

// ---- 方法 ----
// 获取基本数据
const init = (begin: string, end: string) => {
  // nextTick 后并发发起四个请求
  Promise.all([
    getTurnoverStatisticsData(begin, end),
    getUserStatisticsData(begin, end),
    getOrderStatisticsData(begin, end),
    getTopData(begin, end),
  ])
}

// 营业额统计
const getTurnoverStatisticsData = async (begin: string, end: string) => {
  const { data } = await getTurnoverStatistics({ begin, end })
  const turnover = data.data
  turnoverData.value = {
    dateList: turnover.dateList.split(','),
    turnoverList: turnover.turnoverList.split(','),
  }
}

// 用户统计
const getUserStatisticsData = async (begin: string, end: string) => {
  const { data } = await getUserStatistics({ begin, end })
  const user = data.data
  userData.value = {
    dateList: user.dateList.split(','),
    totalUserList: user.totalUserList.split(','),
    newUserList: user.newUserList.split(','),
  }
}

// 订单统计
const getOrderStatisticsData = async (begin: string, end: string) => {
  const { data } = await getOrderStatistics({ begin, end })
  const order = data.data
  orderData.value = {
    data: {
      dateList: order.dateList.split(','),
      orderCountList: order.orderCountList.split(','),
      validOrderCountList: order.validOrderCountList.split(','),
    },
    totalOrderCount: order.totalOrderCount,
    validOrderCount: order.validOrderCount,
    orderCompletionRate: order.orderCompletionRate,
  }
}

// 排行数据
const getTopData = async (begin: string, end: string) => {
  const { data } = await getTop({ begin, end })
  const top = data.data
  top10Data.value = {
    nameList: top.nameList.split(',').reverse(),
    numberList: top.numberList.split(',').reverse(),
  }
}

// 获取当前选中的 tab 时间
const getTitleNum = (data: number) => {
  switch (data) {
    case 1:
      tateData.value = get1stAndToday()
      break
    case 2:
      tateData.value = past7Day()
      break
    case 3:
      tateData.value = past30Day()
      break
    case 4:
      tateData.value = pastWeek()
      break
    case 5:
      tateData.value = pastMonth()
      break
  }
  init(tateData.value[0], tateData.value[1])
}

// ---- 生命周期 ----
onMounted(() => {
  getTitleNum(2)
})
</script>
