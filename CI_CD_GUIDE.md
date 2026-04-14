# 🚀 GitHub Actions CI/CD 配置指南

本文档说明如何配置和使用GitHub Actions自动编译APK。

---

## 📋 目录

- [功能概述](#功能概述)
- [快速开始](#快速开始)
- [工作流程说明](#工作流程说明)
- [配置详解](#配置详解)
- [常见问题](#常见问题)
- [高级配置](#高级配置)

---

## 🎯 功能概述

本项目已配置完整的GitHub Actions CI/CD流程，包括：

### ✅ 自动化任务

| 任务 | 说明 | 触发条件 |
|------|------|----------|
| **构建APK** | 编译Debug和Release版本 | 每次Push/PR |
| **代码检查** | Lint静态分析 | 每次Push/PR |
| **单元测试** | 运行JUnit测试 | 每次Push/PR |
| **安全扫描** | 依赖安全检查 | 每次Push/PR |
| **发布Release** | 创建GitHub Release | 打Tag时 |

### 📦 输出产物

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Lint报告**: HTML格式的代码质量报告
- **测试报告**: 单元测试结果

---

## ⚡ 快速开始

### 步骤1: 推送代码到GitHub

```bash
# 初始化Git仓库（如果还没有）
git init
git add .
git commit -m "Initial commit with CI/CD"

# 添加远程仓库
git remote add origin https://github.com/你的用户名/ucbrowser.git

# 推送到GitHub
git push -u origin main
```

### 步骤2: 查看构建状态

1. 访问你的GitHub仓库
2. 点击 **Actions** 标签
3. 查看工作流运行状态

### 步骤3: 下载APK

构建成功后：
1. 进入 **Actions** → 选择最新的工作流运行
2. 在页面底部的 **Artifacts** 区域
3. 点击 `debug-apk` 或 `release-apk` 下载

---

## 🔄 工作流程说明

### 触发条件

```yaml
on:
  push:
    branches: [ main, master, develop ]  # 推送到这些分支时触发
    tags:
      - 'v*'                              # 打tag时触发（如 v1.0.0）
  pull_request:
    branches: [ main, master ]           # PR时触发
```

### 工作流Jobs

#### 1. Build (构建)
```
✅ 检出代码
✅ 设置JDK 17
✅ 缓存Gradle依赖
✅ 构建Debug APK
✅ 构建Release APK
✅ 上传产物
```

#### 2. Lint (代码检查)
```
✅ 运行Android Lint
✅ 生成HTML报告
✅ 上传报告
```

#### 3. Test (单元测试)
```
✅ 运行JUnit测试
✅ 生成测试报告
✅ 上传报告
```

#### 4. Security Scan (安全扫描)
```
✅ 检查依赖漏洞
✅ 生成安全报告
```

#### 5. Release (发布) - 仅Tag触发
```
✅ 下载构建产物
✅ 创建GitHub Release
✅ 附加APK文件
```

---

## ⚙️ 配置详解

### 环境变量

在 `.github/workflows/android-ci.yml` 中配置：

```yaml
env:
  APP_NAME: "UCBrowser"
  BUILD_TOOLS_VERSION: "34.0.0"
```

### Gradle配置

项目使用以下配置：
- **Gradle版本**: 8.2
- **JDK版本**: 17
- **Compile SDK**: 34
- **Min SDK**: 24
- **Target SDK**: 34

### 缓存优化

工作流会缓存Gradle依赖以加速构建：

```yaml
- name: Cache Gradle packages
  uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
```

---

## ❓ 常见问题

### Q1: 构建失败，提示 "gradlew not found"

**解决方案**:
```bash
# 确保gradlew有执行权限
chmod +x gradlew

# 或者重新生成wrapper
./gradlew wrapper
```

### Q2: 如何只构建Release版本？

修改工作流文件，注释掉Debug构建：

```yaml
# 注释这行
# - name: Build Debug APK
#   run: ./gradlew assembleDebug --no-daemon --stacktrace
```

### Q3: 如何跳过某些Job？

在push时使用 `[skip ci]` 或 `[ci skip]`:

```bash
git commit -m "Update docs [skip ci]"
```

### Q4: APK在哪里下载？

1. 进入 **Actions** 标签
2. 选择最新的工作流运行
3. 滚动到页面底部
4. 点击 **Artifacts** 下的链接下载

### Q5: 如何配置签名打包？

见下方[高级配置](#高级配置-签名打包)章节。

### Q6: 构建太慢怎么办？

**优化建议**:
1. Gradle依赖已自动缓存
2. 使用 `--no-daemon` 避免守护进程问题
3. 考虑使用自托管Runner

---

## 🔧 高级配置

### 签名打包（可选）

如果需要自动签名APK，按以下步骤操作：

#### 步骤1: 准备签名文件

1. 生成Keystore文件（本地执行）:
```bash
keytool -genkey -v -keystore ucbrowser.keystore \
  -alias ucbrowser \
  -keyalg RSA -keysize 2048 -validity 10000
```

2. 将 `ucbrowser.keystore` 添加到 `.gitignore`（**不要提交到Git**）

#### 步骤2: 配置GitHub Secrets

在GitHub仓库设置中添加Secrets：

1. 进入 **Settings** → **Secrets and variables** → **Actions**
2. 添加以下Secrets：

| Secret名称 | 值 | 说明 |
|-----------|-----|------|
| `KEYSTORE_FILE` | Keystore文件的Base64编码 | `base64 ucbrowser.keystore` |
| `KEYSTORE_PASSWORD` | 密钥库密码 | 你的密码 |
| `KEY_ALIAS` | 密钥别名 | ucbrowser |
| `KEY_PASSWORD` | 密钥密码 | 你的密码 |

#### 步骤3: 更新build.gradle

```gradle
android {
    // ... 现有配置
    
    signingConfigs {
        release {
            if (System.getenv("KEYSTORE_FILE")) {
                storeFile file("ucbrowser.keystore")
                storePassword System.getenv("KEYSTORE_PASSWORD")
                keyAlias System.getenv("KEY_ALIAS")
                keyPassword System.getenv("KEY_PASSWORD")
            }
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

#### 步骤4: 更新工作流

在build job中添加解码Keystore步骤：

```yaml
- name: Decode Keystore
  if: github.event_name == 'push' && startsWith(github.ref, 'refs/tags/')
  run: |
    echo "${{ secrets.KEYSTORE_FILE }}" | base64 --decode > app/ucbrowser.keystore
  env:
    KEYSTORE_FILE: ${{ secrets.KEYSTORE_FILE }}
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### 自定义通知

#### Slack通知

```yaml
- name: Notify Slack
  if: always()
  uses: slackapi/slack-github-action@v1.24.0
  with:
    payload: |
      {
        "text": "构建${{ needs.build.result == 'success' && '成功' || '失败' }}",
        "blocks": [
          {
            "type": "section",
            "text": {
              "type": "mrkdwn",
              "text": "*${{ env.APP_NAME }}* 构建${{ needs.build.result == 'success' && '✅ 成功' || '❌ 失败' }}\n分支: `${{ github.ref_name }}`"
            }
          }
        ]
      }
  env:
    SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

#### 邮件通知

GitHub会自动发送邮件通知给：
- 触发工作流的用户
- 代码提交者

### 定时构建

添加cron调度：

```yaml
on:
  schedule:
    - cron: '0 2 * * 1'  # 每周一凌晨2点（UTC）
```

### 多架构构建

构建不同ABI的APK：

```yaml
- name: Build APK for all ABIs
  run: |
    ./gradlew assembleRelease \
      -PabiFilters="armeabi-v7a,arm64-v8a,x86,x86_64"
```

---

## 📊 监控和优化

### 构建时间优化

| 优化项 | 效果 | 说明 |
|--------|------|------|
| Gradle缓存 | ⭐⭐⭐⭐⭐ | 减少依赖下载时间 |
| JDK缓存 | ⭐⭐⭐⭐ | 避免重复安装JDK |
| 并行任务 | ⭐⭐⭐ | lint/test/build并行 |
| 增量构建 | ⭐⭐⭐⭐ | 仅编译变更部分 |

### 查看构建日志

1. 进入 **Actions** 标签
2. 选择工作流运行
3. 点击具体的Job查看详细日志

### 构建统计

在工作流总结中会显示：
- 构建时间
- APK大小
- 测试结果

---

## 🎯 最佳实践

### 1. 分支策略

```
main/master    - 生产分支（受保护）
develop        - 开发分支
feature/*      - 功能分支
release/*      - 发布分支
hotfix/*       - 热修复分支
```

### 2. Tag命名规范

```bash
# 语义化版本
git tag v1.0.0
git tag v1.0.1
git tag v1.1.0

# 预发布版本
git tag v1.0.0-beta.1
git tag v1.0.0-rc.1
```

### 3. Commit消息规范

```bash
feat: 添加新功能
fix: 修复bug
docs: 更新文档
style: 代码格式调整
refactor: 重构代码
test: 添加测试
chore: 构建/工具链变更
```

### 4. 定期清理产物

在GitHub设置中配置：
- **Settings** → **Actions** → **General**
- 设置 **Artifact retention** 为 30天

---

## 🔗 相关资源

- [GitHub Actions文档](https://docs.github.com/en/actions)
- [Android Gradle插件文档](https://developer.android.com/studio/build)
- [Gradle性能优化](https://docs.gradle.org/current/userguide/performance.html)

---

## 📞 需要帮助？

1. 查看工作流运行日志
2. 检查 `.github/workflows/android-ci.yml` 配置
3. 参考 [GitHub Actions故障排除](https://docs.github.com/en/actions/learn-github-actions/troubleshooting-github-actions)

---

**祝构建顺利！** 🚀
