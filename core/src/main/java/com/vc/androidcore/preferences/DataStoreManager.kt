package com.vc.androidcore.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "core_preferences")

/**
 * Reusable, type-safe manager for Jetpack [DataStore] Preferences.
 * Supports String, Int, Long, Boolean, Float with coroutines and reactive Flows.
 */
class DataStoreManager(private val context: Context) {

    private val dataStore: DataStore<Preferences> = context.applicationContext.dataStore

    private val safePreferencesFlow: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    // --- String ---
    suspend fun putString(key: String, value: String?) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            if (value != null) {
                preferences[prefKey] = value
            } else {
                preferences.remove(prefKey)
            }
        }
    }

    suspend fun getString(key: String, defaultValue: String? = null): String? {
        val prefKey = stringPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }.first()
    }

    fun getStringFlow(key: String, defaultValue: String? = null): Flow<String?> {
        val prefKey = stringPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
    }

    // --- Int ---
    suspend fun putInt(key: String, value: Int) {
        val prefKey = intPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getInt(key: String, defaultValue: Int = 0): Int {
        val prefKey = intPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }.first()
    }

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        val prefKey = intPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
    }

    // --- Long ---
    suspend fun putLong(key: String, value: Long) {
        val prefKey = longPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getLong(key: String, defaultValue: Long = 0L): Long {
        val prefKey = longPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }.first()
    }

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        val prefKey = longPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
    }

    // --- Boolean ---
    suspend fun putBoolean(key: String, value: Boolean) {
        val prefKey = booleanPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val prefKey = booleanPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }.first()
    }

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
    }

    // --- Float ---
    suspend fun putFloat(key: String, value: Float) {
        val prefKey = floatPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getFloat(key: String, defaultValue: Float = 0f): Float {
        val prefKey = floatPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }.first()
    }

    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> {
        val prefKey = floatPreferencesKey(key)
        return safePreferencesFlow.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
    }

    // --- Clear / Remove ---
    suspend fun remove(key: String) {
        val stringKey = stringPreferencesKey(key)
        val intKey = intPreferencesKey(key)
        val longKey = longPreferencesKey(key)
        val boolKey = booleanPreferencesKey(key)
        val floatKey = floatPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences.remove(stringKey)
            preferences.remove(intKey)
            preferences.remove(longKey)
            preferences.remove(boolKey)
            preferences.remove(floatKey)
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
