import { onBeforeMount, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAppStore, DeviceType } from '@/stores/modules/app'

const WIDTH = 992 // refer to Bootstrap's responsive design

export function useResize() {
  const route = useRoute()
  const appStore = useAppStore()
  const { device, sidebar } = storeToRefs(appStore)

  const isMobile = () => {
    const rect = document.body.getBoundingClientRect()
    return rect.width - 1 < WIDTH
  }

  const resizeHandler = () => {
    if (!document.hidden) {
      const mobile = isMobile()
      appStore.toggleDevice(mobile ? DeviceType.Mobile : DeviceType.Desktop)
      if (mobile) {
        appStore.closeSideBar(true)
      }
    }
  }

  // 原 @Watch('$route')
  watch(
    () => route.path,
    () => {
      if (device.value === DeviceType.Mobile && sidebar.value.opened) {
        appStore.closeSideBar(false)
      }
    },
  )

  onBeforeMount(() => {
    window.addEventListener('resize', resizeHandler)
  })

  onMounted(() => {
    if (isMobile()) {
      appStore.toggleDevice(DeviceType.Mobile)
      appStore.closeSideBar(true)
    }
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeHandler)
  })

  return { device, sidebar }
}
