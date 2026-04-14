    override fun onCreate(db: SQLiteDatabase?) {
        val createHistoryTable = """
            CREATE TABLE $TABLE_HISTORY (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_URL TEXT,
                $COLUMN_FAVICON TEXT,
                $COLUMN_VISIT_TIME INTEGER,
                $COLUMN_VISIT_COUNT INTEGER DEFAULT 1
            )
        """.trimIndent()
        
        val createDownloadsTable = """
            CREATE TABLE $TABLE_DOWNLOADS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FILE_NAME TEXT,
                $COLUMN_URL TEXT,
                $COLUMN_FILE_SIZE INTEGER,
                $COLUMN_DOWNLOADED_SIZE INTEGER,
                $COLUMN_STATUS INTEGER,
                $COLUMN_CREATE_TIME INTEGER,
                $COLUMN_FILE_PATH TEXT
            )
        """.trimIndent()
        
        val createBookmarksTable = """
            CREATE TABLE $TABLE_BOOKMARKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_URL TEXT,
                $COLUMN_FAVICON TEXT,
                $COLUMN_ICON_COLOR TEXT,
                $COLUMN_CREATE_TIME INTEGER,
                $COLUMN_VISIT_COUNT INTEGER DEFAULT 0,
                $COLUMN_LAST_VISIT_TIME INTEGER
            )
        """.trimIndent()
        
        db?.execSQL(createHistoryTable)
        db?.execSQL(createDownloadsTable)
        db?.execSQL(createBookmarksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DOWNLOADS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKMARKS")
        onCreate(db)
    }
    
    // ==================== 历史记录操作 ====================
    
    // 添加历史记录
    fun addHistory(title: String, url: String, favicon: String? = null): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_URL, url)
            put(COLUMN_FAVICON, favicon)
            put(COLUMN_VISIT_TIME, System.currentTimeMillis())
            put(COLUMN_VISIT_COUNT, 1)
        }
        return db.insert(TABLE_HISTORY, null, values)
    }
    
    // 获取所有历史记录
    fun getAllHistory(): List<HistoryItem> {
        val historyList = mutableListOf<HistoryItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_HISTORY,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_VISIT_TIME DESC"
        )
        
        with(cursor) {
            while (moveToNext()) {
                val history = HistoryItem(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
                    url = getString(getColumnIndexOrThrow(COLUMN_URL)),
                    favicon = getString(getColumnIndexOrThrow(COLUMN_FAVICON)),
                    visitTime = getLong(getColumnIndexOrThrow(COLUMN_VISIT_TIME)),
                    visitCount = getInt(getColumnIndexOrThrow(COLUMN_VISIT_COUNT))
                )
                historyList.add(history)
            }
        }
        cursor.close()
        return historyList
    }
    
    // 清除所有历史记录
    fun clearAllHistory() {
        val db = writableDatabase
        db.delete(TABLE_HISTORY, null, null)
        db.close()
    }
    
    // ==================== 下载记录操作 ====================
    
    // 添加下载记录
    fun addDownload(fileName: String, url: String, fileSize: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FILE_NAME, fileName)
            put(COLUMN_URL, url)
            put(COLUMN_FILE_SIZE, fileSize)
            put(COLUMN_DOWNLOADED_SIZE, 0)
            put(COLUMN_STATUS, 0) // PENDING
            put(COLUMN_CREATE_TIME, System.currentTimeMillis())
        }
        return db.insert(TABLE_DOWNLOADS, null, values)
    }
    
    // 更新下载状态
    fun updateDownloadStatus(id: Int, status: Int, downloadedSize: Long) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, status)
            put(COLUMN_DOWNLOADED_SIZE, downloadedSize)
        }
        db.update(TABLE_DOWNLOADS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
    
    // 获取所有下载记录
    fun getAllDownloads(): List<Map<String, Any>> {
        val downloadList = mutableListOf<Map<String, Any>>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_DOWNLOADS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATE_TIME DESC"
        )
        
        with(cursor) {
            while (moveToNext()) {
                val download = mapOf(
                    "id" to getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    "fileName" to getString(getColumnIndexOrThrow(COLUMN_FILE_NAME)),
                    "url" to getString(getColumnIndexOrThrow(COLUMN_URL)),
                    "fileSize" to getLong(getColumnIndexOrThrow(COLUMN_FILE_SIZE)),
                    "downloadedSize" to getLong(getColumnIndexOrThrow(COLUMN_DOWNLOADED_SIZE)),
                    "status" to getInt(getColumnIndexOrThrow(COLUMN_STATUS)),
                    "createTime" to getLong(getColumnIndexOrThrow(COLUMN_CREATE_TIME)),
                    "filePath" to getString(getColumnIndexOrThrow(COLUMN_FILE_PATH))
                )
                downloadList.add(download)
            }
        }
        cursor.close()
        return downloadList
    }
    
    // ==================== 收藏夹操作 ====================
    
    companion object {
        private const val DATABASE_NAME = "ucbrowser.db"
        private const val DATABASE_VERSION = 2  // 升级版本号
        
        const val TABLE_HISTORY = "history"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_URL = "url"
        const val COLUMN_FAVICON = "favicon"
        const val COLUMN_VISIT_TIME = "visit_time"
        const val COLUMN_VISIT_COUNT = "visit_count"
        
        const val TABLE_DOWNLOADS = "downloads"
        const val COLUMN_FILE_NAME = "file_name"
        const val COLUMN_FILE_SIZE = "file_size"
        const val COLUMN_DOWNLOADED_SIZE = "downloaded_size"
        const val COLUMN_STATUS = "status"
        const val COLUMN_CREATE_TIME = "create_time"
        const val COLUMN_FILE_PATH = "file_path"
        
        const val TABLE_BOOKMARKS = "bookmarks"
        const val COLUMN_ICON_COLOR = "icon_color"
        const val COLUMN_LAST_VISIT_TIME = "last_visit_time"
    }
    
    // 添加书签
    fun addBookmark(title: String, url: String, iconColor: String = "#FF6600"): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_URL, url)
            put(COLUMN_ICON_COLOR, iconColor)
            put(COLUMN_CREATE_TIME, System.currentTimeMillis())
            put(COLUMN_VISIT_COUNT, 0)
        }
        return db.insert(TABLE_BOOKMARKS, null, values)
    }
    
    // 获取所有书签
    fun getAllBookmarks(): List<BookmarkItem> {
        val bookmarkList = mutableListOf<BookmarkItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BOOKMARKS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATE_TIME ASC"
        )
        
        with(cursor) {
            while (moveToNext()) {
                val bookmark = BookmarkItem(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
                    url = getString(getColumnIndexOrThrow(COLUMN_URL)),
                    favicon = getString(getColumnIndexOrThrow(COLUMN_FAVICON)),
                    iconColor = getString(getColumnIndexOrThrow(COLUMN_ICON_COLOR)) ?: "#FF6600",
                    createTime = getLong(getColumnIndexOrThrow(COLUMN_CREATE_TIME)),
                    visitCount = getInt(getColumnIndexOrThrow(COLUMN_VISIT_COUNT)),
                    lastVisitTime = getLong(getColumnIndexOrThrow(COLUMN_LAST_VISIT_TIME))
                )
                bookmarkList.add(bookmark)
            }
        }
        cursor.close()
        return bookmarkList
    }
    
    // 删除书签
    fun deleteBookmark(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_BOOKMARKS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
    
    // 检查URL是否已收藏
    fun isBookmarked(url: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BOOKMARKS,
            arrayOf(COLUMN_ID),
            "$COLUMN_URL = ?",
            arrayOf(url),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    
    // 更新书签访问时间
    fun updateBookmarkVisit(url: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_VISIT_COUNT, rawQuery("SELECT visit_count + 1 FROM $TABLE_BOOKMARKS WHERE $COLUMN_URL = ?", arrayOf(url)).use { 
                if (it.moveToFirst()) it.getInt(0) else 0 
            })
            put(COLUMN_LAST_VISIT_TIME, System.currentTimeMillis())
        }
        db.update(TABLE_BOOKMARKS, values, "$COLUMN_URL = ?", arrayOf(url))
    }
}
