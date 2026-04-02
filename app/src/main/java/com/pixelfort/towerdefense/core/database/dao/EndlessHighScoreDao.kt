package com.pixelfort.towerdefense.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pixelfort.towerdefense.core.database.entity.EndlessHighScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EndlessHighScoreDao {
    @Insert
    suspend fun insert(entity: EndlessHighScoreEntity)

    @Query("SELECT * FROM endless_high_scores ORDER BY wavesReached DESC, totalKills DESC LIMIT 10")
    fun observeTopScores(): Flow<List<EndlessHighScoreEntity>>

    @Query("SELECT * FROM endless_high_scores ORDER BY wavesReached DESC, totalKills DESC LIMIT 1")
    suspend fun getBestScore(): EndlessHighScoreEntity?
}
