package com.photoslide.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.photoslide.data.local.database.dao.CleaningSessionDao
import com.photoslide.data.local.database.dao.PhotoDao
import com.photoslide.data.local.database.entity.CleaningSessionEntity
import com.photoslide.data.local.database.entity.PhotoEntity

@Database(
    entities = [PhotoEntity::class, CleaningSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun cleaningSessionDao(): CleaningSessionDao
}