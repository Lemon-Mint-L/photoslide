package com.photoslide.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoslide.data.model.StorageAnalysis
import com.photoslide.data.repository.PhotoRepository
import com.photoslide.data.repository.CleaningSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalysisUiState(
    val isLoading: Boolean = false,
    val storageAnalysis: StorageAnalysis? = null,
    val totalPhotosDeleted: Int = 0,
    val totalSpaceFreed: Long = 0,
    val completedSessions: Int = 0,
    val error: String? = null
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val cleaningSessionRepository: CleaningSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        loadAnalysis()
    }

    private fun loadAnalysis() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val totalPhotos = photoRepository.getPhotoCount()
                val totalSize = photoRepository.getTotalSize()
                val duplicateSize = photoRepository.getDuplicatePhotosSize()
                val blurrySize = photoRepository.getBlurryPhotosSize()
                val screenshotSize = photoRepository.getScreenshotPhotosSize()
                
                val analysis = StorageAnalysis(
                    totalPhotos = totalPhotos,
                    totalVideos = 0, // 简化版不包含视频
                    totalSize = totalSize,
                    photoSize = totalSize,
                    videoSize = 0,
                    screenshotSize = screenshotSize,
                    duplicateSize = duplicateSize,
                    blurrySize = blurrySize,
                    potentialSavings = duplicateSize + blurrySize
                )
                
                val totalPhotosDeleted = cleaningSessionRepository.getTotalPhotosDeleted()
                val totalSpaceFreed = cleaningSessionRepository.getTotalSpaceFreed()
                val completedSessions = cleaningSessionRepository.getCompletedSessionCount()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    storageAnalysis = analysis,
                    totalPhotosDeleted = totalPhotosDeleted,
                    totalSpaceFreed = totalSpaceFreed,
                    completedSessions = completedSessions
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refreshAnalysis() {
        loadAnalysis()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}