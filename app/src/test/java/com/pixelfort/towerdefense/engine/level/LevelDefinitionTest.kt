package com.pixelfort.towerdefense.engine.level

import com.pixelfort.towerdefense.engine.model.CellType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LevelDefinitionTest {

    @Test
    fun `Level1 has valid map dimensions`() {
        val level = Levels.level1
        assertEquals(12, level.map.rows)
        assertEquals(8, level.map.cols)
    }

    @Test
    fun `Level1 path waypoints start and end are PATH cells`() {
        val level = Levels.level1
        val start = level.map.startPoint
        val end = level.map.endPoint
        assertEquals(CellType.PATH, level.map.getCellType(start.row, start.col))
        assertEquals(CellType.PATH, level.map.getCellType(end.row, end.col))
    }

    @Test
    fun `Level1 has 5 waves`() {
        assertEquals(5, Levels.level1.waves.size)
    }

    @Test
    fun `Level1 waves have increasing wave numbers`() {
        val numbers = Levels.level1.waves.map { it.waveNumber }
        assertEquals(listOf(1, 2, 3, 4, 5), numbers)
    }

    @Test
    fun `Level1 starting gold is 200`() {
        assertEquals(200, Levels.level1.startingGold)
    }

    @Test
    fun `Level1 starting lives is 20`() {
        assertEquals(20, Levels.level1.startingLives)
    }

    @Test
    fun `Level1 has buildable cells for tower placement`() {
        val buildableCount = Levels.level1.map.grid.flatten().count { it == CellType.BUILDABLE }
        assertTrue(buildableCount > 0, "Level must have buildable cells")
    }

    @Test
    fun `getById returns correct level`() {
        val level = Levels.getById(1)
        assertEquals("Green Meadow", level.name)
    }
}
