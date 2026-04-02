package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.ActiveCombo
import com.pixelfort.towerdefense.engine.model.ComboType
import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.StatusEffect
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerEffect
import com.pixelfort.towerdefense.engine.model.TowerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ComboDamageTest {

    private lateinit var system: ProjectileSystem
    private val cellSize = 32f

    @BeforeEach
    fun setup() {
        system = ProjectileSystem(cellSize)
    }

    private fun tower(id: Int, type: TowerType, row: Int = 0, col: Int = 0) =
        Tower(id = id, type = type, level = 2, gridRow = row, gridCol = col)

    private fun enemyAt(id: Int, x: Float, y: Float, hp: Int = 500) =
        Enemy.create(id = id, type = EnemyType.ORC).copy(
            pixelX = x, pixelY = y, hp = hp, maxHp = hp
        )

    private fun projectileHitting(
        sourceTowerId: Int,
        targetEnemyId: Int,
        damage: Int,
        targetX: Float,
        targetY: Float,
        effect: TowerEffect = TowerEffect.None
    ) = Projectile(
        id = 1,
        sourceTowerId = sourceTowerId,
        targetEnemyId = targetEnemyId,
        damage = damage,
        pixelX = targetX - 1f,  // close enough to hit
        pixelY = targetY,
        speed = 8f,
        effect = effect
    )

    @Nested
    inner class `SHATTER_ICE combo` {

        @Test
        fun `Lightning deals 2x damage to slowed enemies`() {
            val frozenEnemy = enemyAt(10, 100f, 100f, 500).copy(
                statusEffects = listOf(StatusEffect.Slowed(0.5f, 3000L))
            )
            val normalEnemy = enemyAt(11, 200f, 200f, 500)
            val towers = listOf(
                tower(1, TowerType.FROST, 0, 0),
                tower(2, TowerType.LIGHTNING, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.SHATTER_ICE, 1, 2))

            // Lightning projectile hitting frozen enemy
            val proj = projectileHitting(2, 10, 50, 100f, 100f)
            val result = system.update(
                listOf(proj), listOf(frozenEnemy, normalEnemy), 100L,
                StatusEffectSystem(), combos, towers
            )
            val damagedEnemy = result.damagedEnemies.first { it.id == 10 }
            // 2x damage: 50 * 2 = 100
            assertEquals(500 - 100, damagedEnemy.hp)
        }

        @Test
        fun `Lightning deals normal damage to non-slowed enemies`() {
            val normalEnemy = enemyAt(10, 100f, 100f, 500)
            val towers = listOf(
                tower(1, TowerType.FROST, 0, 0),
                tower(2, TowerType.LIGHTNING, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.SHATTER_ICE, 1, 2))

            val proj = projectileHitting(2, 10, 50, 100f, 100f)
            val result = system.update(
                listOf(proj), listOf(normalEnemy), 100L,
                StatusEffectSystem(), combos, towers
            )
            val damagedEnemy = result.damagedEnemies.first { it.id == 10 }
            assertEquals(500 - 50, damagedEnemy.hp)
        }
    }

    @Nested
    inner class `CROSSFIRE combo` {

        @Test
        fun `Archer gets 30 percent damage boost`() {
            val enemy = enemyAt(10, 100f, 100f, 500)
            val towers = listOf(
                tower(1, TowerType.SNIPER, 0, 0),
                tower(2, TowerType.ARCHER, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.CROSSFIRE, 1, 2))

            val proj = projectileHitting(2, 10, 100, 100f, 100f)
            val result = system.update(
                listOf(proj), listOf(enemy), 100L,
                StatusEffectSystem(), combos, towers
            )
            val damagedEnemy = result.damagedEnemies.first { it.id == 10 }
            // 100 * 1.3 = 130
            assertEquals(500 - 130, damagedEnemy.hp)
        }

        @Test
        fun `Sniper gets 30 percent damage boost`() {
            val enemy = enemyAt(10, 100f, 100f, 500)
            val towers = listOf(
                tower(1, TowerType.SNIPER, 0, 0),
                tower(2, TowerType.ARCHER, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.CROSSFIRE, 1, 2))

            val proj = projectileHitting(1, 10, 100, 100f, 100f)
            val result = system.update(
                listOf(proj), listOf(enemy), 100L,
                StatusEffectSystem(), combos, towers
            )
            val damagedEnemy = result.damagedEnemies.first { it.id == 10 }
            assertEquals(500 - 130, damagedEnemy.hp)
        }
    }

    @Nested
    inner class `TOXIC_BLAST combo` {

        @Test
        fun `Cannon AoeSplash radius increased by 50 percent`() {
            // Place two enemies: one inside original radius, one inside boosted radius
            val targetEnemy = enemyAt(10, 100f, 100f, 500)
            // Enemy at distance that is within 1.5 * 1.5 * cellSize but outside 1.5 * cellSize
            val farEnemy = enemyAt(11, 100f + cellSize * 2f, 100f, 500)
            val towers = listOf(
                tower(1, TowerType.POISON, 0, 0),
                tower(2, TowerType.CANNON, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.TOXIC_BLAST, 1, 2))

            // Cannon has AoeSplash with radiusCells = 1.5f
            val proj = projectileHitting(
                2, 10, 40, 100f, 100f,
                effect = TowerEffect.AoeSplash(radiusCells = 1.5f)
            )
            val result = system.update(
                listOf(proj), listOf(targetEnemy, farEnemy), 100L,
                StatusEffectSystem(), combos, towers
            )

            // farEnemy is at distance = 2 * cellSize = 64 px
            // Boosted radius = 1.5 * 1.5 * 32 = 72 px, so farEnemy should be hit
            val farDamaged = result.damagedEnemies.first { it.id == 11 }
            assertTrue(farDamaged.hp < 500, "Far enemy should be damaged by boosted AoE radius")
        }
    }

    @Nested
    inner class `INFERNO combo` {

        @Test
        fun `Bomb explosion radius increased by 40 percent`() {
            val targetEnemy = enemyAt(10, 100f, 100f, 500)
            // Enemy at distance within boosted radius (2.5 * 1.4 * 32 = 112 px)
            // but outside original radius (2.5 * 32 = 80 px)
            val farEnemy = enemyAt(11, 100f + cellSize * 3f, 100f, 500) // 96 px away
            val towers = listOf(
                tower(1, TowerType.CANNON, 0, 0),
                tower(2, TowerType.BOMB, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.INFERNO, 1, 2))

            val proj = projectileHitting(
                2, 10, 120, 100f, 100f,
                effect = TowerEffect.AoeSplash(radiusCells = 2.5f)
            )
            val result = system.update(
                listOf(proj), listOf(targetEnemy, farEnemy), 100L,
                StatusEffectSystem(), combos, towers
            )
            val farDamaged = result.damagedEnemies.first { it.id == 11 }
            // 3 * 32 = 96 px; boosted radius = 2.5 * 1.4 * 32 = 112 px
            assertTrue(farDamaged.hp < 500, "Far enemy should be damaged by boosted explosion radius")
        }
    }

    @Nested
    inner class `FROSTBITE combo` {

        @Test
        fun `Poison deals 2x damage to slowed enemies`() {
            val slowedEnemy = enemyAt(10, 100f, 100f, 500).copy(
                statusEffects = listOf(StatusEffect.Slowed(0.5f, 3000L))
            )
            val towers = listOf(
                tower(1, TowerType.FROST, 0, 0),
                tower(2, TowerType.POISON, 0, 1)
            )
            val combos = listOf(ActiveCombo(ComboType.FROSTBITE, 1, 2))

            val proj = projectileHitting(
                2, 10, 20, 100f, 100f,
                effect = TowerEffect.Poison(damagePerTick = 8, tickIntervalMs = 500L, totalDurationMs = 3000L)
            )
            val result = system.update(
                listOf(proj), listOf(slowedEnemy), 100L,
                StatusEffectSystem(), combos, towers
            )
            val damagedEnemy = result.damagedEnemies.first { it.id == 10 }
            // Direct damage doubled: 20 * 2 = 40
            assertEquals(500 - 40, damagedEnemy.hp)
        }
    }

    @Nested
    inner class `No combo` {

        @Test
        fun `damage is unchanged without combos`() {
            val enemy = enemyAt(10, 100f, 100f, 500)
            val proj = projectileHitting(1, 10, 50, 100f, 100f)
            val result = system.update(
                listOf(proj), listOf(enemy), 100L,
                StatusEffectSystem(), emptyList(), emptyList()
            )
            val damagedEnemy = result.damagedEnemies.first { it.id == 10 }
            assertEquals(500 - 50, damagedEnemy.hp)
        }
    }
}
