package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.pixelfort.towerdefense.engine.model.ActiveCombo
import com.pixelfort.towerdefense.engine.model.ComboType
import com.pixelfort.towerdefense.engine.model.Tower

/**
 * SPEC-028: Draws glowing colored lines between combo tower pairs.
 */
object ComboRenderer {

    private val comboColors = mapOf(
        ComboType.SHATTER_ICE   to Color(0xFF80D8FF),   // icy blue
        ComboType.TOXIC_BLAST   to Color(0xFFB2FF59),   // toxic green
        ComboType.CROSSFIRE     to Color(0xFFFFAB40),   // orange
        ComboType.INFERNO       to Color(0xFFFF5252),   // fiery red
        ComboType.ARCANE_STORM  to Color(0xFFB388FF),   // arcane purple
        ComboType.FROSTBITE     to Color(0xFF18FFFF)    // frost cyan
    )

    fun DrawScope.drawCombos(
        activeCombos: List<ActiveCombo>,
        towers: List<Tower>,
        cellSize: Float,
        elapsedMs: Long = 0L
    ) {
        if (activeCombos.isEmpty()) return

        val towerById = towers.associateBy { it.id }
        // Pulse alpha for glow effect
        val pulse = 0.4f + 0.3f * kotlin.math.sin(elapsedMs / 400.0).toFloat()

        for (combo in activeCombos) {
            val towerA = towerById[combo.towerIdA] ?: continue
            val towerB = towerById[combo.towerIdB] ?: continue

            val color = comboColors[combo.comboType] ?: Color.White

            val centerA = Offset(
                (towerA.gridCol + 0.5f) * cellSize,
                (towerA.gridRow + 0.5f) * cellSize
            )
            val centerB = Offset(
                (towerB.gridCol + 0.5f) * cellSize,
                (towerB.gridRow + 0.5f) * cellSize
            )

            // Outer glow line
            drawLine(
                color = color.copy(alpha = pulse * 0.5f),
                start = centerA,
                end = centerB,
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
            // Inner bright line
            drawLine(
                color = color.copy(alpha = pulse),
                start = centerA,
                end = centerB,
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }
    }
}
