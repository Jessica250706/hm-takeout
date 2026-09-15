<template>
  <template v-if="!item.meta || !item.meta.hidden">
    <!-- 只有一个子节点 或 无子节点：渲染 el-menu-item -->
    <el-menu-item
      v-if="theOnlyOneChild && !theOnlyOneChild.children && theOnlyOneChild.meta"
      :index="resolvePath(theOnlyOneChild.path)"
    >
      <i v-if="theOnlyOneChild.meta.icon" class="iconfont" :class="theOnlyOneChild.meta.icon" />
      <template #title>
        <span>{{ theOnlyOneChild.meta.title }}</span>
      </template>
    </el-menu-item>

    <!-- 有多个子节点：渲染 el-sub-menu -->
    <el-sub-menu
      v-else-if="item.children && item.children.length"
      :index="resolvePath(item.path)"
      popper-class="sidebar-popper"
    >
      <template #title>
        <i v-if="item.meta?.icon" class="iconfont" :class="item.meta.icon" />
        <span>{{ item.meta?.title }}</span>
      </template>

      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :is-collapse="isCollapse"
        :is-first-level="false"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'
import { isExternal } from '@/utils/validate'

defineOptions({
  name: 'SidebarItem',
})

const props = withDefaults(
  defineProps<{
    item: RouteRecordRaw
    isCollapse?: boolean
    isFirstLevel?: boolean
    basePath?: string
  }>(),
  {
    isCollapse: false,
    isFirstLevel: true,
    basePath: '',
  },
)

const userStore = useUserStore()
const roles = computed(() => userStore.roles)

const showingChildNumber = computed(() => {
  if (props.item.children) {
    return props.item.children.filter((item) => !item.meta?.hidden).length
  }
  return 0
})

const theOnlyOneChild = computed(() => {
  if (showingChildNumber.value > 0) return null
  if (props.item.children) {
    for (const child of props.item.children) {
      if (!child.meta || !child.meta.hidden) {
        return child
      }
    }
  }
  return { ...props.item, path: '' }
})

function resolvePath(routePath: string) {
  if (isExternal(routePath)) return routePath
  if (isExternal(props.basePath)) return props.basePath
  return resolveUrlPath(props.basePath, routePath)
}

function resolveUrlPath(basePath: string, routePath: string): string {
  if (!routePath) return basePath || '/'
  if (routePath.startsWith('/')) return routePath
  if (!basePath || basePath === '/') {
    return '/' + routePath.replace(/^\//, '')
  }
  return basePath.replace(/\/$/, '') + '/' + routePath.replace(/^\//, '')
}
</script>
