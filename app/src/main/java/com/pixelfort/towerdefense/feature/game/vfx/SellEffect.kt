package com.pixelfort.towerdefense.feature.game.vfx

/**
 * SPEC-030: Dissolve animation when a tower is sold.
 * Tracks position and age for a 300ms dissolve (fade + scale shrink 1.0 -> 0.5).
 */
data class SellEffect(
    val x: Float,
    val y: Float,
    val elapsedMs: Long = 0L,
    val totalMs: Long = DURATION_MS
) {
    /** Progress 0..1 where 1 = animation complete. */
    val progress: Float get() = (elapsedMs.toFloat() / totalMs).coerceIn(0f, 1f)

    /** Alpha fades from 1 to 0 over the duration. */
    val alpha: Float get() = 1f - progress

    /** Scale shrinks from 1.0 to 0.5 over the duration. */
    val scale: Float get() = 1f - 0.5f * progress

    val isDead: Boolean get() = elapsedMs >= totalMs

    fun update(deltaMs: Long): SellEffect =
        copy(elapsedMs = (elapsedMs + deltaMs).coerceAtMost(totalMs))

    companion object {
        const val DURATION_MS = 300L
    }
}
