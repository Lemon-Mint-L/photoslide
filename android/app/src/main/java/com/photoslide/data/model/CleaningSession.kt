package com.photoslide.data.model

data class CleaningSession(
    val id: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val photosViewed: Int = 0,
    val photosKept: Int = 0,
    val photosDeleted: Int = 0,
    val photosFavorited: Int = 0,
    val spaceFreed: Long = 0,
    val isCompleted: Boolean = false
) {
    val duration: Long
        get() = if (endTime != null) endTime - startTime else System.currentTimeMillis() - startTime
    
    val durationInMinutes: Int
        get() = (duration / (1000 * 60)).toInt()
    
    val spaceFreedInMB: Float
        get() = spaceFreed / (1024f * 1024f)
    
    val spaceFreedInGB: Float
        get() = spaceFreed / (1024f * 1024f * 1024f)
    
    val efficiency: Float
        get() = if (photosViewed > 0) photosDeleted.toFloat() / photosViewed else 0f
}

data class PhotoGroup(
    val bestPhoto: Photo,
    val similarPhotos: List<Photo>,
    val type: GroupType
)

enum class GroupType {
    DUPLICATE,      // 完全重复
    SIMILAR,        // 相似照片
    BURST,          // 连拍照片
    SAME_MOMENT     // 同一时刻
}

enum class ActionType {
    KEEP,
    DELETE,
    FAVORITE,
    SKIP
}

data class CleaningAction(
    val photo: Photo,
    val type: ActionType,
    val timestamp: Long = System.currentTimeMillis()
)