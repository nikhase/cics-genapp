import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    host: 'localhost',
    strictPort: false, // Allow fallback to next available port
  },
  preview: {
    port: 3000,
    host: 'localhost',
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          'mui': ['@mui/material', '@emotion/react', '@emotion/styled'],
          'router': ['react-router-dom'],
        },
      },
    },
  },
});
