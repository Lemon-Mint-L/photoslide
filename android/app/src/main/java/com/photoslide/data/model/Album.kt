package com.photoslide.data.model

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val uri: Uri,
    val photoCount: Int,
    val coverPhoto: Photo? = null,
    val dateModified: Long,
    val totalSize: Long = 0
) {
    val totalSizeInMB: Float
        get() = totalSize / (1024f * 1024f)
    
    val totalSizeInGB: Float
        get() = totalSize / (1024f * 1024f * 1024f)
}