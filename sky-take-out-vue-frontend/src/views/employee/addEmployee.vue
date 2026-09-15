<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="180px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="ruleForm.username" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input v-model="ruleForm.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="ruleForm.phone" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio v-model="ruleForm.sex" value="1">男</el-radio>
          <el-radio v-model="ruleForm.sex" value="2">女</el-radio>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="ruleForm.idNumber" />
        </el-form-item>
        <div class="subBox">
          <el-button type="primary" @click="submitForm(false)">保存</el-button>
          <el-button v-if="optType === 'add'" type="primary" @click="submitForm(true)">
            保存并继续添加员工
          </el-button>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { queryEmployeeById, addEmployee, editEmployee } from '@/api/employee'

defineOptions({
  name: 'AddEmployee',
})

const route = useRoute()
const router = useRouter()

const ruleFormRef = ref<FormInstance>()

// add / edit
const optType = ref<'add' | 'edit'>('add')

const ruleForm = reactive<any>({
  id: '',
  username: '',
  name: '',
  phone: '',
  sex: '1',
  idNumber: '',
})

// ---- 校验规则 ----
const rules = computed(() => ({
  username: [
    {
      required: true,
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        if (!value) {
          callback(new Error('请输入账号'))
        } else {
          const reg = /^[A-Za-z0-9]{2,20}$/
          if (!reg.test(value)) {
            callback(new Error('账号输入不符，请输入2-20个字符'))
          } else {
            callback()
          }
        }
      },
      trigger: 'blur',
    },
  ],
  name: [
    {
      required: true,
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        if (!value) {
          callback(new Error('请输入员工姓名'))
        } else {
          const reg = /^[A-Za-z\u4e00-\u9fa5]{2,20}$/
          if (!reg.test(value)) {
            callback(new Error('员工姓名输入不符，请输入2-20个字符'))
          } else {
            callback()
          }
        }
      },
      trigger: 'blur',
    },
  ],
  phone: [
    {
      required: true,
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        const reg = /^1[3-9]\d{9}$/
        if (!value) {
          callback(new Error('请输入手机号'))
        } else if (!reg.test(value)) {
          callback(new Error('手机号格式错误'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
  idNumber: [
    {
      required: true,
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        const reg =
          /^(^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$)|(^\d{15}$)/
        if (!value) {
          callback(new Error('请输入身份证号'))
        } else if (!reg.test(value)) {
          callback(new Error('身份证号格式错误'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}))

// ---- 方法 ----
const handleBack = () => {
  router.push('/employee')
}

// 根据 id 获取员工信息（编辑模式）
const init = async () => {
  try {
    const res: any = await queryEmployeeById(route.query.id)
    if (res?.data?.code === 1) {
      Object.assign(ruleForm, res.data.data)
    } else {
      ElMessage.error(res?.data?.msg || '获取员工信息失败')
    }
  } catch (err: any) {
    ElMessage.error('请求出错了：' + err.message)
  }
}

// 提交表单
const submitForm = (goAnd?: boolean) => {
  ruleFormRef.value?.validate(async (valid: boolean) => {
    if (!valid) return

    const params = { ...ruleForm }
    try {
      if (optType.value === 'add') {
        const res: any = await addEmployee(params)
        if (res.data.code === 1) {
          ElMessage.success('员工添加成功！')
          if (!goAnd) {
            router.push('/employee')
          } else {
            // 保存并继续添加：重置表单
            Object.assign(ruleForm, {
              id: '',
              username: '',
              name: '',
              phone: '',
              sex: '1',
              idNumber: '',
            })
            ruleFormRef.value?.resetFields()
          }
        } else {
          ElMessage.error(res.data.desc || res.data.msg)
        }
      } else {
        const res: any = await editEmployee(params)
        if (res.data.code === 1) {
          ElMessage.success('员工修改成功！')
          router.push('/employee')
        } else {
          ElMessage.error(res.data.desc || res.data.msg)
        }
      }
    } catch (err: any) {
      ElMessage.error('请求出错了：' + err.message)
    }
  })
}

// ---- 生命周期 ----
onMounted(() => {
  optType.value = route.query.id ? 'edit' : 'add'
  if (route.query.id) {
    init()
  }
})
</script>

<style lang="scss" scoped>
.addBrand {
  &-container {
    margin: 30px;
    margin-top: 30px;
    .HeadLable {
      background-color: transparent;
      margin-bottom: 0px;
      padding-left: 0px;
    }
    .container {
      position: relative;
      z-index: 1;
      background: #fff;
      padding: 30px;
      border-radius: 4px;
      .subBox {
        padding-top: 30px;
        text-align: center;
        border-top: solid 1px $gray-5;
      }
    }
    .idNumber {
      margin-bottom: 39px;
    }

    .el-form-item {
      margin-bottom: 29px;
    }
    .el-input {
      width: 293px;
    }
  }
}
</style>
