package com.pixelfort.towerdefense.feature.game.viewmodel

import com.pixelfort.towerdefense.engine.GameSnapshot
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.feature.game.vfx.FlashEffect
import com.pixelfort.towerdefense.feature.game.vfx.FloatingText
import com.pixelfort.towerdefense.feature.game.vfx.Particle
import com.pixelfort.towerdefense.feature.game.vfx.ScreenShake

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Playing(
        val snapshot: GameSnapshot,
        val selectedTowerType: TowerType? = null,
        val selectedTowerId: Int? = null,
        val particles: List<Particle> = emptyList(),
        val floatingTexts: List<FloatingText> = emptyList(),
        val screenShake: ScreenShake = ScreenShake.IDLE,
        val flashEffect: FlashEffect = FlashEffect.NONE,
        val metaBonus: MetaBonus = MetaBonus(),
        val cellSize: Float = 80f
    ) : GameUiState
}
