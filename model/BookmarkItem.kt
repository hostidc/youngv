package com.example.ucbrowser.model

data class BookmarkItem(
    val id: Int = 0,
    var title: String,
    var url: String,
    var favicon: String? = null,
    var iconColor: String = "#FF6600",
    val createTime: Long = System.currentTimeMillis(),
    var visitCount: Int = 0,
    var lastVisitTime: Long = System.currentTimeMillis()
)
