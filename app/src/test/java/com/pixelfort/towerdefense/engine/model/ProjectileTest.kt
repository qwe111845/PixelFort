package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProjectileTest {

    @Test
    fun `Projectile stores basic properties`() {
        val proj = Projectile(
            id = 1,
            sourceTowerId = 10,
            targetEnemyId = 20,
            damage = 30,
            pixelX = 100f,
            pixelY = 200f,
            speed = 8f
        )
        assertEquals(1, proj.id)
        assertEquals(10, proj.sourceTowerId)
        assertEquals(20, proj.targetEnemyId)
        assertEquals(30, proj.damage)
        assertEquals(100f, proj.pixelX)
        assertEquals(200f, proj.pixelY)
        assertEquals(8f, proj.speed)
    }

    @Test
    fun `Projectile hasReachedTarget when distance is below threshold`() {
        val proj = Projectile(
            id = 1, sourceTowerId = 1, targetEnemyId = 2,
            damage = 10, pixelX = 100f, pixelY = 100f, speed = 8f
        )
        // Target very close (within 0.2 cells = ~6.4 pixels at 32px/cell)
        assertTrue(proj.hasReachedTarget(targetX = 101f, targetY = 101f, cellSize = 32f))
    }

    @Test
    fun `Projectile has not reached target when distance is above threshold`() {
        val proj = Projectile(
            id = 1, sourceTowerId = 1, targetEnemyId = 2,
            damage = 10, pixelX = 100f, pixelY = 100f, speed = 8f
        )
        assertFalse(proj.hasReachedTarget(targetX = 200f, targetY = 200f, cellSize = 32f))
    }
}
