package com.pixelfort.towerdefense.core.di

import com.pixelfort.towerdefense.feature.metaupgrade.data.MetaUpgradeRepositoryImpl
import com.pixelfort.towerdefense.feature.metaupgrade.domain.MetaUpgradeRepository
import com.pixelfort.towerdefense.feature.progress.data.ProgressRepositoryImpl
import com.pixelfort.towerdefense.feature.progress.domain.ProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindProgressRepo(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds @Singleton
    abstract fun bindMetaUpgradeRepo(impl: MetaUpgradeRepositoryImpl): MetaUpgradeRepository
}
