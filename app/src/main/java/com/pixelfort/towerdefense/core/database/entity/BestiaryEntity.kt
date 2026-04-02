package com.pixelfort.towerdefense.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bestiary")
data class BestiaryEntity(
    @PrimaryKey val enemyType: String,
    val defeatedCount: Int = 0,
    val firstDefeatedAt: Long = 0L,
    val maxHitDamage: Int = 0
)
