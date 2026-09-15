<template>
  <div class="selectInput">
    <el-input
      :model-value="value"
      type="text"
      style="width: 100%"
      placeholder="请选择口味"
      clearable
      readonly
      @focus="selectFlavor(true)"
      @blur="outSelect(false)"
    />
    <div v-if="mak && dishFlavorsData.length" class="flavorSelect">
      <span
        v-for="(it, ind) in dishFlavorsData"
        :key="ind"
        class="items"
        @click="checkOption(it, ind)"
      >
        {{ it.name }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineOptions({
  name: 'SelectInput',
})

const props = withDefaults(
  defineProps<{
    value?: string
    index?: number
    dishFlavorsData?: any[]
    selectFlavorsData?: any[]
  }>(),
  {
    value: '',
    index: 0,
    dishFlavorsData: () => [],
    selectFlavorsData: () => [],
  },
)

const emit = defineEmits<{
  (e: 'select', name: string, index: number, ind: number): void
}>()

const mak = ref(false)

const selectFlavor = (st: boolean) => {
  mak.value = st
}

const outSelect = (st: boolean) => {
  setTimeout(() => {
    mak.value = st
  }, 200)
}

const checkOption = (val: any, ind: number) => {
  emit('select', val.name, props.index, ind)
}
</script>

<style lang="scss" scoped>
.selectInput {
  position: relative;
  width: 100%;
  min-width: 100px;
  .flavorSelect {
    position: absolute;
    width: 100%;
    border-radius: 3px;
    border: solid 1px #e4e7ed;
    line-height: 30px;
    text-align: center;
    background: #fff;
    top: 50px;
    z-index: 99;
    .items {
      cursor: pointer;
      display: inline-block;
      width: 100%;
      line-height: 35px;
      border-bottom: solid 1px #f4f4f4;
      color: #666;
      margin: 0 !important;
      &:hover {
        background-color: #fffbf0;
      }
      &:active {
        background-color: #fffbf0;
        color: #ffc200;
      }
    }
    .none {
      font-size: 14px;
    }
  }
}
</style>
