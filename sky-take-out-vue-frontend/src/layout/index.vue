<template>
  <div>
    <el-container>
      <sidebar class="sidebar-container" />
      <el-container>
        <el-header>
          <navbar />
        </el-header>
        <el-main>
          <app-main />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { AppMain, Navbar, Sidebar } from './components'
import { useResize } from './mixin/resize'

defineOptions({ name: 'Layout' })

// 使用组合式函数处理响应式 resize
useResize()
</script>
