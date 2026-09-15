<template>
  <section class="app-main">
    <router-view v-slot="{ Component }">
      <transition name="fade-transform" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </section>
</template>

<script setup lang="ts">
defineOptions({
  name: 'AppMain',
})
</script>

<style lang="scss" scoped>
.app-main {
  height: 100%;
  overflow-y: auto;
}
</style>
