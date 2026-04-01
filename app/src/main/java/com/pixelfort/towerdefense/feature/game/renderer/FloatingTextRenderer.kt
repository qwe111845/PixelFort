package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pixelfort.towerdefense.feature.game.vfx.FloatingText

object FloatingTextRenderer {

    fun DrawScope.drawFloatingTexts(
        texts: List<FloatingText>,
        textMeasurer: TextMeasurer
    ) {
        for (ft in texts) {
            if (ft.isDead) continue
            val style = TextStyle(
                color = ft.color.copy(alpha = ft.alpha),
                fontSize = ft.fontSize.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = ft.alpha * 0.8f),
                    offset = Offset(1f, 1f),
                    blurRadius = 2f
                )
            )
            val measured = textMeasurer.measure(ft.text, style)
            val x = if (ft.centered) {
                (size.width - measured.size.width) / 2f
            } else {
                ft.x - measured.size.width / 2f
            }
            val y = if (ft.centered) {
                size.height * 0.35f + ft.y
            } else {
                ft.y - measured.size.height / 2f
            }
            drawText(measured, topLeft = Offset(x, y))
        }
    }
}
