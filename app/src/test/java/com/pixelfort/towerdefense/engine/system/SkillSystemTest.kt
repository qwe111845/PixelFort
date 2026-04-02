package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.SkillState
import com.pixelfort.towerdefense.engine.model.SkillType
import com.pixelfort.towerdefense.engine.model.StatusEffect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SkillSystemTest {

    private val cellSize = 80f

    private fun createSystem() = SkillSystem(cellSize)

    private fun makeEnemy(id: Int, row: Int, col: Int, hp: Int = 100): Enemy {
        return Enemy(
            id = id,
            type = EnemyType.GOBLIN,
            hp = hp,
            maxHp = hp,
            pathProgress = 0f,
            pixelX = (col + 0.5f) * cellSize,
            pixelY = (row + 0.5f) * cellSize,
            speed = 2f,
            reward = 10
        )
    }

    @Nested
    inner class `Initial state` {
        @Test
        fun `All skills start with initial cooldown`() {
            val system = createSystem()
            val skills = system.getSkills()
            assertEquals(3, skills.size)
            skills.forEach { skill ->
                assertEquals(SkillType.INITIAL_COOLDOWN_MS, skill.cooldownRemainingMs)
                assertFalse(skill.isReady)
                assertFalse(skill.isActive)
            }
        }

        @Test
        fun `Gold multiplier starts at 1`() {
            val system = createSystem()
            assertEquals(1.0f, system.getGoldMultiplier())
        }
    }

    @Nested
    inner class `Cooldown ticking` {
        @Test
        fun `Cooldown decreases over time`() {
            val system = createSystem()
            system.tickCooldowns(5_000L)
            val skills = system.getSkills()
            skills.forEach { skill ->
                assertEquals(SkillType.INITIAL_COOLDOWN_MS - 5_000L, skill.cooldownRemainingMs)
            }
        }

        @Test
        fun `Cooldown does not go below zero`() {
            val system = createSystem()
            system.tickCooldowns(20_000L)
            val skills = system.getSkills()
            skills.forEach { skill ->
                assertEquals(0L, skill.cooldownRemainingMs)
                assertTrue(skill.isReady)
            }
        }

        @Test
        fun `Active skill duration ticks down`() {
            val system = createSystem()
            // Clear initial cooldowns
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            // Use Frozen Time
            system.useSkill(SkillType.FROZEN_TIME, emptyList())

            // Tick 3 seconds
            system.tickCooldowns(3_000L)

            val frozenSkill = system.getSkills().first { it.type == SkillType.FROZEN_TIME }
            assertTrue(frozenSkill.isActive)
            assertEquals(2_000L, frozenSkill.durationRemainingMs)
        }

        @Test
        fun `Active skill deactivates when duration expires`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            system.useSkill(SkillType.FROZEN_TIME, emptyList())
            system.tickCooldowns(5_000L)

            val frozenSkill = system.getSkills().first { it.type == SkillType.FROZEN_TIME }
            assertFalse(frozenSkill.isActive)
            assertEquals(0L, frozenSkill.durationRemainingMs)
        }
    }

    @Nested
    inner class `Skill not ready` {
        @Test
        fun `UseSkill returns null when on cooldown`() {
            val system = createSystem()
            val result = system.useSkill(SkillType.FROZEN_TIME, emptyList())
            assertNull(result)
        }
    }

    @Nested
    inner class `Meteor Strike` {
        @Test
        fun `Deals damage to enemies within radius`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            val nearEnemy = makeEnemy(1, row = 3, col = 3, hp = 500)
            val farEnemy = makeEnemy(2, row = 10, col = 10, hp = 500)
            val enemies = listOf(nearEnemy, farEnemy)

            val result = system.useSkill(SkillType.METEOR_STRIKE, enemies, targetGridRow = 3, targetGridCol = 3)

            assertNotNull(result)
            // Near enemy should take 300 damage (bypasses armor)
            assertEquals(200, result!!.enemies[0].hp)
            // Far enemy should be untouched
            assertEquals(500, result.enemies[1].hp)
        }

        @Test
        fun `Returns null without target coordinates`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            val result = system.useSkill(SkillType.METEOR_STRIKE, emptyList())
            assertNull(result)
        }

        @Test
        fun `Returns meteor pixel coordinates`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            val result = system.useSkill(SkillType.METEOR_STRIKE, emptyList(), targetGridRow = 3, targetGridCol = 4)

            assertNotNull(result)
            assertEquals((4 + 0.5f) * cellSize, result!!.meteorPixelX)
            assertEquals((3 + 0.5f) * cellSize, result.meteorPixelY)
        }

        @Test
        fun `Starts cooldown after use`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            system.useSkill(SkillType.METEOR_STRIKE, emptyList(), targetGridRow = 0, targetGridCol = 0)

            val meteorSkill = system.getSkills().first { it.type == SkillType.METEOR_STRIKE }
            assertEquals(SkillType.METEOR_STRIKE.cooldownMs, meteorSkill.cooldownRemainingMs)
            assertFalse(meteorSkill.isReady)
            // Meteor has no duration so it should not be active
            assertFalse(meteorSkill.isActive)
        }
    }

    @Nested
    inner class `Frozen Time` {
        @Test
        fun `Freezes all enemies with Slowed status`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            val enemies = listOf(
                makeEnemy(1, 0, 0),
                makeEnemy(2, 5, 5)
            )
            val result = system.useSkill(SkillType.FROZEN_TIME, enemies)

            assertNotNull(result)
            result!!.enemies.forEach { enemy ->
                assertTrue(enemy.isSlowed)
                val slowEffect = enemy.statusEffects.filterIsInstance<StatusEffect.Slowed>().first()
                assertEquals(SkillType.FROZEN_SPEED_FACTOR, slowEffect.speedFactor)
                assertEquals(SkillType.FROZEN_TIME.durationMs, slowEffect.remainingMs)
            }
        }

        @Test
        fun `Sets skill to active with duration`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            system.useSkill(SkillType.FROZEN_TIME, emptyList())

            val frozenSkill = system.getSkills().first { it.type == SkillType.FROZEN_TIME }
            assertTrue(frozenSkill.isActive)
            assertEquals(SkillType.FROZEN_TIME.durationMs, frozenSkill.durationRemainingMs)
        }
    }

    @Nested
    inner class `Gold Rush` {
        @Test
        fun `Sets gold multiplier to 2x`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            val result = system.useSkill(SkillType.GOLD_RUSH, emptyList())

            assertNotNull(result)
            assertEquals(SkillType.GOLD_RUSH_MULTIPLIER, system.getGoldMultiplier())
        }

        @Test
        fun `Resets gold multiplier when duration expires`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            system.useSkill(SkillType.GOLD_RUSH, emptyList())
            assertEquals(2.0f, system.getGoldMultiplier())

            // Tick past the 10s duration
            system.tickCooldowns(10_000L)

            assertEquals(1.0f, system.getGoldMultiplier())
            val goldSkill = system.getSkills().first { it.type == SkillType.GOLD_RUSH }
            assertFalse(goldSkill.isActive)
        }

        @Test
        fun `Does not affect enemies`() {
            val system = createSystem()
            system.tickCooldowns(SkillType.INITIAL_COOLDOWN_MS)

            val enemies = listOf(makeEnemy(1, 0, 0, hp = 100))
            val result = system.useSkill(SkillType.GOLD_RUSH, enemies)

            assertNotNull(result)
            assertEquals(100, result!!.enemies[0].hp)
            assertTrue(result.enemies[0].statusEffects.isEmpty())
        }
    }

    @Nested
    inner class `SkillState model` {
        @Test
        fun `isReady is true when cooldown is zero and not active`() {
            val state = SkillState(SkillType.METEOR_STRIKE, cooldownRemainingMs = 0L)
            assertTrue(state.isReady)
        }

        @Test
        fun `isReady is false when on cooldown`() {
            val state = SkillState(SkillType.METEOR_STRIKE, cooldownRemainingMs = 5000L)
            assertFalse(state.isReady)
        }

        @Test
        fun `isReady is false when active`() {
            val state = SkillState(SkillType.FROZEN_TIME, cooldownRemainingMs = 0L, isActive = true)
            assertFalse(state.isReady)
        }

        @Test
        fun `cooldownFraction calculated correctly`() {
            val state = SkillState(
                SkillType.METEOR_STRIKE,
                cooldownRemainingMs = 30_000L
            )
            assertEquals(0.5f, state.cooldownFraction, 0.001f)
        }
    }
}
