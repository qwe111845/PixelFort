package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.Wave

class WaveSpawnerSystem(private val waves: List<Wave>) {

    data class Result(
        val spawnedEnemies: List<Enemy>,
        val waveSpawningComplete: Boolean
    )

    var isActive: Boolean = false
        private set

    private var currentWaveIndex = -1
    private var currentGroupIndex = 0
    private var spawnedInGroup = 0
    private var timeSinceLastSpawnMs = 0L
    private var groupDelayRemainingMs = 0L
    private var allGroupsComplete = false

    fun startWave(waveIndex: Int) {
        currentWaveIndex = waveIndex
        currentGroupIndex = 0
        spawnedInGroup = 0
        timeSinceLastSpawnMs = 0L
        groupDelayRemainingMs = 0L
        allGroupsComplete = false
        isActive = true
    }

    fun update(deltaMs: Long, nextEnemyId: Int): Result {
        if (!isActive || allGroupsComplete) {
            return Result(emptyList(), allGroupsComplete)
        }

        val wave = waves[currentWaveIndex]
        val spawnedEnemies = mutableListOf<Enemy>()
        var currentId = nextEnemyId

        // Handle group delay
        if (groupDelayRemainingMs > 0) {
            groupDelayRemainingMs -= deltaMs
            if (groupDelayRemainingMs > 0) {
                return Result(emptyList(), false)
            }
            // Delay finished, reset spawn timer
            timeSinceLastSpawnMs = -groupDelayRemainingMs // carry over excess time
            groupDelayRemainingMs = 0
        }

        timeSinceLastSpawnMs += deltaMs

        while (currentGroupIndex < wave.groups.size) {
            val group = wave.groups[currentGroupIndex]

            if (spawnedInGroup >= group.count) {
                // Move to next group
                currentGroupIndex++
                spawnedInGroup = 0
                if (currentGroupIndex < wave.groups.size) {
                    val nextGroup = wave.groups[currentGroupIndex]
                    if (nextGroup.delayAfterPreviousMs > 0) {
                        groupDelayRemainingMs = nextGroup.delayAfterPreviousMs
                        timeSinceLastSpawnMs = 0
                        break
                    }
                }
                continue
            }

            if (timeSinceLastSpawnMs >= group.spawnIntervalMs) {
                timeSinceLastSpawnMs -= group.spawnIntervalMs
                spawnedEnemies.add(Enemy.create(id = currentId++, type = group.enemyType))
                spawnedInGroup++
            } else {
                break
            }
        }

        if (currentGroupIndex >= wave.groups.size) {
            allGroupsComplete = true
        }

        return Result(spawnedEnemies, allGroupsComplete)
    }
}
