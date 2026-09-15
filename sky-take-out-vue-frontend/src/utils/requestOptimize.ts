import md5 from 'js-md5'
import type { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'

/**
 * 根据请求的地址、方式、参数，统一计算出当前请求的 md5 值作为 key
 */
export function getRequestKey(config?: AxiosRequestConfig | InternalAxiosRequestConfig): string {
  if (!config) {
    // 没有请求配置信息时，根据时间戳生成
    return md5(String(Date.now()))
  }

  const data = typeof config.data === 'string' ? config.data : JSON.stringify(config.data)

  return md5(`${config.url}&${config.method}&${data}`)
}

/**
 * 存储 key 值
 */
export const pending: Record<string, boolean> = {}

/**
 * 检查 key 值是否存在
 */
export function checkPending(key: string): boolean {
  return !!pending[key]
}

/**
 * 删除 key 值
 */
export function removePending(key: string): void {
  delete pending[key]
}
