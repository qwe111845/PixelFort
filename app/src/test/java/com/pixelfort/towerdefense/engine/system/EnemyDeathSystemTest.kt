package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EnemyDeathSystemTest {

    private val system = EnemyDeathSystem()

    @Nested
    inner class `Given enemy HP is zero or below` {
        @Test
        fun `When update Then removes enemy and awards gold`() {
            val deadEnemy = Enemy.create(id = 1, type = EnemyType.GOBLIN).copy(hp = 0)
            val aliveEnemy = Enemy.create(id = 2, type = EnemyType.ORC)

            val result = system.update(listOf(deadEnemy, aliveEnemy))

            assertEquals(1, result.survivors.size)
            assertEquals(2, result.survivors[0].id)
            assertEquals(1, result.killedEnemies.size)
            assertEquals(1, result.killedEnemies[0].id)
            assertEquals(10, result.goldEarned) // GOBLIN reward
        }

        @Test
        fun `When multiple enemies die Then total gold is summed`() {
            val dead1 = Enemy.create(id = 1, type = EnemyType.GOBLIN).copy(hp = 0)
            val dead2 = Enemy.create(id = 2, type = EnemyType.ORC).copy(hp = -5)

            val result = system.update(listOf(dead1, dead2))

            assertTrue(result.survivors.isEmpty())
            assertEquals(35, result.goldEarned) // 10 + 25
        }
    }

    @Nested
    inner class `Given all enemies alive` {
        @Test
        fun `When update Then no enemies removed and no gold earned`() {
            val enemies = listOf(
                Enemy.create(id = 1, type = EnemyType.GOBLIN),
                Enemy.create(id = 2, type = EnemyType.ORC)
            )

            val result = system.update(enemies)

            assertEquals(2, result.survivors.size)
            assertEquals(0, result.goldEarned)
        }
    }
}
