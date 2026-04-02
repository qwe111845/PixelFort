package com.pixelfort.towerdefense.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pixelfort.towerdefense.core.database.entity.BestiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BestiaryDao {
    @Query("SELECT * FROM bestiary")
    fun observeAll(): Flow<List<BestiaryEntity>>

    @Query("SELECT * FROM bestiary")
    suspend fun getAll(): List<BestiaryEntity>

    @Query("SELECT * FROM bestiary WHERE enemyType = :type")
    suspend fun getByType(type: String): BestiaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BestiaryEntity)
}
