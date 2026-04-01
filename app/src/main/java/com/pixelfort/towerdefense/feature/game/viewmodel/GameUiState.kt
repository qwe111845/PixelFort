package com.pixelfort.towerdefense.feature.game.viewmodel

import com.pixelfort.towerdefense.engine.GameSnapshot
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.feature.game.vfx.Particle

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Playing(
        val snapshot: GameSnapshot,
        val selectedTowerType: TowerType? = null,
        val selectedTowerId: Int? = null,
        val particles: List<Particle> = emptyList(),
        val metaBonus: MetaBonus = MetaBonus(),
        val cellSize: Float = 80f
    ) : GameUiState
}
