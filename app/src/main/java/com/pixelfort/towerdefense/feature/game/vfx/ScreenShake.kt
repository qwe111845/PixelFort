package com.pixelfort.towerdefense.feature.game.vfx

import kotlin.random.Random

data class ScreenShake(
    val intensity: Float = 0f,
    val remainingMs: Long = 0L,
    val decayRate: Float = 0.85f
) {
    val isActive: Boolean get() = remainingMs > 0 && intensity > 0.5f

    val offsetX: Float get() = if (isActive) (Random.nextFloat() - 0.5f) * 2f * intensity else 0f
    val offsetY: Float get() = if (isActive) (Random.nextFloat() - 0.5f) * 2f * intensity else 0f

    fun update(deltaMs: Long): ScreenShake {
        if (!isActive) return copy(intensity = 0f, remainingMs = 0)
        return copy(
            intensity = intensity * decayRate,
            remainingMs = remainingMs - deltaMs
        )
    }

    fun trigger(newIntensity: Float, durationMs: Long): ScreenShake {
        // Only replace if new shake is stronger
        if (newIntensity <= intensity && isActive) return this
        return copy(intensity = newIntensity, remainingMs = durationMs)
    }

    companion object {
        val IDLE = ScreenShake()
    }
}
