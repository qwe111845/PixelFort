package com.pixelfort.towerdefense.core.di

import android.content.Context
import androidx.room.Room
import com.pixelfort.towerdefense.core.database.AppDatabase
import com.pixelfort.towerdefense.core.database.dao.MetaUpgradeDao
import com.pixelfort.towerdefense.core.database.dao.ProgressDao
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
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "pixelfort.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()

    @Provides
    fun provideMetaUpgradeDao(db: AppDatabase): MetaUpgradeDao = db.metaUpgradeDao()
}
