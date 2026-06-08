package com.photoslide.di

import android.content.Context
import androidx.room.Room
import com.photoslide.data.local.database.AppDatabase
import com.photoslide.data.local.database.dao.CleaningSessionDao
import com.photoslide.data.local.database.dao.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "photoslide_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePhotoDao(database: AppDatabase): PhotoDao {
        return database.photoDao()
    }

    @Provides
    @Singleton
    fun provideCleaningSessionDao(database: AppDatabase): CleaningSessionDao {
        return database.cleaningSessionDao()
    }
}