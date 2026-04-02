package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.pixelfort.towerdefense.feature.game.vfx.AmbientParticle
import com.pixelfort.towerdefense.feature.game.vfx.AmbientType
import com.pixelfort.towerdefense.feature.game.vfx.Particle
import com.pixelfort.towerdefense.feature.game.vfx.ParticleType
import com.pixelfort.towerdefense.feature.game.vfx.SellEffect

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

    /**
     * SPEC-030: Draw sell dissolve effects (fading gold circle with scale shrink).
     */
    fun DrawScope.drawSellEffects(effects: List<SellEffect>) {
        for (effect in effects) {
            val radius = 20f * effect.scale
            val goldColor = androidx.compose.ui.graphics.Color(0xFFFFD700).copy(alpha = effect.alpha)
            // Outer glow
            drawCircle(
                goldColor.copy(alpha = effect.alpha * 0.3f),
                radius * 2f,
                Offset(effect.x, effect.y)
            )
            // Inner circle
            drawCircle(goldColor, radius, Offset(effect.x, effect.y))
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
