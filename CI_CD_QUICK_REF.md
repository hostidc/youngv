# ⚡ CI/CD 快速参考卡

## 🎯 一句话总结
**推送代码到GitHub → 自动编译APK → 在Actions下载**

---

## 📝 三步开始

### 1️⃣ 推送代码
```bash
git add .
git commit -m "更新代码"
git push origin main
```

### 2️⃣ 查看构建
访问: `https://github.com/你的用户名/ucbrowser/actions`

### 3️⃣ 下载APK
Actions → 选择运行 → Artifacts → 点击下载

---

## 🔖 常用命令

### 本地测试构建
```bash
# 授予权限（首次）
chmod +x gradlew

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK
./gradlew assembleRelease

# 清理后构建
./gradlew clean assembleDebug
```

### Git操作
```bash
# 创建版本标签（触发Release）
git tag v1.0.0
git push origin v1.0.0

# 跳过CI（文档更新等）
git commit -m "docs: 更新说明 [skip ci]"
```

---

## 📂 文件位置

| 文件 | 路径 | 说明 |
|------|------|------|
| 工作流配置 | `.github/workflows/android-ci.yml` | CI/CD核心配置 |
| Gradle Wrapper | `gradlew` / `gradlew.bat` | 构建脚本 |
| 构建配置 | `build.gradle` | Gradle配置 |
| 使用指南 | `CI_CD_GUIDE.md` | 详细文档 |

---

## ❓ 常见问题速查

### Q: APK在哪里？
**A**: Actions → 最新运行 → 页面底部Artifacts区域

### Q: 构建失败怎么办？
**A**: 
1. 点击失败的Job查看详细日志
2. 检查错误信息
3. 本地运行 `./gradlew assembleDebug` 测试

### Q: 如何加速构建？
**A**: 
- Gradle依赖已自动缓存
- 无需额外配置
- 首次构建较慢，后续会快很多

### Q: 可以自定义配置吗？
**A**: 
- 编辑 `.github/workflows/android-ci.yml`
- 参考 `CI_CD_GUIDE.md` 高级配置章节

---

## 🔗 快速链接

- 📖 [完整CI/CD指南](CI_CD_GUIDE.md)
- ⚙️ [工作流配置文件](.github/workflows/android-ci.yml)
- 📦 [项目README](README.md)
- 🔒 [安全审计报告](SECURITY_AUDIT.md)

---

## 💡 提示

> **首次推送后，构建可能需要5-10分钟**
> 
> 后续构建因缓存优化，通常只需2-3分钟

---

**享受自动化构建的便利！** 🚀
