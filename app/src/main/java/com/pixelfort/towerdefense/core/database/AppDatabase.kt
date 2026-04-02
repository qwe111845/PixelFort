package com.pixelfort.towerdefense.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pixelfort.towerdefense.core.database.dao.BestiaryDao
import com.pixelfort.towerdefense.core.database.dao.EndlessHighScoreDao
import com.pixelfort.towerdefense.core.database.dao.MetaUpgradeDao
import com.pixelfort.towerdefense.core.database.dao.ProgressDao
import com.pixelfort.towerdefense.core.database.entity.BestiaryEntity
import com.pixelfort.towerdefense.core.database.entity.EndlessHighScoreEntity
import com.pixelfort.towerdefense.core.database.entity.LevelProgressEntity
import com.pixelfort.towerdefense.core.database.entity.MetaUpgradeEntity
import com.pixelfort.towerdefense.core.database.entity.ResearchPointsEntity

@Database(
    entities = [
        LevelProgressEntity::class,
        MetaUpgradeEntity::class,
        ResearchPointsEntity::class,
        EndlessHighScoreEntity::class,
        BestiaryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun metaUpgradeDao(): MetaUpgradeDao
    abstract fun endlessHighScoreDao(): EndlessHighScoreDao
    abstract fun bestiaryDao(): BestiaryDao
}
