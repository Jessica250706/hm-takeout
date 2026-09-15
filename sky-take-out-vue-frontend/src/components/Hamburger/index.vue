<template>
  <div :class="{ 'is-active': isActive }" @click="toggleClick">
    <el-icon v-if="isActive"><Fold /></el-icon>
    <el-icon v-else><Expand /></el-icon>
  </div>
</template>

<script setup lang="ts">
import { Fold, Expand } from '@element-plus/icons-vue'

defineOptions({
  name: 'Hamburger',
})

withDefaults(
  defineProps<{
    isActive?: boolean
  }>(),
  {
    isActive: false,
  },
)

const emit = defineEmits<{
  (e: 'toggleClick'): void
}>()

const toggleClick = () => {
  emit('toggleClick')
}
</script>

<style lang="scss" scoped>
.svg-icon {
  vertical-align: middle;
}

.is-active {
  transform: rotate(180deg);
}
</style>
