package com.example.ucbrowser.model

data class TabItem(
    val id: String,
    var title: String,
    var url: String,
    var favicon: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
