package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.ActiveWaveEvent
import com.pixelfort.towerdefense.engine.model.WaveEventType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random

class WaveEventSystemTest {

    @Nested
    inner class `rollEvent` {

        @Test
        fun `returns null on wave 0 (first wave)`() {
            val result = WaveEventSystem.rollEvent(
                waveNumber = 0, isEndless = false, enabled = true,
                random = Random(42)
            )
            assertNull(result)
        }

        @Test
        fun `returns null when disabled`() {
            // Use a random that would always produce 0.0 (guaranteed trigger)
            val alwaysZero = object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
            }
            val result = WaveEventSystem.rollEvent(
                waveNumber = 3, isEndless = false, enabled = false,
                random = alwaysZero
            )
            assertNull(result)
        }

        @Test
        fun `can trigger event on wave 1 and above when enabled`() {
            // Force the roll to succeed by using a random that returns 0.0 (below 0.4 threshold)
            val lowRandom = object : Random() {
                private var callCount = 0
                override fun nextBits(bitCount: Int): Int = 0
                override fun nextFloat(): Float = 0.1f
                override fun nextInt(until: Int): Int = 0
            }
            val result = WaveEventSystem.rollEvent(
                waveNumber = 1, isEndless = false, enabled = true,
                random = lowRandom
            )
            assertNotNull(result)
        }

        @Test
        fun `returns null when roll exceeds 40 percent threshold`() {
            val highRandom = object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
                override fun nextFloat(): Float = 0.5f // above 0.4
            }
            val result = WaveEventSystem.rollEvent(
                waveNumber = 2, isEndless = false, enabled = true,
                random = highRandom
            )
            assertNull(result)
        }

        @Test
        fun `endless mode after wave 5 always triggers (100 percent)`() {
            // Even with a high roll, endless after wave 5 should always trigger
            val highRandom = object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
                override fun nextFloat(): Float = 0.99f  // would normally fail
                override fun nextInt(until: Int): Int = 0
            }
            val result = WaveEventSystem.rollEvent(
                waveNumber = 5, isEndless = true, enabled = true,
                random = highRandom
            )
            assertNotNull(result)
        }

        @Test
        fun `endless mode before wave 5 uses normal 40 percent chance`() {
            val highRandom = object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
                override fun nextFloat(): Float = 0.5f // above 0.4
            }
            val result = WaveEventSystem.rollEvent(
                waveNumber = 3, isEndless = true, enabled = true,
                random = highRandom
            )
            assertNull(result)
        }

        @Test
        fun `returns one of the six event types`() {
            val lowRandom = object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
                override fun nextFloat(): Float = 0.1f
                override fun nextInt(until: Int): Int = 2 // index 2 = REINFORCED
            }
            val result = WaveEventSystem.rollEvent(
                waveNumber = 2, isEndless = false, enabled = true,
                random = lowRandom
            )
            assertEquals(WaveEventType.REINFORCED, result)
        }
    }

    @Nested
    inner class `getModifiers` {

        @Test
        fun `returns NONE when no active events`() {
            val mods = WaveEventSystem.getModifiers(emptyList())
            assertEquals(EventModifiers.NONE, mods)
        }

        @Test
        fun `STORM reduces projectile speed by 30 percent`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(ActiveWaveEvent(WaveEventType.STORM))
            )
            assertEquals(0.70f, mods.projectileSpeedMult, 0.001f)
            assertEquals(1f, mods.goldMult, 0.001f)
            assertEquals(1f, mods.enemyHpMult, 0.001f)
        }

        @Test
        fun `GOLDEN_HOUR doubles gold`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(ActiveWaveEvent(WaveEventType.GOLDEN_HOUR))
            )
            assertEquals(2.0f, mods.goldMult, 0.001f)
            assertEquals(1f, mods.projectileSpeedMult, 0.001f)
        }

        @Test
        fun `REINFORCED increases enemy HP and gold by 50 percent`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(ActiveWaveEvent(WaveEventType.REINFORCED))
            )
            assertEquals(1.50f, mods.enemyHpMult, 0.001f)
            assertEquals(1.50f, mods.goldMult, 0.001f)
        }

        @Test
        fun `FOG_OF_WAR reduces tower range by 20 percent`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(ActiveWaveEvent(WaveEventType.FOG_OF_WAR))
            )
            assertEquals(0.80f, mods.rangeMult, 0.001f)
        }

        @Test
        fun `TAILWIND increases attack speed (lower fire rate mult)`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(ActiveWaveEvent(WaveEventType.TAILWIND))
            )
            assertEquals(0.75f, mods.fireRateMult, 0.001f)
        }

        @Test
        fun `BLOOD_MOON increases enemy speed and gold by 30 percent`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(ActiveWaveEvent(WaveEventType.BLOOD_MOON))
            )
            assertEquals(1.30f, mods.enemySpeedMult, 0.001f)
            assertEquals(1.30f, mods.goldMult, 0.001f)
        }

        @Test
        fun `multiple events stack multiplicatively`() {
            val mods = WaveEventSystem.getModifiers(
                listOf(
                    ActiveWaveEvent(WaveEventType.GOLDEN_HOUR),
                    ActiveWaveEvent(WaveEventType.REINFORCED)
                )
            )
            // Gold: 2.0 * 1.5 = 3.0
            assertEquals(3.0f, mods.goldMult, 0.001f)
            // HP: 1.0 * 1.5 = 1.5
            assertEquals(1.5f, mods.enemyHpMult, 0.001f)
        }
    }

    @Nested
    inner class `WaveEventType enum` {

        @Test
        fun `has exactly 6 types`() {
            assertEquals(6, WaveEventType.entries.size)
        }

        @Test
        fun `all types have non-blank display names and descriptions`() {
            for (type in WaveEventType.entries) {
                assertTrue(type.displayName.isNotBlank(), "${type.name} displayName is blank")
                assertTrue(type.description.isNotBlank(), "${type.name} description is blank")
            }
        }
    }
}
