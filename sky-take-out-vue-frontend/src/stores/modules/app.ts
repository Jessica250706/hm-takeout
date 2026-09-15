import { defineStore } from 'pinia'
import { setSidebarStatus } from '@/utils/cookies'

export enum DeviceType {
  Mobile,
  Desktop,
}

export interface IAppState {
  device: DeviceType
  sidebar: {
    opened: boolean
    withoutAnimation: boolean
  }
  statusNumber: number
}

export const useAppStore = defineStore('app', {
  state: (): IAppState => ({
    sidebar: {
      opened: true, // getSidebarStatus() !== 'closed'
      withoutAnimation: false,
    },
    device: DeviceType.Desktop,
    statusNumber: 0,
  }),

  actions: {
    // 原 TOGGLE_SIDEBAR mutation + ToggleSideBar action 合并
    toggleSideBar(withoutAnimation: boolean) {
      this.sidebar.opened = !this.sidebar.opened
      this.sidebar.withoutAnimation = withoutAnimation
      if (this.sidebar.opened) {
        setSidebarStatus('opened')
      } else {
        setSidebarStatus('closed')
      }
    },

    // 原 CLOSE_SIDEBAR mutation + CloseSideBar action 合并
    closeSideBar(withoutAnimation: boolean) {
      this.sidebar.opened = false
      this.sidebar.withoutAnimation = withoutAnimation
      setSidebarStatus('closed')
    },

    // 原 TOGGLE_DEVICE mutation + ToggleDevice action 合并
    toggleDevice(device: DeviceType) {
      this.device = device
    },

    // 原 STATUS_NUMBER mutation + StatusNumber action 合并
    statusNumberAction(device: number) {
      this.statusNumber = device
    },
  },
})
