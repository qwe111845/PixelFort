package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.ComboType
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ComboSystemTest {

    private fun tower(
        id: Int,
        type: TowerType,
        level: Int,
        row: Int,
        col: Int
    ) = Tower(id = id, type = type, level = level, gridRow = row, gridCol = col)

    @Nested
    inner class `detectCombos` {

        @Test
        fun `returns empty when fewer than 2 towers`() {
            val combos = ComboSystem.detectCombos(
                listOf(tower(1, TowerType.FROST, 2, 0, 0))
            )
            assertTrue(combos.isEmpty())
        }

        @Test
        fun `returns empty when towers are not adjacent`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 0, 3) // too far apart
            )
            val combos = ComboSystem.detectCombos(towers)
            assertTrue(combos.isEmpty())
        }

        @Test
        fun `returns empty when both towers are level 1`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 1, 0, 0),
                tower(2, TowerType.LIGHTNING, 1, 0, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertTrue(combos.isEmpty())
        }

        @Test
        fun `returns empty when only one tower is level 2`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 1, 0, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertTrue(combos.isEmpty())
        }

        @Test
        fun `detects SHATTER_ICE combo for adjacent Frost and Lightning at level 2+`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 3, 0, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertEquals(1, combos.size)
            assertEquals(ComboType.SHATTER_ICE, combos[0].comboType)
            assertEquals(1, combos[0].towerIdA)
            assertEquals(2, combos[0].towerIdB)
        }

        @Test
        fun `detects combo for diagonal adjacency`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 1, 1) // diagonal
            )
            val combos = ComboSystem.detectCombos(towers)
            assertEquals(1, combos.size)
            assertEquals(ComboType.SHATTER_ICE, combos[0].comboType)
        }

        @Test
        fun `detects all 6 combo types`() {
            val towers = listOf(
                // SHATTER_ICE
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 0, 1),
                // TOXIC_BLAST
                tower(3, TowerType.POISON, 2, 2, 0),
                tower(4, TowerType.CANNON, 2, 2, 1),
                // CROSSFIRE
                tower(5, TowerType.SNIPER, 2, 4, 0),
                tower(6, TowerType.ARCHER, 2, 4, 1),
                // INFERNO
                tower(7, TowerType.CANNON, 2, 6, 0),
                tower(8, TowerType.BOMB, 2, 6, 1),
                // ARCANE_STORM
                tower(9, TowerType.MAGIC, 2, 8, 0),
                tower(10, TowerType.LIGHTNING, 2, 8, 1),
                // FROSTBITE
                tower(11, TowerType.FROST, 2, 10, 0),
                tower(12, TowerType.POISON, 2, 10, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            val comboTypes = combos.map { it.comboType }.toSet()
            assertEquals(ComboType.entries.toSet(), comboTypes)
        }

        @Test
        fun `tower participates in multiple combos`() {
            // Lightning adjacent to both Frost and Magic
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 0, 1),
                tower(3, TowerType.MAGIC, 2, 0, 2)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertEquals(2, combos.size)
            val types = combos.map { it.comboType }.toSet()
            assertTrue(ComboType.SHATTER_ICE in types)
            assertTrue(ComboType.ARCANE_STORM in types)
        }

        @Test
        fun `non-matching adjacent towers produce no combo`() {
            val towers = listOf(
                tower(1, TowerType.ARCHER, 2, 0, 0),
                tower(2, TowerType.CANNON, 2, 0, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertTrue(combos.isEmpty())
        }

        @Test
        fun `same cell towers are not adjacent`() {
            // Same position but different towers (hypothetical)
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 3, 3),
                tower(2, TowerType.LIGHTNING, 2, 3, 3)
            )
            val combos = ComboSystem.detectCombos(towers)
            // Same cell means rowDiff=0 && colDiff=0, which is excluded
            assertTrue(combos.isEmpty())
        }
    }

    @Nested
    inner class `towerHasCombo` {

        @Test
        fun `returns true when tower has specified combo`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 0, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertTrue(ComboSystem.towerHasCombo(1, ComboType.SHATTER_ICE, combos))
            assertTrue(ComboSystem.towerHasCombo(2, ComboType.SHATTER_ICE, combos))
        }

        @Test
        fun `returns false when tower does not have specified combo`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 0, 1)
            )
            val combos = ComboSystem.detectCombos(towers)
            assertFalse(ComboSystem.towerHasCombo(1, ComboType.CROSSFIRE, combos))
        }
    }

    @Nested
    inner class `combosForTower` {

        @Test
        fun `returns all combos a tower participates in`() {
            val towers = listOf(
                tower(1, TowerType.FROST, 2, 0, 0),
                tower(2, TowerType.LIGHTNING, 2, 0, 1),
                tower(3, TowerType.MAGIC, 2, 0, 2)
            )
            val combos = ComboSystem.detectCombos(towers)
            val lightningCombos = ComboSystem.combosForTower(2, combos)
            assertEquals(2, lightningCombos.size)
        }

        @Test
        fun `returns empty for tower with no combos`() {
            val towers = listOf(
                tower(1, TowerType.ARCHER, 2, 0, 0),
                tower(2, TowerType.CANNON, 2, 5, 5) // not adjacent
            )
            val combos = ComboSystem.detectCombos(towers)
            val archerCombos = ComboSystem.combosForTower(1, combos)
            assertTrue(archerCombos.isEmpty())
        }
    }
}
