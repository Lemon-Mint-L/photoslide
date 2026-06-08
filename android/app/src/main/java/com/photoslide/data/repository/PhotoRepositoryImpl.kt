package com.photoslide.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.photoslide.data.local.database.dao.PhotoDao
import com.photoslide.data.local.database.entity.PhotoEntity
import com.photoslide.data.model.Photo
import com.photoslide.data.model.PhotoGroup
import com.photoslide.data.model.GroupType
import com.photoslide.service.AIDetectionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoDao: PhotoDao,
    private val aiDetectionService: AIDetectionService
) : PhotoRepository {

    private val contentResolver: ContentResolver = context.contentResolver

    override fun getAllPhotos(): Flow<List<Photo>> {
        return photoDao.getAllPhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoritePhotos(): Flow<List<Photo>> {
        return photoDao.getFavoritePhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDuplicatePhotos(): Flow<List<Photo>> {
        return photoDao.getDuplicatePhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getScreenshotPhotos(): Flow<List<Photo>> {
        return photoDao.getScreenshotPhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBlurryPhotos(): Flow<List<Photo>> {
        return photoDao.getBlurryPhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBestShotPhotos(): Flow<List<Photo>> {
        return photoDao.getBestShotPhotos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPhotoById(photoId: Long): Photo? {
        return photoDao.getPhotoById(photoId)?.toDomain()
    }

    override suspend fun getPhotosWithPagination(limit: Int, offset: Int): List<Photo> {
        return photoDao.getPhotosWithPagination(limit, offset).map { it.toDomain() }
    }

    override suspend fun getPhotoCount(): Int {
        return photoDao.getPhotoCount()
    }

    override suspend fun getTotalSize(): Long {
        return photoDao.getTotalSize() ?: 0L
    }

    override suspend fun getDuplicatePhotosSize(): Long {
        return photoDao.getDuplicatePhotosSize() ?: 0L
    }

    override suspend fun getBlurryPhotosSize(): Long {
        return photoDao.getBlurryPhotosSize() ?: 0L
    }

    override suspend fun getScreenshotPhotosSize(): Long {
        return photoDao.getScreenshotPhotosSize() ?: 0L
    }

    override suspend fun insertPhoto(photo: Photo) {
        photoDao.insertPhoto(photo.toEntity())
    }

    override suspend fun insertPhotos(photos: List<Photo>) {
        photoDao.insertPhotos(photos.map { it.toEntity() })
    }

    override suspend fun updatePhoto(photo: Photo) {
        photoDao.updatePhoto(photo.toEntity())
    }

    override suspend fun updateFavoriteStatus(photoId: Long, isFavorite: Boolean) {
        photoDao.updateFavoriteStatus(photoId, isFavorite)
    }

    override suspend fun markAsDeleted(photoId: Long) {
        photoDao.markAsDeleted(photoId)
    }

    override suspend fun markAsDeleted(photoIds: List<Long>) {
        photoDao.markAsDeleted(photoIds)
    }

    override suspend fun permanentlyDeleteMarkedPhotos() {
        photoDao.permanentlyDeleteMarkedPhotos()
    }

    override suspend fun scanPhotosForBlur(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = photoDao.getPhotosWithPagination(1000, 0).map { it.toDomain() }
        aiDetectionService.detectBlurryPhotos(photos)
    }

    override suspend fun scanPhotosForDuplicates(): List<PhotoGroup> = withContext(Dispatchers.IO) {
        val photos = photoDao.getPhotosWithPagination(1000, 0).map { it.toDomain() }
        aiDetectionService.detectDuplicatePhotos(photos)
    }

    override suspend fun scanPhotosForScreenshots(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = photoDao.getPhotosWithPagination(1000, 0).map { it.toDomain() }
        aiDetectionService.detectScreenshots(photos)
    }

    override suspend fun findBestShots(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = photoDao.getPhotosWithPagination(1000, 0).map { it.toDomain() }
        aiDetectionService.findBestShots(photos)
    }

    override suspend fun calculateQualityScore(photo: Photo): Float = withContext(Dispatchers.IO) {
        val photos = listOf(photo)
        val bestShots = aiDetectionService.findBestShots(photos)
        bestShots.firstOrNull()?.qualityScore ?: 0f
    }

    // 从MediaStore加载照片
    suspend fun loadPhotosFromMediaStore(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: ""
                val path = cursor.getString(pathColumn) ?: ""
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateAddedColumn) * 1000
                val dateModified = cursor.getLong(dateModifiedColumn) * 1000
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val mimeType = cursor.getString(mimeTypeColumn) ?: ""

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photos.add(
                    Photo(
                        id = id,
                        uri = uri,
                        name = name,
                        path = path,
                        size = size,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        width = width,
                        height = height,
                        mimeType = mimeType
                    )
                )
            }
        }

        photos
    }

    // 扩展函数：Entity转Domain
    private fun PhotoEntity.toDomain(): Photo {
        return Photo(
            id = id,
            uri = Uri.parse(uri),
            name = name,
            path = path,
            size = size,
            dateAdded = dateAdded,
            dateModified = dateModified,
            width = width,
            height = height,
            mimeType = mimeType,
            isFavorite = isFavorite,
            isDeleted = isDeleted,
            blurScore = blurScore,
            isDuplicate = isDuplicate,
            duplicateGroupId = duplicateGroupId,
            isScreenshot = isScreenshot,
            isBestShot = isBestShot,
            qualityScore = qualityScore
        )
    }

    // 扩展函数：Domain转Entity
    private fun Photo.toEntity(): PhotoEntity {
        return PhotoEntity(
            id = id,
            uri = uri.toString(),
            name = name,
            path = path,
            size = size,
            dateAdded = dateAdded,
            dateModified = dateModified,
            width = width,
            height = height,
            mimeType = mimeType,
            isFavorite = isFavorite,
            isDeleted = isDeleted,
            blurScore = blurScore,
            isDuplicate = isDuplicate,
            duplicateGroupId = duplicateGroupId,
            isScreenshot = isScreenshot,
            isBestShot = isBestShot,
            qualityScore = qualityScore
        )
    }
}