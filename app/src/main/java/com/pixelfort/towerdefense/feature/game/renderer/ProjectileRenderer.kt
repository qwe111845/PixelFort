package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.TowerEffect

object ProjectileRenderer {

    fun DrawScope.drawProjectiles(projectiles: List<Projectile>) {
        for (proj in projectiles) {
            val (color, radius) = when (proj.effect) {
                is TowerEffect.AoeSplash, is TowerEffect.AoeWithSlow ->
                    Color(0xFFFF8F00) to 7f
                is TowerEffect.Slow ->
                    Color(0xFF80DEEA) to 5f
                is TowerEffect.Poison ->
                    Color(0xFF9CCC65) to 5f
                is TowerEffect.Chain ->
                    Color(0xFFFFF176) to 6f
                else ->
                    Color.White to 4f
            }
            drawCircle(color, radius, Offset(proj.pixelX, proj.pixelY))
            // Inner bright core
            drawCircle(Color.White.copy(alpha = 0.8f), radius * 0.4f, Offset(proj.pixelX, proj.pixelY))
        }
    }
}
