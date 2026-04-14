package com.example.ucbrowser.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager private constructor(private val context: Context) {
    
    companion object {
        private const val PREF_NAME = "ucbrowser_prefs"
        private const val KEY_NIGHT_MODE = "night_mode"
        private const val KEY_IMAGE_FREE_MODE = "image_free_mode"
        private const val KEY_HOME_PAGE = "home_page"
        
        @Volatile
        private var instance: PreferenceManager? = null
        
        fun getInstance(context: Context): PreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: PreferenceManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    // 夜间模式
    var isNightMode: Boolean
        get() = prefs.getBoolean(KEY_NIGHT_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_NIGHT_MODE, value).apply()
    
    // 无图模式
    var isImageFreeMode: Boolean
        get() = prefs.getBoolean(KEY_IMAGE_FREE_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_IMAGE_FREE_MODE, value).apply()
    
    // 主页URL
    var homePageUrl: String
        get() = prefs.getString(KEY_HOME_PAGE, "about:home") ?: "about:home"
        set(value) = prefs.edit().putString(KEY_HOME_PAGE, value).apply()
}
