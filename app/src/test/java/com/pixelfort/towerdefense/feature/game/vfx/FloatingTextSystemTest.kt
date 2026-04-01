package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.TowerEffect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FloatingTextSystemTest {

    private lateinit var system: FloatingTextSystem

    @BeforeEach
    fun setup() {
        system = FloatingTextSystem()
    }

    @Nested
    inner class FloatingTextDataClass {
        @Test
        fun `alpha returns ratio of remaining to max life`() {
            val ft = FloatingText(0, 0f, 0f, "10", Color.White, 14f, 400L, 800L)
            assertEquals(0.5f, ft.alpha, 0.01f)
        }

        @Test
        fun `alpha is 1 when full life`() {
            val ft = FloatingText(0, 0f, 0f, "10", Color.White, 14f, 800L, 800L)
            assertEquals(1f, ft.alpha, 0.01f)
        }

        @Test
        fun `alpha is 0 when dead`() {
            val ft = FloatingText(0, 0f, 0f, "10", Color.White, 14f, 0L, 800L)
            assertEquals(0f, ft.alpha, 0.01f)
        }

        @Test
        fun `isDead is true when lifeMs is 0`() {
            val ft = FloatingText(0, 0f, 0f, "10", Color.White, 14f, 0L, 800L)
            assertTrue(ft.isDead)
        }

        @Test
        fun `isDead is false when lifeMs is positive`() {
            val ft = FloatingText(0, 0f, 0f, "10", Color.White, 14f, 100L, 800L)
            assertFalse(ft.isDead)
        }
    }

    @Nested
    inner class UpdateBehavior {
        @Test
        fun `update moves text upward and reduces lifeMs`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.None, 25)
            ))
            val before = system.activeTexts[0]
            system.update(100L)
            val after = system.activeTexts[0]

            assertTrue(after.y < before.y, "Text should move upward")
            assertTrue(after.lifeMs < before.lifeMs, "Life should decrease")
        }

        @Test
        fun `update removes dead texts`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.None, 25)
            ))
            assertEquals(1, system.activeTexts.size)

            // Fast-forward past lifetime
            system.update(1000L)
            assertEquals(0, system.activeTexts.size)
        }
    }

    @Nested
    inner class DamageTextEvents {
        @Test
        fun `ProjectileHit creates damage text with correct value`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.None, 42)
            ))
            val texts = system.activeTexts
            assertEquals(1, texts.size)
            assertEquals("42", texts[0].text)
        }

        @Test
        fun `ProjectileHit with AoE effect creates orange text`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.AoeSplash(1.5f), 30)
            ))
            assertEquals(Color(0xFFFF8F00), system.activeTexts[0].color)
        }

        @Test
        fun `ProjectileHit with Slow effect creates cyan text`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.Slow(0.5f, 2000L), 10)
            ))
            assertEquals(Color(0xFF80DEEA), system.activeTexts[0].color)
        }

        @Test
        fun `ProjectileHit with Poison effect creates green text`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.Poison(8, 500L, 3000L), 5)
            ))
            assertEquals(Color(0xFF9CCC65), system.activeTexts[0].color)
        }

        @Test
        fun `ProjectileHit with Chain effect creates yellow text`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.Chain(3), 25)
            ))
            assertEquals(Color(0xFFFFEE58), system.activeTexts[0].color)
        }

        @Test
        fun `ProjectileHit with 0 damage does not create text`() {
            system.processEvents(listOf(
                GameEvent.ProjectileHit(100f, 200f, TowerEffect.None, 0)
            ))
            assertTrue(system.activeTexts.isEmpty())
        }
    }

    @Nested
    inner class GoldTextEvents {
        @Test
        fun `EnemyKilled creates gold text with reward`() {
            system.processEvents(listOf(
                GameEvent.EnemyKilled(1, EnemyType.GOBLIN, 10, 150f, 250f)
            ))
            val texts = system.activeTexts
            assertEquals(1, texts.size)
            assertEquals("+10g", texts[0].text)
            assertEquals(Color(0xFFFFD700), texts[0].color)
        }
    }

    @Nested
    inner class StatusTextEvents {
        @Test
        fun `WaveCompleted creates centered wave complete text`() {
            system.processEvents(listOf(
                GameEvent.WaveCompleted(3, 18)
            ))
            val texts = system.activeTexts
            assertEquals(1, texts.size)
            assertEquals("Wave 3 Complete!", texts[0].text)
            assertTrue(texts[0].centered)
        }

        @Test
        fun `LivesLost creates centered lives lost text`() {
            system.processEvents(listOf(
                GameEvent.LivesLost(1, 19)
            ))
            val texts = system.activeTexts
            assertEquals(1, texts.size)
            assertEquals("-1 Life!", texts[0].text)
            assertTrue(texts[0].centered)
            assertEquals(Color(0xFFEF5350), texts[0].color)
        }
    }

    @Nested
    inner class CapacityLimit {
        @Test
        fun `does not exceed MAX_TEXTS limit`() {
            val events = (1..60).map {
                GameEvent.ProjectileHit(it.toFloat(), it.toFloat(), TowerEffect.None, it)
            }
            system.processEvents(events)
            assertTrue(system.activeTexts.size <= FloatingTextSystem.MAX_TEXTS)
        }
    }
}
