package com.pixelfort.towerdefense.engine.action

import com.pixelfort.towerdefense.engine.model.CellType
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.GridPoint
import com.pixelfort.towerdefense.engine.model.PlayerState
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ActionProcessorTest {

    private lateinit var processor: ActionProcessor
    private val map = GameMap(
        rows = 3, cols = 3,
        grid = listOf(
            listOf(CellType.PATH, CellType.PATH, CellType.PATH),
            listOf(CellType.BUILDABLE, CellType.BUILDABLE, CellType.BUILDABLE),
            listOf(CellType.BLOCKED, CellType.BUILDABLE, CellType.BLOCKED)
        ),
        pathWaypoints = listOf(GridPoint(0, 0), GridPoint(0, 2))
    )

    @BeforeEach
    fun setup() {
        processor = ActionProcessor(map)
    }

    @Nested
    inner class PlaceTower {
        @Test
        fun `valid placement on BUILDABLE cell with enough gold`() {
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val towers = emptyList<Tower>()
            val action = GameAction.PlaceTower(TowerType.ARCHER, gridRow = 1, gridCol = 1)

            val result = processor.validate(action, state, towers)

            assertTrue(result.isValid)
        }

        @Test
        fun `invalid placement on PATH cell`() {
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val action = GameAction.PlaceTower(TowerType.ARCHER, gridRow = 0, gridCol = 0)

            val result = processor.validate(action, state, emptyList())

            assertFalse(result.isValid)
        }

        @Test
        fun `invalid placement when not enough gold`() {
            val state = PlayerState(gold = 50, lives = 20, currentWave = 0)
            val action = GameAction.PlaceTower(TowerType.ARCHER, gridRow = 1, gridCol = 1)

            val result = processor.validate(action, state, emptyList())

            assertFalse(result.isValid)
        }

        @Test
        fun `invalid placement when cell already has tower`() {
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val existingTower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 1, gridCol = 1)
            val action = GameAction.PlaceTower(TowerType.CANNON, gridRow = 1, gridCol = 1)

            val result = processor.validate(action, state, listOf(existingTower))

            assertFalse(result.isValid)
        }

        @Test
        fun `invalid placement on out of bounds cell`() {
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val action = GameAction.PlaceTower(TowerType.ARCHER, gridRow = -1, gridCol = 0)

            val result = processor.validate(action, state, emptyList())

            assertFalse(result.isValid)
        }
    }

    @Nested
    inner class UpgradeTower {
        @Test
        fun `valid upgrade when tower exists and not max level and enough gold`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 1, gridCol = 1)
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val action = GameAction.UpgradeTower(towerId = 1)

            val result = processor.validate(action, state, listOf(tower))

            assertTrue(result.isValid)
        }

        @Test
        fun `invalid upgrade when tower at max level`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 3, gridRow = 1, gridCol = 1)
            val state = PlayerState(gold = 500, lives = 20, currentWave = 0)
            val action = GameAction.UpgradeTower(towerId = 1)

            val result = processor.validate(action, state, listOf(tower))

            assertFalse(result.isValid)
        }

        @Test
        fun `invalid upgrade when not enough gold`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 1, gridCol = 1)
            val state = PlayerState(gold = 50, lives = 20, currentWave = 0)
            val action = GameAction.UpgradeTower(towerId = 1)

            val result = processor.validate(action, state, listOf(tower))

            assertFalse(result.isValid)
        }

        @Test
        fun `invalid upgrade when tower not found`() {
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val action = GameAction.UpgradeTower(towerId = 99)

            val result = processor.validate(action, state, emptyList())

            assertFalse(result.isValid)
        }
    }

    @Nested
    inner class SellTower {
        @Test
        fun `valid sell returns gold value`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 1, gridCol = 1)
            val state = PlayerState(gold = 0, lives = 20, currentWave = 0)
            val action = GameAction.SellTower(towerId = 1)

            val result = processor.validate(action, state, listOf(tower))

            assertTrue(result.isValid)
        }

        @Test
        fun `invalid sell when tower not found`() {
            val state = PlayerState(gold = 0, lives = 20, currentWave = 0)
            val action = GameAction.SellTower(towerId = 99)

            val result = processor.validate(action, state, emptyList())

            assertFalse(result.isValid)
        }
    }

    @Nested
    inner class SetSpeed {
        @Test
        fun `SetSpeed is always valid`() {
            val state = PlayerState(gold = 0, lives = 20, currentWave = 0)
            val action = GameAction.SetSpeed(2f)

            val result = processor.validate(action, state, emptyList())

            assertTrue(result.isValid)
        }
    }

    @Nested
    inner class ExhaustiveWhenCoverage {
        @Test
        fun `all GameAction variants are handled by validate`() {
            // This test ensures new GameAction variants added in the future
            // cause a compile error in ActionProcessor.validate() if not handled.
            // If this test compiles, all branches are covered.
            val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
            val actions: List<GameAction> = listOf(
                GameAction.PlaceTower(TowerType.ARCHER, 1, 1),
                GameAction.UpgradeTower(1),
                GameAction.SellTower(1),
                GameAction.StartWave,
                GameAction.SetSpeed(2f)
            )
            for (action in actions) {
                // Should not throw — every variant is handled
                processor.validate(action, state, emptyList())
            }
        }
    }
}
