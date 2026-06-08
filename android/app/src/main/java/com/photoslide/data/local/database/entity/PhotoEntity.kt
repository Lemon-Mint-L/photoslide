package com.photoslide.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey
    val id: Long,
    val uri: String,
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
    val blurScore: Float = 1.0f,
    val isDuplicate: Boolean = false,
    val duplicateGroupId: Long? = null,
    val isScreenshot: Boolean = false,
    val isBestShot: Boolean = false,
    val qualityScore: Float = 0f,
    val lastViewed: Long? = null,
    val viewCount: Int = 0
)