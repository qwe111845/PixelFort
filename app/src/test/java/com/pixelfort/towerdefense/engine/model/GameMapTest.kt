package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GameMapTest {

    private fun createSimpleMap(): GameMap {
        // 3x3 map with path from (0,0) to (0,2) across top row
        val grid = listOf(
            listOf(CellType.PATH, CellType.PATH, CellType.PATH),
            listOf(CellType.BUILDABLE, CellType.BUILDABLE, CellType.BUILDABLE),
            listOf(CellType.BLOCKED, CellType.BUILDABLE, CellType.BLOCKED)
        )
        val waypoints = listOf(
            GridPoint(0, 0),
            GridPoint(0, 1),
            GridPoint(0, 2)
        )
        return GameMap(rows = 3, cols = 3, grid = grid, pathWaypoints = waypoints)
    }

    @Nested
    inner class Construction {
        @Test
        fun `GameMap stores dimensions correctly`() {
            val map = createSimpleMap()
            assertEquals(3, map.rows)
            assertEquals(3, map.cols)
        }

        @Test
        fun `GameMap grid has correct dimensions`() {
            val map = createSimpleMap()
            assertEquals(3, map.grid.size)
            assertEquals(3, map.grid[0].size)
        }

        @Test
        fun `GameMap throws if grid dimensions don't match declared rows and cols`() {
            val grid = listOf(
                listOf(CellType.PATH, CellType.PATH)
            )
            assertThrows<IllegalArgumentException> {
                GameMap(rows = 2, cols = 2, grid = grid, pathWaypoints = emptyList())
            }
        }
    }

    @Nested
    inner class CellAccess {
        @Test
        fun `getCellType returns correct type for valid position`() {
            val map = createSimpleMap()
            assertEquals(CellType.PATH, map.getCellType(0, 0))
            assertEquals(CellType.BUILDABLE, map.getCellType(1, 1))
            assertEquals(CellType.BLOCKED, map.getCellType(2, 0))
        }

        @Test
        fun `isInBounds returns true for valid position`() {
            val map = createSimpleMap()
            assertTrue(map.isInBounds(0, 0))
            assertTrue(map.isInBounds(2, 2))
        }

        @Test
        fun `isInBounds returns false for out of bounds position`() {
            val map = createSimpleMap()
            assertFalse(map.isInBounds(-1, 0))
            assertFalse(map.isInBounds(0, -1))
            assertFalse(map.isInBounds(3, 0))
            assertFalse(map.isInBounds(0, 3))
        }

        @Test
        fun `isBuildable returns true for BUILDABLE cell`() {
            val map = createSimpleMap()
            assertTrue(map.isBuildable(1, 1))
        }

        @Test
        fun `isBuildable returns false for PATH cell`() {
            val map = createSimpleMap()
            assertFalse(map.isBuildable(0, 0))
        }

        @Test
        fun `isBuildable returns false for out of bounds`() {
            val map = createSimpleMap()
            assertFalse(map.isBuildable(-1, 0))
        }
    }

    @Nested
    inner class PathWaypoints {
        @Test
        fun `pathWaypoints returns the defined waypoints`() {
            val map = createSimpleMap()
            assertEquals(3, map.pathWaypoints.size)
            assertEquals(GridPoint(0, 0), map.pathWaypoints.first())
            assertEquals(GridPoint(0, 2), map.pathWaypoints.last())
        }

        @Test
        fun `startPoint returns first waypoint`() {
            val map = createSimpleMap()
            assertEquals(GridPoint(0, 0), map.startPoint)
        }

        @Test
        fun `endPoint returns last waypoint`() {
            val map = createSimpleMap()
            assertEquals(GridPoint(0, 2), map.endPoint)
        }

        @Test
        fun `totalPathSegments returns waypoints size minus 1`() {
            val map = createSimpleMap()
            assertEquals(2, map.totalPathSegments)
        }
    }
}
