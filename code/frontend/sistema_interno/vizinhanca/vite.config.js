import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import svgr from 'vite-plugin-svgr'


// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), svgr()],
  server: {
    proxy: {
      '/api': {
        target: 'https://api-vizinhanca-dqfucdazb0eef3d5.brazilsouth-01.azurewebsites.net',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      }
    }
  }
})
