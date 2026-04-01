package com.pixelfort.towerdefense.feature.progress.domain

import kotlinx.coroutines.flow.Flow

data class LevelProgress(val levelId: Int, val starsEarned: Int, val completed: Boolean)

interface ProgressRepository {
    fun observeAll(): Flow<List<LevelProgress>>
    suspend fun saveProgress(levelId: Int, stars: Int)
}
