package com.pixelfort.towerdefense.engine.model

import kotlin.math.sqrt

data class Projectile(
    val id: Int,
    val sourceTowerId: Int,
    val targetEnemyId: Int,
    val damage: Int,
    val pixelX: Float,
    val pixelY: Float,
    val speed: Float = 8f,
    val effect: TowerEffect = TowerEffect.None,
    val chainSourceIds: Set<Int> = emptySet()   // enemy IDs already hit in chain
) {
    fun hasReachedTarget(targetX: Float, targetY: Float, cellSize: Float): Boolean {
        val dx = targetX - pixelX
        val dy = targetY - pixelY
        val distance = sqrt(dx * dx + dy * dy)
        return distance <= cellSize * 0.2f
    }
}
