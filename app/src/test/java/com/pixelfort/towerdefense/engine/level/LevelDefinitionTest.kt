package com.pixelfort.towerdefense.engine.level

import com.pixelfort.towerdefense.engine.model.CellType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.abs

class LevelDefinitionTest {

    // ──────────────────────────────────────────────
    // Helper: validates path integrity for any level
    // ──────────────────────────────────────────────
    private fun assertPathIntegrity(level: LevelDefinition) {
        val map = level.map
        val waypoints = map.pathWaypoints

        // 1. Every waypoint must be on a PATH cell
        waypoints.forEachIndexed { i, wp ->
            assertTrue(
                map.isInBounds(wp.row, wp.col),
                "Waypoint $i (${wp.row},${wp.col}) is out of bounds"
            )
            assertEquals(
                CellType.PATH,
                map.getCellType(wp.row, wp.col),
                "Waypoint $i (${wp.row},${wp.col}) is not a PATH cell (is ${map.getCellType(wp.row, wp.col)})"
            )
        }

        // 2. Consecutive waypoints must be axis-aligned (same row or same col, no diagonals)
        for (i in 0 until waypoints.size - 1) {
            val from = waypoints[i]
            val to = waypoints[i + 1]
            assertTrue(
                from.row == to.row || from.col == to.col,
                "Waypoints $i→${i+1}: (${from.row},${from.col})→(${to.row},${to.col}) are diagonal — path must be axis-aligned"
            )
        }

        // 3. All cells between consecutive waypoints must be PATH
        for (i in 0 until waypoints.size - 1) {
            val from = waypoints[i]
            val to = waypoints[i + 1]
            if (from.row == to.row) {
                // Horizontal segment
                val minCol = minOf(from.col, to.col)
                val maxCol = maxOf(from.col, to.col)
                for (c in minCol..maxCol) {
                    assertEquals(
                        CellType.PATH, map.getCellType(from.row, c),
                        "Cell (${from.row},$c) between waypoints $i→${i+1} should be PATH"
                    )
                }
            } else {
                // Vertical segment
                val minRow = minOf(from.row, to.row)
                val maxRow = maxOf(from.row, to.row)
                for (r in minRow..maxRow) {
                    assertEquals(
                        CellType.PATH, map.getCellType(r, from.col),
                        "Cell ($r,${from.col}) between waypoints $i→${i+1} should be PATH"
                    )
                }
            }
        }
    }

    @Nested
    inner class Level1Tests {
        private val level = Levels.level1

        @Test fun `valid map dimensions`() {
            assertEquals(12, level.map.rows)
            assertEquals(8, level.map.cols)
        }

        @Test fun `path waypoints are all on PATH cells and axis-aligned`() {
            assertPathIntegrity(level)
        }

        @Test fun `has 5 waves`() = assertEquals(5, level.waves.size)
        @Test fun `starting gold is 200`() = assertEquals(200, level.startingGold)
        @Test fun `starting lives is 20`() = assertEquals(20, level.startingLives)

        @Test fun `has buildable cells`() {
            assertTrue(level.map.grid.flatten().count { it == CellType.BUILDABLE } > 0)
        }
    }

    @Nested
    inner class Level2Tests {
        private val level = Levels.level2

        @Test fun `valid map dimensions`() {
            assertEquals(14, level.map.rows)
            assertEquals(9, level.map.cols)
        }

        @Test fun `path waypoints are all on PATH cells and axis-aligned`() {
            assertPathIntegrity(level)
        }

        @Test fun `has 6 waves`() = assertEquals(6, level.waves.size)
        @Test fun `starting gold is 250`() = assertEquals(250, level.startingGold)

        @Test fun `has buildable cells`() {
            assertTrue(level.map.grid.flatten().count { it == CellType.BUILDABLE } > 0)
        }
    }

    @Nested
    inner class Level3Tests {
        private val level = Levels.level3

        @Test fun `valid map dimensions`() {
            assertEquals(16, level.map.rows)
            assertEquals(10, level.map.cols)
        }

        @Test fun `path waypoints are all on PATH cells and axis-aligned`() {
            assertPathIntegrity(level)
        }

        @Test fun `has 7 waves`() = assertEquals(7, level.waves.size)
        @Test fun `starting gold is 300`() = assertEquals(300, level.startingGold)
        @Test fun `starting lives is 25`() = assertEquals(25, level.startingLives)

        @Test fun `has buildable cells`() {
            assertTrue(level.map.grid.flatten().count { it == CellType.BUILDABLE } > 0)
        }
    }

