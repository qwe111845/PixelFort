package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.pixelfort.towerdefense.feature.game.vfx.AmbientParticle
import com.pixelfort.towerdefense.feature.game.vfx.AmbientType
import com.pixelfort.towerdefense.feature.game.vfx.Particle
import com.pixelfort.towerdefense.feature.game.vfx.ParticleType

object VfxRenderer {

    fun DrawScope.drawAmbientParticles(particles: List<AmbientParticle>) {
        for (p in particles) {
            val color = p.color.copy(alpha = p.alpha)
            val half = p.size / 2f
            when (p.type) {
                AmbientType.FIREFLY -> {
                    // Glow circle for firefly
                    drawCircle(color.copy(alpha = p.alpha * 0.3f), half * 3f, Offset(p.x, p.y))
                    drawCircle(color, half, Offset(p.x, p.y))
                }
                AmbientType.SAND_WISP -> {
                    drawRect(color, Offset(p.x - half, p.y - half * 0.4f), Size(p.size, p.size * 0.4f))
                }
                AmbientType.SNOWFLAKE -> {
                    drawCircle(color, half, Offset(p.x, p.y))
                }
            }
        }
    }

    fun DrawScope.drawParticles(particles: List<Particle>) {
        for (p in particles) {
            val color = p.color.copy(alpha = p.color.alpha * p.alpha)
            val half = p.size / 2f
            when (p.type) {
                ParticleType.SQUARE -> drawRect(
                    color, Offset(p.x - half, p.y - half), Size(p.size, p.size)
                )
                ParticleType.CIRCLE -> drawCircle(color, half, Offset(p.x, p.y))
                ParticleType.STAR   -> {
                    // 2×2 cross star
                    drawRect(color, Offset(p.x - half, p.y - half * 0.4f), Size(p.size, p.size * 0.4f))
                    drawRect(color, Offset(p.x - half * 0.4f, p.y - half), Size(p.size * 0.4f, p.size))
                }
            }
        }
    }
}
