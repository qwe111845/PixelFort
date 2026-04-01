package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy

class EndReachSystem {

    data class Result(
        val survivors: List<Enemy>,
        val reachedEnemies: List<Enemy>,
        val livesLost: Int
    )

    fun update(enemies: List<Enemy>): Result {
        val (reached, onPath) = enemies.partition { it.hasReachedEnd }
        return Result(
            survivors = onPath,
            reachedEnemies = reached,
            livesLost = reached.size
        )
    }
}
