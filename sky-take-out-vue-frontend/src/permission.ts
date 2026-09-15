import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import Cookies from 'js-cookie'
import type { RouteLocationNormalized, NavigationGuardNext } from 'vue-router'

NProgress.configure({ showSpinner: false })

router.beforeEach(
  async (
    to: RouteLocationNormalized,
    _from: RouteLocationNormalized,
    next: NavigationGuardNext,
  ) => {
    NProgress.start()
    if (Cookies.get('token')) {
      next()
    } else {
      if (!to.meta.notNeedAuth) {
        next('/login')
      } else {
        next()
      }
    }
  },
)

router.afterEach((to: RouteLocationNormalized) => {
  NProgress.done()
  document.title = (to.meta.title as string) || '默认标题'
})
