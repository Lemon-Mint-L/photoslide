package com.photoslide.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photoslide.data.model.AIDetectionResult
import com.photoslide.data.model.Photo
import com.photoslide.data.model.PhotoGroup
import com.photoslide.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AIScanUiState(
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val scanResult: AIDetectionResult? = null,
    val error: String? = null,
    val selectedCategory: String? = null,
    val categoryPhotos: List<Photo> = emptyList(),
    val duplicateGroups: List<PhotoGroup> = emptyList()
)

@HiltViewModel
class AIScanViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIScanUiState())
    val uiState: StateFlow<AIScanUiState> = _uiState.asStateFlow()

    fun startScan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, scanProgress = 0f)
            
            try {
                // 扫描模糊照片
                _uiState.value = _uiState.value.copy(scanProgress = 0.25f)
                val blurPhotos = photoRepository.scanPhotosForBlur()
                
                // 扫描重复照片
                _uiState.value = _uiState.value.copy(scanProgress = 0.5f)
                val duplicateGroups = photoRepository.scanPhotosForDuplicates()
                
                // 扫描截图
                _uiState.value = _uiState.value.copy(scanProgress = 0.75f)
                val screenshots = photoRepository.scanPhotosForScreenshots()
                
                // 查找最佳照片
                _uiState.value = _uiState.value.copy(scanProgress = 1f)
                val bestShots = photoRepository.findBestShots()
                
                // 计算潜在节省空间
                val duplicatePhotos = duplicateGroups.flatMap { it.similarPhotos }
                val potentialSpaceSavings = duplicatePhotos.sumOf { it.size } + 
                    blurPhotos.sumOf { it.size }
                
                val result = AIDetectionResult(
                    blurPhotos = blurPhotos,
                    duplicateGroups = duplicateGroups,
                    screenshots = screenshots,
                    bestShots = bestShots,
                    totalPhotos = blurPhotos.size + duplicatePhotos.size + screenshots.size + bestShots.size,
                    scanDuration = 0, // 实际应该记录扫描时间
                    potentialSpaceSavings = potentialSpaceSavings
                )
                
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    scanResult = result,
                    duplicateGroups = duplicateGroups
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    error = e.message
                )
            }
        }
    }

    fun selectCategory(category: String) {
        val result = _uiState.value.scanResult ?: return
        
        val photos = when (category) {
            "模糊照片" -> result.blurPhotos
            "重复照片" -> result.duplicateGroups.flatMap { it.similarPhotos }
            "截图" -> result.screenshots
            "最佳照片" -> result.bestShots
            else -> emptyList()
        }
        
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            categoryPhotos = photos
        )
    }

    fun clearCategorySelection() {
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            categoryPhotos = emptyList()
        )
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            photoRepository.markAsDeleted(photo.id)
            // 刷新扫描结果
            startScan()
        }
    }

    fun deletePhotos(photos: List<Photo>) {
        viewModelScope.launch {
            photoRepository.markAsDeleted(photos.map { it.id })
            // 刷新扫描结果
            startScan()
        }
    }

    fun keepPhoto(photo: Photo) {
        // 保留照片，不做任何操作
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}