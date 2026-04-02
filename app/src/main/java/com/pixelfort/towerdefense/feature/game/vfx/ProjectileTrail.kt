package com.pixelfort.towerdefense.feature.game.vfx

import com.pixelfort.towerdefense.engine.model.Projectile

/**
 * A single recorded trail point for a projectile.
 */
data class TrailPoint(
    val x: Float,
    val y: Float,
    val age: Int  // 0 = newest, increases toward oldest
)

/**
 * Tracks the last N positions of each projectile to render fading trails.
 *
 * Call [update] each frame with the current projectile list to record positions.
 * Call [getTrail] to retrieve the trail points for a given projectile ID.
 */
class TrailSystem(private val maxTrailLength: Int = 7) {

    // projectileId -> ring buffer of recent positions (newest first)
    private val trails = mutableMapOf<Int, MutableList<TrailPoint>>()

    /**
     * Record the current position of every active projectile and prune
     * trails for projectiles that no longer exist.
     */
    fun update(projectiles: List<Projectile>) {
        val activeIds = mutableSetOf<Int>()
        for (proj in projectiles) {
            activeIds.add(proj.id)
            val trail = trails.getOrPut(proj.id) { mutableListOf() }
            // Insert newest at front
            trail.add(0, TrailPoint(proj.pixelX, proj.pixelY, 0))
            // Re-index ages
            for (i in trail.indices) {
                trail[i] = trail[i].copy(age = i)
            }
            // Trim to max length
            while (trail.size > maxTrailLength) {
                trail.removeAt(trail.lastIndex)
            }
        }
        // Remove trails for dead/removed projectiles
        trails.keys.retainAll(activeIds)
    }

    /**
     * Returns trail points for the given projectile (newest first),
     * excluding the current position (index 0) so the trail is drawn
     * *behind* the projectile.
     */
    fun getTrail(projectileId: Int): List<TrailPoint> {
        val trail = trails[projectileId] ?: return emptyList()
        return if (trail.size > 1) trail.subList(1, trail.size) else emptyList()
    }

    fun clear() {
        trails.clear()
    }
}
