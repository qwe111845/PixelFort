package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sqrt

class TowerTargetingSystem(private val cellSize: Float) {

    data class Result(
        val updatedTowers: List<Tower>,
        val projectiles: List<Projectile>
    )

    private var nextProjectileId = 1

    companion object {
        /** Turret rotation speed in radians per second. */
        const val ROTATION_SPEED = 10f

        /**
         * Smoothly interpolate an angle toward a target, wrapping around -PI..PI.
         * [maxDelta] is the maximum change in radians this tick.
         */
        fun lerpAngle(current: Float, target: Float, maxDelta: Float): Float {
            var diff = target - current
            // Normalize to -PI..PI
            while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
            while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()
            return if (abs(diff) <= maxDelta) target
            else current + if (diff > 0) maxDelta else -maxDelta
        }
    }

    /**
     * @param rangeMult    SPEC-032 wave-event range multiplier (default 1.0)
     * @param fireRateMult SPEC-032 wave-event fire-rate multiplier (< 1 = faster, default 1.0)
     */
    fun update(
        towers: List<Tower>,
        enemies: List<Enemy>,
        deltaMs: Long,
        rangeMult: Float = 1f,
        fireRateMult: Float = 1f
    ): Result {
        val newProjectiles = mutableListOf<Projectile>()
        val deltaSec = deltaMs / 1000f
        val maxAngleDelta = ROTATION_SPEED * deltaSec

        val updatedTowers = towers.map { tower ->
            val reducedCooldown = max(0L, tower.cooldownRemainingMs - deltaMs)
            var towerReady = tower.copy(cooldownRemainingMs = reducedCooldown)

            // Find target for rotation (even if on cooldown)
            val target = selectTarget(towerReady, enemies, rangeMult)

            // Update facing angle toward target (or keep last direction)
            if (target != null) {
                val towerCx = tower.gridCol * cellSize + cellSize / 2f
                val towerCy = tower.gridRow * cellSize + cellSize / 2f
                val desiredAngle = atan2(target.pixelY - towerCy, target.pixelX - towerCx)
                val newAngle = lerpAngle(towerReady.facingAngle, desiredAngle, maxAngleDelta)
                towerReady = towerReady.copy(facingAngle = newAngle)
            }
            // When no target: tower keeps its current facingAngle (last direction)

            if (reducedCooldown > 0) return@map towerReady

            if (target == null) return@map towerReady

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
            // SPEC-032: apply fire rate multiplier from wave events
            towerReady.copy(cooldownRemainingMs = (stats.fireRateMs * fireRateMult).toLong())
        }

        return Result(updatedTowers, newProjectiles)
    }

    private fun selectTarget(tower: Tower, enemies: List<Enemy>, rangeMult: Float = 1f): Enemy? {
        val stats = tower.stats
        val towerCx = tower.gridCol * cellSize + cellSize / 2f
        val towerCy = tower.gridRow * cellSize + cellSize / 2f
        val rangePx = stats.range * cellSize * rangeMult

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
