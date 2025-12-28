# Xiaohongshu Admin Panel

This is the web-based administration panel for the Xiaohongshu clone project.

## Tech Stack
- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router

## Setup

1. Install dependencies:
   ```bash
   npm install
   ```

2. Run development server:
   ```bash
   npm run dev
   ```

3. Build for production:
   ```bash
   npm run build
   ```

## Configuration
The API proxy is configured in `vite.config.js`. By default, it proxies `/api` requests to `http://localhost:3000`.

## Login
You need a user with `role = 1` in the database to log in.
