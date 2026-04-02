package com.pixelfort.towerdefense.feature.game.renderer

import com.pixelfort.towerdefense.engine.model.CellType
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.GridPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RangeOverlayRendererTest {

    // Helper to build a simple 5x5 map with a path down the middle column
    private fun makeTestMap(): GameMap {
        val grid = List(5) { row ->
            List(5) { col ->
                when {
                    col == 2 -> CellType.PATH
                    else -> CellType.BUILDABLE
                }
            }
        }
        return GameMap(
            rows = 5,
            cols = 5,
            grid = grid,
            pathWaypoints = listOf(GridPoint(0, 2), GridPoint(4, 2))
        )
    }

    @Nested
    inner class CellDistanceTests {
        @Test
        fun `same cell has distance zero`() {
            assertEquals(0f, RangeOverlayRenderer.cellDistance(2, 3, 2, 3))
        }

        @Test
        fun `adjacent horizontal cells have distance 1`() {
            assertEquals(1f, RangeOverlayRenderer.cellDistance(2, 2, 2, 3))
        }

        @Test
        fun `adjacent vertical cells have distance 1`() {
            assertEquals(1f, RangeOverlayRenderer.cellDistance(2, 2, 3, 2))
        }

        @Test
        fun `diagonal cells have correct distance`() {
            val dist = RangeOverlayRenderer.cellDistance(0, 0, 3, 4)
            // sqrt(9 + 16) = 5.0
            assertEquals(5f, dist, 0.001f)
        }

        @Test
        fun `distance is symmetric`() {
            val d1 = RangeOverlayRenderer.cellDistance(1, 2, 4, 3)
            val d2 = RangeOverlayRenderer.cellDistance(4, 3, 1, 2)
            assertEquals(d1, d2, 0.001f)
        }
    }

    @Nested
    inner class MinDistanceToPathTests {
        @Test
        fun `tower on path has distance zero`() {
            val map = makeTestMap()
            val dist = RangeOverlayRenderer.minDistanceToPath(2, 2, map)
            assertEquals(0f, dist, 0.001f)
        }

        @Test
        fun `tower adjacent to path has distance 1`() {
            val map = makeTestMap()
            // col=1 is adjacent to path at col=2
            val dist = RangeOverlayRenderer.minDistanceToPath(2, 1, map)
            assertEquals(1f, dist, 0.001f)
        }

        @Test
        fun `tower two cells from path has distance 2`() {
            val map = makeTestMap()
            // col=0, path at col=2 => distance 2
            val dist = RangeOverlayRenderer.minDistanceToPath(2, 0, map)
            assertEquals(2f, dist, 0.001f)
        }

        @Test
        fun `tower diagonal from nearest path cell`() {
            val map = makeTestMap()
            // (0, 0) to nearest path at (0, 2) => sqrt(0 + 4) = 2.0
            val dist = RangeOverlayRenderer.minDistanceToPath(0, 0, map)
            assertEquals(2f, dist, 0.001f)
        }

        @Test
        fun `map with no path cells returns MAX_VALUE`() {
            val grid = List(3) { List(3) { CellType.BUILDABLE } }
            val map = GameMap(
                rows = 3,
                cols = 3,
                grid = grid,
                pathWaypoints = listOf(GridPoint(0, 0), GridPoint(2, 2))
            )
            val dist = RangeOverlayRenderer.minDistanceToPath(1, 1, map)
            assertEquals(Float.MAX_VALUE, dist)
        }

        @Test
        fun `corner tower finds closest path cell correctly`() {
            val map = makeTestMap()
            // (4, 4) => nearest path at (4, 2), distance = 2.0
            val dist = RangeOverlayRenderer.minDistanceToPath(4, 4, map)
            assertEquals(2f, dist, 0.001f)
        }

        @Test
        fun `distance is always non-negative`() {
            val map = makeTestMap()
            for (r in 0 until map.rows) {
                for (c in 0 until map.cols) {
                    assertTrue(RangeOverlayRenderer.minDistanceToPath(r, c, map) >= 0f)
                }
            }
        }
    }
}
