package com.photoslide.data.model

data class AIDetectionResult(
    val blurPhotos: List<Photo> = emptyList(),
    val duplicateGroups: List<PhotoGroup> = emptyList(),
    val screenshots: List<Photo> = emptyList(),
    val bestShots: List<Photo> = emptyList(),
    val totalPhotos: Int = 0,
    val scanDuration: Long = 0,
    val potentialSpaceSavings: Long = 0
) {
    val blurCount: Int
        get() = blurPhotos.size
    
    val duplicateCount: Int
        get() = duplicateGroups.size
    
    val screenshotCount: Int
        get() = screenshots.size
    
    val bestShotCount: Int
        get() = bestShots.size
    
    val potentialSpaceSavingsInMB: Float
        get() = potentialSpaceSavings / (1024f * 1024f)
    
    val potentialSpaceSavingsInGB: Float
        get() = potentialSpaceSavings / (1024f * 1024f * 1024f)
    
    val scanDurationInSeconds: Float
        get() = scanDuration / 1000f
}

data class StorageAnalysis(
    val totalPhotos: Int,
    val totalVideos: Int,
    val totalSize: Long,
    val photoSize: Long,
    val videoSize: Long,
    val screenshotSize: Long,
    val duplicateSize: Long,
    val blurrySize: Long,
    val potentialSavings: Long
) {
    val totalSizeInGB: Float
        get() = totalSize / (1024f * 1024f * 1024f)
    
    val photoSizeInGB: Float
        get() = photoSize / (1024f * 1024f * 1024f)
    
    val videoSizeInGB: Float
        get() = videoSize / (1024f * 1024f * 1024f)
    
    val potentialSavingsInGB: Float
        get() = potentialSavings / (1024f * 1024f * 1024f)
    
    val savingsPercentage: Float
        get() = if (totalSize > 0) potentialSavings.toFloat() / totalSize * 100 else 0f
}