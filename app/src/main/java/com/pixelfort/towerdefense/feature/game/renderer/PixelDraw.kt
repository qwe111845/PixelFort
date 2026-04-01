package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Pixel-art drawing helpers. All coordinates are in "pixel units" where
 * 1 unit = [scale] actual pixels.  Call with cx/cy = cell center.
 */
object PixelDraw {

    /** Draw a rectangle in pixel-art units (origin top-left of the pixel grid) */
    fun DrawScope.pixel(
        originX: Float, originY: Float,
        col: Int, row: Int,
        w: Int, h: Int,
        color: Color,
        scale: Float
    ) {
        drawRect(
            color = color,
            topLeft = Offset(originX + col * scale, originY + row * scale),
            size = Size(w * scale, h * scale)
        )
    }

    /** Draw a 8×8 pixel sprite defined as a grid of color indices.
     *  palette[0] = transparent, palette[1..n] = colors */
    fun DrawScope.sprite8(
        cx: Float, cy: Float,
        scale: Float,
        grid: Array<IntArray>,
        palette: Array<Color>
    ) {
        val ox = cx - 4 * scale
        val oy = cy - 4 * scale
        grid.forEachIndexed { row, cols ->
            cols.forEachIndexed { col, idx ->
                if (idx == 0) return@forEachIndexed
                drawRect(
                    color = palette[idx],
                    topLeft = Offset(ox + col * scale, oy + row * scale),
                    size = Size(scale, scale)
                )
            }
        }
    }
}
