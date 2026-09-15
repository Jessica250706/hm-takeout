<template>
  <div class="empty-box">
    <div class="img-box">
      <img v-if="!isSearch" src="../../assets/table_empty.png" alt="" />
      <img v-else src="../../assets/search_table_empty.png" alt="" />
      <p>{{ !isSearch ? '这里空空如也~' : 'Sorry，木有找到您搜索的内容哦~' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: 'Empty',
})

withDefaults(
  defineProps<{
    isSearch?: boolean
  }>(),
  {
    isSearch: false,
  },
)
</script>

<style scoped lang="scss">
.empty-box {
  text-align: center;
  margin: 120px 0;
  img {
    margin: 0 auto;
    width: 238px;
    height: 184px;
    margin-top: 156px;
    margin-bottom: 26px;
  }
  p {
    color: #818693;
  }
}
</style>
