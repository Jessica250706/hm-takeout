<template>
  <el-dialog
    v-model="visible"
    title="修改密码"
    width="568px"
    class="pwdCon"
    @close="handlePwdClose"
  >
    <el-form ref="formRef" :model="form" label-width="85px" :rules="rules">
      <el-form-item label="原始密码：" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="新密码：" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="6 - 20位密码，数字或字母，区分大小写"
        />
      </el-form-item>
      <el-form-item label="确认密码：" prop="affirmPassword">
        <el-input v-model="form.affirmPassword" type="password" placeholder="请输入" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handlePwdClose">取 消</el-button>
        <el-button type="primary" @click="handleSave">保 存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { editPassword } from '@/api/users'

defineOptions({
  name: 'Password',
})

const props = defineProps<{
  dialogFormVisible: boolean
}>()

const emit = defineEmits<{
  (e: 'handleclose'): void
}>()

const formRef = ref<FormInstance>()

// ⚠️ 用 v-model 绑定时不能直接绑 props，需要中间 computed（见下方说明）
const visible = computed({
  get: () => props.dialogFormVisible,
  set: () => {
    // 弹窗关闭时触发 handleclose 让父组件把 prop 置 false
    emit('handleclose')
  },
})

interface PasswordForm {
  oldPassword: string
  newPassword: string
  affirmPassword: string
}

const form = reactive<PasswordForm>({
  oldPassword: '',
  newPassword: '',
  affirmPassword: '',
})

const validatePwd = (_rule: any, value: string, callback: (err?: Error) => void) => {
  const reg = /^[0-9A-Za-z]{6,20}$/
  if (!value) {
    callback(new Error('请输入'))
  } else if (!reg.test(value)) {
    callback(new Error('6 - 20位密码，数字或字母，区分大小写'))
  } else {
    callback()
  }
}

const validatePass2 = (_rule: any, value: string, callback: (err?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('密码不一致，请重新输入密码'))
  } else {
    callback()
  }
}

const rules: FormRules<PasswordForm> = {
  oldPassword: [{ validator: validatePwd, trigger: 'blur' }],
  newPassword: [{ validator: validatePwd, trigger: 'blur' }],
  affirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
}

const handleSave = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      const params = {
        oldPassword: form.oldPassword,
        newPassword: form.newPassword,
      }
      await editPassword(params)
      emit('handleclose')
      formRef.value?.resetFields()
    }
  })
}

const handlePwdClose = () => {
  formRef.value?.resetFields()
  emit('handleclose')
}
</script>

<style lang="scss">
.navbar {
  .pwdCon {
    .el-dialog__body {
      padding-top: 60px;
      padding: 60px 100px 0;
    }
    .el-input__inner {
      padding: 0 12px;
    }
    .el-form-item {
      margin-bottom: 26px;
    }
    .el-form-item__label {
      text-align: left;
    }
    .el-dialog__footer {
      padding-top: 14px;
    }
  }
}
</style>
