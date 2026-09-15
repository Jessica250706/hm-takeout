<template>
  <div>
    <div class="container homecon">
      <h2 class="homeTitle homeTitleBtn">
        订单信息
        <ul class="conTab">
          <li
            v-for="(item, index) in tabList"
            :key="index"
            :class="activeIndex === index ? 'active' : ''"
            @click="handleClass(index)"
          >
            <el-badge
              class="item"
              :class="item.num >= 10 ? 'badgeW' : ''"
              :value="item.num > 99 ? '99+' : item.num"
              :hidden="!([2, 3].includes(item.value) && item.num)"
            >
              {{ item.label }}
            </el-badge>
          </li>
        </ul>
      </h2>
      <div>
        <div v-if="orderData.length > 0">
          <el-table
            :data="orderData"
            stripe
            class="tableBox"
            style="width: 100%"
            @row-click="handleTable"
          >
            <el-table-column prop="number" label="订单号" />
            <el-table-column label="订单菜品">
              <template #default="scope">
                <div class="ellipsisHidden">
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    :content="scope.row.orderDishes"
                  >
                    <template #reference>
                      <span>{{ scope.row.orderDishes }}</span>
                    </template>
                  </el-popover>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="地址" :class-name="dialogOrderStatus === 2 ? 'address' : ''">
              <template #default="scope">
                <div class="ellipsisHidden">
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    :content="scope.row.address"
                  >
                    <template #reference>
                      <span>{{ scope.row.address }}</span>
                    </template>
                  </el-popover>
                </div>
              </template>
            </el-table-column>

            <el-table-column
              prop="estimatedDeliveryTime"
              label="预计送达时间"
              sortable
              class-name="orderTime"
              min-width="130"
            />
            <el-table-column prop="amount" label="实收金额" />
            <el-table-column label="备注">
              <template #default="scope">
                <div class="ellipsisHidden">
                  <el-popover
                    placement="top-start"
                    width="200"
                    trigger="hover"
                    :content="scope.row.remark"
                  >
                    <template #reference>
                      <span>{{ scope.row.remark }}</span>
                    </template>
                  </el-popover>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              v-if="status === 3"
              prop="tablewareNumber"
              label="餐具数量"
              min-width="80"
              align="center"
            />
            <el-table-column
              label="操作"
              align="center"
              :class-name="dialogOrderStatus === 0 ? 'operate' : 'otherOperate'"
              :min-width="
                [2, 3].includes(dialogOrderStatus)
                  ? 130
                  : [0].includes(dialogOrderStatus)
                    ? 140
                    : 'auto'
              "
            >
              <template #default="{ row }">
                <div class="before">
                  <el-button
                    v-if="row.status === 2"
                    type="text"
                    class="blueBug"
                    @click="(orderAccept(row, $event), (isTableOperateBtn = true))"
                  >
                    接单
                  </el-button>
                  <el-button
                    v-if="row.status === 3"
                    type="text"
                    class="blueBug"
                    @click="cancelOrDeliveryOrComplete(3, row.id, $event)"
                  >
                    派送
                  </el-button>
                </div>
                <div class="middle">
                  <el-button
                    v-if="row.status === 2"
                    type="text"
                    class="delBut"
                    @click="(orderReject(row, $event), (isTableOperateBtn = true))"
                  >
                    拒单
                  </el-button>
                  <el-button
                    v-if="[1, 3, 4, 5].includes(row.status)"
                    type="text"
                    class="delBut"
                    @click="cancelOrder(row, $event)"
                  >
                    取消
                  </el-button>
                </div>
                <div class="after">
                  <el-button
                    type="text"
                    class="blueBug non"
                    @click="goDetail(row.id, row.status, row, $event)"
                  >
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
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

    <!-- 查看弹框部分 -->
    <el-dialog
      v-model="dialogVisible"
      title="订单信息"
      width="53%"
      :before-close="handleClose"
      class="order-dialog"
    >
      <el-scrollbar style="height: 100%">
        <div class="order-top">
          <div>
            <div style="display: inline-block">
              <label style="font-size: 16px">订单号：</label>
              <div class="order-num">{{ diaForm.number }}</div>
            </div>
            <div
              style="display: inline-block"
              class="order-status"
              :class="{ status3: [3, 4].includes(dialogOrderStatus) }"
            >
              {{ orderList.filter((item) => item.value === dialogOrderStatus)[0]?.label }}
            </div>
          </div>
          <p>
            <label>下单时间：</label>
            {{ diaForm.orderTime }}
          </p>
        </div>

        <div class="order-middle">
          <div class="user-info">
            <div class="user-info-box">
              <div class="user-name">
                <label>用户名：</label>
                <span>{{ diaForm.consignee }}</span>
              </div>
              <div class="user-phone">
                <label>手机号：</label>
                <span>{{ diaForm.phone }}</span>
              </div>
              <div v-if="[2, 3, 4, 5].includes(dialogOrderStatus)" class="user-getTime">
                <label>{{ dialogOrderStatus === 5 ? '送达时间：' : '预计送达时间：' }}</label>
                <span>
                  {{
                    dialogOrderStatus === 5 ? diaForm.deliveryTime : diaForm.estimatedDeliveryTime
                  }}
                </span>
              </div>
              <div class="user-address">
                <label>地址：</label>
                <span>{{ diaForm.address }}</span>
              </div>
            </div>
            <div class="user-remark" :class="{ orderCancel: dialogOrderStatus === 6 }">
              <div>{{ dialogOrderStatus === 6 ? '取消原因' : '备注' }}</div>
              <span>
                {{
                  dialogOrderStatus === 6
                    ? diaForm.cancelReason || diaForm.rejectionReason
                    : diaForm.remark
                }}
              </span>
            </div>
          </div>

          <div class="dish-info">
            <div class="dish-label">菜品</div>
            <div class="dish-list">
              <div v-for="(item, index) in diaForm.orderDetailList" :key="index" class="dish-item">
                <span class="dish-name">{{ item.name }}</span>
                <span class="dish-num">x{{ item.number }}</span>
                <span class="dish-price">￥{{ item.amount ? item.amount.toFixed(2) : '' }}</span>
              </div>
            </div>
            <div class="dish-all-amount">
              <label>菜品小计</label>
              <span>￥{{ dishSubtotal }}</span>
            </div>
          </div>
        </div>

        <div class="order-bottom">
          <div class="amount-info">
            <div class="amount-label">费用</div>
            <div class="amount-list">
              <div class="dish-amount">
                <span class="amount-name">菜品小计：</span>
                <span class="amount-price">￥{{ dishSubtotalRaw }}</span>
              </div>
              <div class="send-amount">
                <span class="amount-name">派送费：</span>
                <span class="amount-price">￥{{ 6 }}</span>
              </div>
              <div class="package-amount">
                <span class="amount-name">打包费：</span>
                <span class="amount-price">￥{{ packAmountDisplay }}</span>
              </div>
              <div class="all-amount">
                <span class="amount-name">合计：</span>
                <span class="amount-price">￥{{ totalAmountDisplay }}</span>
              </div>
              <div class="pay-type">
                <span class="pay-name">支付渠道：</span>
                <span class="pay-value">
                  {{ diaForm.payMethod === 1 ? '微信支付' : '支付宝支付' }}
                </span>
              </div>
              <div class="pay-time">
                <span class="pay-name">支付时间：</span>
                <span class="pay-value">{{ diaForm.checkoutTime }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>
      <template v-if="dialogOrderStatus !== 6" #footer>
        <span class="dialog-footer">
          <el-checkbox v-if="dialogOrderStatus === 2 && status === 2" v-model="isAutoNext">
            处理完自动跳转下一条
          </el-checkbox>
          <el-button
            v-if="dialogOrderStatus === 2"
            @click="(orderReject(row, $event), (isTableOperateBtn = false))"
          >
            拒 单
          </el-button>
          <el-button
            v-if="dialogOrderStatus === 2"
            type="primary"
            @click="(orderAccept(row, $event), (isTableOperateBtn = false))"
          >
            接 单
          </el-button>
          <el-button v-if="[1, 3, 4, 5].includes(dialogOrderStatus)" @click="dialogVisible = false">
            返 回
          </el-button>
          <el-button
            v-if="dialogOrderStatus === 3"
            type="primary"
            @click="cancelOrDeliveryOrComplete(3, row.id, $event)"
          >
            派 送
          </el-button>
          <el-button
            v-if="dialogOrderStatus === 4"
            type="primary"
            @click="cancelOrDeliveryOrComplete(4, row.id, $event)"
          >
            完 成
          </el-button>
          <el-button
            v-if="[1].includes(dialogOrderStatus)"
            type="primary"
            @click="cancelOrder(row, $event)"
          >
            取消订单
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 拒单，取消弹窗 -->
    <el-dialog
      v-model="cancelDialogVisible"
      :title="cancelDialogTitle + '原因'"
      width="42%"
      :before-close="() => ((cancelDialogVisible = false), (cancelReason = ''))"
      class="cancelDialog"
    >
      <el-form label-width="90px">
        <el-form-item :label="cancelDialogTitle + '原因：'">
          <el-select v-model="cancelReason" :placeholder="'请选择' + cancelDialogTitle + '原因'">
            <el-option
              v-for="(item, index) in cancelDialogTitle === '取消'
                ? cancelrReasonList
                : cancelOrderReasonList"
              :key="index"
              :label="item.label"
              :value="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="cancelReason === '自定义原因'" label="原因：">
          <el-input
            v-model.trim="remark"
            type="textarea"
            :placeholder="'请填写您' + cancelDialogTitle + '的原因（限20字内）'"
            maxlength="20"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click=";((cancelDialogVisible = false), (cancelReason = ''))">
            取 消
          </el-button>
          <el-button type="primary" @click="confirmCancel">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Empty from '@/components/Empty/index.vue'
import {
  getOrderDetailPage,
  queryOrderDetailById,
  completeOrder,
  deliveryOrder,
  orderCancel,
  orderReject as orderRejectApi,
  orderAccept as orderAcceptApi,
} from '@/api/order'

defineOptions({
  name: 'Orderview',
})

const props = withDefaults(
  defineProps<{
    orderStatics?: any
  }>(),
  {
    orderStatics: () => ({}),
  },
)

const emit = defineEmits<{
  (e: 'getOrderListBy3Status'): void
}>()

// ---- state ----
const orderId = ref('') // 订单号
const dialogOrderStatus = ref(0) // 弹窗所需订单状态
const activeIndex = ref(0)

const dialogVisible = ref(false)
const cancelDialogVisible = ref(false)
const cancelDialogTitle = ref('')
const cancelReason = ref('')
const remark = ref('')
const diaForm = ref<any>({})
const row = ref<any>({})
const isAutoNext = ref(true)
const isSearch = ref(false)
const counts = ref(0)
const page = ref(1)
const pageSize = ref(10)
const status = ref(2)
const orderData = ref<any[]>([])
const isTableOperateBtn = ref(true)

// 菜品小计（amount - 6 - packAmount）
const dishSubtotal = computed(() => {
  const amount = Number(diaForm.value.amount) || 0
  const packAmount = Number(diaForm.value.packAmount) || 0
  return (amount - 6 - packAmount).toFixed(2)
})

// 菜品小计（*100/100 的版本，用于费用区）
const dishSubtotalRaw = computed(() => {
  const amount = Number(diaForm.value.amount) || 0
  const packAmount = Number(diaForm.value.packAmount) || 0
  const value = (Number((amount - 6 - packAmount).toFixed(2)) * 100) / 100
  return value.toFixed(2)
})

// 打包费
const packAmountDisplay = computed(() => {
  const packAmount = Number(diaForm.value.packAmount)
  return packAmount ? ((packAmount * 100) / 100).toFixed(2) : ''
})

// 合计
const totalAmountDisplay = computed(() => {
  const amount = Number(diaForm.value.amount)
  return amount ? ((amount * 100) / 100).toFixed(2) : ''
})

// ---- 静态数据 ----
const cancelOrderReasonList = [
  { value: 1, label: '订单量较多，暂时无法接单' },
  { value: 2, label: '菜品已销售完，暂时无法接单' },
  { value: 3, label: '餐厅已打烊，暂时无法接单' },
  { value: 0, label: '自定义原因' },
]

const cancelrReasonList = [
  { value: 1, label: '订单量较多，暂时无法接单' },
  { value: 2, label: '菜品已销售完，暂时无法接单' },
  { value: 3, label: '骑手不足无法配送' },
  { value: 4, label: '客户电话取消' },
  { value: 0, label: '自定义原因' },
]

const orderList = [
  { label: '全部订单', value: 0 },
  { label: '待付款', value: 1 },
  { label: '待接单', value: 2 },
  { label: '待派送', value: 3 },
  { label: '派送中', value: 4 },
  { label: '已完成', value: 5 },
  { label: '已取消', value: 6 },
]

// ---- computed ----
const tabList = computed(() => [
  { label: '待接单', value: 2, num: props.orderStatics.toBeConfirmed },
  { label: '待派送', value: 3, num: props.orderStatics.confirmed },
])

// ---- 方法 ----
const getOrderListData = async (statusParam: number) => {
  const params = {
    page: page.value,
    pageSize: pageSize.value,
    status: statusParam,
  }
  const { data } = await getOrderDetailPage(params)

  if (!data || String(data.code) !== '1' || !data.data) {
    ElMessage.error(data?.msg || '获取订单列表失败')
    orderData.value = []
    counts.value = 0
    return
  }

  orderData.value = data.data.records
  counts.value = data.data.total
  emit('getOrderListBy3Status')

  if (
    dialogOrderStatus.value === 2 &&
    status.value === 2 &&
    isAutoNext.value &&
    !isTableOperateBtn.value &&
    data.data.records.length > 1
  ) {
    const firstRow = data.data.records[0]
    goDetail(firstRow.id, firstRow.status, firstRow, firstRow)
  }
}

// 接单
const orderAccept = (rowData: any, event: Event) => {
  event.stopPropagation()
  orderId.value = rowData.id
  dialogOrderStatus.value = rowData.status
  orderAcceptApi({ id: orderId.value })
    .then((res: any) => {
      if (res.data.code === 1) {
        ElMessage.success('操作成功')
        orderId.value = ''
        dialogVisible.value = false
        getOrderListData(status.value)
      } else {
        ElMessage.error(res.data.msg)
      }
    })
    .catch((err: any) => {
      ElMessage.error('请求出错了：' + err.message)
    })
}

// 打开取消订单弹窗
const cancelOrder = (rowData: any, event: Event) => {
  event.stopPropagation()
  cancelDialogVisible.value = true
  orderId.value = rowData.id
  dialogOrderStatus.value = rowData.status
  cancelDialogTitle.value = '取消'
  dialogVisible.value = false
  cancelReason.value = ''
}

// 打开拒单弹窗
const orderReject = (rowData: any, event: Event) => {
  event.stopPropagation()
  cancelDialogVisible.value = true
  orderId.value = rowData.id
  dialogOrderStatus.value = rowData.status
  cancelDialogTitle.value = '拒绝'
  dialogVisible.value = false
  cancelReason.value = ''
}

// 确认取消或拒绝订单并填写原因
const confirmCancel = () => {
  if (!cancelReason.value) {
    ElMessage.error(`请选择${cancelDialogTitle.value}原因`)
    return
  } else if (cancelReason.value === '自定义原因' && !remark.value) {
    ElMessage.error(`请输入${cancelDialogTitle.value}原因`)
    return
  }

  const isCancel = cancelDialogTitle.value === '取消'
  const api = isCancel ? orderCancel : orderRejectApi
  const reasonKey = isCancel ? 'cancelReason' : 'rejectionReason'

  api({
    id: orderId.value,
    [reasonKey]: cancelReason.value === '自定义原因' ? remark.value : cancelReason.value,
  } as any)
    .then((res: any) => {
      if (res.data.code === 1) {
        ElMessage.success('操作成功')
        cancelDialogVisible.value = false
        orderId.value = ''
        getOrderListData(status.value)
      } else {
        ElMessage.error(res.data.msg)
      }
    })
    .catch((err: any) => {
      ElMessage.error('请求出错了：' + err.message)
    })
}

// 派送、完成
const cancelOrDeliveryOrComplete = (statusParam: number, id: string, event: Event) => {
  event.stopPropagation()
  const params = { status: statusParam, id }
  const api = statusParam === 3 ? deliveryOrder : completeOrder
  api(params)
    .then((res: any) => {
      if (res.data.code === 1) {
        ElMessage.success('操作成功')
        orderId.value = ''
        dialogVisible.value = false
        getOrderListData(status.value)
      } else {
        ElMessage.error(res.data.msg)
      }
    })
    .catch((err: any) => {
      ElMessage.error('请求出错了：' + err.message)
    })
}

// 查看详情
const goDetail = async (id: any, statusParam: number, rowData: any, event: Event) => {
  event.stopPropagation()
  diaForm.value = {}
  dialogVisible.value = true
  dialogOrderStatus.value = statusParam
  const { data } = await queryOrderDetailById({ orderId: id })
  diaForm.value = data.data
  row.value = rowData
}

// 关闭弹层
const handleClose = () => {
  dialogVisible.value = false
}

// tab 切换
const handleClass = (index: number) => {
  activeIndex.value = index
  if (index === 0) {
    status.value = 2
    getOrderListData(2)
  } else {
    status.value = 3
    getOrderListData(3)
  }
}

// 触发 table 某一行
const handleTable = (rowData: any, _column: any, event: Event) => {
  event.stopPropagation()
  goDetail(rowData.id, rowData.status, rowData, event)
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  getOrderListData(status.value)
}

const handleCurrentChange = (val: number) => {
  page.value = val
  getOrderListData(status.value)
}

// ---- 生命周期 ----
onMounted(() => {
  getOrderListData(status.value)
})
</script>

<style lang="scss" scoped>
.dashboard-container.home .homecon {
  margin-bottom: 0;
}
.order-top {
  border-bottom: 1px solid #e7e6e6;
  padding-bottom: 26px;
  padding-left: 22px;
  padding-right: 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .order-status {
    width: 57.25px;
    height: 27px;
    background: #333333;
    border-radius: 13.5px;
    color: white;
    margin-left: 19px;
    text-align: center;
    line-height: 27px;
  }
  .status3 {
    background: #f56c6c;
  }
  p {
    color: #333;
    label {
      color: #666;
    }
  }
  .order-num {
    font-size: 16px;
    color: #2a2929;
    font-weight: bold;
    display: inline-block;
  }
}

.order-middle {
  .user-info {
    min-height: 140px;
    background: #fbfbfa;
    margin-top: 23px;
    padding: 20px 43px;
    color: #333;
    .user-info-box {
      min-height: 55px;
      display: flex;
      flex-wrap: wrap;
      .user-name {
        flex: 67%;
      }
      .user-phone {
        flex: 33%;
      }
      .user-getTime {
        margin-top: 14px;
        flex: 80%;
        label {
          margin-right: 3px;
        }
      }
      label {
        margin-right: 17px;
        color: #666;
      }
      .user-address {
        margin-top: 14px;
        flex: 80%;
        label {
          margin-right: 30px;
        }
      }
    }
    .user-remark {
      height: 43px;
      line-height: 43px;
      background: #fffbf0;
      border: 1px solid #fbe396;
      border-radius: 4px;
      margin-top: 10px;
      padding: 6px;
      display: flex;
      align-items: center;
      div {
        display: inline-block;
        min-width: 53px;
        height: 32px;
        background: #fbe396;
        border-radius: 4px;
        text-align: center;
        line-height: 32px;
        color: #333;
        margin-right: 30px;
      }
      span {
        color: #f2a402;
      }
    }
    .orderCancel {
      background: #ffffff;
      border: 1px solid #b6b6b6;
      div {
        padding: 0 10px;
        background-color: #e5e4e4;
      }
      span {
        color: #f56c6c;
      }
    }
  }
  .dish-info {
    display: flex;
    flex-wrap: wrap;
    padding: 20px 40px;
    border-bottom: 1px solid #e7e6e6;
    .dish-label {
      color: #666;
    }
    .dish-list {
      flex: 80%;
      display: flex;
      flex-wrap: wrap;
      .dish-item {
        flex: 50%;
        margin-bottom: 14px;
        color: #333;
        .dish-num {
          margin-right: 51px;
        }
      }
    }
    .dish-label {
      margin-right: 65px;
    }
    .dish-all-amount {
      flex: 1;
      padding-left: 92px;
      margin-top: 10px;
      label {
        color: #333333;
        font-weight: bold;
        margin-right: 5px;
      }
      span {
        color: #f56c6c;
      }
    }
  }
}
.order-bottom {
  .amount-info {
    display: flex;
    flex-wrap: wrap;
    padding: 20px 40px;
    padding-bottom: 0px;
    .amount-label {
      color: #666;
      margin-right: 65px;
    }
    .amount-list {
      flex: 80%;
      display: flex;
      flex-wrap: wrap;
      color: #333;
      .dish-amount,
      .package-amount,
      .pay-type {
        display: inline-block;
        width: 300px;
        margin-bottom: 14px;
        flex: 50%;
      }
      .send-amount,
      .all-amount,
      .pay-time {
        display: inline-block;
        flex: 50%;
        padding-left: 10%;
      }
      .package-amount {
        .amount-name {
          margin-right: 14px;
        }
      }
      .all-amount {
        .amount-name {
          margin-right: 24px;
        }
        .amount-price {
          color: #f56c6c;
        }
      }
      .send-amount {
        .amount-name {
          margin-right: 10px;
        }
      }
    }
  }
}
</style>

<style lang="scss">
.dashboard-container {
  .cancelTime {
    padding-left: 30px;
  }
  .orderTime {
    padding-left: 30px;
  }
  td.operate .cell {
    .before,
    .middle,
    .after {
      height: 39px;
      width: 48px;
    }
  }
  td.operate .cell,
  td.otherOperate .cell {
    display: flex;
    flex-wrap: nowrap;
    justify-content: center;
  }
}
</style>
