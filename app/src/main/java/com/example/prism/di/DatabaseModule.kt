package com.example.prism.di

import android.content.Context
import androidx.room.Room
import com.example.prism.data.local.BehaviorDao
import com.example.prism.data.local.BehaviorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBehaviorDatabase(
        @ApplicationContext context: Context
    ): BehaviorDatabase {
        return Room.databaseBuilder(
            context,
            BehaviorDatabase::class.java,
            BehaviorDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBehaviorDao(database: BehaviorDatabase): BehaviorDao {
        return database.behaviorDao()
    }
}