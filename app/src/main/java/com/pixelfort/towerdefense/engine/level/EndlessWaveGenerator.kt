package com.pixelfort.towerdefense.engine.level

import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.Wave
import com.pixelfort.towerdefense.engine.model.WaveGroup

object EndlessWaveGenerator {

    /**
     * Generate wave N (0-indexed) for endless mode.
     * Scaling: HP *= 1 + (N * 0.15), count += N/3
     * New enemy types introduced every 5 waves.
     * Boss every 10 waves.
     */
    fun generate(waveIndex: Int): Wave {
        val groups = mutableListOf<WaveGroup>()
        val baseCount = 6 + waveIndex / 3

        // Available enemy types grow with wave number
        val availableTypes = buildList {
            add(EnemyType.GOBLIN)
            if (waveIndex >= 5)  add(EnemyType.ORC)
            if (waveIndex >= 10) add(EnemyType.SPECTER)
            if (waveIndex >= 15) add(EnemyType.TROLL)
            if (waveIndex >= 20) add(EnemyType.DRAGON)
        }

        // Mix available enemy types
        val primaryType = availableTypes.last()
        val secondaryType = if (availableTypes.size > 1) availableTypes[availableTypes.size - 2] else null

        // Primary group
        groups.add(
            WaveGroup(
                enemyType = primaryType,
                count = baseCount,
                spawnIntervalMs = maxOf(400L, 1200L - waveIndex * 30L)
            )
        )

        // Secondary group (if available)
        if (secondaryType != null && waveIndex >= 3) {
            groups.add(
                WaveGroup(
                    enemyType = secondaryType,
                    count = baseCount / 2 + 1,
                    spawnIntervalMs = maxOf(500L, 1000L - waveIndex * 20L),
                    delayAfterPreviousMs = 2000L
                )
            )
        }

        // Boss every 10 waves
        if (waveIndex > 0 && waveIndex % 10 == 9) {
            groups.add(
                WaveGroup(
                    enemyType = EnemyType.BOSS_DRAGON,
                    count = 1,
                    spawnIntervalMs = 5000L,
                    delayAfterPreviousMs = 5000L
                )
            )
        }

        return Wave(waveNumber = waveIndex + 1, groups = groups)
    }

    /** Get the HP multiplier for a given wave index. */
    fun hpMultiplier(waveIndex: Int): Float = 1f + waveIndex * 0.15f
}
