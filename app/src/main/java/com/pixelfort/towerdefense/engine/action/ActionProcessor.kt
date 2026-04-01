package com.pixelfort.towerdefense.engine.action

import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.PlayerState
import com.pixelfort.towerdefense.engine.model.Tower

class ActionProcessor(private val map: GameMap) {

    data class ValidationResult(
        val isValid: Boolean,
        val reason: String = ""
    )

    fun validate(
        action: GameAction,
        playerState: PlayerState,
        towers: List<Tower>,
        metaBonus: MetaBonus = MetaBonus()
    ): ValidationResult = when (action) {
        is GameAction.PlaceTower -> validatePlaceTower(action, playerState, towers, metaBonus)
        is GameAction.UpgradeTower -> validateUpgradeTower(action, playerState, towers)
        is GameAction.SellTower -> validateSellTower(action, towers)
        is GameAction.StartWave -> ValidationResult(isValid = true)
        is GameAction.SetSpeed -> ValidationResult(isValid = true)
    }

    private fun validatePlaceTower(
        action: GameAction.PlaceTower,
        playerState: PlayerState,
        towers: List<Tower>,
        metaBonus: MetaBonus
    ): ValidationResult {
        // Check if tower type is locked
        if (action.towerType.isLockedByDefault &&
            action.towerType !in metaBonus.unlockedTowers) {
            return ValidationResult(false, "${action.towerType.nameZh} 尚未解鎖")
        }
        if (!map.isBuildable(action.gridRow, action.gridCol)) {
            return ValidationResult(false, "Cell is not buildable")
        }
        if (!playerState.canAfford(action.towerType.baseCost)) {
            return ValidationResult(false, "Not enough gold")
        }
        if (towers.any { it.gridRow == action.gridRow && it.gridCol == action.gridCol }) {
            return ValidationResult(false, "Cell already has a tower")
        }
        return ValidationResult(true)
    }

    private fun validateUpgradeTower(
        action: GameAction.UpgradeTower,
        playerState: PlayerState,
        towers: List<Tower>
    ): ValidationResult {
        val tower = towers.find { it.id == action.towerId }
            ?: return ValidationResult(false, "Tower not found")
        if (tower.isMaxLevel) {
            return ValidationResult(false, "Tower is at max level")
        }
        if (!playerState.canAfford(tower.upgradeCost)) {
            return ValidationResult(false, "Not enough gold")
        }
        return ValidationResult(true)
    }

    private fun validateSellTower(
        action: GameAction.SellTower,
        towers: List<Tower>
    ): ValidationResult {
        towers.find { it.id == action.towerId }
            ?: return ValidationResult(false, "Tower not found")
        return ValidationResult(true)
    }
}
