package com.photoslide.data.local.database.dao

import androidx.room.*
import com.photoslide.data.local.database.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE isDeleted = 0 ORDER BY dateAdded DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE isDeleted = 0 AND isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoritePhotos(): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE isDeleted = 0 AND isDuplicate = 1 ORDER BY dateAdded DESC")
    fun getDuplicatePhotos(): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE isDeleted = 0 AND isScreenshot = 1 ORDER BY dateAdded DESC")
    fun getScreenshotPhotos(): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE isDeleted = 0 AND blurScore < 0.3 ORDER BY dateAdded DESC")
    fun getBlurryPhotos(): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE isDeleted = 0 AND isBestShot = 1 ORDER BY dateAdded DESC")
    fun getBestShotPhotos(): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): PhotoEntity?
    
    @Query("SELECT * FROM photos WHERE isDeleted = 0 ORDER BY dateAdded DESC LIMIT :limit OFFSET :offset")
    suspend fun getPhotosWithPagination(limit: Int, offset: Int): List<PhotoEntity>
    
    @Query("SELECT COUNT(*) FROM photos WHERE isDeleted = 0")
    suspend fun getPhotoCount(): Int
    
    @Query("SELECT SUM(size) FROM photos WHERE isDeleted = 0")
    suspend fun getTotalSize(): Long?
    
    @Query("SELECT SUM(size) FROM photos WHERE isDeleted = 0 AND isDuplicate = 1")
    suspend fun getDuplicatePhotosSize(): Long?
    
    @Query("SELECT SUM(size) FROM photos WHERE isDeleted = 0 AND blurScore < 0.3")
    suspend fun getBlurryPhotosSize(): Long?
    
    @Query("SELECT SUM(size) FROM photos WHERE isDeleted = 0 AND isScreenshot = 1")
    suspend fun getScreenshotPhotosSize(): Long?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)
    
    @Update
    suspend fun updatePhoto(photo: PhotoEntity)
    
    @Query("UPDATE photos SET isFavorite = :isFavorite WHERE id = :photoId")
    suspend fun updateFavoriteStatus(photoId: Long, isFavorite: Boolean)
    
    @Query("UPDATE photos SET isDeleted = 1 WHERE id = :photoId")
    suspend fun markAsDeleted(photoId: Long)
    
    @Query("UPDATE photos SET isDeleted = 1 WHERE id IN (:photoIds)")
    suspend fun markAsDeleted(photoIds: List<Long>)
    
    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)
    
    @Query("DELETE FROM photos WHERE isDeleted = 1")
    suspend fun permanentlyDeleteMarkedPhotos()
}