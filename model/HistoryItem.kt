package com.example.ucbrowser.model

data class HistoryItem(
    val id: Int = 0,
    var title: String,
    var url: String,
    var favicon: String? = null,
    val visitTime: Long = System.currentTimeMillis(),
    var visitCount: Int = 1
) {
    fun getFormattedTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - visitTime
        
        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            else -> {
                val date = java.util.Date(visitTime)
                android.text.format.DateFormat.format("yyyy-MM-dd", date).toString()
            }
        }
    }
}
