package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.StatusEffect
import com.pixelfort.towerdefense.engine.model.TowerEffect
import kotlin.math.sqrt

class ProjectileSystem(private val cellSize: Float) {

    data class HitEvent(
        val pixelX: Float,
        val pixelY: Float,
        val effect: TowerEffect
    )

    data class Result(
        val remainingProjectiles: List<Projectile>,
        val damagedEnemies: List<Enemy>,
        val newChainProjectiles: List<Projectile>,
        val hitEvents: List<HitEvent>
    )

    private var nextProjectileId = 10_000

    fun update(
        projectiles: List<Projectile>,
        enemies: List<Enemy>,
        deltaMs: Long,
        statusEffectSystem: StatusEffectSystem
    ): Result {
        val remaining = mutableListOf<Projectile>()
        val directDamageMap = mutableMapOf<Int, Int>()        // id -> dmg
        val effectsToApply = mutableMapOf<Int, MutableList<TowerEffect>>() // id -> effects
        val chainProjectiles = mutableListOf<Projectile>()
        val hitEvents = mutableListOf<HitEvent>()

        for (proj in projectiles) {
            val target = enemies.find { it.id == proj.targetEnemyId }
            if (target == null) continue   // target gone

            if (proj.hasReachedTarget(target.pixelX, target.pixelY, cellSize)) {
                hitEvents.add(HitEvent(target.pixelX, target.pixelY, proj.effect))
                applyHit(proj, target, enemies, directDamageMap, effectsToApply, chainProjectiles)
            } else {
                remaining.add(moveToward(proj, target.pixelX, target.pixelY, deltaMs))
            }
        }

        // Apply direct damage
        var result = enemies.map { enemy ->
            val dmg = directDamageMap[enemy.id] ?: return@map enemy
            enemy.copy(hp = enemy.hp - dmg)
        }

        // Apply status effects
        for ((enemyId, effects) in effectsToApply) {
            val idx = result.indexOfFirst { it.id == enemyId }
            if (idx < 0) continue
            var e = result[idx]
            for (eff in effects) {
                val status = effectToStatus(eff) ?: continue
                e = statusEffectSystem.applyEffect(e, status)
            }
            result = result.toMutableList().also { it[idx] = e }
        }

        return Result(remaining, result, chainProjectiles, hitEvents)
    }

    // ── Overload for tests (no statusEffectSystem needed for basic tests) ──
    fun update(projectiles: List<Projectile>, enemies: List<Enemy>, deltaMs: Long): Result =
        update(projectiles, enemies, deltaMs, StatusEffectSystem())

    private fun applyHit(
        proj: Projectile,
        primaryTarget: Enemy,
        allEnemies: List<Enemy>,
        damageMap: MutableMap<Int, Int>,
        effectsMap: MutableMap<Int, MutableList<TowerEffect>>,
        chainProjs: MutableList<Projectile>
    ) {
        when (val effect = proj.effect) {
            is TowerEffect.None -> {
                addDamage(damageMap, primaryTarget.id, proj.damage)
            }
            is TowerEffect.AoeSplash -> {
                val radiusPx = effect.radiusCells * cellSize
                allEnemies.filter { e ->
                    val dx = e.pixelX - primaryTarget.pixelX
                    val dy = e.pixelY - primaryTarget.pixelY
                    sqrt(dx * dx + dy * dy) <= radiusPx
                }.forEach { addDamage(damageMap, it.id, proj.damage) }
            }
            is TowerEffect.Slow -> {
                addDamage(damageMap, primaryTarget.id, proj.damage)
                effectsMap.getOrPut(primaryTarget.id) { mutableListOf() }.add(effect)
            }
            is TowerEffect.Poison -> {
                addDamage(damageMap, primaryTarget.id, proj.damage)
                effectsMap.getOrPut(primaryTarget.id) { mutableListOf() }.add(effect)
            }
            is TowerEffect.AoeWithSlow -> {
                val radiusPx = effect.radiusCells * cellSize
                allEnemies.filter { e ->
                    val dx = e.pixelX - primaryTarget.pixelX
                    val dy = e.pixelY - primaryTarget.pixelY
                    sqrt(dx * dx + dy * dy) <= radiusPx
                }.forEach { e ->
                    addDamage(damageMap, e.id, proj.damage)
                    effectsMap.getOrPut(e.id) { mutableListOf() }
                        .add(TowerEffect.Slow(effect.speedFactor, effect.durationMs))
                }
            }
            is TowerEffect.Chain -> {
                addDamage(damageMap, primaryTarget.id, proj.damage)
                if (effect.jumps > 0) {
                    val alreadyHit = proj.chainSourceIds + primaryTarget.id
                    val nextTarget = allEnemies
                        .filter { it.id !in alreadyHit && !it.isDead }
                        .minByOrNull {
                            val dx = it.pixelX - primaryTarget.pixelX
                            val dy = it.pixelY - primaryTarget.pixelY
                            dx * dx + dy * dy
                        }
                    if (nextTarget != null) {
                        chainProjs.add(
                            Projectile(
                                id = nextProjectileId++,
                                sourceTowerId = proj.sourceTowerId,
                                targetEnemyId = nextTarget.id,
                                damage = (proj.damage * effect.damageDecay).toInt(),
                                pixelX = primaryTarget.pixelX,
                                pixelY = primaryTarget.pixelY,
                                speed = proj.speed,
                                effect = TowerEffect.Chain(effect.jumps - 1, effect.damageDecay),
                                chainSourceIds = alreadyHit
                            )
                        )
                    }
                }
            }
        }
    }

    private fun addDamage(map: MutableMap<Int, Int>, id: Int, dmg: Int) {
        map[id] = (map[id] ?: 0) + dmg
    }

    private fun effectToStatus(eff: TowerEffect): StatusEffect? = when (eff) {
        is TowerEffect.Slow -> StatusEffect.Slowed(eff.speedFactor, eff.durationMs)
        is TowerEffect.Poison -> StatusEffect.Poisoned(eff.damagePerTick, eff.tickIntervalMs, eff.totalDurationMs)
        else -> null
    }

    private fun moveToward(proj: Projectile, targetX: Float, targetY: Float, deltaMs: Long): Projectile {
        val dx = targetX - proj.pixelX
        val dy = targetY - proj.pixelY
        val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val move = (proj.speed * cellSize * deltaMs / 1000f).coerceAtMost(dist)
        return proj.copy(
            pixelX = proj.pixelX + dx / dist * move,
            pixelY = proj.pixelY + dy / dist * move
        )
    }
}
