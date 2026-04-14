package com.example.ucbrowser.model

data class DownloadItem(
    val id: Int = 0,
    var fileName: String,
    var url: String,
    var fileSize: Long = 0,
    var downloadedSize: Long = 0,
    var status: DownloadStatus = DownloadStatus.PENDING,
    val createTime: Long = System.currentTimeMillis(),
    var filePath: String? = null
) {
    enum class DownloadStatus {
        PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED
    }
    
    fun getProgress(): Int {
        return if (fileSize > 0) {
            ((downloadedSize * 100) / fileSize).toInt()
        } else 0
    }
    
    fun getFileSizeString(): String {
        return formatFileSize(fileSize)
    }
    
    private fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
}
