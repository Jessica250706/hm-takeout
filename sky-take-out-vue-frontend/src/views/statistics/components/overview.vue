<template>
  <div class="container">
    <h2 class="homeTitle">数据概览</h2>
    <div class="overviewBox">
      <ul>
        <li>
          <p class="tit">营业额</p>
          <p class="num">{{ overviewData.turnover }}</p>
          <p class="tip">
            同比增长
            <span v-if="overviewData.turnoverGrowth">
              <span class="red" :class="growthClass(overviewData.turnoverGrowth)">
                {{ formatGrowth(overviewData.turnoverGrowth) }}
              </span>
            </span>
            <span v-else>-</span>
          </p>
        </li>
        <li>
          <p class="tit">有效订单</p>
          <p class="num">{{ overviewData.validOrderCount }}</p>
          <p class="tip">
            同比增长
            <span v-if="overviewData.validOrderCountGrowth">
              <span class="red" :class="growthClass(overviewData.validOrderCountGrowth)">
                {{ formatGrowth(overviewData.validOrderCountGrowth) }}
              </span>
            </span>
            <span v-else>-</span>
          </p>
        </li>
        <li>
          <p class="tit">订单完成率</p>
          <p class="num">{{ completionRateText }}%</p>
          <p class="tip">
            同比增长
            <span v-if="overviewData.orderCompletionRateGrowth">
              <span class="red" :class="growthClass(overviewData.orderCompletionRateGrowth)">
                {{ formatGrowth(overviewData.orderCompletionRateGrowth) }}
              </span>
            </span>
            <span v-else>-</span>
          </p>
        </li>
        <li>
          <p class="tit">平均客单价</p>
          <p class="num">{{ unitPriceText }}</p>
          <p class="tip">
            同比增长
            <span v-if="overviewData.unitPriceGrowth">
              <span class="red" :class="growthClass(overviewData.unitPriceGrowth)">
                {{ formatGrowth(overviewData.unitPriceGrowth) }}
              </span>
            </span>
            <span v-else>-</span>
          </p>
        </li>
        <li>
          <p class="tit">用户总量</p>
          <p class="num">{{ overviewData.totalUsers }}</p>
        </li>
        <li>
          <p class="tit">新增用户</p>
          <p class="num">{{ overviewData.newUsers }}</p>
          <p class="tip">
            同比增长
            <span v-if="overviewData.newUsersGrowth">
              <span class="red" :class="growthClass(overviewData.newUsersGrowth)">
                {{ formatGrowth(overviewData.newUsersGrowth) }}
              </span>
            </span>
            <span v-else>-</span>
          </p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

defineOptions({
  name: 'Overview',
})

const props = withDefaults(
  defineProps<{
    overviewData?: any
  }>(),
  {
    overviewData: () => ({}),
  },
)

// ---- computed ----
// 订单完成率（* 100 的运算放到脚本里）
const completionRateText = computed(() => {
  const rate = Number(props.overviewData?.orderCompletionRate)
  return Number.isFinite(rate) ? (rate * 100).toFixed(2) : '0.00'
})

// 平均客单价
const unitPriceText = computed(() => {
  const price = Number(props.overviewData?.unitPrice)
  return Number.isFinite(price) ? price.toFixed(2) : '0.00'
})

// ---- 工具函数 ----
// 增长率文本：正数带 +，负数保持原样，尾部带 %
const formatGrowth = (val: any): string => {
  const v = Number(val)
  if (!Number.isFinite(v)) return '-'
  return `${v > 0 ? '+' + v : v}%`
}

// 增长率颜色：正数保持红色（无类），非正数加 green
const growthClass = (val: any): string => {
  const v = Number(val)
  return v > 0 ? '' : 'green'
}
</script>
