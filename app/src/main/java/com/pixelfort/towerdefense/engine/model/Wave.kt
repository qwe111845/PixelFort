package com.pixelfort.towerdefense.engine.model

data class WaveGroup(
    val enemyType: EnemyType,
    val count: Int,
    val spawnIntervalMs: Long,
    val delayAfterPreviousMs: Long = 0L
)

data class Wave(
    val waveNumber: Int,
    val groups: List<WaveGroup>
) {
    val totalEnemies: Int get() = groups.sumOf { it.count }
}
