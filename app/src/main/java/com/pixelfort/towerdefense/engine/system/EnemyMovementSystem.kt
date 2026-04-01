package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.GridPoint
import kotlin.math.min
import kotlin.math.sqrt

class EnemyMovementSystem(
    private val waypoints: List<GridPoint>,
    private val cellSize: Float
) {
    private val totalPathLength: Float by lazy {
        var len = 0f
        for (i in 0 until waypoints.size - 1)
            len += waypoints[i].distanceTo(waypoints[i + 1])
        len
    }

    fun moveEnemy(enemy: Enemy, deltaMs: Long): Enemy {
        if (enemy.hasReachedEnd || enemy.isDead) return enemy
        val deltaSeconds = deltaMs / 1000f
        // effectiveSpeed respects slow status effects
        val progressDelta = (enemy.effectiveSpeed * deltaSeconds) / totalPathLength
        val newProgress = min(enemy.pathProgress + progressDelta, 1.0f)
        val (px, py) = interpolate(newProgress)
        return enemy.copy(pathProgress = newProgress, pixelX = px, pixelY = py)
    }

    fun moveAll(enemies: List<Enemy>, deltaMs: Long) = enemies.map { moveEnemy(it, deltaMs) }

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
