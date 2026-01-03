# XiaoHongShu (Little Red Book) Clone

这是一个基于 **NestJS** (服务端)、**Android** (移动端) 和 **Vue 3** (管理后台) 的全栈仿小红书项目。实现了核心的社交媒体功能，包括笔记发布、瀑布流展示、点赞收藏、评论互动、关注粉丝体系以及即时通讯。

## 🌟 项目亮点

*   **全栈闭环**: 涵盖移动端 App、后端 API 服务和 Web 管理后台的完整生态。
*   **现代化架构**:
    *   **Android**: MVVM + Hilt + Coroutines + Jetpack Components。
    *   **Server**: NestJS 模块化设计 + TypeORM + MySQL。
    *   **Admin**: Vue 3 + Vite + Element Plus。
*   **核心体验复刻**:
    *   双列瀑布流布局 (StaggeredGridLayout)。
    *   高效的数据聚合 (Feed 流一次性返回点赞/收藏/关注状态)。
    *   完整的社交互动 (点赞、收藏、评论、@提及、私信)。

## 🛠️ 技术栈

### 服务端 (Server)
*   **Framework**: NestJS
*   **Language**: TypeScript
*   **Database**: MySQL 8.0
*   **ORM**: TypeORM
*   **Auth**: Passport + JWT
*   **File Upload**: Multer

### 移动端 (Android)
*   **Language**: Kotlin
*   **Architecture**: MVVM
*   **DI**: Hilt
*   **Network**: Retrofit + OkHttp
*   **Async**: Coroutines
*   **Image Loading**: Glide

### 管理后台 (Admin Web)
*   **Framework**: Vue 3
*   **Build Tool**: Vite
*   **UI Library**: Element Plus

## 🚀 快速开始

### 1. 环境准备
*   Node.js v18+
*   MySQL 8.0+
*   Android Studio Ladybug+
*   JDK 17+

### 2. 服务端启动
```bash
cd server
# 安装依赖
npm install
# 配置数据库连接 (修改 src/app.module.ts 中的数据库配置)
# 启动服务
npm run start:dev
```
服务默认运行在 `http://localhost:3000`。

### 3. 管理后台启动
```bash
cd admin-web
# 安装依赖
npm install
# 启动开发服务器
npm run dev
```
访问地址: `http://localhost:5173`

### 4. Android 客户端运行
1.  使用 Android Studio 打开 `android` 目录。
2.  修改 `api/NetworkModule.kt` 中的 `BASE_URL` 为你的本机 IP 地址 (例如 `http://192.168.1.x:3000/`)。
    *   *注意: Android 模拟器可以使用 `http://10.0.2.2:3000/` 访问宿主机 localhost。*
3.  Sync Gradle 并运行 App。

## 📂 目录结构

```
xiaohongshu/
├── android/          # Android 客户端源码
├── server/           # NestJS 服务端源码
├── admin-web/        # Vue3 管理后台源码
├── 功能需求文档.md    # 详细功能说明
├── 技术架构文档.md    # 系统架构设计
└── 业务功能清单.md    # 业务模块列表
```

## ✨ 主要功能

*   **用户模块**: 注册登录、个人主页、关注/粉丝列表、黑名单。
*   **笔记模块**: 图片上传发布、首页瀑布流推荐、笔记详情、删除笔记。
*   **互动模块**: 点赞、收藏、评论 (支持回复)、@用户。
*   **消息模块**: 私信聊天、互动通知 (赞/藏/关注/评论)。
*   **管理后台**: 数据统计、内容审核、用户管理。

## 📝 开发文档
更多详细设计请参考：
*   [技术架构文档](./技术架构文档.md)
*   [业务功能清单](./业务功能清单.md)
*   [功能需求文档](./功能需求文档.md)

## 📄 License
[MIT](LICENSE)
