package com.pixelfort.towerdefense.engine

import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.PlayerState
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.Tower

import com.pixelfort.towerdefense.engine.model.DifficultyMode
import com.pixelfort.towerdefense.engine.model.EnemyType

data class GameSnapshot(
    val state: GameState,
    val towers: List<Tower>,
    val enemies: List<Enemy>,
    val projectiles: List<Projectile>,
    val playerState: PlayerState,
    val currentWave: Int,
    val totalWaves: Int,
    val events: List<GameEvent>,
    val metaBonus: MetaBonus = MetaBonus(),
    /** Stars earned (1-3) when game is won, -1 otherwise */
    val starsEarned: Int = -1,
    /** Research points earned this run */
    val rpEarned: Int = 0,
    /** Current game speed multiplier (1.0 / 2.0 / 3.0) */
    val speedMultiplier: Float = 1f,
    /** Next wave enemy composition: list of (EnemyType, count) */
    val wavePreview: List<Pair<EnemyType, Int>> = emptyList(),
    /** Current difficulty mode */
    val difficulty: DifficultyMode = DifficultyMode.NORMAL,
    /** Whether this is an endless mode game */
    val isEndless: Boolean = false,
    /** Total kills in this run (used for endless high score) */
    val totalKills: Int = 0
)
