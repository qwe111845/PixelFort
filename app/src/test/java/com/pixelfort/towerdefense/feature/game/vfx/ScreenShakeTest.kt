package com.pixelfort.towerdefense.feature.game.vfx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ScreenShakeTest {

    @Nested
    inner class ShakeState {
        @Test
        fun `idle shake is not active`() {
            assertFalse(ScreenShake.IDLE.isActive)
        }

        @Test
        fun `triggered shake is active`() {
            val shake = ScreenShake.IDLE.trigger(10f, 300L)
            assertTrue(shake.isActive)
            assertEquals(10f, shake.intensity)
            assertEquals(300L, shake.remainingMs)
        }

        @Test
        fun `offset is zero when not active`() {
            assertEquals(0f, ScreenShake.IDLE.offsetX)
            assertEquals(0f, ScreenShake.IDLE.offsetY)
        }

        @Test
        fun `offset is non-zero when active`() {
            val shake = ScreenShake.IDLE.trigger(20f, 300L)
            // Run 100 samples — at least some should be non-zero
            val offsets = (1..100).map { shake.offsetX }
            assertTrue(offsets.any { it != 0f })
        }

        @Test
        fun `offset within intensity bounds`() {
            val shake = ScreenShake.IDLE.trigger(10f, 300L)
            repeat(100) {
                val ox = shake.offsetX
                val oy = shake.offsetY
                assertTrue(ox >= -10f && ox <= 10f, "offsetX $ox out of bounds")
                assertTrue(oy >= -10f && oy <= 10f, "offsetY $oy out of bounds")
            }
        }
    }

    @Nested
    inner class ShakeUpdate {
        @Test
        fun `update reduces remainingMs`() {
            val shake = ScreenShake.IDLE.trigger(10f, 300L)
            val updated = shake.update(100L)
            assertEquals(200L, updated.remainingMs)
        }

        @Test
        fun `update decays intensity`() {
            val shake = ScreenShake.IDLE.trigger(10f, 300L)
            val updated = shake.update(16L)
            assertTrue(updated.intensity < 10f)
            assertEquals(10f * 0.85f, updated.intensity, 0.01f)
        }

        @Test
        fun `update returns inactive when time expires`() {
            val shake = ScreenShake.IDLE.trigger(10f, 100L)
            val updated = shake.update(150L)
            assertFalse(updated.isActive)
        }

        @Test
        fun `update returns inactive when intensity decays below threshold`() {
            var shake = ScreenShake.IDLE.trigger(1f, 5000L)
            // Decay many times
            repeat(20) { shake = shake.update(16L) }
            assertFalse(shake.isActive)
        }
    }

    @Nested
    inner class ShakeStacking {
        @Test
        fun `stronger shake replaces weaker`() {
            val weak = ScreenShake.IDLE.trigger(5f, 200L)
            val result = weak.trigger(15f, 400L)
            assertEquals(15f, result.intensity)
            assertEquals(400L, result.remainingMs)
        }

        @Test
        fun `weaker shake does not replace stronger active shake`() {
            val strong = ScreenShake.IDLE.trigger(15f, 400L)
            val result = strong.trigger(5f, 200L)
            assertEquals(15f, result.intensity)
            assertEquals(400L, result.remainingMs)
        }
    }

    @Nested
    inner class FlashEffectTest {
        @Test
        fun `idle flash is not active`() {
            assertFalse(FlashEffect.NONE.isActive)
        }

        @Test
        fun `triggered flash is active`() {
            val flash = FlashEffect.NONE.trigger(androidx.compose.ui.graphics.Color.White, 50L)
            assertTrue(flash.isActive)
        }

        @Test
        fun `flash alpha decays`() {
            val flash = FlashEffect.NONE.trigger(androidx.compose.ui.graphics.Color.White, 200L)
            val half = flash.update(100L)
            assertTrue(half.alpha < flash.alpha)
        }

        @Test
        fun `flash becomes inactive after duration`() {
            val flash = FlashEffect.NONE.trigger(androidx.compose.ui.graphics.Color.Red, 200L)
            val done = flash.update(250L)
            assertFalse(done.isActive)
        }
    }
}
