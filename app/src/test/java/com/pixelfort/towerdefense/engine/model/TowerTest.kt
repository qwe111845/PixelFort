package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TowerTest {

    @Nested
    inner class TowerTypeStats {
        @Test
        fun `ARCHER base stats are correct`() {
            val stats = TowerType.ARCHER.statsForLevel(1)
            assertEquals(20, stats.damage)
            assertEquals(3.5f, stats.range)
            assertEquals(800L, stats.fireRateMs)
            assertEquals(100, stats.cost)
        }

        @Test
        fun `CANNON base stats are correct`() {
            val stats = TowerType.CANNON.statsForLevel(1)
            assertEquals(40, stats.damage)
            assertEquals(2.5f, stats.range)
            assertEquals(1500L, stats.fireRateMs)
            assertEquals(150, stats.cost)
        }

        @Test
        fun `MAGIC base stats are correct`() {
            val stats = TowerType.MAGIC.statsForLevel(1)
            assertEquals(10, stats.damage)
            assertEquals(3.0f, stats.range)
            assertEquals(1000L, stats.fireRateMs)
            assertEquals(200, stats.cost)
        }

        @Test
        fun `level 2 stats scale damage by 50 percent`() {
            val level1 = TowerType.ARCHER.statsForLevel(1)
            val level2 = TowerType.ARCHER.statsForLevel(2)
            assertEquals((level1.damage * 1.5f).toInt(), level2.damage)
        }

        @Test
        fun `level 3 stats scale damage further`() {
            val level1 = TowerType.ARCHER.statsForLevel(1)
            val level3 = TowerType.ARCHER.statsForLevel(3)
            assertEquals((level1.damage * 2.25f).toInt(), level3.damage)
        }

        @Test
        fun `level 2 range increases by 0_3 cells`() {
            val level1 = TowerType.ARCHER.statsForLevel(1)
            val level2 = TowerType.ARCHER.statsForLevel(2)
            assertEquals(level1.range + 0.3f, level2.range, 0.001f)
        }

        @Test
        fun `fire rate decreases by 10 percent per level`() {
            val level1 = TowerType.ARCHER.statsForLevel(1)
            val level2 = TowerType.ARCHER.statsForLevel(2)
            assertEquals((level1.fireRateMs * 0.9f).toLong(), level2.fireRateMs)
        }
    }

    @Nested
    inner class TowerEntity {
        @Test
        fun `Tower stores basic properties`() {
            val tower = Tower(
                id = 1,
                type = TowerType.ARCHER,
                level = 1,
                gridRow = 3,
                gridCol = 4
            )
            assertEquals(1, tower.id)
            assertEquals(TowerType.ARCHER, tower.type)
            assertEquals(1, tower.level)
            assertEquals(3, tower.gridRow)
            assertEquals(4, tower.gridCol)
        }

        @Test
        fun `Tower stats delegates to TowerType for current level`() {
            val tower = Tower(id = 1, type = TowerType.CANNON, level = 2, gridRow = 0, gridCol = 0)
            val expected = TowerType.CANNON.statsForLevel(2)
            assertEquals(expected, tower.stats)
        }

        @Test
        fun `Tower upgrade cost equals base cost times current level`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 0, gridCol = 0)
            assertEquals(100, tower.upgradeCost) // 100 * 1
            val tower2 = tower.copy(level = 2)
            assertEquals(200, tower2.upgradeCost) // 100 * 2
        }

        @Test
        fun `Tower sell value is 60 percent of total invested cost`() {
            // Level 1: invested = 100
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 0, gridCol = 0)
            assertEquals(60, tower.sellValue) // 100 * 0.6

            // Level 2: invested = 100 (build) + 100 (upgrade from 1 to 2) = 200
            val tower2 = tower.copy(level = 2)
            assertEquals(120, tower2.sellValue) // 200 * 0.6
        }

        @Test
        fun `Tower maxLevel is 3`() {
            assertEquals(3, Tower.MAX_LEVEL)
        }

        @Test
        fun `Tower isMaxLevel returns true at level 3`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 3, gridRow = 0, gridCol = 0)
            assertTrue(tower.isMaxLevel)
        }

        @Test
        fun `Tower isMaxLevel returns false below level 3`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 2, gridRow = 0, gridCol = 0)
            assertFalse(tower.isMaxLevel)
        }

        private fun assertTrue(value: Boolean) = org.junit.jupiter.api.Assertions.assertTrue(value)
        private fun assertFalse(value: Boolean) = org.junit.jupiter.api.Assertions.assertFalse(value)
    }
}
