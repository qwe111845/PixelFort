package com.pixelfort.towerdefense.engine.model

data class GameMap(
    val rows: Int,
    val cols: Int,
    val grid: List<List<CellType>>,
    val pathWaypoints: List<GridPoint>
) {
    init {
        require(grid.size == rows) {
            "Grid row count ${grid.size} does not match declared rows $rows"
        }
        grid.forEachIndexed { index, row ->
            require(row.size == cols) {
                "Grid row $index has ${row.size} cols, expected $cols"
            }
        }
    }

    val startPoint: GridPoint get() = pathWaypoints.first()
    val endPoint: GridPoint get() = pathWaypoints.last()
    val totalPathSegments: Int get() = pathWaypoints.size - 1

    fun getCellType(row: Int, col: Int): CellType = grid[row][col]

    fun isInBounds(row: Int, col: Int): Boolean =
        row in 0 until rows && col in 0 until cols

    fun isBuildable(row: Int, col: Int): Boolean =
        isInBounds(row, col) && grid[row][col].isBuildable
}
