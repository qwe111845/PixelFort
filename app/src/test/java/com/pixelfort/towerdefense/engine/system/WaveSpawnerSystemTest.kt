package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.Wave
import com.pixelfort.towerdefense.engine.model.WaveGroup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WaveSpawnerSystemTest {

    @Nested
    inner class `Given wave started` {
        @Test
        fun `When interval elapsed Then spawns enemy`() {
            val wave = Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 3, 1000L)))
            val system = WaveSpawnerSystem(listOf(wave))
            system.startWave(0)

            // Advance past first spawn interval
            val result = system.update(deltaMs = 1100L, nextEnemyId = 1)

            assertEquals(1, result.spawnedEnemies.size)
            assertEquals(EnemyType.GOBLIN, result.spawnedEnemies[0].type)
        }

        @Test
        fun `When not enough time elapsed Then does not spawn`() {
            val wave = Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 3, 1000L)))
            val system = WaveSpawnerSystem(listOf(wave))
            system.startWave(0)

            val result = system.update(deltaMs = 500L, nextEnemyId = 1)

            assertTrue(result.spawnedEnemies.isEmpty())
        }

        @Test
        fun `When all enemies in wave spawned Then wave is complete`() {
            val wave = Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 1, 100L)))
            val system = WaveSpawnerSystem(listOf(wave))
            system.startWave(0)

            system.update(deltaMs = 200L, nextEnemyId = 1) // spawns the 1 enemy
            val result = system.update(deltaMs = 200L, nextEnemyId = 2)

            assertTrue(result.waveSpawningComplete)
        }
    }

    @Nested
    inner class `Given multiple groups in wave` {
        @Test
        fun `Second group spawns after delay`() {
            val wave = Wave(1, listOf(
                WaveGroup(EnemyType.GOBLIN, 1, 100L),
                WaveGroup(EnemyType.ORC, 1, 100L, delayAfterPreviousMs = 500L)
            ))
            val system = WaveSpawnerSystem(listOf(wave))
            system.startWave(0)

            // Spawn first goblin
            system.update(deltaMs = 200L, nextEnemyId = 1)
            // Wait through delay
            val result = system.update(deltaMs = 600L, nextEnemyId = 2)

            assertTrue(result.spawnedEnemies.any { it.type == EnemyType.ORC })
        }
    }

    @Nested
    inner class `Given wave not started` {
        @Test
        fun `isActive is false`() {
            val wave = Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 3, 1000L)))
            val system = WaveSpawnerSystem(listOf(wave))
            assertFalse(system.isActive)
        }
    }
}
