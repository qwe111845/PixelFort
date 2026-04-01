package com.pixelfort.towerdefense.engine.model

import kotlin.math.sqrt

data class GridPoint(val row: Int, val col: Int) {
    fun distanceTo(other: GridPoint): Float {
        val dr = (other.row - row).toFloat()
        val dc = (other.col - col).toFloat()
        return sqrt(dr * dr + dc * dc)
    }
}

enum class CellType(val isBuildable: Boolean) {
    PATH(isBuildable = false),
    BUILDABLE(isBuildable = true),
    BLOCKED(isBuildable = false)
}

data class GridCell(val row: Int, val col: Int, val type: CellType) {
    val isBuildable: Boolean get() = type.isBuildable
}
