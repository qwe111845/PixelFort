package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.GridPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EnemyMovementSystemTest {

    private lateinit var system: EnemyMovementSystem
    private val waypoints = listOf(
        GridPoint(0, 0),
        GridPoint(0, 4),
        GridPoint(4, 4)
    )
    private val cellSize = 32f

    @BeforeEach
    fun setup() {
        system = EnemyMovementSystem(waypoints, cellSize)
    }

    @Nested
    inner class `Given enemy at path start` {
        @Test
        fun `When update Then pathProgress increases`() {
            val enemy = Enemy.create(id = 1, type = EnemyType.GOBLIN)
            val updated = system.moveEnemy(enemy, deltaMs = 500L)
            assertTrue(updated.pathProgress > 0f)
        }

        @Test
        fun `When update Then pixel position changes`() {
            val enemy = Enemy.create(id = 1, type = EnemyType.GOBLIN)
            val updated = system.moveEnemy(enemy, deltaMs = 500L)
            assertTrue(updated.pixelX > 0f || updated.pixelY > 0f)
        }
    }

    @Nested
    inner class `Given enemy near end of path` {
        @Test
        fun `When update with enough time Then pathProgress reaches 1_0`() {
            val enemy = Enemy.create(id = 1, type = EnemyType.GOBLIN)
                .copy(pathProgress = 0.99f)
            val updated = system.moveEnemy(enemy, deltaMs = 5000L)
            assertTrue(updated.pathProgress >= 1.0f)
        }
    }

    @Nested
    inner class `Given enemy speed affects movement` {
        @Test
        fun `Faster enemy covers more distance in same time`() {
            val slow = Enemy.create(id = 1, type = EnemyType.ORC) // speed 1.0
            val fast = Enemy.create(id = 2, type = EnemyType.GOBLIN) // speed 2.0

            val slowMoved = system.moveEnemy(slow, deltaMs = 1000L)
            val fastMoved = system.moveEnemy(fast, deltaMs = 1000L)

            assertTrue(fastMoved.pathProgress > slowMoved.pathProgress)
        }
    }

    @Nested
    inner class `Given batch update` {
        @Test
        fun `moveAll updates all enemies`() {
            val enemies = listOf(
                Enemy.create(id = 1, type = EnemyType.GOBLIN),
                Enemy.create(id = 2, type = EnemyType.ORC)
            )
            val updated = system.moveAll(enemies, deltaMs = 500L)
            assertEquals(2, updated.size)
            assertTrue(updated.all { it.pathProgress > 0f })
        }
    }
}
