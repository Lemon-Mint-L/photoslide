package com.photoslide.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoslide.data.model.Photo
import com.photoslide.data.repository.PhotoRepository
import com.photoslide.data.repository.CleaningSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val photos: List<Photo> = emptyList(),
    val currentPhotoIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionId: Long? = null,
    val photosViewed: Int = 0,
    val photosKept: Int = 0,
    val photosDeleted: Int = 0,
    val photosFavorited: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val cleaningSessionRepository: CleaningSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val photos = photoRepository.getPhotosWithPagination(100, 0)
                _uiState.value = _uiState.value.copy(
                    photos = photos,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun startCleaningSession() {
        viewModelScope.launch {
            val sessionId = cleaningSessionRepository.startNewSession()
            _uiState.value = _uiState.value.copy(sessionId = sessionId)
        }
    }

    fun keepCurrentPhoto() {
        val state = _uiState.value
        val currentPhoto = state.photos.getOrNull(state.currentPhotoIndex) ?: return
        
        viewModelScope.launch {
            // 更新统计
            _uiState.value = state.copy(
                photosViewed = state.photosViewed + 1,
                photosKept = state.photosKept + 1,
                currentPhotoIndex = state.currentPhotoIndex + 1
            )
            
            // 更新会话
            state.sessionId?.let { sessionId ->
                cleaningSessionRepository.incrementPhotosViewed(sessionId)
                cleaningSessionRepository.incrementPhotosKept(sessionId)
            }
        }
    }

    fun deleteCurrentPhoto() {
        val state = _uiState.value
        val currentPhoto = state.photos.getOrNull(state.currentPhotoIndex) ?: return
        
        viewModelScope.launch {
            // 标记为删除
            photoRepository.markAsDeleted(currentPhoto.id)
            
            // 更新统计
            _uiState.value = state.copy(
                photosViewed = state.photosViewed + 1,
                photosDeleted = state.photosDeleted + 1,
                currentPhotoIndex = state.currentPhotoIndex + 1
            )
            
            // 更新会话
            state.sessionId?.let { sessionId ->
                cleaningSessionRepository.incrementPhotosViewed(sessionId)
                cleaningSessionRepository.incrementPhotosDeleted(sessionId)
            }
        }
    }

    fun favoriteCurrentPhoto() {
        val state = _uiState.value
        val currentPhoto = state.photos.getOrNull(state.currentPhotoIndex) ?: return
        
        viewModelScope.launch {
            // 更新收藏状态
            photoRepository.updateFavoriteStatus(currentPhoto.id, true)
            
            // 更新统计
            _uiState.value = state.copy(
                photosViewed = state.photosViewed + 1,
                photosFavorited = state.photosFavorited + 1,
                currentPhotoIndex = state.currentPhotoIndex + 1
            )
            
            // 更新会话
            state.sessionId?.let { sessionId ->
                cleaningSessionRepository.incrementPhotosViewed(sessionId)
                cleaningSessionRepository.incrementPhotosFavorited(sessionId)
            }
        }
    }

    fun getCurrentPhoto(): Photo? {
        val state = _uiState.value
        return state.photos.getOrNull(state.currentPhotoIndex)
    }

    fun moveToNextPhoto() {
        val state = _uiState.value
        if (state.currentPhotoIndex < state.photos.size - 1) {
            _uiState.value = state.copy(
                currentPhotoIndex = state.currentPhotoIndex + 1
            )
        }
    }

    fun moveToPreviousPhoto() {
        val state = _uiState.value
        if (state.currentPhotoIndex > 0) {
            _uiState.value = state.copy(
                currentPhotoIndex = state.currentPhotoIndex - 1
            )
        }
    }

    fun refreshPhotos() {
        loadPhotos()
    }
}