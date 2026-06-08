package com.photoslide.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoslide.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: Boolean = false,
    val dynamicColor: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val cleaningReminder: Boolean = true,
    val reminderIntervalDays: Int = 7,
    val autoDeleteEnabled: Boolean = false,
    val autoDeleteDays: Int = 30,
    val confirmBeforeDelete: Boolean = true,
    val keepFavorites: Boolean = true,
    val aiScanEnabled: Boolean = true,
    val blurThreshold: Int = 50,
    val duplicateThreshold: Int = 10,
    val analyticsEnabled: Boolean = false,
    val crashReporting: Boolean = true,
    val totalPhotosDeleted: Int = 0,
    val totalSpaceFreed: Long = 0,
    val cleaningStreak: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // 加载所有设置
                settingsDataStore.darkMode.collect { darkMode ->
                    _uiState.value = _uiState.value.copy(darkMode = darkMode)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }

            try {
                settingsDataStore.dynamicColor.collect { dynamicColor ->
                    _uiState.value = _uiState.value.copy(dynamicColor = dynamicColor)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.notificationsEnabled.collect { enabled ->
                    _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.cleaningReminder.collect { enabled ->
                    _uiState.value = _uiState.value.copy(cleaningReminder = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.reminderIntervalDays.collect { days ->
                    _uiState.value = _uiState.value.copy(reminderIntervalDays = days)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.autoDeleteEnabled.collect { enabled ->
                    _uiState.value = _uiState.value.copy(autoDeleteEnabled = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.autoDeleteDays.collect { days ->
                    _uiState.value = _uiState.value.copy(autoDeleteDays = days)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.confirmBeforeDelete.collect { enabled ->
                    _uiState.value = _uiState.value.copy(confirmBeforeDelete = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.keepFavorites.collect { enabled ->
                    _uiState.value = _uiState.value.copy(keepFavorites = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.aiScanEnabled.collect { enabled ->
                    _uiState.value = _uiState.value.copy(aiScanEnabled = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.blurThreshold.collect { threshold ->
                    _uiState.value = _uiState.value.copy(blurThreshold = threshold)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.duplicateThreshold.collect { threshold ->
                    _uiState.value = _uiState.value.copy(duplicateThreshold = threshold)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.analyticsEnabled.collect { enabled ->
                    _uiState.value = _uiState.value.copy(analyticsEnabled = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.crashReporting.collect { enabled ->
                    _uiState.value = _uiState.value.copy(crashReporting = enabled)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.totalPhotosDeleted.collect { count ->
                    _uiState.value = _uiState.value.copy(totalPhotosDeleted = count)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.totalSpaceFreed.collect { bytes ->
                    _uiState.value = _uiState.value.copy(totalSpaceFreed = bytes)
                }
            } catch (e: Exception) {
                // 忽略
            }

            try {
                settingsDataStore.cleaningStreak.collect { streak ->
                    _uiState.value = _uiState.value.copy(cleaningStreak = streak)
                }
            } catch (e: Exception) {
                // 忽略
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
            _uiState.value = _uiState.value.copy(darkMode = enabled)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDynamicColor(enabled)
            _uiState.value = _uiState.value.copy(dynamicColor = enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationsEnabled(enabled)
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        }
    }

    fun setCleaningReminder(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setCleaningReminder(enabled)
            _uiState.value = _uiState.value.copy(cleaningReminder = enabled)
        }
    }

    fun setReminderIntervalDays(days: Int) {
        viewModelScope.launch {
            settingsDataStore.setReminderIntervalDays(days)
            _uiState.value = _uiState.value.copy(reminderIntervalDays = days)
        }
    }

    fun setAutoDeleteEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAutoDeleteEnabled(enabled)
            _uiState.value = _uiState.value.copy(autoDeleteEnabled = enabled)
        }
    }

    fun setAutoDeleteDays(days: Int) {
        viewModelScope.launch {
            settingsDataStore.setAutoDeleteDays(days)
            _uiState.value = _uiState.value.copy(autoDeleteDays = days)
        }
    }

    fun setConfirmBeforeDelete(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setConfirmBeforeDelete(enabled)
            _uiState.value = _uiState.value.copy(confirmBeforeDelete = enabled)
        }
    }

    fun setKeepFavorites(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setKeepFavorites(enabled)
            _uiState.value = _uiState.value.copy(keepFavorites = enabled)
        }
    }

    fun setAiScanEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAiScanEnabled(enabled)
            _uiState.value = _uiState.value.copy(aiScanEnabled = enabled)
        }
    }

    fun setBlurThreshold(threshold: Int) {
        viewModelScope.launch {
            settingsDataStore.setBlurThreshold(threshold)
            _uiState.value = _uiState.value.copy(blurThreshold = threshold)
        }
    }

    fun setDuplicateThreshold(threshold: Int) {
        viewModelScope.launch {
            settingsDataStore.setDuplicateThreshold(threshold)
            _uiState.value = _uiState.value.copy(duplicateThreshold = threshold)
        }
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAnalyticsEnabled(enabled)
            _uiState.value = _uiState.value.copy(analyticsEnabled = enabled)
        }
    }

    fun setCrashReporting(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setCrashReporting(enabled)
            _uiState.value = _uiState.value.copy(crashReporting = enabled)
        }
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            settingsDataStore.resetAllSettings()
            _uiState.value = SettingsUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}