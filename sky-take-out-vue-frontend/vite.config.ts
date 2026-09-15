import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // 加载 .env / .env.[mode] 里的 VITE_ 前缀环境变量
  const env = loadEnv(mode, process.cwd(), '')

  const isProd = mode === 'production'

  return {
    plugins: [
      vue(),
      vueDevTools(),
      // PWA（可选）
      VitePWA({
        registerType: 'autoUpdate',
        manifest: {
          name: 'Vue Typescript Admin',
          short_name: 'Vue TS Admin',
          // ...
        },
      }),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      open: true,
      host: true,
      hmr: {
        overlay: true,
      },
      proxy: {
        '/api': {
          target: env.VITE_APP_URL,
          ws: false,
          secure: false,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },

    build: {
      // 对应 configureWebpack.devtool: 'source-map'
      sourcemap: isProd,
    },

    css: {
      devSourcemap: false,
      preprocessorOptions: {
        scss: {
          additionalData: `
          @use "@/styles/_variables.scss" as *;
          @use "@/styles/_mixins.scss" as *;
        `,
        },
      },
    },
  }
})
