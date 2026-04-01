package com.pixelfort.towerdefense.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val starsEarned: Int,
    val completed: Boolean
)
