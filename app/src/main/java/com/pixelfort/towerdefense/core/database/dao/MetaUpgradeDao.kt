package com.pixelfort.towerdefense.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pixelfort.towerdefense.core.database.entity.MetaUpgradeEntity
import com.pixelfort.towerdefense.core.database.entity.ResearchPointsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaUpgradeDao {
    @Query("SELECT * FROM meta_upgrades")
    fun observeAll(): Flow<List<MetaUpgradeEntity>>

    @Query("SELECT * FROM meta_upgrades")
    suspend fun getAll(): List<MetaUpgradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MetaUpgradeEntity)

    @Query("SELECT * FROM research_points WHERE id = 0")
    fun observePoints(): Flow<ResearchPointsEntity?>

    @Query("SELECT * FROM research_points WHERE id = 0")
    suspend fun getPoints(): ResearchPointsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPoints(entity: ResearchPointsEntity)
}
