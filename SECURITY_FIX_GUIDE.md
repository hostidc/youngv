# 🔧 安全问题快速修复指南

本文档提供安全审核中发现问题的**逐步修复方案**。

---

## 🚨 第一阶段：高危问题修复（立即执行）

### 修复1: WebView安全配置

**文件**: `MainActivity.kt`

**修改位置**: `setupWebView()` 方法

```kotlin
private fun setupWebView() {
    webView.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        
        // ✅ 新增：安全配置
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        setGeolocationEnabled(false)
        allowFileAccess = false
        allowContentAccess = false
        allowUniversalAccessFromFileURLs = false
        allowFileAccessFromFileURLs = false
        savePassword = false
        saveFormData = false
        setSupportMultipleWindows(false)
        
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        useWideViewPort = true
        loadWithOverviewMode = true
        cacheMode = WebSettings.LOAD_DEFAULT
        
        blockNetworkImage = preferenceManager.isImageFreeMode
        
        userAgentString = "Mozilla/5.0 (Linux; Android 10; UCBrowser) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
    }
    
    // ✅ 新增：移除危险的JavaScript接口
    removeJavascriptInterface("searchBoxJavaBridge_")
    removeJavascriptInterface("accessibility")
    removeJavascriptInterface("accessibilityTraversal")
    
    // ... 其余代码保持不变
}
```

---

### 修复2: 禁用明文HTTP流量

**步骤1**: 创建网络安全配置文件

**新建文件**: `res/xml/network_security_config.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- 默认禁止明文流量 -->
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

**步骤2**: 更新AndroidManifest.xml

**文件**: `AndroidManifest.xml`

```xml
<application
    android:allowBackup="false"
    android:dataExtractionRules="@xml/data_extraction_rules"
    android:fullBackupContent="false"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.UCBrowser"
    android:usesCleartextTraffic="false"
    android:networkSecurityConfig="@xml/network_security_config"
    tools:targetApi="31">
```

**关键修改**:
- `android:usesCleartextTraffic="true"` → `"false"`
- `android:allowBackup="true"` → `"false"`
- `android:fullBackupContent="@xml/backup_rules"` → `"false"`
- 添加 `android:networkSecurityConfig="@xml/network_security_config"`

---

### 修复3: SQL注入防护

**文件**: `data/DatabaseHelper.kt`

**替换整个 `updateBookmarkVisit` 方法**:

```kotlin
// 更新书签访问时间
fun updateBookmarkVisit(url: String) {
    // ✅ 输入验证
    if (url.isBlank() || url.length > 2048) {
        return
    }
    
    val db = writableDatabase
    
    // ✅ 使用事务确保原子性
    db.beginTransaction()
    try {
        // 先查询当前访问次数
        val cursor = db.query(
            TABLE_BOOKMARKS,
            arrayOf(COLUMN_VISIT_COUNT),
            "$COLUMN_URL = ?",
            arrayOf(url),
            null, null, null
        )
        
        val currentCount = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }
        cursor.close()
        
        // 更新访问次数和时间
        val values = ContentValues().apply {
            put(COLUMN_VISIT_COUNT, currentCount + 1)
            put(COLUMN_LAST_VISIT_TIME, System.currentTimeMillis())
        }
        
        db.update(TABLE_BOOKMARKS, values, "$COLUMN_URL = ?", arrayOf(url))
        db.setTransactionSuccessful()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        db.endTransaction()
    }
}
```

---

## ⚡ 第二阶段：中危问题修复（1周内）

### 修复4: 限制FileProvider路径

**文件**: `res/xml/file_paths.xml`

**完全替换内容**:

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 仅允许访问应用专属外部存储 -->
    <external-files-path 
        name="downloads" 
        path="Download/" />
    
    <!-- 缓存目录 -->
    <external-cache-path 
        name="cache" 
        path="." />
    
    <!-- 内部文件 -->
    <files-path 
        name="internal_files" 
        path="." />
</paths>
```

---

### 修复5: 优化广告拦截脚本

**文件**: `utils/AdBlocker.kt`

**替换 `getAdBlockScript()` 方法**:

```kotlin
/**
 * 清理页面中的广告元素（注入JavaScript）
 */
fun getAdBlockScript(): String {
    // ✅ 使用静态脚本，避免动态拼接
    return """
        (function() {
            'use strict';
            
            try {
                // 广告选择器列表
                var adSelectors = [
                    'div[class*="ads"]',
                    'div[id*="ads"]',
                    'ins.adsbygoogle',
                    '.advertisement',
                    '.ads-container',
                    '[class*="banner-ad"]',
                    '[id*="banner-ad"]'
                ];
                
                adSelectors.forEach(function(selector) {
                    var elements = document.querySelectorAll(selector);
                    elements.forEach(function(el) {
                        el.style.display = 'none';
                        el.remove();
                    });
                });
            } catch (e) {
                console.warn('AdBlock error:', e);
            }
        })();
    """.trimIndent()
}
```

---

### 修复6: 移除不必要的权限

**文件**: `AndroidManifest.xml`

**删除以下权限**（第16-19行）:

```xml
<!-- ❌ 删除这些行 -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

**保留的权限**:
```xml
<!-- 网络权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<!-- 下载权限（Android 12及以下） -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

---

### 修复7: 禁用数据备份

已在**修复2**中完成（设置 `android:allowBackup="false"`）。

如需选择性备份，参考SECURITY_AUDIT.md中的方案B。

---

### 修复8: WebView缓存优化

