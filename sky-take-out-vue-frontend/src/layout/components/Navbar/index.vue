<template>
  <div class="navbar">
    <div class="statusBox">
      <hamburger
        id="hamburger-container"
        :is-active="sidebar.opened"
        class="hamburger-container"
        @toggleClick="toggleSideBar"
      />
      <span v-if="status === 1" class="businessBtn">营业中</span>
      <span v-else class="businessBtn closing">打烊中</span>
    </div>

    <div :key="restKey" class="right-menu">
      <div class="rightStatus">
        <audio ref="audioVo" hidden>
          <source src="@/assets/preview.mp3" type="audio/mp3" />
        </audio>
        <audio ref="audioVo2" hidden>
          <source src="@/assets/reminder.mp3" type="audio/mp3" />
        </audio>
        <span class="navicon operatingState" @click="handleStatus">
          <i />
          营业状态设置
        </span>
      </div>
      <div class="avatar-wrapper">
        <div :class="shopShow ? 'userInfo' : ''" @mouseenter="toggleShow" @mouseleave="mouseLeaves">
          <el-button type="primary" :class="shopShow ? 'active' : ''">
            {{ name }}
            <i class="el-icon-arrow-down" />
          </el-button>
          <div v-if="shopShow" class="userList">
            <p class="amendPwdIcon" @click="handlePwd">
              修改密码
              <i />
            </p>
            <p class="outLogin" @click="logout">
              退出登录
              <i />
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 营业状态弹层 -->
    <el-dialog v-model="dialogVisible" title="营业状态设置" width="25%" :show-close="false">
      <el-radio-group v-model="setStatus">
        <el-radio :value="1">
          营业中
          <span>当前餐厅处于营业状态，自动接收任何订单，可点击打烊进入店铺打烊状态。</span>
        </el-radio>
        <el-radio :value="0">
          打烊中
          <span>
            当前餐厅处于打烊状态，仅接受营业时间内的预定订单，可点击营业中手动恢复营业状态。
          </span>
        </el-radio>
      </el-radio-group>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleSave">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码 -->
    <Password :dialog-form-visible="dialogFormVisible" @handleclose="handlePwdClose" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElNotification, ElMessage } from 'element-plus'
import { getUserInfo } from '@/utils/cookies'
import { useAppStore, DeviceType } from '@/stores/modules/app'
import { useUserStore } from '@/stores/modules/user'
import Hamburger from '@/components/Hamburger/index.vue'
import Password from '../components/password.vue'
import { getStatus, setStatus as setStatusApi } from '@/api/users'
import { getCountUnread } from '@/api/inform'

defineOptions({
  name: 'Navbar',
})

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// ---- 模板 refs ----
const audioVo = ref<HTMLAudioElement | null>(null)
const audioVo2 = ref<HTMLAudioElement | null>(null)

// ---- 本地 state ----
const restKey = ref(0)
const shopShow = ref(false)
const dialogVisible = ref(false)
const status = ref(1)
const setStatus = ref(1)
const dialogFormVisible = ref(false)
const ountUnread = ref(0)
const websocket = ref<WebSocket | null>(null)

// ---- computed ----
const sidebar = computed(() => appStore.sidebar)
const device = computed(() => appStore.device.toString())

const name = computed(() => {
  if (userStore.userInfo?.name) return userStore.userInfo.name
  return getUserInfo<{ name?: string }>()?.name || ''
})

const storeId = computed(() => {
  let id = ''
  if (userStore.storeId) {
    id = userStore.storeId
  } else if (userStore.userInfo?.stores != null) {
    id = userStore.userInfo.stores[0].storeId
  }
  return id
})

// ---- 方法 ----
const toggleSideBar = () => {
  appStore.toggleSideBar(false)
}

const logout = async () => {
  await userStore.logOut()
  router.replace({ path: '/login' })
}

const getCountUnreadData = async () => {
  const { data } = await getCountUnread()
  if (data.code === 1) {
    appStore.statusNumberAction(data.data)
  } else {
    ElMessage.error(data.msg)
  }
}

// ⚠️ 原方法名 getStatus 和导入的 getStatus 重名，这里改名
const getStatusData = async () => {
  const { data } = await getStatus()
  status.value = data.data
  setStatus.value = status.value
}

