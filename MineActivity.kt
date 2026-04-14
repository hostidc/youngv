package com.example.ucbrowser

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ucbrowser.utils.PreferenceManager

/**
 * 我的页面活动
 */
class MineActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine)
        
        preferenceManager = PreferenceManager.getInstance(this)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "我的"
        }
        
        initViews()
    }
    
    private fun initViews() {
        // 我的收藏
        findViewById<LinearLayout>(R.id.menuBookmarks).setOnClickListener {
            Toast.makeText(this, "收藏夹功能开发中", Toast.LENGTH_SHORT).show()
        }
        
        // 历史记录
        findViewById<LinearLayout>(R.id.menuHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        
        // 下载管理
        findViewById<LinearLayout>(R.id.menuDownloads).setOnClickListener {
            startActivity(Intent(this, DownloadsActivity::class.java))
        }
        
        // 设置
        findViewById<LinearLayout>(R.id.menuSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // 夜间模式
        val switchNightMode = findViewById<Switch>(R.id.switchNightModeMine)
        switchNightMode.isChecked = preferenceManager.isNightMode
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.isNightMode = isChecked
            Toast.makeText(this, if (isChecked) "已开启夜间模式" else "已关闭夜间模式", Toast.LENGTH_SHORT).show()
        }
        
        // 无图模式
        val switchImageFree = findViewById<Switch>(R.id.switchImageFreeMine)
        switchImageFree.isChecked = preferenceManager.isImageFreeMode
        switchImageFree.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.isImageFreeMode = isChecked
            Toast.makeText(this, if (isChecked) "已开启无图模式" else "已关闭无图模式", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
