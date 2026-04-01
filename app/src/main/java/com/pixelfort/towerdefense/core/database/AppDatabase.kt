package com.pixelfort.towerdefense.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pixelfort.towerdefense.core.database.dao.MetaUpgradeDao
import com.pixelfort.towerdefense.core.database.dao.ProgressDao
import com.pixelfort.towerdefense.core.database.entity.LevelProgressEntity
import com.pixelfort.towerdefense.core.database.entity.MetaUpgradeEntity
import com.pixelfort.towerdefense.core.database.entity.ResearchPointsEntity

@Database(
    entities = [
        LevelProgressEntity::class,
        MetaUpgradeEntity::class,
        ResearchPointsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun metaUpgradeDao(): MetaUpgradeDao
}
