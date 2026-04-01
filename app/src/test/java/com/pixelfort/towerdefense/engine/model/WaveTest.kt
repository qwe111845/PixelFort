package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WaveTest {

    @Test
    fun `WaveGroup stores enemy type, count and intervals`() {
        val group = WaveGroup(
            enemyType = EnemyType.GOBLIN,
            count = 5,
            spawnIntervalMs = 1000L,
            delayAfterPreviousMs = 0L
        )
        assertEquals(EnemyType.GOBLIN, group.enemyType)
        assertEquals(5, group.count)
        assertEquals(1000L, group.spawnIntervalMs)
    }

    @Test
    fun `Wave stores wave number and groups`() {
        val groups = listOf(
            WaveGroup(EnemyType.GOBLIN, 3, 1000L),
            WaveGroup(EnemyType.ORC, 1, 2000L, delayAfterPreviousMs = 3000L)
        )
        val wave = Wave(waveNumber = 1, groups = groups)
        assertEquals(1, wave.waveNumber)
        assertEquals(2, wave.groups.size)
    }

    @Test
    fun `Wave totalEnemies sums all group counts`() {
        val wave = Wave(
            waveNumber = 1,
            groups = listOf(
                WaveGroup(EnemyType.GOBLIN, 5, 1000L),
                WaveGroup(EnemyType.ORC, 3, 1500L)
            )
        )
        assertEquals(8, wave.totalEnemies)
    }
}
