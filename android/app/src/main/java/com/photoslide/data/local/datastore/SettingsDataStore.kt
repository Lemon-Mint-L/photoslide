package com.photoslide.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // 主题设置
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")

    // 通知设置
    private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    private val CLEANING_REMINDER = booleanPreferencesKey("cleaning_reminder")
    private val REMINDER_INTERVAL_DAYS = intPreferencesKey("reminder_interval_days")

    // 清理设置
    private val AUTO_DELETE_ENABLED = booleanPreferencesKey("auto_delete_enabled")
    private val AUTO_DELETE_DAYS = intPreferencesKey("auto_delete_days")
    private val CONFIRM_BEFORE_DELETE = booleanPreferencesKey("confirm_before_delete")
    private val KEEP_FAVORITES = booleanPreferencesKey("keep_favorites")

    // 存储设置
    private val STORAGE_PATH = stringPreferencesKey("storage_path")
    private val MAX_CACHE_SIZE = intPreferencesKey("max_cache_size")

    // AI设置
    private val AI_SCAN_ENABLED = booleanPreferencesKey("ai_scan_enabled")
    private val BLUR_THRESHOLD = intPreferencesKey("blur_threshold")
    private val DUPLICATE_THRESHOLD = intPreferencesKey("duplicate_threshold")

    // 统计设置
    private val TOTAL_PHOTOS_DELETED = intPreferencesKey("total_photos_deleted")
    private val TOTAL_SPACE_FREED = longPreferencesKey("total_space_freed")
    private val CLEANING_STREAK = intPreferencesKey("cleaning_streak")
    private val LAST_CLEANING_DATE = longPreferencesKey("last_cleaning_date")

    // 隐私设置
    private val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
    private val CRASH_REPORTING = booleanPreferencesKey("crash_reporting")

    // 主题设置
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }

    val dynamicColor: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR] ?: true
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = enabled
        }
    }

    // 通知设置
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val cleaningReminder: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CLEANING_REMINDER] ?: true
    }

    val reminderIntervalDays: Flow<Int> = dataStore.data.map { preferences ->
        preferences[REMINDER_INTERVAL_DAYS] ?: 7
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setCleaningReminder(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CLEANING_REMINDER] = enabled
        }
    }

    suspend fun setReminderIntervalDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[REMINDER_INTERVAL_DAYS] = days
        }
    }

    // 清理设置
    val autoDeleteEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_DELETE_ENABLED] ?: false
    }

    val autoDeleteDays: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AUTO_DELETE_DAYS] ?: 30
    }

    val confirmBeforeDelete: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CONFIRM_BEFORE_DELETE] ?: true
    }

    val keepFavorites: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEEP_FAVORITES] ?: true
    }

    suspend fun setAutoDeleteEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_DELETE_ENABLED] = enabled
        }
    }

    suspend fun setAutoDeleteDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[AUTO_DELETE_DAYS] = days
        }
    }

    suspend fun setConfirmBeforeDelete(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CONFIRM_BEFORE_DELETE] = enabled
        }
    }

    suspend fun setKeepFavorites(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEEP_FAVORITES] = enabled
        }
    }

    // 存储设置
    val storagePath: Flow<String> = dataStore.data.map { preferences ->
        preferences[STORAGE_PATH] ?: "/storage/emulated/0"
    }

    val maxCacheSize: Flow<Int> = dataStore.data.map { preferences ->
        preferences[MAX_CACHE_SIZE] ?: 100 // MB
    }

    suspend fun setStoragePath(path: String) {
        dataStore.edit { preferences ->
            preferences[STORAGE_PATH] = path
        }
    }

    suspend fun setMaxCacheSize(sizeMB: Int) {
        dataStore.edit { preferences ->
            preferences[MAX_CACHE_SIZE] = sizeMB
        }
    }

    // AI设置
    val aiScanEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AI_SCAN_ENABLED] ?: true
    }

    val blurThreshold: Flow<Int> = dataStore.data.map { preferences ->
        preferences[BLUR_THRESHOLD] ?: 50
    }

    val duplicateThreshold: Flow<Int> = dataStore.data.map { preferences ->
        preferences[DUPLICATE_THRESHOLD] ?: 10
    }

    suspend fun setAiScanEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AI_SCAN_ENABLED] = enabled
        }
    }

    suspend fun setBlurThreshold(threshold: Int) {
        dataStore.edit { preferences ->
            preferences[BLUR_THRESHOLD] = threshold
        }
    }

    suspend fun setDuplicateThreshold(threshold: Int) {
        dataStore.edit { preferences ->
            preferences[DUPLICATE_THRESHOLD] = threshold
        }
    }

    // 统计设置
    val totalPhotosDeleted: Flow<Int> = dataStore.data.map { preferences ->
        preferences[TOTAL_PHOTOS_DELETED] ?: 0
    }

    val totalSpaceFreed: Flow<Long> = dataStore.data.map { preferences ->
        preferences[TOTAL_SPACE_FREED] ?: 0L
    }

    val cleaningStreak: Flow<Int> = dataStore.data.map { preferences ->
        preferences[CLEANING_STREAK] ?: 0
    }

    val lastCleaningDate: Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_CLEANING_DATE] ?: 0L
    }

    suspend fun incrementPhotosDeleted(count: Int) {
        dataStore.edit { preferences ->
            val current = preferences[TOTAL_PHOTOS_DELETED] ?: 0
            preferences[TOTAL_PHOTOS_DELETED] = current + count
        }
    }

    suspend fun addSpaceFreed(bytes: Long) {
        dataStore.edit { preferences ->
            val current = preferences[TOTAL_SPACE_FREED] ?: 0L
            preferences[TOTAL_SPACE_FREED] = current + bytes
        }
    }

    suspend fun updateCleaningStreak() {
        dataStore.edit { preferences ->
            val lastDate = preferences[LAST_CLEANING_DATE] ?: 0L
            val now = System.currentTimeMillis()
            val daysSinceLastCleaning = (now - lastDate) / (1000 * 60 * 60 * 24)

            val currentStreak = if (daysSinceLastCleaning <= 1) {
                (preferences[CLEANING_STREAK] ?: 0) + 1
            } else {
                1
            }

            preferences[CLEANING_STREAK] = currentStreak
            preferences[LAST_CLEANING_DATE] = now
        }
    }

    // 隐私设置
    val analyticsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ANALYTICS_ENABLED] ?: false
    }

    val crashReporting: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CRASH_REPORTING] ?: true
    }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ANALYTICS_ENABLED] = enabled
        }
    }

    suspend fun setCrashReporting(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CRASH_REPORTING] = enabled
        }
    }

    // 重置所有设置
    suspend fun resetAllSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}