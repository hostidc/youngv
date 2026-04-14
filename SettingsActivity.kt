package com.example.ucbrowser

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ucbrowser.data.DatabaseHelper
import com.example.ucbrowser.utils.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var dbHelper: DatabaseHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        preferenceManager = PreferenceManager.getInstance(this)
        dbHelper = DatabaseHelper(this)
        
        initViews()
    }
    
    private fun initViews() {
        val switchNightMode: Switch = findViewById(R.id.switchNightMode)
        val switchImageFreeMode: Switch = findViewById(R.id.switchImageFreeMode)
        val tvClearHistory: TextView = findViewById(R.id.tvClearHistory)
        val tvClearCache: TextView = findViewById(R.id.tvClearCache)
        
        // 设置开关状态
        switchNightMode.isChecked = preferenceManager.isNightMode
        switchImageFreeMode.isChecked = preferenceManager.isImageFreeMode
        
        // 夜间模式切换
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.isNightMode = isChecked
            Toast.makeText(this, if (isChecked) "已开启夜间模式" else "已关闭夜间模式", Toast.LENGTH_SHORT).show()
        }
        
        // 无图模式切换
        switchImageFreeMode.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.isImageFreeMode = isChecked
            Toast.makeText(this, if (isChecked) "已开启无图模式" else "已关闭无图模式", Toast.LENGTH_SHORT).show()
        }
        
        // 清除历史记录
        tvClearHistory.setOnClickListener {
            dbHelper.clearAllHistory()
            Toast.makeText(this, "历史记录已清除", Toast.LENGTH_SHORT).show()
        }
        
        // 清除缓存
        tvClearCache.setOnClickListener {
            clearAppCache()
            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun clearAppCache() {
        // 清除WebView缓存
        val webView = android.webkit.WebView(this)
        webView.clearCache(true)
        webView.destroy()
        
        // 清除应用缓存
        cacheDir.deleteRecursively()
    }
}
