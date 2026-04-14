# 🚀 UC浏览器风格 Android APP（完整版）

[![Android CI/CD](https://github.com/你的用户名/ucbrowser/actions/workflows/android-ci.yml/badge.svg)](https://github.com/你的用户名/ucbrowser/actions/workflows/android-ci.yml)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)

基于 **Android Studio + Kotlin + WebView** 开发的类UC浏览器应用，提供完整的浏览体验和高级功能。

**🎉 已配置GitHub Actions自动构建，每次Push自动编译APK！**

## ✨ 核心功能清单

### 🎯 基础功能
- ✅ **多标签页管理** - 仿UC标签切换，支持多网页同时浏览
- ✅ **地址栏搜索** - 集成百度搜索，关键词/URL智能识别
- ✅ **前进/后退/刷新** - 完整的页面导航控制
- ✅ **主页** - 自定义主页，常用网站快捷入口
- ✅ **加载进度** - 实时显示网页加载进度

### 🌟 高级功能
- ✅ **手势操作**
  - 右滑 → 后退
  - 左滑 → 前进
  - 下滑 → 刷新页面
  - 双击 → 返回顶部
  
- ✅ **广告拦截** - 自动过滤页面广告，清爽浏览体验
- ✅ **收藏夹** - 保存常用网站，主页快速访问
- ✅ **历史记录** - 自动保存浏览历史，时间线展示
- ✅ **下载管理** - 文件下载任务管理
- ✅ **夜间模式** - 护眼暗色主题
- ✅ **无图模式** - 节省流量，提升加载速度

### 📱 UI/UX优化
- ✅ **底部导航菜单**
  - 首页 - 返回浏览器主页
  - 视频 - 热门视频聚合页面
  - 我的 - 个人中心，功能设置
  
- ✅ **Material Design 3** - 现代化设计风格
- ✅ **橙色主题** - 经典UC浏览器配色 (#FF6600)

## 🛠️ 技术栈

- **语言**: Kotlin
- **最低SDK**: 24 (Android 7.0)
- **目标SDK**: 34 (Android 14)
- **UI框架**: Material Design 3
- **核心组件**: WebView
- **数据存储**: SQLite
- **架构模式**: MVC
- **手势处理**: GestureDetector
- **图片加载**: Glide 4.16.0

## 📂 项目结构

```
ucbrowser/
├── MainActivity.kt                  # 主活动（核心浏览器+手势+广告拦截）
├── VideoActivity.kt                 # 视频页面活动
├── MineActivity.kt                  # 我的页面活动
├── HistoryActivity.kt               # 历史记录活动
├── DownloadsActivity.kt             # 下载管理活动
├── SettingsActivity.kt              # 设置活动
├── model/                           # 数据模型
│   ├── TabItem.kt                   # 标签页模型
│   ├── HistoryItem.kt               # 历史记录模型
│   ├── DownloadItem.kt              # 下载项模型
│   └── BookmarkItem.kt              # 收藏夹模型 ⭐新增
├── adapter/                         # RecyclerView适配器
│   ├── TabAdapter.kt                # 标签页适配器
│   ├── HistoryAdapter.kt            # 历史记录适配器
│   └── DownloadAdapter.kt           # 下载列表适配器
├── data/                            # 数据层
│   └── DatabaseHelper.kt            # SQLite数据库（含收藏夹）⭐升级
├── utils/                           # 工具类
│   ├── PreferenceManager.kt         # 偏好设置管理器
│   ├── UrlUtils.kt                  # URL处理工具
│   ├── GestureHandler.kt            # 手势处理器 ⭐新增
│   └── AdBlocker.kt                 # 广告拦截器 ⭐新增
└── res/                             # 资源文件
    ├── layout/                      # 布局文件
    │   ├── activity_main.xml        # 主布局（含底部导航）⭐升级
    │   ├── activity_video.xml       # 视频页面 ⭐新增
    │   ├── activity_mine.xml        # 我的页面 ⭐新增
    │   └── ...
    ├── menu/                        # 菜单资源 ⭐新增
    │   └── bottom_navigation_menu.xml
    ├── color/                       # 颜色选择器 ⭐新增
    │   └── nav_item_color.xml
    ├── drawable/                    # 图形资源（矢量图标）
    ├── values/                      # 值资源
    └── xml/                         # XML配置
```

## 🎨 界面展示

### 主要页面
1. **浏览器主页** 
   - 顶部地址栏 + 导航按钮
   - 中部WebView
   - 底部导航菜单
   
2. **收藏夹主页**
   - 我的收藏（动态显示）
   - 常用网站（8个快捷入口）

3. **视频页面**
   - 分类标签（推荐、抖音、快手等）
   - 视频卡片列表

4. **我的页面**
   - 用户信息卡片
   - 功能菜单（收藏、历史、下载、设置）
   - 快捷开关（夜间模式、无图模式）

---

## 🤖 CI/CD 自动构建

本项目已配置 **GitHub Actions** 实现自动化构建：

### ✨ 自动化功能

- ✅ **自动编译APK** - 每次Push到main/master分支时触发
- ✅ **代码质量检查** - 运行Android Lint静态分析
- ✅ **单元测试** - 执行JUnit测试用例
- ✅ **安全扫描** - 检查依赖漏洞
- ✅ **自动发布** - 打Tag时自动创建GitHub Release并附加APK

### 📦 获取APK

#### 方式1: 从Actions下载（推荐）

1. 访问仓库的 **Actions** 标签页
2. 选择最新的工作流运行
3. 在页面底部点击 **Artifacts**
4. 下载 `debug-apk` 或 `release-apk`

#### 方式2: 本地构建

```bash
# Debug版本（未签名，用于测试）
./gradlew assembleDebug

# Release版本（需配置签名）
./gradlew assembleRelease

# APK输出位置
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

### 🔧 配置说明

详细配置请查看：
- 📄 [.github/workflows/android-ci.yml](.github/workflows/android-ci.yml) - 工作流配置
- 📖 [CI_CD_GUIDE.md](CI_CD_GUIDE.md) - 完整使用指南

### 🚀 快速开始CI/CD

```bash
# 1. 推送代码到GitHub
git add .
git commit -m "feat: 添加CI/CD配置"
git push origin main

# 2. 查看构建状态
# 访问: https://github.com/你的用户名/ucbrowser/actions

# 3. 下载APK
# Actions → 最新运行 → Artifacts → 下载
```

---

## 📖 使用说明

### 手势操作
| 手势 | 功能 |
|------|------|
| **右滑** | 页面后退 |
| **左滑** | 页面前进 |
| **下滑** | 刷新页面 |
| **双击** | 返回顶部 |

### 底部导航
- **首页** - 返回浏览器主页
- **视频** - 打开视频聚合页面
- **我的** - 进入个人中心

### 添加收藏
1. 点击左上角菜单按钮
2. 选择"添加收藏"
3. 当前页面会保存到主页收藏夹

### 开启广告拦截
- 默认启用广告拦截
- 菜单中可临时关闭

### 夜间模式/无图模式
- 方式1: 我的页面 → 快捷开关
- 方式2: 菜单 → 对应选项

## 🔧 配置说明

### 修改主题色
编辑 `res/values/colors.xml`:
```xml
<color name="primary">#FF6600</color>  <!-- 改为你喜欢的颜色 -->
```

### 修改默认搜索引擎
编辑 `utils/UrlUtils.kt`:
```kotlin
return "https://www.baidu.com/s?wd=${URLEncoder.encode(url, ...)}"
// 改为 Google: https://www.google.com/search?q=
```

### 自定义主页快捷方式
编辑 `MainActivity.kt` 的 `getHomePageHtml()` 方法中的 shortcuts 部分

## 📝 功能对比表

| 功能 | 基础版 | 完整版 ⭐ |
|------|--------|----------|
| 多标签页 | ✅ | ✅ 优化 |
| 历史记录 | ✅ | ✅ |
| 下载管理 | ✅ | ✅ |
| 夜间模式 | ✅ | ✅ |
| 无图模式 | ✅ | ✅ |
| 设置页面 | ✅ | ✅ |
| **手势操作** | ❌ | ✅ 新增 |
| **广告拦截** | ❌ | ✅ 新增 |
| **收藏夹** | ❌ | ✅ 新增 |
| **底部导航** | ❌ | ✅ 新增 |
| **视频页面** | ❌ | ✅ 新增 |
| **我的页面** | ❌ | ✅ 新增 |

## 🎯 开发计划

- [ ] 书签管理界面
- [ ] 广告拦截规则更新
- [ ] 全屏沉浸模式
- [ ] 语音搜索
- [ ] 二维码扫描
- [ ] 数据云同步
- [ ] 插件扩展系统
- [ ] 视频播放器增强
- [ ] 小说阅读模式

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

## 📧 联系方式

- Issue: GitHub Issues
- Email: example@example.com

---

**完整UC浏览器体验，从这里开始！🎉**

*Enjoy Browsing!*
