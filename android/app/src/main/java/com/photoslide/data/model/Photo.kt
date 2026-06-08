package com.photoslide.data.model

import android.net.Uri

data class Photo(
    val id: Long,
    val uri: Uri,
    val name: String,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val width: Int,
    val height: Int,
    val mimeType: String,
    val isFavorite: Boolean = false,
    val isDeleted: Boolean = false,
    val blurScore: Float = 1.0f, // 0-1, 1 = 清晰
    val isDuplicate: Boolean = false,
    val duplicateGroupId: Long? = null,
    val isScreenshot: Boolean = false,
    val isBestShot: Boolean = false,
    val qualityScore: Float = 0f
) {
    val sizeInMB: Float
        get() = size / (1024f * 1024f)
    
    val resolution: Int
        get() = width * height
    
    val isHighResolution: Boolean
        get() = width >= 1920 && height >= 1080
    
    val isBlurry: Boolean
        get() = blurScore < 0.3f
}