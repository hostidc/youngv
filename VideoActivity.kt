package com.example.ucbrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 视频页面活动
 */
class VideoActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "热门视频"
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
