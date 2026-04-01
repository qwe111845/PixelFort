package com.pixelfort.towerdefense.engine

import com.pixelfort.towerdefense.engine.action.GameAction
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.level.Levels
import com.pixelfort.towerdefense.engine.model.TowerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GameEngineTest {

    private lateinit var engine: GameEngine
    private val cellSize = 32f

    @BeforeEach
    fun setup() {
        engine = GameEngine(Levels.level1, cellSize)
    }

    @Nested
    inner class InitialState {
        @Test
        fun `engine starts in WaitingForWave state`() {
            val snapshot = engine.snapshot()
            assertEquals(GameState.WaitingForWave, snapshot.state)
        }

        @Test
        fun `engine starts with correct gold and lives`() {
            val snapshot = engine.snapshot()
            assertEquals(200, snapshot.playerState.gold)
            assertEquals(20, snapshot.playerState.lives)
        }

        @Test
        fun `engine starts with no towers or enemies`() {
            val snapshot = engine.snapshot()
            assertTrue(snapshot.towers.isEmpty())
            assertTrue(snapshot.enemies.isEmpty())
        }
    }

    @Nested
    inner class TowerPlacement {
        @Test
        fun `placing tower on buildable cell succeeds`() {
            val result = engine.processAction(
                GameAction.PlaceTower(TowerType.ARCHER, gridRow = 1, gridCol = 1)
            )
            assertTrue(result)
            assertEquals(1, engine.snapshot().towers.size)
            assertEquals(100, engine.snapshot().playerState.gold) // 200 - 100
        }

        @Test
        fun `placing tower on path cell fails`() {
            val result = engine.processAction(
                GameAction.PlaceTower(TowerType.ARCHER, gridRow = 0, gridCol = 2)
            )
            assertFalse(result)
            assertTrue(engine.snapshot().towers.isEmpty())
        }

        @Test
        fun `placing tower emits TowerPlaced event`() {
            engine.processAction(
                GameAction.PlaceTower(TowerType.ARCHER, gridRow = 1, gridCol = 1)
            )
            val events = engine.snapshot().events
            assertTrue(events.any { it is GameEvent.TowerPlaced })
        }
    }

    @Nested
    inner class TowerUpgrade {
        @Test
        fun `upgrading tower increases level and costs gold`() {
            engine.processAction(GameAction.PlaceTower(TowerType.ARCHER, 1, 1))
            val towerId = engine.snapshot().towers[0].id
            val goldBefore = engine.snapshot().playerState.gold

            engine.processAction(GameAction.UpgradeTower(towerId))

            assertEquals(2, engine.snapshot().towers[0].level)
            assertTrue(engine.snapshot().playerState.gold < goldBefore)
        }
    }

    @Nested
    inner class TowerSell {
        @Test
        fun `selling tower removes it and returns gold`() {
            engine.processAction(GameAction.PlaceTower(TowerType.ARCHER, 1, 1))
            val towerId = engine.snapshot().towers[0].id
            val goldBefore = engine.snapshot().playerState.gold

            engine.processAction(GameAction.SellTower(towerId))

            assertTrue(engine.snapshot().towers.isEmpty())
            assertTrue(engine.snapshot().playerState.gold > goldBefore)
        }
    }

    @Nested
    inner class WaveExecution {
        @Test
        fun `starting wave changes state to Playing`() {
            engine.processAction(GameAction.StartWave)
            assertEquals(GameState.Playing, engine.snapshot().state)
        }

        @Test
        fun `update during Playing state spawns enemies`() {
            engine.processAction(GameAction.StartWave)
            // Run enough time for first enemy to spawn
            engine.update(2000L)
            assertTrue(engine.snapshot().enemies.isNotEmpty())
        }
    }

    @Nested
    inner class PauseResume {
        @Test
        fun `pause changes Playing to Paused`() {
            engine.processAction(GameAction.StartWave)
            engine.pause()
            assertEquals(GameState.Paused, engine.snapshot().state)
        }

        @Test
        fun `resume changes Paused back to Playing`() {
            engine.processAction(GameAction.StartWave)
            engine.pause()
            engine.resume()
            assertEquals(GameState.Playing, engine.snapshot().state)
        }

        @Test
        fun `update does nothing when paused`() {
            engine.processAction(GameAction.StartWave)
            engine.update(2000L) // spawn some enemies
            val enemyCount = engine.snapshot().enemies.size
            engine.pause()
            engine.update(5000L)
            // Enemy count should be same (no movement/spawning while paused)
            assertEquals(enemyCount, engine.snapshot().enemies.size)
        }
    }
}
