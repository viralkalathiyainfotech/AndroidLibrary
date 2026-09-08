package com.vc.androidcore.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Thread-safe wrapper for Android [SharedPreferences].
 */
class PreferenceManager(
    context: Context,
    prefName: String = "${context.packageName}_preferences"
) {

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun putString(key: String, value: String?) {
        sharedPreferences.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPreferences.edit { remove(key) }
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }

    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}
