import { defineStore } from 'pinia'
import { login, userLogout } from '@/api/employee'
import {
  getToken,
  setToken,
  removeToken,
  getStoreId,
  setStoreId,
  getUserInfo,
  removeUserInfo,
} from '@/utils/cookies'
import Cookies from 'js-cookie'
import { ElMessage } from 'element-plus'

export interface IUserState {
  token: string
  name: string
  avatar: string
  storeId: string
  introduction: string
  userInfo: any
  roles: string[]
  username: string
}

export const useUserStore = defineStore('user', {
  state: (): IUserState => ({
    token: getToken() || '',
    name: '',
    avatar: '',
    storeId: getStoreId() || '',
    introduction: '',
    userInfo: {},
    roles: [],
    username: Cookies.get('username') || '',
  }),

  actions: {
    // 原 SET_TOKEN + SET_NAME 等 mutation 已合并进各 action，见下

    async login(userInfo: { username: string; password: string }) {
      let { username, password } = userInfo
      username = username.trim()
      const { data } = await login({ username, password })

      if (String(data.code) === '1') {
        this.username = username
        this.token = data.data.token
        this.userInfo = { ...data.data }

        Cookies.set('username', username)
        Cookies.set('user_info', data.data)
        Cookies.set('token', data.data.token)
        return data
      } else {
        return ElMessage.error(data.msg)
      }
    },

    resetToken() {
      removeToken()
      this.token = ''
      this.roles = []
    },

    async changeStore(data: any) {
      // ⚠️ 原代码写的是 this.SET_STOREID = data.data（把赋值写错了），这里修正为直接赋值
      this.storeId = data.data
      this.token = data.authorization
      setStoreId(data.data)
      setToken(data.authorization)
    },

    async getUserInfo() {
      if (this.token === '') {
        throw Error('GetUserInfo: token is undefined!')
      }

      const data = JSON.parse(getUserInfo() as string)
      if (!data) {
        throw Error('Verification failed, please Login again.')
      }

      const { roles, name, avatar, introduction, applicant, storeManagerName, storeId = '' } = data

      if (!roles || roles.length <= 0) {
        throw Error('GetUserInfo: roles must be a non-null array!')
      }

      this.roles = roles
      this.userInfo = { ...data }
      this.name = name || applicant || storeManagerName
      this.avatar = avatar
      this.introduction = introduction
      this.storeId = storeId
    },

    async logOut() {
      const { data } = await userLogout({})
      removeToken()
      this.token = ''
      this.roles = []
      Cookies.remove('username')
      Cookies.remove('user_info')
      removeUserInfo()
    },
  },
})
