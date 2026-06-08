package com.photoslide.data.local.database.dao

import androidx.room.*
import com.photoslide.data.local.database.entity.CleaningSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CleaningSessionDao {
    @Query("SELECT * FROM cleaning_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<CleaningSessionEntity>>
    
    @Query("SELECT * FROM cleaning_sessions ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastSession(): CleaningSessionEntity?
    
    @Query("SELECT * FROM cleaning_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): CleaningSessionEntity?
    
    @Query("SELECT COUNT(*) FROM cleaning_sessions WHERE isCompleted = 1")
    suspend fun getCompletedSessionCount(): Int
    
    @Query("SELECT SUM(photosDeleted) FROM cleaning_sessions WHERE isCompleted = 1")
    suspend fun getTotalPhotosDeleted(): Int?
    
    @Query("SELECT SUM(spaceFreed) FROM cleaning_sessions WHERE isCompleted = 1")
    suspend fun getTotalSpaceFreed(): Long?
    
    @Query("SELECT * FROM cleaning_sessions WHERE isCompleted = 1 ORDER BY startTime DESC LIMIT :limit")
    suspend fun getRecentCompletedSessions(limit: Int): List<CleaningSessionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CleaningSessionEntity): Long
    
    @Update
    suspend fun updateSession(session: CleaningSessionEntity)
    
    @Delete
    suspend fun deleteSession(session: CleaningSessionEntity)
    
    @Query("DELETE FROM cleaning_sessions WHERE isCompleted = 0")
    suspend fun deleteIncompleteSessions()
}