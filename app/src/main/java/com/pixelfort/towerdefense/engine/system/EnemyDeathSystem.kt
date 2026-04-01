package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy

class EnemyDeathSystem {

    data class Result(
        val survivors: List<Enemy>,
        val killedEnemies: List<Enemy>,
        val goldEarned: Int
    )

    fun update(enemies: List<Enemy>): Result {
        val (dead, alive) = enemies.partition { it.isDead }
        val goldEarned = dead.sumOf { it.reward }
        return Result(survivors = alive, killedEnemies = dead, goldEarned = goldEarned)
    }
}
