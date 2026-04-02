package com.pixelfort.towerdefense.engine.event

import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.ActiveWaveEvent
import com.pixelfort.towerdefense.engine.model.SkillType
import com.pixelfort.towerdefense.engine.model.TowerEffect
import com.pixelfort.towerdefense.engine.model.TowerType

sealed interface GameEvent {
    data class EnemyKilled(
        val enemyId: Int, val enemyType: EnemyType,
        val reward: Int, val pixelX: Float, val pixelY: Float
    ) : GameEvent

    data class WaveCompleted(val waveNumber: Int, val livesRemaining: Int) : GameEvent

    data class TowerPlaced(
        val towerId: Int, val towerType: TowerType,
        val gridRow: Int, val gridCol: Int
    ) : GameEvent

    data class TowerUpgraded(
        val towerId: Int, val newLevel: Int,
        val gridRow: Int, val gridCol: Int
    ) : GameEvent

    data class TowerSold(val towerId: Int, val goldReturned: Int) : GameEvent

    data class LivesLost(val enemyId: Int, val remainingLives: Int) : GameEvent

    /** Fired when a projectile hits — used to spawn VFX particles + damage text */
    data class ProjectileHit(
        val pixelX: Float, val pixelY: Float,
        val effect: TowerEffect,
        val damage: Int = 0
    ) : GameEvent

    /** Fired each time a chain lightning jumps */
    data class ChainLightningJump(
        val fromX: Float, val fromY: Float,
        val toX: Float, val toY: Float
    ) : GameEvent

    data class BossWarning(val waveIndex: Int) : GameEvent
    data class BossEnraged(val enemyId: Int) : GameEvent

    data object GameWon : GameEvent
    data object GameLost : GameEvent

    /** SPEC-029: Fired when a skill is used */
    data class SkillUsed(
        val type: SkillType,
        val targetPixelX: Float? = null,
        val targetPixelY: Float? = null
    ) : GameEvent

    /** SPEC-032: Fired when a random wave event is triggered */
    data class WaveEventTriggered(val event: ActiveWaveEvent) : GameEvent
}
