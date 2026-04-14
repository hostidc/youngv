package com.example.ucbrowser

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ucbrowser.adapter.TabAdapter
import com.example.ucbrowser.data.DatabaseHelper
import com.example.ucbrowser.model.BookmarkItem
import com.example.ucbrowser.model.HistoryItem
import com.example.ucbrowser.model.TabItem
import com.example.ucbrowser.utils.AdBlocker
import com.example.ucbrowser.utils.GestureHandler
import com.example.ucbrowser.utils.PreferenceManager
import com.example.ucbrowser.utils.UrlUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.UUID

/**
 * UC浏览器主活动 - 增强版
 * 功能：多标签、手势操作、广告拦截、底部导航、收藏夹等
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var etUrl: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTabs: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var gestureHandler: GestureHandler
    
    private val tabs = mutableListOf<TabItem>()
    private var currentTabId: String? = null
    private lateinit var tabAdapter: TabAdapter
    
    private var isNightMode = false
    private var isAdBlockEnabled = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager.getInstance(this)
        isNightMode = preferenceManager.isNightMode
        
        if (isNightMode) {
            setTheme(R.style.Theme_UCBrowser_Night)
        }
        
        setContentView(R.layout.activity_main)
        
        dbHelper = DatabaseHelper(this)
        
        initViews()
        setupWebView()
        setupGestures()
        setupBottomNavigation()
        loadHomePage()
    }
    
    private fun initViews() {
        webView = findViewById(R.id.webView)
        etUrl = findViewById(R.id.etUrl)
        progressBar = findViewById(R.id.progressBar)
        rvTabs = findViewById(R.id.rvTabs)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        setupToolbarButtons()
        setupTabAdapter()
    }
    
    private fun setupToolbarButtons() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        
        findViewById<ImageButton>(R.id.btnForward).setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
        }
        
        findViewById<ImageButton>(R.id.btnRefresh).setOnClickListener {
            webView.reload()
        }
        
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            loadHomePage()
        }
        
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            showEnhancedMenuDialog()
        }
        
        findViewById<ImageView>(R.id.btnTabs).setOnClickListener {
            toggleTabsVisibility()
        }
        
        etUrl.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                loadUrl(etUrl.text.toString())
                true
            } else {
                false
            }
        }
    }
    
    private fun setupTabAdapter() {
        tabAdapter = TabAdapter(tabs,
            onTabClick = { tab -> switchToTab(tab) },
            onTabClose = { tab -> closeTab(tab) }
        )
        rvTabs.layoutManager = LinearLayoutManager(this)
        rvTabs.adapter = tabAdapter
    }
    
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_DEFAULT
            
            // 无图模式
            blockNetworkImage = preferenceManager.isImageFreeMode
            
            // 用户代理
            userAgentString = "Mozilla/5.0 (Linux; Android 10; UCBrowser) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
            
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                updateCurrentTab(title ?: UrlUtils.getDisplayUrl(webView.url ?: ""))
            }
        }
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                
                // 广告拦截
                if (isAdBlockEnabled && AdBlocker.isAd(url)) {
                    return true // 阻止加载广告
                }
                
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false
                } else {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
            }
            
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                etUrl.setText(url)
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    addToHistory(view?.title ?: UrlUtils.getDomain(it), it)
                    // 注入广告拦截脚本
                    if (isAdBlockEnabled) {
                        view?.evaluateJavascript(AdBlocker.getAdBlockScript(), null)
                    }
                }
            }
        }
        
        // 下载监听
        webView.setDownloadListener { url, _, contentDisposition, _, _ ->
            startDownload(url, contentDisposition)
        }
    }
    
    private fun setupGestures() {
        gestureHandler = GestureHandler(
            context = this,
            onSwipeLeft = {
                // 左滑 - 前进
                if (webView.canGoForward()) webView.goForward()
            },
            onSwipeRight = {
                // 右滑 - 后退
                if (webView.canGoBack()) webView.goBack()
            },
            onSwipeDown = {
                // 下滑 - 刷新
                webView.reload()
                Toast.makeText(this, "页面刷新", Toast.LENGTH_SHORT).show()
            },
            onDoubleTap = {
                // 双击 - 返回顶部
                webView.scrollTo(0, 0)
            }
        )
        
        webView.setOnTouchListener { _, event ->
            gestureHandler.onTouchEvent(event)
            false
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadHomePage()
                    true
                }
                R.id.nav_video -> {
                    startActivity(Intent(this, VideoActivity::class.java))
                    true
                }
                R.id.nav_mine -> {
                    startActivity(Intent(this, MineActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadUrl(url: String) {
        val formattedUrl = UrlUtils.formatUrl(url)
        webView.loadUrl(formattedUrl)
        
        if (currentTabId == null) {
            createNewTab(formattedUrl)
        } else {
            updateCurrentTab(formattedUrl)
        }
        
        hideKeyboard()
    }
    
    private fun loadHomePage() {
        webView.loadDataWithBaseURL(null, getHomePageHtml(), "text/html", "UTF-8", null)
        etUrl.setText("")
    }
    
    private fun getHomePageHtml(): String {
        val bookmarks = dbHelper.getAllBookmarks()
        val bookmarkHtml = bookmarks.joinToString("") { bookmark ->
            """
            <div class="shortcut" onclick="location.href='${bookmark.url}'">
                <div class="shortcut-icon" style="background: ${bookmark.iconColor}">
                    ${bookmark.title.first()}
                </div>
                <div class="shortcut-text">${bookmark.title}</div>
            </div>
            """.trimIndent()
        }
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 20px;
                        font-family: Arial, sans-serif;
                        background-color: #f5f5f5;
                    }
                    .logo {
                        text-align: center;
                        padding: 40px 0;
                        color: #FF6600;
                        font-size: 48px;
                        font-weight: bold;
                    }
                    .section-title {
                        font-size: 16px;
                        font-weight: bold;
                        color: #333;
                        margin: 20px 0 12px 0;
                    }
                    .shortcuts {
                        display: grid;
                        grid-template-columns: repeat(4, 1fr);
                        gap: 16px;
                        max-width: 600px;
                        margin: 0 auto;
                    }
                    .shortcut {
                        background: white;
                        border-radius: 8px;
                        padding: 16px;
                        text-align: center;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .shortcut-icon {
                        width: 40px;
                        height: 40px;
                        margin: 0 auto 8px;
                        background: #FF6600;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: white;
                        font-size: 20px;
                        font-weight: bold;
                    }
                    .shortcut-text {
                        font-size: 12px;
                        color: #333;
                    }
                    .add-bookmark {
                        border: 2px dashed #ccc;
                        background: transparent;
                    }
                    .add-bookmark .shortcut-icon {
                        background: #ccc;
                        font-size: 24px;
                    }
                </style>
            </head>
            <body>
                <div class="logo">UC浏览器</div>
                
                <div class="section-title">我的收藏</div>
                <div class="shortcuts">
                    $bookmarkHtml
                    <div class="shortcut add-bookmark" onclick="alert('长按网页可添加收藏')">
                        <div class="shortcut-icon">+</div>
                        <div class="shortcut-text">添加</div>
                    </div>
                </div>
                
                <div class="section-title">常用网站</div>
                <div class="shortcuts">
                    <div class="shortcut" onclick="location.href='https://www.baidu.com'">
                        <div class="shortcut-icon">百</div>
                        <div class="shortcut-text">百度</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://m.taobao.com'">
                        <div class="shortcut-icon">淘</div>
                        <div class="shortcut-text">淘宝</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://m.jd.com'">
                        <div class="shortcut-icon">京</div>
                        <div class="shortcut-text">京东</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://m.weibo.com'">
                        <div class="shortcut-icon">微</div>
                        <div class="shortcut-text">微博</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://qq.com'">
                        <div class="shortcut-icon">腾</div>
                        <div class="shortcut-text">腾讯</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://zhihu.com'">
                        <div class="shortcut-icon">知</div>
                        <div class="shortcut-text">知乎</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://bilibili.com'">
                        <div class="shortcut-icon">B</div>
                        <div class="shortcut-text">B站</div>
                    </div>
                    <div class="shortcut" onclick="location.href='https://douyin.com'">
                        <div class="shortcut-icon">抖</div>
                        <div class="shortcut-text">抖音</div>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
    
    private fun createNewTab(url: String) {
        val tab = TabItem(
            id = UUID.randomUUID().toString(),
            title = "加载中...",
            url = url
        )
        tabs.add(tab)
        currentTabId = tab.id
        tabAdapter.notifyDataSetChanged()
    }
    
    private fun updateCurrentTab(title: String) {
        currentTabId?.let { tabId ->
            val tab = tabs.find { it.id == tabId }
            tab?.let {
                it.title = title
                tabAdapter.updateTab(it)
            }
        }
    }
    
    private fun switchToTab(tab: TabItem) {
        currentTabId = tab.id
        webView.loadUrl(tab.url)
        rvTabs.visibility = View.GONE
    }
    
    private fun closeTab(tab: TabItem) {
        tabs.remove(tab)
        tabAdapter.notifyDataSetChanged()
        
        if (tabs.isEmpty()) {
            currentTabId = null
            loadHomePage()
        } else if (tab.id == currentTabId) {
            tabs.lastOrNull()?.let { switchToTab(it) }
        }
    }
    
    private fun toggleTabsVisibility() {
        rvTabs.visibility = if (rvTabs.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
    
    private fun addToHistory(title: String, url: String) {
        if (url != "about:home" && !url.startsWith("data:")) {
            dbHelper.addHistory(title, url)
            // 如果已收藏，更新访问次数
            if (dbHelper.isBookmarked(url)) {
                dbHelper.updateBookmarkVisit(url)
            }
        }
    }
    
    private fun startDownload(url: String, contentDisposition: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show()
    }
    
    private fun showEnhancedMenuDialog() {
        val items = arrayOf("历史记录", "下载管理", "设置", "夜间模式", "无图模式", "广告拦截", "新标签页", "添加收藏")
        AlertDialog.Builder(this)
            .setTitle("菜单")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, HistoryActivity::class.java))
                    1 -> startActivity(Intent(this, DownloadsActivity::class.java))
                    2 -> startActivity(Intent(this, SettingsActivity::class.java))
                    3 -> toggleNightMode()
                    4 -> toggleImageFreeMode()
                    5 -> toggleAdBlock()
                    6 -> {
                        createNewTab("https://www.baidu.com")
                        webView.loadUrl("https://www.baidu.com")
                    }
                    7 -> addCurrentPageToBookmarks()
                }
            }
            .show()
    }
    
    private fun addCurrentPageToBookmarks() {
        val url = webView.url ?: return
        val title = webView.title ?: UrlUtils.getDomain(url)
        
        if (dbHelper.isBookmarked(url)) {
            Toast.makeText(this, "该页面已在收藏中", Toast.LENGTH_SHORT).show()
            return
        }
        
        val colors = listOf("#FF6600", "#FF9800", "#4CAF50", "#2196F3", "#9C27B0", "#E91E63")
        val randomColor = colors.random()
        
        dbHelper.addBookmark(title, url, randomColor)
        Toast.makeText(this, "已添加到收藏", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleNightMode() {
        isNightMode = !isNightMode
        preferenceManager.isNightMode = isNightMode
        recreate()
    }
    
    private fun toggleImageFreeMode() {
        preferenceManager.isImageFreeMode = !preferenceManager.isImageFreeMode
        webView.settings.blockNetworkImage = preferenceManager.isImageFreeMode
        webView.reload()
        Toast.makeText(this, if (preferenceManager.isImageFreeMode) "已开启无图模式" else "已关闭无图模式", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleAdBlock() {
        isAdBlockEnabled = !isAdBlockEnabled
        Toast.makeText(this, if (isAdBlockEnabled) "已开启广告拦截" else "已关闭广告拦截", Toast.LENGTH_SHORT).show()
        webView.reload()
    }
    
    private fun hideKeyboard() {
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(etUrl.windowToken, 0)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureHandler.onTouchEvent(event) || super.onTouchEvent(event)
    }
    
    override fun onBackPressed() {
        when {
            rvTabs.visibility == View.VISIBLE -> rvTabs.visibility = View.GONE
            webView.canGoBack() -> webView.goBack()
            else -> super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
