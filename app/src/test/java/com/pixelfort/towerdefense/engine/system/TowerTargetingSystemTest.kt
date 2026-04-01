package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TowerTargetingSystemTest {

    private lateinit var system: TowerTargetingSystem
    private val cellSize = 32f

    @BeforeEach
    fun setup() {
        system = TowerTargetingSystem(cellSize)
    }

    @Nested
    inner class `Given tower cooldown expired and enemy in range` {
        @Test
        fun `When update Then produces Projectile`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 3, gridCol = 3, cooldownRemainingMs = 0)
            val enemy = Enemy.create(id = 10, type = EnemyType.GOBLIN)
                .copy(pixelX = 3f * cellSize, pixelY = 4f * cellSize) // 1 cell away

            val result = system.update(listOf(tower), listOf(enemy), deltaMs = 100L)

            assertEquals(1, result.projectiles.size)
            assertEquals(10, result.projectiles[0].targetEnemyId)
            assertEquals(1, result.projectiles[0].sourceTowerId)
        }

        @Test
        fun `When update Then tower cooldown resets`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 3, gridCol = 3, cooldownRemainingMs = 0)
            val enemy = Enemy.create(id = 10, type = EnemyType.GOBLIN)
                .copy(pixelX = 3f * cellSize, pixelY = 4f * cellSize)

            val result = system.update(listOf(tower), listOf(enemy), deltaMs = 100L)

            assertTrue(result.updatedTowers[0].cooldownRemainingMs > 0)
        }
    }

    @Nested
    inner class `Given no enemy in range` {
        @Test
        fun `When update Then does not fire`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 0, gridCol = 0, cooldownRemainingMs = 0)
            // Enemy very far away
            val enemy = Enemy.create(id = 10, type = EnemyType.GOBLIN)
                .copy(pixelX = 100f * cellSize, pixelY = 100f * cellSize)

            val result = system.update(listOf(tower), listOf(enemy), deltaMs = 100L)

            assertTrue(result.projectiles.isEmpty())
        }
    }

    @Nested
    inner class `Given tower still on cooldown` {
        @Test
        fun `When update Then cooldown decreases but does not fire`() {
            val tower = Tower(id = 1, type = TowerType.ARCHER, level = 1, gridRow = 3, gridCol = 3, cooldownRemainingMs = 500)
            val enemy = Enemy.create(id = 10, type = EnemyType.GOBLIN)
                .copy(pixelX = 3f * cellSize, pixelY = 4f * cellSize)

            val result = system.update(listOf(tower), listOf(enemy), deltaMs = 100L)

            assertTrue(result.projectiles.isEmpty())
            assertEquals(400, result.updatedTowers[0].cooldownRemainingMs)
        }
    }
}
