package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.pixelfort.towerdefense.engine.model.CellType
import com.pixelfort.towerdefense.engine.model.GameMap

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
    }
}
