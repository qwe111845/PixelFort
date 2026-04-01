package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color

data class Particle(
    val id: Int,
    val x: Float,
    val y: Float,
    val vx: Float,          // velocity x pixels/sec
    val vy: Float,          // velocity y pixels/sec
    val lifeMs: Long,       // remaining lifetime
    val maxLifeMs: Long,
    val color: Color,
    val size: Float,
    val type: ParticleType = ParticleType.SQUARE
) {
    val alpha: Float get() = (lifeMs.toFloat() / maxLifeMs).coerceIn(0f, 1f)
    val isDead: Boolean get() = lifeMs <= 0
}

enum class ParticleType { SQUARE, CIRCLE, STAR }
