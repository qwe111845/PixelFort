package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.StatusEffect

class StatusEffectSystem {

    data class Result(
        val updatedEnemies: List<Enemy>,
        val poisonDamageMap: Map<Int, Int>,   // enemyId -> total poison damage this tick
        val newlyEnragedIds: List<Int> = emptyList()  // enemy IDs that just enraged
    )

    fun update(enemies: List<Enemy>, deltaMs: Long): Result {
        val poisonDamage = mutableMapOf<Int, Int>()
        val newlyEnraged = mutableListOf<Int>()

        val updated = enemies.map { enemy ->
            val newEffects = mutableListOf<StatusEffect>()

            for (effect in enemy.statusEffects) {
                when (effect) {
                    is StatusEffect.Slowed -> {
                        val remaining = effect.remainingMs - deltaMs
                        if (remaining > 0) newEffects.add(effect.copy(remainingMs = remaining))
                    }
                    is StatusEffect.Poisoned -> {
                        val newTimeSinceTick = effect.timeSinceLastTickMs + deltaMs
                        val ticks = (newTimeSinceTick / effect.tickIntervalMs).toInt()
                        val remaining = effect.remainingMs - deltaMs

                        if (ticks > 0) {
                            poisonDamage[enemy.id] =
                                (poisonDamage[enemy.id] ?: 0) + effect.damagePerTick * ticks
                        }

                        if (remaining > 0) {
                            newEffects.add(
                                effect.copy(
                                    remainingMs = remaining,
                                    timeSinceLastTickMs = newTimeSinceTick % effect.tickIntervalMs
                                )
                            )
                        }
                    }
                    is StatusEffect.Enraged -> {
                        // Enraged is permanent, always keep it
                        newEffects.add(effect)
                    }
                }
            }

            enemy.copy(statusEffects = newEffects)
        }

        // Apply poison damage (bypasses armor)
        val withPoison = updated.map { enemy ->
            val dmg = poisonDamage[enemy.id] ?: return@map enemy
            enemy.copy(hp = enemy.hp - dmg)
        }

        // Check for boss enrage at 30% HP
        val withEnrage = withPoison.map { enemy ->
            if (enemy.isBoss && !enemy.isEnraged && enemy.hpPercentage <= 0.3f && !enemy.isDead) {
                newlyEnraged.add(enemy.id)
                applyEffect(enemy, StatusEffect.Enraged())
            } else {
                enemy
            }
        }

        return Result(withEnrage, poisonDamage, newlyEnraged)
    }

    /** Apply a new status effect to an enemy, stacking or refreshing as appropriate. */
    fun applyEffect(enemy: Enemy, newEffect: StatusEffect): Enemy {
        val existing = enemy.statusEffects.toMutableList()
        when (newEffect) {
            is StatusEffect.Slowed -> {
                // Keep strongest slow; refresh if new is stronger or longer
                val idx = existing.indexOfFirst { it is StatusEffect.Slowed }
                if (idx >= 0) {
                    val cur = existing[idx] as StatusEffect.Slowed
                    // Take the more severe slow factor and longer duration
                    existing[idx] = StatusEffect.Slowed(
                        speedFactor = minOf(cur.speedFactor, newEffect.speedFactor),
                        remainingMs = maxOf(cur.remainingMs, newEffect.remainingMs)
                    )
                } else {
                    existing.add(newEffect)
                }
            }
            is StatusEffect.Poisoned -> {
                // Refresh poison (reset timer if already poisoned)
                val idx = existing.indexOfFirst { it is StatusEffect.Poisoned }
                if (idx >= 0) {
                    existing[idx] = newEffect   // refresh
                } else {
                    existing.add(newEffect)
                }
            }
            is StatusEffect.Enraged -> {
                // Enraged is permanent, only apply once
                if (existing.none { it is StatusEffect.Enraged }) {
                    existing.add(newEffect)
                }
            }
        }
        return enemy.copy(statusEffects = existing)
    }
}
