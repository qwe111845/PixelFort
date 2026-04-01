package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EndReachSystemTest {

    private val system = EndReachSystem()

    @Nested
    inner class `Given enemy reaches end` {
        @Test
        fun `When update Then removes enemy and records lives lost`() {
            val reachedEnd = Enemy.create(id = 1, type = EnemyType.GOBLIN)
                .copy(pathProgress = 1.0f)
            val onPath = Enemy.create(id = 2, type = EnemyType.ORC)
                .copy(pathProgress = 0.5f)

            val result = system.update(listOf(reachedEnd, onPath))

            assertEquals(1, result.survivors.size)
            assertEquals(2, result.survivors[0].id)
            assertEquals(1, result.livesLost)
            assertEquals(1, result.reachedEnemies.size)
        }

        @Test
        fun `When multiple enemies reach end Then loses multiple lives`() {
            val reached1 = Enemy.create(id = 1, type = EnemyType.GOBLIN).copy(pathProgress = 1.0f)
            val reached2 = Enemy.create(id = 2, type = EnemyType.ORC).copy(pathProgress = 1.1f)

            val result = system.update(listOf(reached1, reached2))

            assertTrue(result.survivors.isEmpty())
            assertEquals(2, result.livesLost)
        }
    }

    @Nested
    inner class `Given no enemy has reached end` {
        @Test
        fun `When update Then no lives lost`() {
            val enemies = listOf(
                Enemy.create(id = 1, type = EnemyType.GOBLIN).copy(pathProgress = 0.3f),
                Enemy.create(id = 2, type = EnemyType.ORC).copy(pathProgress = 0.7f)
            )

            val result = system.update(enemies)

            assertEquals(2, result.survivors.size)
            assertEquals(0, result.livesLost)
        }
    }
}
