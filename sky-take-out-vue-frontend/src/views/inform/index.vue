<template>
  <div class="dashboard-container">
    <div class="informBox">
      <ul class="conTab">
        <li
          v-for="(item, index) in tabList"
          :key="index"
          :class="activeIndex === index ? 'active' : ''"
          @click="handleClass(index)"
        >
          <el-badge
            class="item"
            :class="ountUnread >= 10 ? 'badgeW' : ''"
            :value="ountUnread === 0 ? null : ountUnread > 99 ? '99+' : ountUnread"
            :hidden="!([1].includes(item.value) && ountUnread)"
          >
            {{ item.label }}
          </el-badge>
        </li>
      </ul>

      <el-button
        v-if="status === 1 && baseData.length > 0"
        icon="iconfont icon-clear"
        class="right-el-button"
        @click="handleBatch"
      >
        全部已读
      </el-button>
      <el-button v-else icon="iconfont icon-clear" class="right-el-button onbutton" disabled>
        全部已读
      </el-button>
    </div>

    <div class="container newBox" :class="{ hContainer: baseData.length }">
      <div v-if="baseData.length > 0" class="informList">
        <div v-for="(item, index) in baseData" :key="index">
          <!-- 待接单 -->
          <div v-if="item.type === 1" class="item">
            <div class="tit">
              <span>【待接单】</span>
              {{ item.arrNew[0] }}
              <span class="fontOrderTip" @click="handleSetStatus(item.id)">
                <router-link :to="'/order?status=' + 2">{{ item.arrNew[1] }}</router-link>
              </span>
              {{ item.arrNew[2] }}
              <span class="time">{{ item.createTime }}</span>
            </div>
          </div>

          <div v-if="item.type === 2" class="item">
            <div class="tit">
              <i>急</i>
              <span>【待接单】</span>
              {{ item.arrNew[0] }}
              <span class="fontOrderTip" @click="handleSetStatus(item.id)">
                <router-link :to="'/order?status=' + 2">{{ item.arrNew[1] }}</router-link>
              </span>
              {{ item.arrNew[2] }}
              <span class="time">{{ item.createTime }}</span>
            </div>
          </div>

          <!-- 待派送 -->
          <div v-if="item.type === 3" class="item">
            <div class="tit">
              <span>【待派送】</span>
              {{ item.arrNew[0] }}
              <span class="fontOrderTip" @click="handleSetStatus(item.id)">
                <router-link :to="'/order?status=' + 2">{{ item.arrNew[1] }}</router-link>
              </span>
              {{ item.arrNew[2] }}
              <span class="time">{{ item.createTime }}</span>
            </div>
          </div>

          <!-- 催单 -->
          <div
            v-if="item.type === 4"
            class="item"
            @mouseenter="toggleShow(item.id, index)"
            @mouseleave="mouseLeaves(index)"
          >
            <div :class="isActive ? 'titAlready' : ''">
              <div class="tit">
                <span>【催单】</span>
                {{ item.arrNew[0] }}
                <span class="time">{{ item.createTime }}</span>
              </div>
              <div v-if="shopShow && showIndex === index" class="orderInfo">
                <p>
                  <span>
                    <label>下单时间：</label>
                    {{ item.details.orderTime }}
                  </span>
                  <span>
                    <label>预计送达时间：</label>
                    {{ item.details.estimatedDeliveryTime }}
                  </span>
                </p>
                <p>
                  {{ item.details.consignee }}，{{ item.details.phone }}，{{ item.details.address }}
                </p>
                <p>
                  <span>
                    <label>菜品：</label>
                    {{ item.details.orderDishes }}
                  </span>
                </p>
              </div>
            </div>
          </div>

          <!-- 今日数据 -->
          <div
            v-if="item.type === 5"
            class="item"
            @mouseenter="toggleShow(item.id, index)"
            @mouseleave="mouseLeaves(index)"
          >
            <div :class="isActive ? 'titAlready' : ''">
              <div class="tit">
                <span>【今日数据】</span>
                认真工作的同时也要好好生活。
                <span class="time">{{ item.createTime }}</span>
              </div>
              <div v-if="shopShow && showIndex === index" class="orderInfo">
                <p>
                  <span>
                    <label>营业额：</label>
                    {{ item.details.turnover }}
                  </span>
                  <span>
                    <label>有效订单：</label>
                    {{ item.details.validOrderCount }}笔
                  </span>
                  <span>
                    <label>订单完成率：</label>
                    {{ item.details.orderCompletionRate }}
                  </span>
                </p>
                <p>
                  <span>
                    <label>今日新增用户：</label>
                    {{ item.details.newUsers }}
                  </span>
                  <span>
                    <label>今日取消：</label>
                    {{ item.details.cancelledOrders }}笔
                  </span>
                  <span>
                    <label>今日取消金额：</label>
                    ￥{{ item.details.cancelledAmount }}
                  </span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <Empty v-else :is-search="isSearch" />

      <el-pagination
        v-if="counts > 10"
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Empty from '@/components/Empty/index.vue'
import { useAppStore } from '@/store/modules/app'
import { getInformData, batchMsg, setStatus, getCountUnread } from '@/api/inform'

