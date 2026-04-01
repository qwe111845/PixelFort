package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EnemyTest {

    @Nested
    inner class EnemyTypeStats {
        @Test
        fun `GOBLIN has low HP, fast speed, low reward`() {
            assertEquals(50, EnemyType.GOBLIN.baseHp)
            assertEquals(2.0f, EnemyType.GOBLIN.baseSpeed)
            assertEquals(10, EnemyType.GOBLIN.reward)
        }

        @Test
        fun `ORC has high HP, slow speed, medium reward`() {
            assertEquals(150, EnemyType.ORC.baseHp)
            assertEquals(1.0f, EnemyType.ORC.baseSpeed)
            assertEquals(25, EnemyType.ORC.reward)
        }

        @Test
        fun `DRAGON has very high HP, medium speed, high reward`() {
            assertEquals(400, EnemyType.DRAGON.baseHp)
            assertEquals(1.5f, EnemyType.DRAGON.baseSpeed)
            assertEquals(50, EnemyType.DRAGON.reward)
        }
    }

    @Nested
    inner class EnemyEntity {
        @Test
        fun `Enemy stores basic properties`() {
            val enemy = Enemy(
                id = 1,
                type = EnemyType.GOBLIN,
                hp = 50,
                maxHp = 50,
                pathProgress = 0f,
                pixelX = 0f,
                pixelY = 0f,
                speed = 2.0f,
                reward = 10
            )
            assertEquals(1, enemy.id)
            assertEquals(EnemyType.GOBLIN, enemy.type)
            assertEquals(50, enemy.hp)
        }

        @Test
        fun `Enemy isDead when hp is zero`() {
            val enemy = createGoblin(hp = 0)
            assertTrue(enemy.isDead)
        }

        @Test
        fun `Enemy isDead when hp is negative`() {
            val enemy = createGoblin(hp = -5)
            assertTrue(enemy.isDead)
        }

        @Test
        fun `Enemy is not dead when hp is positive`() {
            val enemy = createGoblin(hp = 10)
            assertFalse(enemy.isDead)
        }

        @Test
        fun `Enemy hasReachedEnd when pathProgress is 1`() {
            val enemy = createGoblin(pathProgress = 1.0f)
            assertTrue(enemy.hasReachedEnd)
        }

        @Test
        fun `Enemy hasReachedEnd when pathProgress exceeds 1`() {
            val enemy = createGoblin(pathProgress = 1.1f)
            assertTrue(enemy.hasReachedEnd)
        }

        @Test
        fun `Enemy has not reached end when pathProgress below 1`() {
            val enemy = createGoblin(pathProgress = 0.5f)
            assertFalse(enemy.hasReachedEnd)
        }

        @Test
        fun `Enemy hpPercentage calculates correctly`() {
            val enemy = createGoblin(hp = 25, maxHp = 50)
            assertEquals(0.5f, enemy.hpPercentage, 0.001f)
        }

        @Test
        fun `Enemy created from EnemyType has correct defaults`() {
            val enemy = Enemy.create(id = 5, type = EnemyType.ORC)
            assertEquals(5, enemy.id)
            assertEquals(EnemyType.ORC, enemy.type)
            assertEquals(150, enemy.hp)
            assertEquals(150, enemy.maxHp)
            assertEquals(1.0f, enemy.speed)
            assertEquals(25, enemy.reward)
            assertEquals(0f, enemy.pathProgress)
        }

        private fun createGoblin(
            hp: Int = 50,
            maxHp: Int = 50,
            pathProgress: Float = 0f
        ) = Enemy(
            id = 1,
            type = EnemyType.GOBLIN,
            hp = hp,
            maxHp = maxHp,
            pathProgress = pathProgress,
            pixelX = 0f,
            pixelY = 0f,
            speed = 2.0f,
            reward = 10
        )
    }
}