**文件**: `MainActivity.kt`

**在 `onDestroy()` 方法中添加**:

```kotlin
override fun onDestroy() {
    super.onDestroy()
    
    // ✅ 清除敏感数据
    webView.clearHistory()
    webView.clearCache(true)
    webView.clearFormData()
    webView.destroy()
}
```

**在 `setupWebView()` 中添加**:

```kotlin
webView.settings.apply {
    // ... 现有配置
    
    // ✅ 禁用表单和密码保存
    savePassword = false
    saveFormData = false
}
```

---

## 📝 第三阶段：低危问题修复（2周内）

### 修复9: URL协议验证

**文件**: `utils/UrlUtils.kt`

**替换 `formatUrl()` 方法**:

```kotlin
// 格式化URL，添加协议前缀
fun formatUrl(input: String): String {
    val trimmed = input.trim()
    
    // ✅ 阻止危险的伪协议
    val dangerousProtocols = listOf(
        "javascript:", "vbscript:", "data:", "file:",
        "ftp:", "telnet:", "mailto:"
    )
    
    if (dangerousProtocols.any { trimmed.startsWith(it, ignoreCase = true) }) {
        throw IllegalArgumentException("Unsupported protocol")
    }
    
    // 如果是搜索关键词
    if (!isValidUrl(trimmed) && 
        !trimmed.startsWith("http://", ignoreCase = true) &&
        !trimmed.startsWith("https://", ignoreCase = true)) {
        return "https://www.baidu.com/s?wd=${URLEncoder.encode(trimmed, StandardCharsets.UTF_8.toString())}"
    }
    
    // 添加协议前缀
    var url = trimmed
    if (!url.startsWith("http://", ignoreCase = true) && 
        !url.startsWith("https://", ignoreCase = true)) {
        url = "https://$url"
    }
    
    return url
}
```

---

### 修复10: 改进错误处理

**文件**: 所有Kotlin文件

**查找并替换所有**:
```kotlin
} catch (e: Exception) {
    e.printStackTrace()
}
```

**改为**:
```kotlin
} catch (e: Exception) {
    Log.e("UCBrowser", "Error occurred", e)
    // 可选：显示用户友好的提示
    // Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show()
}
```

**在文件顶部添加**:
```kotlin
import android.util.Log

private const val TAG = "UCBrowser"
```

---

### 修复11: 明确Activity导出属性

**文件**: `AndroidManifest.xml`

**为所有非启动Activity添加 `android:exported="false"`**:

```xml
<activity
    android:name=".SettingsActivity"
    android:exported="false"
    android:label="@string/settings"
    android:parentActivityName=".MainActivity" />
    
<activity
    android:name=".HistoryActivity"
    android:exported="false"
    android:label="@string/history"
    android:parentActivityName=".MainActivity" />
    
<activity
    android:name=".DownloadsActivity"
    android:exported="false"
    android:label="@string/downloads"
    android:parentActivityName=".MainActivity" />

<activity
    android:name=".VideoActivity"
    android:exported="false"
    android:label="热门视频"
    android:parentActivityName=".MainActivity" />

<activity
    android:name=".MineActivity"
    android:exported="false"
    android:label="我的"
    android:parentActivityName=".MainActivity" />
```

---

## ✅ 验证清单

完成修复后，请验证以下项目：

### 编译检查
```bash
# Android Studio中执行
Build → Rebuild Project
```

### 功能测试
- [ ] WebView能正常加载HTTPS网站
- [ ] HTTP网站被正确阻止
- [ ] 手势操作正常工作
- [ ] 标签页管理正常
- [ ] 历史记录保存正常
- [ ] 收藏夹功能正常
- [ ] 下载功能正常
- [ ] 夜间模式切换正常
- [ ] 无图模式工作正常

### 安全测试
- [ ] 尝试访问 `javascript:alert(1)` - 应被阻止
- [ ] 尝试访问 `file:///etc/passwd` - 应被阻止
- [ ] 查看Logcat - 无敏感信息泄露
- [ ] 检查备份 - 数据库不应被备份
- [ ] 检查权限 - 仅请求必要权限

### 性能测试
- [ ] 页面加载速度无明显下降
- [ ] 内存使用正常
- [ ] 电池消耗正常

---

## 🎯 修复优先级总结

| 优先级 | 修复项 | 预计时间 | 风险等级 |
|--------|--------|----------|----------|
| P0 | WebView安全配置 | 30分钟 | 高 |
| P0 | 禁用HTTP明文 | 15分钟 | 高 |
| P0 | SQL注入修复 | 30分钟 | 高 |
| P1 | FileProvider限制 | 10分钟 | 中 |
| P1 | 广告脚本优化 | 20分钟 | 中 |
| P1 | 移除多余权限 | 5分钟 | 中 |
| P1 | 禁用备份 | 已包含在P0 | 中 |
| P2 | WebView缓存清理 | 10分钟 | 中 |
| P3 | URL验证增强 | 15分钟 | 低 |
| P3 | 错误处理改进 | 30分钟 | 低 |
| P3 | Activity导出声明 | 10分钟 | 低 |

**总预计时间**: 约3小时

---

## 📞 需要帮助？

如果在修复过程中遇到问题：

1. **查看完整报告**: `SECURITY_AUDIT.md`
2. **参考官方文档**: 
   - [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
   - [WebView Security](https://developer.android.com/guide/webapps/managing-webview)
3. **提交Issue**: GitHub Issues

---

**祝修复顺利！安全第一！** 🔒
