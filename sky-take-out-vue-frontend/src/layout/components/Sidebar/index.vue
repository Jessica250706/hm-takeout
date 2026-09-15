<template>
  <el-aside :width="width">
    <div class="sidebar-wrapper">
      <!-- logo -->
      <div class="logo">
        <div v-if="!isCollapse" class="sidebar-logo">
          <img src="@/assets/login/logo.png" style="width: 120px; height: 31px" />
        </div>
        <div v-else class="sidebar-logo-mini">
          <img src="@/assets/login/mini-logo.png" />
        </div>
      </div>

      <!-- 滚动区 + 菜单 -->
      <el-scrollbar wrap-class="scrollbar-wrapper">
        <el-menu
          :default-openeds="defOpen"
          :default-active="defAct"
          :unique-opened="false"
          :collapse="isCollapse"
          :collapse-transition="false"
          mode="vertical"
          router
        >
          <sidebar-item
            v-for="route in routes"
            :key="route.path"
            :item="route"
            :base-path="route.path"
            :is-collapse="isCollapse"
          />
        </el-menu>
      </el-scrollbar>
    </div>
  </el-aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '@/utils/cookies'
import { useAppStore } from '@/stores/modules/app'
import { useUserStore } from '@/stores/modules/user'
import SidebarItem from './components/SidebarItem.vue'

defineOptions({
  name: 'SideBar',
})

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const width = computed(() => (!appStore.sidebar.opened ? '80px' : '190px'))

const name = computed(() => {
  if (userStore.userInfo?.name) return userStore.userInfo.name
  return getUserInfo<{ name?: string }>()?.name || ''
})

const isCollapse = computed(() => !appStore.sidebar.opened)
const roles = computed(() => userStore.roles)

const routes = computed(() => {
  const allRoutes = JSON.parse(JSON.stringify(router.options.routes))
  const menu = allRoutes.find((item: any) => item.path === '/')
  return menu?.children || []
})

const defOpen = computed(() => {
  const path = ['/']
  routes.value.forEach((n: any) => {
    if (n.meta?.roles && n.meta.roles[0] === roles.value[0]) {
      path.splice(0, 1, n.path)
    }
  })
  return path
})

const defAct = computed(() => route.path)
</script>

<style lang="scss" scoped>
.sidebar-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  flex-shrink: 0;
  text-align: center;
  background-color: #ffc100;
  padding: 15px 0 0;
  height: 60px;
  img {
    display: inline-block;
  }
}

.sidebar-logo-mini {
  img {
    width: 30px;
    height: 30px;
  }
}
</style>
