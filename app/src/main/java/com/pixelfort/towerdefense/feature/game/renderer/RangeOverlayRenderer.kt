package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import com.pixelfort.towerdefense.engine.model.CellType
import com.pixelfort.towerdefense.engine.model.GameMap
import kotlin.math.sqrt

object RangeOverlayRenderer {

    private val PATH_IN_RANGE_COLOR = Color(0x4000FF00) // green alpha ~0.25
    private val BUILDABLE_IN_RANGE_COLOR = Color(0x2600AAFF) // light blue alpha ~0.15
    private val RANGE_TEXT_COLOR = Color(0xDDFFFFFF)

    /**
     * Compute the Euclidean distance (in cell units) between the center of
     * cell (row, col) and the center of cell (centerRow, centerCol).
     */
    fun cellDistance(centerRow: Int, centerCol: Int, row: Int, col: Int): Float {
        val dr = (row - centerRow).toFloat()
        val dc = (col - centerCol).toFloat()
        return sqrt(dr * dr + dc * dc)
    }

    /**
     * Find the minimum distance (in cell units) from (centerRow, centerCol) to the
     * nearest PATH cell on the map. Returns [Float.MAX_VALUE] if no path cells exist.
     */
    fun minDistanceToPath(centerRow: Int, centerCol: Int, map: GameMap): Float {
        var minDist = Float.MAX_VALUE
        for (r in 0 until map.rows) {
            for (c in 0 until map.cols) {
                if (map.getCellType(r, c) == CellType.PATH) {
                    val d = cellDistance(centerRow, centerCol, r, c)
                    if (d < minDist) minDist = d
                }
            }
        }
        return minDist
    }

    /**
     * Draw colored overlays on cells within [range] of (centerRow, centerCol).
     * - PATH cells get a green tint (alpha 0.25)
     * - BUILDABLE cells get a light blue tint (alpha 0.15)
     * Also draws the range value text at the edge of the range circle.
     */
    fun DrawScope.drawRangeOverlay(
        centerRow: Int,
        centerCol: Int,
        range: Float,
        cellSize: Float,
        gameMap: GameMap,
        textMeasurer: TextMeasurer? = null,
        showRangeText: Boolean = false
    ) {
        for (r in 0 until gameMap.rows) {
            for (c in 0 until gameMap.cols) {
                val dist = cellDistance(centerRow, centerCol, r, c)
                if (dist <= range) {
                    val cellType = gameMap.getCellType(r, c)
                    val overlayColor = when (cellType) {
                        CellType.PATH -> PATH_IN_RANGE_COLOR
                        CellType.BUILDABLE -> BUILDABLE_IN_RANGE_COLOR
                        else -> null
                    }
                    if (overlayColor != null) {
                        drawRect(
                            color = overlayColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }

        // Range text at circle edge (right side)
        if (showRangeText && textMeasurer != null) {
            val cx = centerCol * cellSize + cellSize / 2f
            val cy = centerRow * cellSize + cellSize / 2f
            val rangeLabel = "%.1f".format(range)
            val style = TextStyle(
                color = RANGE_TEXT_COLOR,
                fontSize = 11.sp
            )
            val measured = textMeasurer.measure(rangeLabel, style)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    cx + range * cellSize + 4f,
                    cy - measured.size.height / 2f
                )
            )
        }
    }

    /**
     * Draw a small distance-to-path label near the ghost tower position.
     * Color: green if within range, yellow if close (within range * 1.2), red if out.
     */
    fun DrawScope.drawDistanceToPathText(
        ghostRow: Int,
        ghostCol: Int,
        range: Float,
        cellSize: Float,
        gameMap: GameMap,
        textMeasurer: TextMeasurer
    ) {
        val dist = minDistanceToPath(ghostRow, ghostCol, gameMap)
        if (dist == Float.MAX_VALUE) return

        val label = "%.1f".format(dist)
        val labelColor = when {
            dist <= range -> Color(0xFF4CAF50) // green
            dist <= range * 1.2f -> Color(0xFFFFEB3B) // yellow
            else -> Color(0xFFF44336) // red
        }
        val style = TextStyle(
            color = labelColor,
            fontSize = 10.sp
        )
        val measured = textMeasurer.measure(label, style)
        val cx = ghostCol * cellSize + cellSize / 2f
        val cy = ghostRow * cellSize + cellSize / 2f

        // Draw below-right of the ghost
        drawText(
            textLayoutResult = measured,
            topLeft = Offset(
                cx + cellSize * 0.45f,
                cy + cellSize * 0.35f
            )
        )
    }
}
