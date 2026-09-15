import Cookies from 'js-cookie'

/**
 * 安全地从 Cookie 读取并解析 JSON
 * - Cookie 不存在返回 null
 * - JSON 解析失败返回 null
 */
export function getJsonCookie<T = any>(key: string): T | null {
  const raw = Cookies.get(key)
  if (!raw) return null
  try {
    return JSON.parse(raw) as T
  } catch {
    return null
  }
}

// App
const sidebarStatusKey = 'sidebar_status'
export const getSidebarStatus = () => Cookies.get(sidebarStatusKey)
export const setSidebarStatus = (sidebarStatus: string) =>
  Cookies.set(sidebarStatusKey, sidebarStatus)

// User
const storeIdKey = 'storeId'
export const getStoreId = () => Cookies.get(storeIdKey)
export const setStoreId = (id: string) => Cookies.set(storeIdKey, id)
export const removeStoreId = () => Cookies.remove(storeIdKey)

// Token
const tokenKey = 'token'
export const getToken = () => Cookies.get(tokenKey)
export const setToken = (token: string) => Cookies.set(tokenKey, token)
export const removeToken = () => Cookies.remove(tokenKey)

// userInfo —— ⚠️ key 统一为 'user_info'
const userInfoKey = 'user_info'
export const getUserInfoRaw = () => Cookies.get(userInfoKey)
export const getUserInfo = <T = any>(): T | null => getJsonCookie<T>(userInfoKey)
export const setUserInfo = (userInfo: object) => Cookies.set(userInfoKey, JSON.stringify(userInfo))
export const removeUserInfo = () => Cookies.remove(userInfoKey)

// printinfo
const printKey = 'print'
export const getPrint = () => Cookies.get(printKey)
export const setPrint = (useInfor: object) => Cookies.set(printKey, JSON.stringify(useInfor))
export const removePrint = () => Cookies.remove(printKey)

// 消息未读数
const newDataKey = 'new'
export const getNewData = () => Cookies.get(newDataKey)
export const setNewData = (val: object) => Cookies.set(newDataKey, JSON.stringify(val))