defineOptions({
  name: 'Inform',
})

const appStore = useAppStore()

// ---- state ----
const activeIndex = ref(0)
const shopShow = ref(false)
const counts = ref(0)
const page = ref(1)
const pageSize = ref(10)
const status = ref(1)
const baseData = ref<any[]>([])
const showIndex = ref(0)
const isSearch = ref(false)
const isActive = ref(false)

// ---- computed ----
const tabList = computed(() => [
  { label: '未读', value: 1 },
  { label: '已读', value: 2 },
])

const ountUnread = computed(() => appStore.statusNumber)

// ---- 方法 ----
// 获取列表数据
const getData = async () => {
  const params = {
    pageNum: page.value,
    pageSize: pageSize.value,
    status: status.value,
  }
  const { data } = await getInformData(params)
  if (data.code === 1) {
    const records = data.data.records
    counts.value = data.data.total

    const arrDetails = records.map((val: any) => {
      const arrContent = val.content.split(' ')
      const objNew: any = { ...val, arrNew: arrContent }
      // 处理后端返回的 details 字符串
      // ⚠️ eval 已经不再安全/推荐，改用手写的宽松解析
      objNew.details = parseDetails(objNew.details)
      return objNew
    })

    baseData.value = arrDetails
  } else {
    ElMessage.error(data.msg)
  }
}

// 宽松解析后端 details 字符串（替代 eval）
const parseDetails = (detailStr: any) => {
  if (!detailStr) return {}
  if (typeof detailStr === 'object') return detailStr
  try {
    return JSON.parse(detailStr)
  } catch {
    // 兼容单引号、非严格 JSON 的写法
    try {
      return new Function('return (' + detailStr + ')')()
    } catch {
      return {}
    }
  }
}

// 全部已读
const handleBatch = async () => {
  const ids = baseData.value.map((val) => val.id)
  const { data } = await batchMsg(ids)
  if (data.code === 1) {
    getCountUnreadData()
    getData()
  } else {
    ElMessage.error(data.msg)
  }
}

// 设置单个订单已读
const handleSetStatus = async (id: any) => {
  const { data } = await setStatus(id)
  if (data.code === 1) {
    if (!isActive.value) {
      getCountUnreadData()
      getData()
    }
  } else {
    ElMessage.error(data.msg)
  }
}

// 获取未读消息
const getCountUnreadData = async () => {
  const { data } = await getCountUnread()
  if (data.code === 1) {
    appStore.statusNumberAction(data.data)
  } else {
    ElMessage.error(data.msg)
  }
}

// 触发已读未读按钮
const handleClass = (index: number) => {
  activeIndex.value = index
  status.value = index === 0 ? 1 : 2
  getData()
}

// 下拉菜单显示
const toggleShow = (id: any, index: number) => {
  shopShow.value = true
  showIndex.value = index
  let t = 3
  const timer = setInterval(() => {
    t--
    if (t === 0) {
      if (status.value === 1) {
        isActive.value = true
        handleSetStatus(id)
      }
      clearInterval(timer)
    }
  }, 1000)
}

// 下拉菜单隐藏
const mouseLeaves = (index: number) => {
  shopShow.value = false
  showIndex.value = index
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  getData()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  getData()
}

// ---- 生命周期 ----
onMounted(() => {
  getData()
})
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
    .container {
      background: #fff;
      position: relative;
      z-index: 1;
      padding: 0 30px;
      border-radius: 4px;
      height: calc(100% - 55px);
      overflow: hidden;
      &.newBox {
        .pageList {
          border-top: 1px solid #f3f4f7;
          padding: 40px;
          margin-top: 0;
        }
      }
    }
    .hContainer {
      height: auto !important;
    }
  }
}
</style>
