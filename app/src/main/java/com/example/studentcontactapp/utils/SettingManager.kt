package com.example.studentcontactapp.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE)

    companion object {
        private const val SETTINGS_PREF = "AppSettings"
        private const val KEY_DARK_MODE = "darkMode"
        private const val KEY_FONT_SIZE = "fontSize"
        private const val KEY_NOTIFICATION = "notificationEnabled"
    }

    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var fontSize: Int
        get() = prefs.getInt(KEY_FONT_SIZE, 16)
        set(value) = prefs.edit().putInt(KEY_FONT_SIZE, value).apply()

    var notificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION, value).apply()
}