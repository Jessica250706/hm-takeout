<template>
  <div class="tab-change">
    <div
      v-for="item in changedOrderList"
      :key="item.value"
      class="tab-item"
      :class="{ active: item.value === activeIndex }"
      @click="tabChange(item.value)"
    >
      <el-badge
        :class="{ 'special-item': item.num < 10 }"
        class="item"
        :value="item.num > 99 ? '99+' : item.num"
        :hidden="!([2, 3, 4].includes(item.value) && item.num)"
      >
        {{ item.label }}
      </el-badge>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

defineOptions({
  name: 'TabChange',
})

const props = withDefaults(
  defineProps<{
    orderStatics?: any
    defaultActivity?: any
  }>(),
  {
    orderStatics: () => ({}),
    defaultActivity: '',
  },
)

const emit = defineEmits<{
  (e: 'tabChange', activeIndex: number): void
}>()

const activeIndex = ref<number>(Number(props.defaultActivity) || 0)

watch(
  () => props.defaultActivity,
  (val) => {
    activeIndex.value = Number(val) || 0
  },
)

const changedOrderList = computed(() => [
  { label: '全部订单', value: 0 },
  { label: '待接单', value: 2, num: props.orderStatics.toBeConfirmed },
  { label: '待派送', value: 3, num: props.orderStatics.confirmed },
  { label: '派送中', value: 4, num: props.orderStatics.deliveryInProgress },
  { label: '已完成', value: 5 },
  { label: '已取消', value: 6 },
])

const tabChange = (index: number) => {
  activeIndex.value = index
  emit('tabChange', index)
}
</script>

<style lang="scss">
.tab-change {
  display: flex;
  border-radius: 4px;
  margin-bottom: 20px;

  .tab-item {
    width: 120px;
    height: 40px;
    text-align: center;
    line-height: 40px;
    color: #333;
    border: 1px solid #e5e4e4;
    background-color: white;
    border-left: none;
    cursor: pointer;
    .special-item {
      .el-badge__content {
        width: 20px;
        padding: 0 5px;
      }
    }
    .item {
      .el-badge__content {
        background-color: #fd3333 !important;
        line-height: 18px;
        height: auto;
        min-width: 18px;
        min-height: 18px;
      }
      .el-badge__content.is-fixed {
        top: 14px;
        right: 2px;
      }
    }
  }
  .active {
    background-color: #ffc300;
    font-weight: bold;
  }
  .tab-item:first-child {
    border-left: 1px solid #e5e4e4;
  }
}
</style>
