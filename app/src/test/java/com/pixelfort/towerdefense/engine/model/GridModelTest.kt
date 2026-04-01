package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GridModelTest {

    @Nested
    inner class GridPointTest {
        @Test
        fun `GridPoint stores row and col correctly`() {
            val point = GridPoint(row = 3, col = 5)
            assertEquals(3, point.row)
            assertEquals(5, point.col)
        }

        @Test
        fun `GridPoint distanceTo returns correct Euclidean distance`() {
            val a = GridPoint(0, 0)
            val b = GridPoint(3, 4)
            assertEquals(5.0f, a.distanceTo(b), 0.001f)
        }

        @Test
        fun `GridPoint distanceTo self is zero`() {
            val a = GridPoint(2, 3)
            assertEquals(0.0f, a.distanceTo(a), 0.001f)
        }
    }

    @Nested
    inner class CellTypeTest {
        @Test
        fun `PATH cell is not buildable`() {
            assertFalse(CellType.PATH.isBuildable)
        }

        @Test
        fun `BUILDABLE cell is buildable`() {
            assertTrue(CellType.BUILDABLE.isBuildable)
        }

        @Test
        fun `BLOCKED cell is not buildable`() {
            assertFalse(CellType.BLOCKED.isBuildable)
        }
    }

    @Nested
    inner class GridCellTest {
        @Test
        fun `GridCell stores position and type`() {
            val cell = GridCell(row = 1, col = 2, type = CellType.BUILDABLE)
            assertEquals(1, cell.row)
            assertEquals(2, cell.col)
            assertEquals(CellType.BUILDABLE, cell.type)
        }

        @Test
        fun `GridCell isBuildable delegates to CellType`() {
            val buildable = GridCell(0, 0, CellType.BUILDABLE)
            val path = GridCell(0, 0, CellType.PATH)
            assertTrue(buildable.isBuildable)
            assertFalse(path.isBuildable)
        }
    }
}
