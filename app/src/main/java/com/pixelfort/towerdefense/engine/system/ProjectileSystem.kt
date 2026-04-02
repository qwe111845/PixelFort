package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.ActiveCombo
import com.pixelfort.towerdefense.engine.model.ComboType
import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.StatusEffect
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerEffect
import com.pixelfort.towerdefense.engine.model.TowerType
import kotlin.math.sqrt

class ProjectileSystem(private val cellSize: Float) {

    data class HitEvent(
        val pixelX: Float,
        val pixelY: Float,
        val effect: TowerEffect,
        val damage: Int = 0
    )

    data class Result(
        val remainingProjectiles: List<Projectile>,
        val damagedEnemies: List<Enemy>,
        val newChainProjectiles: List<Projectile>,
        val hitEvents: List<HitEvent>
    )

    private var nextProjectileId = 10_000

    /**
     * @param projectileSpeedMult SPEC-032 wave-event projectile speed multiplier (default 1.0)
     */
    fun update(
        projectiles: List<Projectile>,
        enemies: List<Enemy>,
        deltaMs: Long,
        statusEffectSystem: StatusEffectSystem,
        activeCombos: List<ActiveCombo> = emptyList(),
        towers: List<Tower> = emptyList(),
        projectileSpeedMult: Float = 1f
    ): Result {
        val remaining = mutableListOf<Projectile>()
        val directDamageMap = mutableMapOf<Int, Int>()        // id -> dmg
        val effectsToApply = mutableMapOf<Int, MutableList<TowerEffect>>() // id -> effects
        val chainProjectiles = mutableListOf<Projectile>()
        val hitEvents = mutableListOf<HitEvent>()

        // Build tower lookup for combo logic
        val towerById = towers.associateBy { it.id }

        for (proj in projectiles) {
            val target = enemies.find { it.id == proj.targetEnemyId }
            if (target == null) continue   // target gone

            if (proj.hasReachedTarget(target.pixelX, target.pixelY, cellSize)) {
                // Apply combo-modified projectile before hit
                val comboProj = applyComboModifiers(proj, target, activeCombos, towerById)
                hitEvents.add(HitEvent(target.pixelX, target.pixelY, comboProj.effect, comboProj.damage))
                applyHit(comboProj, target, enemies, directDamageMap, effectsToApply, chainProjectiles, activeCombos, towerById)
            } else {
                remaining.add(moveToward(proj, target.pixelX, target.pixelY, deltaMs, projectileSpeedMult))
            }
        }

        // Apply direct damage (with armor reduction)
        var result = enemies.map { enemy ->
            val rawDmg = directDamageMap[enemy.id] ?: return@map enemy
            val actualDmg = enemy.calculateDamage(rawDmg)
            enemy.copy(hp = enemy.hp - actualDmg)
        }

        // Apply status effects (with combo-boosted poison for Frostbite)
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

    /**
     * SPEC-028: Apply combo damage/effect modifiers to a projectile before hit resolution.
     *
     * - SHATTER_ICE: Lightning tower deals 2x damage to frozen (slowed) enemies
     * - CROSSFIRE: Sniper & Archer +30% damage when targeting same enemy
     * - TOXIC_BLAST: Cannon AoeSplash radius +50% when poison combo active
     * - INFERNO: Cannon & Bomb explosion radius +40%
     * - ARCANE_STORM: Magic chain jumps +1
     * - FROSTBITE: Poison tick damage 2x on slowed enemies
     */
    private fun applyComboModifiers(
        proj: Projectile,
        target: Enemy,
        combos: List<ActiveCombo>,
        towerById: Map<Int, Tower>
    ): Projectile {
        if (combos.isEmpty()) return proj

        val sourceTower = towerById[proj.sourceTowerId] ?: return proj
        val towerCombos = ComboSystem.combosForTower(proj.sourceTowerId, combos)
        if (towerCombos.isEmpty()) return proj

        var damage = proj.damage
        var effect = proj.effect

        for (combo in towerCombos) {
            when (combo.comboType) {
                ComboType.SHATTER_ICE -> {
                    // Lightning tower deals 2x damage to frozen/slowed enemies
                    if (sourceTower.type == TowerType.LIGHTNING && target.isSlowed) {
                        damage = (damage * 2)
                    }
                }
                ComboType.CROSSFIRE -> {
                    // Both Sniper and Archer get +30% damage
                    if (sourceTower.type == TowerType.SNIPER || sourceTower.type == TowerType.ARCHER) {
                        damage = (damage * 1.3f).toInt()
                    }
                }
                ComboType.TOXIC_BLAST -> {
                    // Cannon hit: poison cloud radius +50%
                    if (sourceTower.type == TowerType.CANNON && effect is TowerEffect.AoeSplash) {
                        val splash = effect as TowerEffect.AoeSplash
                        effect = TowerEffect.AoeSplash(radiusCells = splash.radiusCells * 1.5f)
                    }
                }
                ComboType.INFERNO -> {
                    // Both Cannon and Bomb: explosion radius +40%
                    if (sourceTower.type == TowerType.CANNON || sourceTower.type == TowerType.BOMB) {
                        when (effect) {
                            is TowerEffect.AoeSplash -> {
                                effect = TowerEffect.AoeSplash(
                                    radiusCells = (effect as TowerEffect.AoeSplash).radiusCells * 1.4f
                                )
                            }
                            else -> { /* no-op */ }
                        }
                    }
                }
                ComboType.ARCANE_STORM -> {
                    // Magic projectiles chain to 1 extra target
                    if (sourceTower.type == TowerType.MAGIC && effect is TowerEffect.Chain) {
                        val chain = effect as TowerEffect.Chain
                        effect = TowerEffect.Chain(
                            jumps = chain.jumps + 1,
                            damageDecay = chain.damageDecay
                        )
                    }
                    // If Magic tower has Slow effect normally, also add chain
                    if (sourceTower.type == TowerType.MAGIC && effect is TowerEffect.Slow) {
                        // Magic tower's base effect is Slow; with Arcane Storm it also chains
                        // We keep Slow as primary but this combo note is handled differently:
                        // The spec says "magic projectiles chain to 1 extra target"
                        // This means we should add chaining capability.
                        // Since TowerEffect is a sealed interface, we handle this in applyHit
                    }
                }
                ComboType.FROSTBITE -> {
                    // Poison tower deals 2x tick damage to slowed enemies
                    if (sourceTower.type == TowerType.POISON && target.isSlowed) {
                        when (effect) {
                            is TowerEffect.Poison -> {
                                val poison = effect as TowerEffect.Poison
                                effect = TowerEffect.Poison(
                                    damagePerTick = poison.damagePerTick * 2,
                                    tickIntervalMs = poison.tickIntervalMs,
                                    totalDurationMs = poison.totalDurationMs
                                )
                            }
                            else -> { /* no-op */ }
                        }
                        // Also double the direct hit damage
                        damage = (damage * 2)
                    }
                }
            }
        }

        return proj.copy(damage = damage, effect = effect)
    }

    private fun applyHit(
        proj: Projectile,
        primaryTarget: Enemy,
        allEnemies: List<Enemy>,
        damageMap: MutableMap<Int, Int>,
        effectsMap: MutableMap<Int, MutableList<TowerEffect>>,
        chainProjs: MutableList<Projectile>,
        activeCombos: List<ActiveCombo> = emptyList(),
        towerById: Map<Int, Tower> = emptyMap()
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
                // SPEC-028 Arcane Storm: Magic tower with Slow effect chains to 1 extra target
                val sourceTower = towerById[proj.sourceTowerId]
                if (sourceTower?.type == TowerType.MAGIC &&
                    ComboSystem.towerHasCombo(proj.sourceTowerId, ComboType.ARCANE_STORM, activeCombos)) {
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
                                damage = (proj.damage * 0.6f).toInt(),
                                pixelX = primaryTarget.pixelX,
                                pixelY = primaryTarget.pixelY,
                                speed = proj.speed,
                                effect = effect,
                                chainSourceIds = alreadyHit
                            )
                        )
                    }
                }
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

    private fun moveToward(
        proj: Projectile, targetX: Float, targetY: Float, deltaMs: Long,
        speedMult: Float = 1f
    ): Projectile {
        val dx = targetX - proj.pixelX
        val dy = targetY - proj.pixelY
        val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val move = (proj.speed * speedMult * cellSize * deltaMs / 1000f).coerceAtMost(dist)
        return proj.copy(
            pixelX = proj.pixelX + dx / dist * move,
            pixelY = proj.pixelY + dy / dist * move
        )
    }
}
