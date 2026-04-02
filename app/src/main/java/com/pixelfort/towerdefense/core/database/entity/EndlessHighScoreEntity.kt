package com.pixelfort.towerdefense.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "endless_high_scores")
data class EndlessHighScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wavesReached: Int,
    val totalKills: Int,
    val date: Long // epoch millis
)
