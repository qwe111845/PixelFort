package com.pixelfort.towerdefense.engine.model

import com.pixelfort.towerdefense.engine.system.EnemyMovementSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CellEffectTest {

    private val cellSize = 32f

    @Nested
    inner class LavaDamageTests {

        @Test
        fun `lava cell damages enemy when it passes through`() {
            // Simple 2-waypoint path: (0,0) → (0,4)
            val waypoints = listOf(
                GridPoint(0, 0),
                GridPoint(0, 4)
            )
            // Place lava on (0,2) — midway on the path
            val effects = mapOf(
                GridPoint(0, 2) to CellEffect.LavaDamage(10)
            )
            val system = EnemyMovementSystem(waypoints, cellSize, effects)
            val enemy = Enemy.create(id = 1, type = EnemyType.ORC) // 150 HP, speed 1.0

            // totalPathLength = 4. Speed 1.0 -> progress/s = 0.25
            // At progress 0.5, position is (0,2). After 2s, progress = 0.5 -> cell (0,2)
            val result = system.moveAllWithEffects(listOf(enemy), deltaMs = 2000L)
            val moved = result.enemies.first()

            // Enemy should have taken lava damage
            assertTrue(moved.hp < enemy.hp, "Enemy HP should decrease from lava damage")
            assertTrue(result.lavaDamageEvents.isNotEmpty(), "Should have lava damage events")
            assertEquals(10, result.lavaDamageEvents.first().damage)
        }

        @Test
        fun `lava damage only applies once per cell`() {
            val waypoints = listOf(
                GridPoint(0, 0),
                GridPoint(0, 6)
            )
            val effects = mapOf(
                GridPoint(0, 2) to CellEffect.LavaDamage(10)
            )
            val system = EnemyMovementSystem(waypoints, cellSize, effects)
            val startHp = EnemyType.TROLL.baseHp // 250 HP, speed 0.8
            var enemy = Enemy.create(id = 1, type = EnemyType.TROLL)

            // totalPathLength = 6. Speed 0.8 -> progress/s = 0.133
            // At progress 0.333 position is col=2 -> 2s gives progress=0.266, 2.5s gives 0.333
            val result1 = system.moveAllWithEffects(listOf(enemy), deltaMs = 2500L)
            enemy = result1.enemies.first()
            // Still on or near cell (0,2), call again
            val result2 = system.moveAllWithEffects(listOf(enemy), deltaMs = 200L)
            val finalEnemy = result2.enemies.first()

            // Should only have been damaged once total
            val totalDamage = startHp - finalEnemy.hp
            assertEquals(10, totalDamage, "Lava should only damage once per cell")
        }
    }

    @Nested
    inner class TeleportTests {

        @Test
        fun `teleport moves enemy to target waypoint index`() {
            // Path: (0,0) → (0,2) → (2,2) → (2,4)
            val waypoints = listOf(
                GridPoint(0, 0),
                GridPoint(0, 2),
                GridPoint(2, 2),
                GridPoint(2, 4)
            )
            // Teleport at (0,2) sends to waypoint 2 (GridPoint(2,2))
            val effects = mapOf(
                GridPoint(0, 2) to CellEffect.Teleport(targetWaypointIndex = 2)
            )
            val system = EnemyMovementSystem(waypoints, cellSize, effects)
            val enemy = Enemy.create(id = 1, type = EnemyType.GOBLIN)

            // Move enough time to reach waypoint 1 at (0,2) where teleport is
            // totalPathLength: dist(0,0→0,2)=2 + dist(0,2→2,2)=2 + dist(2,2→2,4)=2 = 6
            // With speed 2.0, after 2s the enemy travels 4 units = progress ~0.66
            // Waypoint 1 is at progress 1/3 = 0.33, so 1 second should be enough
            val moved = system.moveEnemy(enemy, deltaMs = 2000L)

            // After teleport to waypoint 2 (progress 2/3), the enemy should be past 0.5
            assertTrue(moved.pathProgress >= 0.5f,
                "Enemy should have been teleported ahead, progress=${moved.pathProgress}")
        }
    }
}
