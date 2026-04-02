package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.CellEffect
import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.GridPoint
import kotlin.math.min
import kotlin.math.sqrt

data class MovementResult(
    val enemies: List<Enemy>,
    val lavaDamageEvents: List<LavaDamageEvent> = emptyList()
)

data class LavaDamageEvent(val enemyId: Int, val damage: Int, val pixelX: Float, val pixelY: Float)

class EnemyMovementSystem(
    private val waypoints: List<GridPoint>,
    private val cellSize: Float,
    private val cellEffects: Map<GridPoint, CellEffect> = emptyMap()
) {
    private val totalPathLength: Float by lazy {
        var len = 0f
        for (i in 0 until waypoints.size - 1)
            len += waypoints[i].distanceTo(waypoints[i + 1])
        len
    }

    // Track which lava cells each enemy has already been damaged by (to avoid repeat damage per cell)
    private val lavaDamaged = mutableMapOf<Int, MutableSet<GridPoint>>()

    fun moveEnemy(enemy: Enemy, deltaMs: Long): Enemy {
        if (enemy.hasReachedEnd || enemy.isDead) return enemy
        val deltaSeconds = deltaMs / 1000f
        // effectiveSpeed respects slow status effects
        val progressDelta = (enemy.effectiveSpeed * deltaSeconds) / totalPathLength
        var newProgress = min(enemy.pathProgress + progressDelta, 1.0f)

        // Check for teleport effect at the current grid cell
        val (px, py) = interpolate(newProgress)
        val gridRow = (py / cellSize).toInt()
        val gridCol = (px / cellSize).toInt()
        val currentCell = GridPoint(gridRow, gridCol)

        val effect = cellEffects[currentCell]
        if (effect is CellEffect.Teleport) {
            // Jump the enemy forward to the target waypoint
            val targetIdx = effect.targetWaypointIndex
            if (targetIdx in 0 until waypoints.size) {
                val targetProgress = targetIdx.toFloat() / (waypoints.size - 1).toFloat()
                if (targetProgress > newProgress) {
                    newProgress = min(targetProgress, 1.0f)
                    val (tpx, tpy) = interpolate(newProgress)
                    return enemy.copy(pathProgress = newProgress, pixelX = tpx, pixelY = tpy)
                }
            }
        }

        return enemy.copy(pathProgress = newProgress, pixelX = px, pixelY = py)
    }

    fun moveAll(enemies: List<Enemy>, deltaMs: Long): List<Enemy> =
        enemies.map { moveEnemy(it, deltaMs) }

    /**
     * Move all enemies and apply cell effects (lava damage).
     * Returns a [MovementResult] with updated enemies and any lava damage events.
     */
    fun moveAllWithEffects(enemies: List<Enemy>, deltaMs: Long): MovementResult {
        val lavaEvents = mutableListOf<LavaDamageEvent>()
        val updated = enemies.map { enemy ->
            var moved = moveEnemy(enemy, deltaMs)
            if (moved.isDead || moved.hasReachedEnd) return@map moved

            // Check for lava damage at current cell
            val gridRow = (moved.pixelY / cellSize).toInt()
            val gridCol = (moved.pixelX / cellSize).toInt()
            val currentCell = GridPoint(gridRow, gridCol)
            val effect = cellEffects[currentCell]
            if (effect is CellEffect.LavaDamage) {
                val visited = lavaDamaged.getOrPut(moved.id) { mutableSetOf() }
                if (visited.add(currentCell)) {
                    val dmg = moved.calculateDamage(effect.damage, bypassArmor = true)
                    moved = moved.copy(hp = moved.hp - dmg)
                    lavaEvents.add(LavaDamageEvent(moved.id, dmg, moved.pixelX, moved.pixelY))
                }
            }
            moved
        }
        // Clean up tracking for dead or finished enemies
        val activeIds = updated.filter { !it.isDead && !it.hasReachedEnd }.map { it.id }.toSet()
        lavaDamaged.keys.retainAll(activeIds)

        return MovementResult(updated, lavaEvents)
    }

    private fun interpolate(progress: Float): Pair<Float, Float> {
        if (waypoints.size < 2) {
            val wp = waypoints.first()
            return wp.col * cellSize + cellSize / 2 to wp.row * cellSize + cellSize / 2
        }
        val totalSeg = waypoints.size - 1
        val scaled   = progress * totalSeg
        val seg      = min(scaled.toInt(), totalSeg - 1)
        val local    = scaled - seg
        val from     = waypoints[seg]
        val to       = waypoints[min(seg + 1, waypoints.size - 1)]
        val px = (from.col + (to.col - from.col) * local) * cellSize + cellSize / 2
        val py = (from.row + (to.row - from.row) * local) * cellSize + cellSize / 2
        return px to py
    }
}
