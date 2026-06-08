package com.photoslide.data.repository

import com.photoslide.data.local.database.dao.CleaningSessionDao
import com.photoslide.data.local.database.entity.CleaningSessionEntity
import com.photoslide.data.model.CleaningSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CleaningSessionRepository @Inject constructor(
    private val cleaningSessionDao: CleaningSessionDao
) {
    fun getAllSessions(): Flow<List<CleaningSession>> {
        return cleaningSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getLastSession(): CleaningSession? {
        return cleaningSessionDao.getLastSession()?.toDomain()
    }

    suspend fun getSessionById(sessionId: Long): CleaningSession? {
        return cleaningSessionDao.getSessionById(sessionId)?.toDomain()
    }

    suspend fun getCompletedSessionCount(): Int {
        return cleaningSessionDao.getCompletedSessionCount()
    }

    suspend fun getTotalPhotosDeleted(): Int {
        return cleaningSessionDao.getTotalPhotosDeleted() ?: 0
    }

    suspend fun getTotalSpaceFreed(): Long {
        return cleaningSessionDao.getTotalSpaceFreed() ?: 0L
    }

    suspend fun getRecentCompletedSessions(limit: Int): List<CleaningSession> {
        return cleaningSessionDao.getRecentCompletedSessions(limit).map { it.toDomain() }
    }

    suspend fun startNewSession(): Long {
        val session = CleaningSessionEntity(
            startTime = System.currentTimeMillis()
        )
        return cleaningSessionDao.insertSession(session)
    }

    suspend fun updateSession(session: CleaningSession) {
        cleaningSessionDao.updateSession(session.toEntity())
    }

    suspend fun completeSession(sessionId: Long, photosDeleted: Int, spaceFreed: Long) {
        val session = cleaningSessionDao.getSessionById(sessionId)
        session?.let {
            cleaningSessionDao.updateSession(
                it.copy(
                    endTime = System.currentTimeMillis(),
                    photosDeleted = photosDeleted,
                    spaceFreed = spaceFreed,
                    isCompleted = true
                )
            )
        }
    }

    suspend fun incrementPhotosViewed(sessionId: Long) {
        val session = cleaningSessionDao.getSessionById(sessionId)
        session?.let {
            cleaningSessionDao.updateSession(
                it.copy(photosViewed = it.photosViewed + 1)
            )
        }
    }

    suspend fun incrementPhotosKept(sessionId: Long) {
        val session = cleaningSessionDao.getSessionById(sessionId)
        session?.let {
            cleaningSessionDao.updateSession(
                it.copy(photosKept = it.photosKept + 1)
            )
        }
    }

    suspend fun incrementPhotosDeleted(sessionId: Long) {
        val session = cleaningSessionDao.getSessionById(sessionId)
        session?.let {
            cleaningSessionDao.updateSession(
                it.copy(photosDeleted = it.photosDeleted + 1)
            )
        }
    }

    suspend fun incrementPhotosFavorited(sessionId: Long) {
        val session = cleaningSessionDao.getSessionById(sessionId)
        session?.let {
            cleaningSessionDao.updateSession(
                it.copy(photosFavorited = it.photosFavorited + 1)
            )
        }
    }

    suspend fun deleteSession(session: CleaningSession) {
        cleaningSessionDao.deleteSession(session.toEntity())
    }

    suspend fun deleteIncompleteSessions() {
        cleaningSessionDao.deleteIncompleteSessions()
    }

    // 扩展函数：Entity转Domain
    private fun CleaningSessionEntity.toDomain(): CleaningSession {
        return CleaningSession(
            id = id,
            startTime = startTime,
            endTime = endTime,
            photosViewed = photosViewed,
            photosKept = photosKept,
            photosDeleted = photosDeleted,
            photosFavorited = photosFavorited,
            spaceFreed = spaceFreed,
            isCompleted = isCompleted
        )
    }

    // 扩展函数：Domain转Entity
    private fun CleaningSession.toEntity(): CleaningSessionEntity {
        return CleaningSessionEntity(
            id = id,
            startTime = startTime,
            endTime = endTime,
            photosViewed = photosViewed,
            photosKept = photosKept,
            photosDeleted = photosDeleted,
            photosFavorited = photosFavorited,
            spaceFreed = spaceFreed,
            isCompleted = isCompleted
        )
    }
}