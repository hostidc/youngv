package com.example.ucbrowser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ucbrowser.adapter.HistoryAdapter
import com.example.ucbrowser.data.DatabaseHelper
import com.example.ucbrowser.model.HistoryItem

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmpty: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        dbHelper = DatabaseHelper(this)
        
        initViews()
        loadHistory()
    }
    
    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        tvEmpty = findViewById(R.id.tvEmpty)
        
        rvHistory.layoutManager = LinearLayoutManager(this)
    }
    
    private fun loadHistory() {
        val historyList = dbHelper.getAllHistory()
        
        if (historyList.isEmpty()) {
            rvHistory.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rvHistory.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            
            val adapter = HistoryAdapter(historyList) { history ->
                // 点击历史记录，打开网页
                val intent = Intent()
                intent.putExtra("url", history.url)
                setResult(RESULT_OK, intent)
                finish()
            }
            rvHistory.adapter = adapter
        }
    }
}
