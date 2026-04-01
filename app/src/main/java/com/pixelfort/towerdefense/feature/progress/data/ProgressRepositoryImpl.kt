package com.pixelfort.towerdefense.feature.progress.data

import com.pixelfort.towerdefense.core.database.dao.ProgressDao
import com.pixelfort.towerdefense.core.database.entity.LevelProgressEntity
import com.pixelfort.towerdefense.feature.progress.domain.LevelProgress
import com.pixelfort.towerdefense.feature.progress.domain.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val dao: ProgressDao
) : ProgressRepository {

    override fun observeAll(): Flow<List<LevelProgress>> =
        dao.observeAll().map { list ->
            list.map { LevelProgress(it.levelId, it.starsEarned, it.completed) }
        }

    override suspend fun saveProgress(levelId: Int, stars: Int) {
        val existing = dao.getById(levelId)
        val newStars = maxOf(existing?.starsEarned ?: 0, stars)
        dao.upsert(LevelProgressEntity(levelId, newStars, completed = true))
    }
}
