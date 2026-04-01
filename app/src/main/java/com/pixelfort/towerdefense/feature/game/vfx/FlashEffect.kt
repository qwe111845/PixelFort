package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color

data class FlashEffect(
    val color: Color = Color.Transparent,
    val remainingMs: Long = 0L,
    val maxMs: Long = 0L
) {
    val isActive: Boolean get() = remainingMs > 0
    val alpha: Float get() = if (maxMs > 0) (remainingMs.toFloat() / maxMs).coerceIn(0f, 0.4f) else 0f

    fun update(deltaMs: Long): FlashEffect {
        if (!isActive) return this
        return copy(remainingMs = remainingMs - deltaMs)
    }

    fun trigger(newColor: Color, durationMs: Long): FlashEffect {
        return copy(color = newColor, remainingMs = durationMs, maxMs = durationMs)
    }

    companion object {
        val NONE = FlashEffect()
    }
}
