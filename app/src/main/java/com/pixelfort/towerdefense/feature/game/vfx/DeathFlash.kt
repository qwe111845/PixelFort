package com.pixelfort.towerdefense.feature.game.vfx

/**
 * Brief white flash at an enemy's position for 60ms just before death burst particles.
 */
data class DeathFlash(
    val x: Float,
    val y: Float,
    val size: Float,      // enemy type visual scale
    val remainingMs: Long,
    val maxMs: Long = FLASH_DURATION_MS
) {
    val isDead: Boolean get() = remainingMs <= 0

    fun update(deltaMs: Long): DeathFlash =
        copy(remainingMs = (remainingMs - deltaMs).coerceAtLeast(0L))

    companion object {
        const val FLASH_DURATION_MS = 60L
    }
}
