package com.photoslide.data.repository

import com.photoslide.data.model.Photo
import com.photoslide.data.model.PhotoGroup
import com.photoslide.data.model.GroupType
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(): Flow<List<Photo>>
    fun getFavoritePhotos(): Flow<List<Photo>>
    fun getDuplicatePhotos(): Flow<List<Photo>>
    fun getScreenshotPhotos(): Flow<List<Photo>>
    fun getBlurryPhotos(): Flow<List<Photo>>
    fun getBestShotPhotos(): Flow<List<Photo>>
    
    suspend fun getPhotoById(photoId: Long): Photo?
    suspend fun getPhotosWithPagination(limit: Int, offset: Int): List<Photo>
    suspend fun getPhotoCount(): Int
    suspend fun getTotalSize(): Long
    suspend fun getDuplicatePhotosSize(): Long
    suspend fun getBlurryPhotosSize(): Long
    suspend fun getScreenshotPhotosSize(): Long
    
    suspend fun insertPhoto(photo: Photo)
    suspend fun insertPhotos(photos: List<Photo>)
    suspend fun updatePhoto(photo: Photo)
    suspend fun updateFavoriteStatus(photoId: Long, isFavorite: Boolean)
    suspend fun markAsDeleted(photoId: Long)
    suspend fun markAsDeleted(photoIds: List<Long>)
    suspend fun permanentlyDeleteMarkedPhotos()
    
    // AI相关
    suspend fun scanPhotosForBlur(): List<Photo>
    suspend fun scanPhotosForDuplicates(): List<PhotoGroup>
    suspend fun scanPhotosForScreenshots(): List<Photo>
    suspend fun findBestShots(): List<Photo>
    suspend fun calculateQualityScore(photo: Photo): Float
}