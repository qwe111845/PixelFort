package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color

data class FloatingText(
    val id: Int,
    val x: Float,
    val y: Float,
    val text: String,
    val color: Color,
    val fontSize: Float,
    val lifeMs: Long,
    val maxLifeMs: Long,
    val vy: Float = -80f,
    /** If true, renderer draws at screen center instead of (x, y) */
    val centered: Boolean = false
) {
    val alpha: Float get() = (lifeMs.toFloat() / maxLifeMs).coerceIn(0f, 1f)
    val isDead: Boolean get() = lifeMs <= 0
}
