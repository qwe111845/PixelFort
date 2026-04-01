package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import kotlin.math.max
import kotlin.math.sqrt

class TowerTargetingSystem(private val cellSize: Float) {

    data class Result(
        val updatedTowers: List<Tower>,
        val projectiles: List<Projectile>
    )

    private var nextProjectileId = 1

    fun update(towers: List<Tower>, enemies: List<Enemy>, deltaMs: Long): Result {
        val newProjectiles = mutableListOf<Projectile>()

        val updatedTowers = towers.map { tower ->
            val reducedCooldown = max(0L, tower.cooldownRemainingMs - deltaMs)
            val towerReady = tower.copy(cooldownRemainingMs = reducedCooldown)

            if (reducedCooldown > 0) return@map towerReady

            val target = selectTarget(towerReady, enemies) ?: return@map towerReady

            val stats = towerReady.stats
            newProjectiles.add(
                Projectile(
                    id = nextProjectileId++,
                    sourceTowerId = tower.id,
                    targetEnemyId = target.id,
                    damage = stats.damage,
                    pixelX = tower.gridCol * cellSize + cellSize / 2f,
                    pixelY = tower.gridRow * cellSize + cellSize / 2f,
                    speed = 8f,
                    effect = stats.effect
                )
            )
            towerReady.copy(cooldownRemainingMs = stats.fireRateMs)
        }

        return Result(updatedTowers, newProjectiles)
    }

    private fun selectTarget(tower: Tower, enemies: List<Enemy>): Enemy? {
        val stats = tower.stats
        val towerCx = tower.gridCol * cellSize + cellSize / 2f
        val towerCy = tower.gridRow * cellSize + cellSize / 2f
        val rangePx = stats.range * cellSize

        val inRange = enemies.filter { e ->
            if (e.isDead || e.hasReachedEnd) return@filter false
            val dx = e.pixelX - towerCx
            val dy = e.pixelY - towerCy
            sqrt(dx * dx + dy * dy) <= rangePx
        }

        return when (tower.type) {
            // Sniper: target the enemy furthest along the path (closest to end)
            TowerType.SNIPER -> inRange.maxByOrNull { it.pathProgress }
            // Frost/Bomb: target highest-hp cluster (closest to tower = center of cluster)
            TowerType.FROST, TowerType.BOMB -> inRange.maxByOrNull { it.hp }
            // Default: closest enemy to tower
            else -> inRange.minByOrNull { e ->
                val dx = e.pixelX - towerCx
                val dy = e.pixelY - towerCy
                dx * dx + dy * dy
            }
        }
    }
}
