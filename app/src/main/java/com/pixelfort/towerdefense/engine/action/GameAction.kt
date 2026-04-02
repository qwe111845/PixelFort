package com.pixelfort.towerdefense.engine.action

import com.pixelfort.towerdefense.engine.model.SkillType
import com.pixelfort.towerdefense.engine.model.TowerType

sealed interface GameAction {
    data class PlaceTower(
        val towerType: TowerType,
        val gridRow: Int,
        val gridCol: Int
    ) : GameAction

    data class UpgradeTower(val towerId: Int) : GameAction

    data class SellTower(val towerId: Int) : GameAction

    data object StartWave : GameAction

    data class SetSpeed(val multiplier: Float) : GameAction

    /** SPEC-029: Use an active skill. targetGridRow/Col only needed for METEOR_STRIKE. */
    data class UseSkill(
        val type: SkillType,
        val targetGridRow: Int? = null,
        val targetGridCol: Int? = null
    ) : GameAction
}
