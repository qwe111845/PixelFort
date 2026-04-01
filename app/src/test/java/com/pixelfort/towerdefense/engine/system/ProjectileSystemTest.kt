package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.Projectile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProjectileSystemTest {

    private lateinit var system: ProjectileSystem
    private val cellSize = 32f

    @BeforeEach
    fun setup() {
        system = ProjectileSystem(cellSize)
    }

    @Nested
    inner class `Given projectile near target` {
        @Test
        fun `When update Then deals damage and removes projectile`() {
            val enemy = Enemy.create(id = 10, type = EnemyType.GOBLIN)
                .copy(pixelX = 100f, pixelY = 100f)
            val projectile = Projectile(
                id = 1, sourceTowerId = 1, targetEnemyId = 10,
                damage = 20, pixelX = 99f, pixelY = 99f, speed = 8f
            )

            val result = system.update(listOf(projectile), listOf(enemy), deltaMs = 100L)

            assertTrue(result.remainingProjectiles.isEmpty(), "Projectile should be consumed")
            assertEquals(1, result.damagedEnemies.size)
            assertEquals(30, result.damagedEnemies[0].hp) // 50 - 20
        }
    }

    @Nested
    inner class `Given projectile far from target` {
        @Test
        fun `When update Then projectile moves toward target`() {
            val enemy = Enemy.create(id = 10, type = EnemyType.GOBLIN)
                .copy(pixelX = 200f, pixelY = 200f)
            val projectile = Projectile(
                id = 1, sourceTowerId = 1, targetEnemyId = 10,
                damage = 20, pixelX = 0f, pixelY = 0f, speed = 8f
            )

            val result = system.update(listOf(projectile), listOf(enemy), deltaMs = 100L)

            assertEquals(1, result.remainingProjectiles.size)
            val moved = result.remainingProjectiles[0]
            assertTrue(moved.pixelX > 0f)
            assertTrue(moved.pixelY > 0f)
        }
    }

    @Nested
    inner class `Given target already dead` {
        @Test
        fun `When update Then removes projectile without damage`() {
            // Target not in enemy list (already removed)
            val projectile = Projectile(
                id = 1, sourceTowerId = 1, targetEnemyId = 99,
                damage = 20, pixelX = 50f, pixelY = 50f, speed = 8f
            )

            val result = system.update(listOf(projectile), emptyList(), deltaMs = 100L)

            assertTrue(result.remainingProjectiles.isEmpty())
            assertTrue(result.damagedEnemies.isEmpty())
        }
    }
}
