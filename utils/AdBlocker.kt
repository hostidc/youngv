package com.example.ucbrowser.utils

object AdBlocker {
    
    // 广告关键词列表
    private val adKeywords = listOf(
        "ads", "advertising", "advertisement", "banner", "popup",
        "doubleclick", "googlesyndication", "adservice", "analytics",
        "tracking", "tracker", "statcounter", "cnzz", "51la",
        ".ad.", "_ad.", "ad_", "-ad-", "/ad/", "ads/",
        "casino", "betting", "gambling", "loan", "insurance"
    )
    
    // 广告域名列表
    private val adDomains = listOf(
        "doubleclick.net",
        "googlesyndication.com",
        "adservice.google.com",
        "google-analytics.com",
        "cnzz.com",
        "51.la",
        "tanx.com",
        "mmstat.com",
        "adnxs.com",
        "rubiconproject.com"
    )
    
    /**
     * 检查URL是否是广告
     */
    fun isAd(url: String): Boolean {
        val lowerUrl = url.lowercase()
        
        // 检查是否包含广告关键词
        for (keyword in adKeywords) {
            if (lowerUrl.contains(keyword)) {
                return true
            }
        }
        
        // 检查是否是广告域名
        for (domain in adDomains) {
            if (lowerUrl.contains(domain)) {
                return true
            }
        }
        
        return false
    }
    
    /**
     * 清理页面中的广告元素（注入JavaScript）
     */
    fun getAdBlockScript(): String {
        return """
            (function() {
                // 隐藏常见广告元素
                var adSelectors = [
                    'div[class*="ad"]',
                    'div[id*="ad"]',
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
                        el.style.visibility = 'hidden';
                    });
                });
                
                // 阻止广告脚本加载
                var scripts = document.getElementsByTagName('script');
                for (var i = scripts.length - 1; i >= 0; i--) {
                    var src = scripts[i].src || '';
                    if (${adKeywords.map { "'$it'" }.joinToString(" || ")} .some(function(kw) { 
                        return src.toLowerCase().indexOf(kw) !== -1; 
                    })) {
                        scripts[i].parentNode.removeChild(scripts[i]);
                    }
                }
            })();
        """.trimIndent()
    }
}
