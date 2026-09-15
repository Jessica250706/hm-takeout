<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style>
#app {
  width: 100%;
  height: 100%;
  overflow: hidden; /* 隐藏超出部分，防止滚动条出现 */
}
/* 覆盖用户代理样式表 */
body {
  margin: 0px;
  background-color: #f3f4f7;
}
</style>
