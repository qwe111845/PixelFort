package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.pixelfort.towerdefense.engine.model.CellType
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.GridPoint

object MapRenderer {

    // Each tile drawn as 2-tone pixel-art checkerboard + border
    private val grassA    = Color(0xFF388E3C)
    private val grassB    = Color(0xFF43A047)
    private val pathA     = Color(0xFF8D6E63)
    private val pathB     = Color(0xFF795548)
    private val blockedA  = Color(0xFF455A64)
    private val blockedB  = Color(0xFF37474F)
    private val gridLine  = Color(0x22000000)

    fun DrawScope.drawMap(map: GameMap, cellSize: Float) {
        for (row in 0 until map.rows) {
            for (col in 0 until map.cols) {
                val x = col * cellSize
                val y = row * cellSize
                val checker = (row + col) % 2 == 0
                val (base, accent) = when (map.getCellType(row, col)) {
                    CellType.BUILDABLE -> if (checker) grassA to grassB  else grassB to grassA
                    CellType.PATH      -> if (checker) pathA  to pathB   else pathB  to pathA
                    CellType.BLOCKED   -> if (checker) blockedA to blockedB else blockedB to blockedA
                }

                // Base fill
                drawRect(base, Offset(x, y), Size(cellSize, cellSize))

                // Pixel-art detail: small accent square in corner
                val d = cellSize * 0.15f
                drawRect(accent, Offset(x + d, y + d), Size(cellSize - d * 2, cellSize - d * 2))

                // Path gets cobblestone lines
                if (map.getCellType(row, col) == CellType.PATH) {
                    drawLine(pathB, Offset(x, y + cellSize * 0.33f),
                        Offset(x + cellSize, y + cellSize * 0.33f), cellSize * 0.04f)
                    drawLine(pathB, Offset(x, y + cellSize * 0.66f),
                        Offset(x + cellSize, y + cellSize * 0.66f), cellSize * 0.04f)
                }

                // Grid line
                drawRect(gridLine, Offset(x, y), Size(cellSize, cellSize),
                    style = Stroke(cellSize * 0.025f))
            }
        }

        // Entry marker (green arrow)
        val entry = map.startPoint
        drawEntryMarker(entry, map, cellSize)

        // Exit marker (red X)
        val exit = map.endPoint
        drawExitMarker(exit, cellSize)
    }

    private fun DrawScope.drawEntryMarker(entry: GridPoint, map: GameMap, cellSize: Float) {
        val cx = entry.col * cellSize + cellSize / 2
        val cy = entry.row * cellSize + cellSize / 2
        val s = cellSize * 0.3f
        val green = Color(0xFF66BB6A)

        // Determine arrow direction based on next waypoint
        val waypoints = map.pathWaypoints
        if (waypoints.size < 2) return
        val next = waypoints[1]
        val dx = next.col - entry.col
        val dy = next.row - entry.row

        // Draw arrow pointing in movement direction
        when {
            dx > 0 -> { // right
                drawLine(green, Offset(cx - s, cy), Offset(cx + s, cy), s * 0.5f)
                drawLine(green, Offset(cx + s * 0.5f, cy - s * 0.6f), Offset(cx + s, cy), s * 0.4f)
                drawLine(green, Offset(cx + s * 0.5f, cy + s * 0.6f), Offset(cx + s, cy), s * 0.4f)
            }
            dx < 0 -> { // left
                drawLine(green, Offset(cx + s, cy), Offset(cx - s, cy), s * 0.5f)
                drawLine(green, Offset(cx - s * 0.5f, cy - s * 0.6f), Offset(cx - s, cy), s * 0.4f)
                drawLine(green, Offset(cx - s * 0.5f, cy + s * 0.6f), Offset(cx - s, cy), s * 0.4f)
            }
            dy > 0 -> { // down
                drawLine(green, Offset(cx, cy - s), Offset(cx, cy + s), s * 0.5f)
                drawLine(green, Offset(cx - s * 0.6f, cy + s * 0.5f), Offset(cx, cy + s), s * 0.4f)
                drawLine(green, Offset(cx + s * 0.6f, cy + s * 0.5f), Offset(cx, cy + s), s * 0.4f)
            }
            else -> { // up
                drawLine(green, Offset(cx, cy + s), Offset(cx, cy - s), s * 0.5f)
                drawLine(green, Offset(cx - s * 0.6f, cy - s * 0.5f), Offset(cx, cy - s), s * 0.4f)
                drawLine(green, Offset(cx + s * 0.6f, cy - s * 0.5f), Offset(cx, cy - s), s * 0.4f)
            }
        }
    }

    private fun DrawScope.drawExitMarker(exit: GridPoint, cellSize: Float) {
        val cx = exit.col * cellSize + cellSize / 2
        val cy = exit.row * cellSize + cellSize / 2
        val s = cellSize * 0.25f
        val red = Color(0xFFEF5350)

        // Draw red X
        drawLine(red, Offset(cx - s, cy - s), Offset(cx + s, cy + s), s * 0.4f)
        drawLine(red, Offset(cx + s, cy - s), Offset(cx - s, cy + s), s * 0.4f)
    }
}
