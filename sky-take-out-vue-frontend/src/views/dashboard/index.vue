<template>
  <div class="dashboard-container home">
    <!-- 营业数据 -->
    <Overview :overview-data="overviewData" />
    <!-- 订单管理 -->
    <Orderview :orderview-data="orderviewData" />
    <div class="homeMain">
      <!-- 菜品总览 -->
      <CuisineStatistics :dishes-data="dishesData" />
      <!-- 套餐总览 -->
      <SetMealStatistics :set-meal-data="setMealData" />
    </div>
    <!-- 订单信息 -->
    <OrderList :order-statics="orderStatics" @getOrderListBy3Status="getOrderListBy3Status" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getBusinessData,
  getDataOverView,
  getOrderData,
  getOverviewDishes,
  getSetMealStatistics,
} from '@/api/index'
import { getOrderListBy } from '@/api/order'
import Overview from './components/overview.vue'
import Orderview from './components/orderview.vue'
import CuisineStatistics from './components/cuisineStatistics.vue'
import SetMealStatistics from './components/setMealStatistics.vue'
import OrderList from './components/orderList.vue'

defineOptions({
  name: 'Dashboard',
})

// ---- state ----
const todayData = ref({})
const overviewData = ref<any>({})
const orderviewData = ref<any>({})
const flag = ref(2)
const tateData = ref<any[]>([])
const dishesData = ref<any>({})
const setMealData = ref<any>({})
const orderListData = ref<any[]>([])
const counts = ref(0)
const page = ref(1)
const pageSize = ref(10)
const status = ref(2)
const orderStatics = ref<any>({})

// ---- 方法 ----
const getBusinessDataFn = async () => {
  const { data } = await getBusinessData()
  overviewData.value = data.data
}

const getOrderStatisticsData = async () => {
  const { data } = await getOrderData()
  orderviewData.value = data.data
}

const getOverStatisticsData = async () => {
  const { data } = await getOverviewDishes()
  dishesData.value = data.data
}

const getSetMealStatisticsData = async () => {
  const { data } = await getSetMealStatistics()
  setMealData.value = data.data
}

// 获取待处理、待派送、派送中数量
const getOrderListBy3Status = () => {
  getOrderListBy({})
    .then((res: any) => {
      if (res.data.code === 1) {
        orderStatics.value = res.data.data
      } else {
        ElMessage.error(res.data.msg)
      }
    })
    .catch((err: any) => {
      ElMessage.error('请求出错了：' + err.message)
    })
}

const init = () => {
  nextTick(() => {
    getBusinessDataFn()
    getOrderStatisticsData()
    getOverStatisticsData()
    getSetMealStatisticsData()
  })
}

// ---- 生命周期 ----
onMounted(() => {
  init()
})
</script>

<style lang="scss"></style>