const toggleShow = () => {
  shopShow.value = true
}
const mouseLeaves = () => {
  shopShow.value = false
}
const handleClose = () => {
  // 空实现，原样保留
}
const handleStatus = () => {
  dialogVisible.value = true
}

const handleSave = async () => {
  const { data } = await setStatusApi(setStatus.value)
  if (data.code === 1) {
    dialogVisible.value = false
    getStatusData()
  }
}

const handlePwd = () => {
  dialogFormVisible.value = true
}
const handlePwdClose = () => {
  dialogFormVisible.value = false
}

// ---- WebSocket ----
const webSocket = () => {
  const clientId = Math.random().toString(36).substring(2)
  // ⚠️ Vite 下要改成 VITE_ 前缀，见下方说明
  const socketUrl = import.meta.env.VITE_APP_SOCKET_URL + clientId
  console.log(socketUrl, 'socketUrl')

  if (typeof WebSocket === 'undefined') {
    ElNotification({
      title: '提示',
      message: '当前浏览器无法接收实时报警信息，请使用谷歌浏览器！',
      type: 'warning',
      duration: 0,
    })
    return
  }

  websocket.value = new WebSocket(socketUrl)

  websocket.value.onopen = function () {
    console.log('浏览器WebSocket已打开')
  }

  websocket.value.onmessage = function (msg) {
    if (audioVo.value) audioVo.value.currentTime = 0
    if (audioVo2.value) audioVo2.value.currentTime = 0

    console.log(msg, JSON.parse(msg.data), 'msg')
    const jsonMsg = JSON.parse(msg.data)

    if (jsonMsg.type === 1) {
      audioVo.value?.play()
    } else if (jsonMsg.type === 2) {
      audioVo2.value?.play()
    }

    ElNotification({
      title: jsonMsg.type === 1 ? '待接单' : '催单',
      duration: 0,
      dangerouslyUseHTMLString: true,
      onClick: () => {
        router.push(`/order?orderId=${jsonMsg.orderId}`).catch((err) => {
          console.log(err)
        })
        setTimeout(() => {
          location.reload()
        }, 100)
      },
      message: `${
        jsonMsg.type === 1
          ? `<span>您有1个<span style=color:#419EFF>订单待处理</span>,${jsonMsg.content},请及时接单</span>`
          : `${jsonMsg.content}<span style='color:#419EFF;cursor: pointer'>去处理</span>`
      }`,
    })
  }

  websocket.value.onerror = function () {
    ElNotification({
      title: '错误',
      message: '服务器错误，无法接收实时报警信息',
      type: 'error',
      duration: 0,
    })
  }

  websocket.value.onclose = function () {
    console.log('WebSocket已关闭')
  }
}

// ---- 生命周期 ----
// created 阶段同步执行 webSocket（setup 本身相当于 created 之前）
webSocket()

onMounted(() => {
  document.addEventListener('click', handleClose)
  getStatusData()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClose)
})

// destroyed → onUnmounted
onUnmounted(() => {
  websocket.value?.close()
})
</script>

<style lang="scss" scoped>
/* 完全保留原样式，未改动 */
.navbar {
  height: 60px;
  position: relative;
  background: #ffc100;

  .statusBox {
    float: left;
    height: 100%;
    align-items: center;
    display: flex;
  }
  .hamburger-container {
    padding: 0 12px 0 20px;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }
  .right-menu {
    float: right;
    margin-right: 20px;
    color: #333333;
    font-size: 14px;

    span {
      padding: 0 10px;
      width: 130px;
      display: inline-block;
      cursor: pointer;
      &:hover {
        background: rgba(255, 255, 255, 0.52);
      }
    }
    .amendPwdIcon {
      i {
        width: 18px;
        height: 18px;
        background: url(./../../../assets/icons/btn_gaimi@2x.png) no-repeat;
        background-size: contain;
        margin-top: 8px;
      }
    }
    .outLogin {
      i {
        width: 18px;
        height: 18px;
        background: url(./../../../assets/icons/btn_close@2x.png) no-repeat 100% 100%;
        background-size: contain;
        margin-top: 8px;
      }
    }
    .outLogin {
      cursor: pointer;
    }

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }
  }
  .rightStatus {
    height: 100%;
    line-height: 60px;
    display: flex;
    align-items: center;
    float: left;
  }
  .avatar-wrapper {
    margin-top: 14px;
    margin-left: 18px;
    position: relative;
    float: right;
    width: 120px;
    text-align: left;
    .user-avatar {
      cursor: pointer;
      width: 40px;
      height: 40px;
      border-radius: 10px;
    }

    .el-icon-caret-bottom {
      cursor: pointer;
      position: absolute;
      right: -20px;
      top: 25px;
      font-size: 12px;
    }

    .el-button--primary {
      background: rgba(255, 255, 255, 0.52);
      border-radius: 4px;
      padding-top: 0px;
      padding-bottom: 0px;
      position: relative;
      width: 120px;
      padding-left: 12px;
      text-align: left;
      border: 0 none;
      height: 32px;
      line-height: 32px;
      &.active {
        background: rgba(250, 250, 250, 0);
        border: 0 none;
        .el-icon-arrow-down {
          transform: rotate(-180deg);
        }
      }
    }
  }
  .businessBtn {
    height: 22px;
    line-height: 20px;
    background: #fd3333;
    border: 1px solid #ffffff;
    border-radius: 4px;
    display: inline-block;
    padding: 0 6px;
    color: #fff;
  }
  .closing {
    background: #6a6a6a;
  }
  .navicon {
    i {
      display: inline-block;
      width: 18px;
      height: 18px;
      vertical-align: sub;
      margin: 0 4px 0 0;
    }
  }
  .operatingState {
    i {
      background: url('./../../../assets/icons/time.png') no-repeat;
      background-size: contain;
    }
  }
  .mesCenter {
    i {
      background: url('./../../../assets/icons/msg.png') no-repeat;
      background-size: contain;
    }
  }
}
</style>

<style lang="scss">
/* 全局样式保持原样，未改动 */
.el-notification {
  width: 419px !important;
  .el-notification__title {
    margin-bottom: 14px;
    color: #333;
    .el-notification__content {
      color: #333;
    }
  }
}
.navbar {
  .el-dialog {
    min-width: auto !important;
  }
  .el-dialog__header {
    height: 61px;
    line-height: 60px;
    background: #fbfbfa;
    padding: 0 30px;
    font-size: 16px;
    color: #333;
    border: 0 none;
  }
  .el-dialog__body {
    padding: 10px 30px 30px;
    .el-radio,
    .el-radio__input {
      white-space: normal;
    }
    .el-radio__label {
      padding-left: 5px;
      color: #333;
      font-weight: 700;
      span {
        display: block;
        line-height: 20px;
        padding-top: 12px;
        color: #666;
        font-weight: normal;
      }
    }
    .el-radio__input.is-checked .el-radio__inner {
      &::after {
        background: #333;
      }
    }
    .el-radio-group {
      & > .is-checked {
        border: 1px solid #ffc200;
      }
    }
    .el-radio {
      width: 100%;
      background: #fbfbfa;
      border: 1px solid #e5e4e4;
      border-radius: 4px;
      padding: 14px 22px;
      margin-top: 20px;
    }
  }
  .el-badge__content.is-fixed {
    top: 24px;
    right: 2px;
    width: 18px;
    height: 18px;
    font-size: 10px;
    line-height: 16px;
    font-size: 10px;
    border-radius: 50%;
    padding: 0;
  }
  .badgeW {
    .el-badge__content.is-fixed {
      width: 30px;
      border-radius: 20px;
    }
  }
}
.el-icon-arrow-down {
  background: url('./../../../assets/icons/up.png') no-repeat 50% 50%;
  background-size: contain;
  width: 8px;
  height: 8px;
  transform: rotate(0eg);
  margin-left: 16px;
  position: absolute;
  right: 16px;
  top: 12px;
  &:before {
    content: '';
  }
}

.userInfo {
  background: #fff;
  position: absolute;
  top: 0px;
  left: 0;
  z-index: 99;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.14);
  width: 100%;
  border-radius: 4px;
  line-height: 32px;
  padding: 0 0 5px;
  height: 105px;
  .userList {
    width: 95%;
    padding-left: 5px;
  }
  p {
    cursor: pointer;
    height: 32px;
    line-height: 32px;
    padding: 0 5px 0 7px;
    i {
      margin-left: 10px;
      vertical-align: middle;
      margin-top: 4px;
      float: right;
    }
    &:hover {
      background: #f6f1e1;
    }
  }
}
.msgTip {
  color: #419eff;
  padding: 0 5px;
}
</style>
