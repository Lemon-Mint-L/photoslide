package com.photoslide.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleaning_sessions")
data class CleaningSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val photosViewed: Int = 0,
    val photosKept: Int = 0,
    val photosDeleted: Int = 0,
    val photosFavorited: Int = 0,
    val spaceFreed: Long = 0,
    val isCompleted: Boolean = false
)