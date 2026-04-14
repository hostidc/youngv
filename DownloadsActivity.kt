package com.example.ucbrowser

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ucbrowser.adapter.DownloadAdapter
import com.example.ucbrowser.data.DatabaseHelper
import com.example.ucbrowser.model.DownloadItem

class DownloadsActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvDownloads: RecyclerView
    private lateinit var tvEmpty: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloads)
        
        dbHelper = DatabaseHelper(this)
        
        initViews()
        loadDownloads()
    }
    
    private fun initViews() {
        rvDownloads = findViewById(R.id.rvDownloads)
        tvEmpty = findViewById(R.id.tvEmpty)
        
        rvDownloads.layoutManager = LinearLayoutManager(this)
    }
    
    private fun loadDownloads() {
        val downloadRecords = dbHelper.getAllDownloads()
        
        if (downloadRecords.isEmpty()) {
            rvDownloads.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rvDownloads.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            
            // 转换为DownloadItem列表
            val downloadList = downloadRecords.map { record ->
                DownloadItem(
                    id = record["id"] as Int,
                    fileName = record["fileName"] as String,
                    url = record["url"] as String,
                    fileSize = record["fileSize"] as Long,
                    downloadedSize = record["downloadedSize"] as Long,
                    status = when (record["status"] as Int) {
                        0 -> DownloadItem.DownloadStatus.PENDING
                        1 -> DownloadItem.DownloadStatus.DOWNLOADING
                        2 -> DownloadItem.DownloadStatus.COMPLETED
                        3 -> DownloadItem.DownloadStatus.FAILED
                        else -> DownloadItem.DownloadStatus.PENDING
                    },
                    filePath = record["filePath"] as String?
                )
            }.toList()
            
            val adapter = DownloadAdapter(downloadList) { download ->
                // 点击下载项，打开文件或重新下载
            }
            rvDownloads.adapter = adapter
        }
    }
}
