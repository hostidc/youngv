package com.example.ucbrowser.utils

import android.util.Patterns
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object UrlUtils {
    
    // 判断是否是有效的URL
    fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
    
    // 格式化URL，添加协议前缀
    fun formatUrl(input: String): String {
        var url = input.trim()
        
        // 如果是搜索关键词，返回百度搜索URL
        if (!isValidUrl(url) && !url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://www.baidu.com/s?wd=${URLEncoder.encode(url, StandardCharsets.UTF_8.toString())}"
        }
        
        // 添加协议前缀
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        
        return url
    }
    
    // 获取域名
    fun getDomain(url: String): String {
        return try {
            val uri = android.net.Uri.parse(url)
            uri.host ?: url
        } catch (e: Exception) {
            url
        }
    }
    
    // 获取简洁的URL显示
    fun getDisplayUrl(url: String): String {
        return try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                val uri = android.net.Uri.parse(url)
                uri.host ?: url
            } else {
                url
            }
        } catch (e: Exception) {
            url
        }
    }
}
