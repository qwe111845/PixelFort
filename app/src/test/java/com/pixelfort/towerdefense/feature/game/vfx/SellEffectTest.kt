package com.pixelfort.towerdefense.feature.game.vfx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SellEffectTest {

    @Nested
    inner class Lifecycle {
        @Test
        fun `new effect starts alive with full alpha and scale`() {
            val effect = SellEffect(x = 100f, y = 200f)
            assertFalse(effect.isDead)
            assertEquals(0f, effect.progress, 0.001f)
            assertEquals(1f, effect.alpha, 0.001f)
            assertEquals(1f, effect.scale, 0.001f)
        }

        @Test
        fun `effect at half duration has expected alpha and scale`() {
            val effect = SellEffect(x = 100f, y = 200f, elapsedMs = 150L)
            assertFalse(effect.isDead)
            assertEquals(0.5f, effect.progress, 0.001f)
            assertEquals(0.5f, effect.alpha, 0.001f)
            assertEquals(0.75f, effect.scale, 0.001f)
        }

        @Test
        fun `effect is dead after total duration`() {
            val effect = SellEffect(x = 100f, y = 200f, elapsedMs = 300L)
            assertTrue(effect.isDead)
            assertEquals(1f, effect.progress, 0.001f)
            assertEquals(0f, effect.alpha, 0.001f)
            assertEquals(0.5f, effect.scale, 0.001f)
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `update advances elapsed time`() {
            val effect = SellEffect(x = 0f, y = 0f)
            val updated = effect.update(100L)
            assertEquals(100L, updated.elapsedMs)
        }

        @Test
        fun `update clamps at totalMs`() {
            val effect = SellEffect(x = 0f, y = 0f, elapsedMs = 250L)
            val updated = effect.update(200L)
            assertEquals(300L, updated.elapsedMs)
            assertTrue(updated.isDead)
        }
    }

    @Nested
    inner class Duration {
        @Test
        fun `default duration is 300ms`() {
            assertEquals(300L, SellEffect.DURATION_MS)
        }
    }
}
