package com.pixelfort.towerdefense.feature.game.viewmodel

import com.pixelfort.towerdefense.engine.GameSnapshot
import com.pixelfort.towerdefense.engine.model.ActiveWaveEvent
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.feature.game.tutorial.TutorialState
import com.pixelfort.towerdefense.feature.game.vfx.AmbientParticle
import com.pixelfort.towerdefense.feature.game.vfx.DeathFlash
import com.pixelfort.towerdefense.feature.game.vfx.FlashEffect
import com.pixelfort.towerdefense.feature.game.vfx.FloatingText
import com.pixelfort.towerdefense.feature.game.vfx.Particle
import com.pixelfort.towerdefense.feature.game.vfx.ScreenShake
import com.pixelfort.towerdefense.feature.game.vfx.SellEffect
import com.pixelfort.towerdefense.feature.game.vfx.TrailSystem

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Playing(
        val snapshot: GameSnapshot,
        val selectedTowerType: TowerType? = null,
        val selectedTowerId: Int? = null,
        val isMeteorTargeting: Boolean = false,
        val particles: List<Particle> = emptyList(),
        val floatingTexts: List<FloatingText> = emptyList(),
        val screenShake: ScreenShake = ScreenShake.IDLE,
        val flashEffect: FlashEffect = FlashEffect.NONE,
        val metaBonus: MetaBonus = MetaBonus(),
        val cellSize: Float = 80f,
        val elapsedMs: Long = 0L,
        val bossWarningActive: Boolean = false,
        val tutorialState: TutorialState = TutorialState(isActive = false, isCompleted = true),
        val trailSystem: TrailSystem? = null,
        val ambientParticles: List<AmbientParticle> = emptyList(),
        val deathFlashes: List<DeathFlash> = emptyList(),
        val sellEffects: List<SellEffect> = emptyList(),
        val sellConfirmTowerId: Int? = null,
        /** SPEC-032: Active wave event shown in banner */
        val waveEventBanner: ActiveWaveEvent? = null,
        /** SPEC-032: Banner remaining display time in ms */
        val waveEventBannerRemainingMs: Long = 0L
    ) : GameUiState
}
