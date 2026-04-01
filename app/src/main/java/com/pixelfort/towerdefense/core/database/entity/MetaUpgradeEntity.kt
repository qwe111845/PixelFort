package com.pixelfort.towerdefense.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meta_upgrades")
data class MetaUpgradeEntity(
    @PrimaryKey val upgradeId: String,
    val level: Int
)

@Entity(tableName = "research_points")
data class ResearchPointsEntity(
    @PrimaryKey val id: Int = 0,
    val points: Int
)
