package com.pixelfort.towerdefense.engine.level

import com.pixelfort.towerdefense.engine.model.CellEffect
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.GridPoint
import com.pixelfort.towerdefense.engine.model.Wave

data class LevelDefinition(
    val id: Int,
    val name: String,
    val map: GameMap,
    val waves: List<Wave>,
    val startingGold: Int,
    val startingLives: Int,
    val cellEffects: Map<GridPoint, CellEffect> = emptyMap()
)