    @Nested
    inner class Level4Tests {
        private val level = Levels.level4

        @Test fun `valid map dimensions`() {
            assertEquals(14, level.map.rows)
            assertEquals(10, level.map.cols)
        }

        @Test fun `path waypoints are all on PATH cells and axis-aligned`() {
            assertPathIntegrity(level)
        }

        @Test fun `has 8 waves`() = assertEquals(8, level.waves.size)
        @Test fun `starting gold is 350`() = assertEquals(350, level.startingGold)
        @Test fun `starting lives is 25`() = assertEquals(25, level.startingLives)

        @Test fun `has buildable cells`() {
            assertTrue(level.map.grid.flatten().count { it == CellType.BUILDABLE } > 0)
        }

        @Test fun `has lava cell effects`() {
            assertTrue(level.cellEffects.isNotEmpty(), "Level 4 should have lava cell effects")
            assertTrue(
                level.cellEffects.values.all { it is com.pixelfort.towerdefense.engine.model.CellEffect.LavaDamage },
                "All cell effects in level 4 should be LavaDamage"
            )
        }

        @Test fun `all lava cells are on PATH cells`() {
            level.cellEffects.keys.forEach { point ->
                assertEquals(
                    CellType.PATH,
                    level.map.getCellType(point.row, point.col),
                    "Lava cell at (${point.row},${point.col}) must be on a PATH cell"
                )
            }
        }
    }

    @Nested
    inner class Level5Tests {
        private val level = Levels.level5

        @Test fun `valid map dimensions`() {
            assertEquals(16, level.map.rows)
            assertEquals(12, level.map.cols)
        }

        @Test fun `path waypoints are all on PATH cells and axis-aligned`() {
            assertPathIntegrity(level)
        }

        @Test fun `has 10 waves`() = assertEquals(10, level.waves.size)
        @Test fun `starting gold is 400`() = assertEquals(400, level.startingGold)
        @Test fun `starting lives is 30`() = assertEquals(30, level.startingLives)

        @Test fun `has buildable cells`() {
            assertTrue(level.map.grid.flatten().count { it == CellType.BUILDABLE } > 0)
        }

        @Test fun `has teleport cell effect`() {
            assertTrue(level.cellEffects.isNotEmpty(), "Level 5 should have teleport cell effects")
            assertTrue(
                level.cellEffects.values.any { it is com.pixelfort.towerdefense.engine.model.CellEffect.Teleport },
                "Level 5 should have at least one Teleport effect"
            )
        }

        @Test fun `teleport cell is on PATH cell`() {
            level.cellEffects.keys.forEach { point ->
                assertEquals(
                    CellType.PATH,
                    level.map.getCellType(point.row, point.col),
                    "Teleport cell at (${point.row},${point.col}) must be on a PATH cell"
                )
            }
        }

        @Test fun `has double boss in final wave`() {
            val finalWave = level.waves.last()
            val bossCount = finalWave.groups
                .filter { it.enemyType == com.pixelfort.towerdefense.engine.model.EnemyType.BOSS_DRAGON }
                .sumOf { it.count }
            assertTrue(bossCount >= 2, "Final wave should have at least 2 bosses, got $bossCount")
        }
    }

    @Nested
    inner class AllLevels {
        @Test fun `getById returns correct levels`() {
            assertEquals(1, Levels.getById(1).id)
            assertEquals(2, Levels.getById(2).id)
            assertEquals(3, Levels.getById(3).id)
            assertEquals(4, Levels.getById(4).id)
            assertEquals(5, Levels.getById(5).id)
        }

        @Test fun `all levels list has 5 levels`() {
            assertEquals(5, Levels.all.size)
        }

        @Test fun `all levels have valid path integrity`() {
            Levels.all.forEach { assertPathIntegrity(it) }
        }

        @Test fun `all levels entry and exit are PATH cells`() {
            Levels.all.forEach { level ->
                val start = level.map.startPoint
                val end = level.map.endPoint
                assertEquals(CellType.PATH, level.map.getCellType(start.row, start.col))
                assertEquals(CellType.PATH, level.map.getCellType(end.row, end.col))
            }
        }
    }
}
