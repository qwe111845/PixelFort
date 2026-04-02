package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.SkillState
import com.pixelfort.towerdefense.engine.model.SkillType
import com.pixelfort.towerdefense.engine.model.StatusEffect
import kotlin.math.sqrt

class SkillSystem(
    private val cellSize: Float
) {

    data class SkillResult(
        val skills: List<SkillState>,
        val enemies: List<Enemy>,
        val goldMultiplier: Float,
        val skillUsedType: SkillType? = null,
        /** Pixel coords for meteor VFX */
        val meteorPixelX: Float? = null,
        val meteorPixelY: Float? = null
    )

    private var skills = SkillType.entries.map { SkillState(type = it) }
    private var goldMultiplier = 1.0f

    fun getSkills(): List<SkillState> = skills
    fun getGoldMultiplier(): Float = goldMultiplier

    /**
     * Tick cooldowns and active durations. Uses real-time delta (not scaled).
     */
    fun tickCooldowns(realDeltaMs: Long) {
        skills = skills.map { skill ->
            var updated = skill
            // Tick cooldown
            if (updated.cooldownRemainingMs > 0) {
                updated = updated.copy(
                    cooldownRemainingMs = (updated.cooldownRemainingMs - realDeltaMs).coerceAtLeast(0L)
                )
            }
            // Tick active duration
            if (updated.isActive && updated.durationRemainingMs > 0) {
                val newDuration = (updated.durationRemainingMs - realDeltaMs).coerceAtLeast(0L)
                updated = updated.copy(durationRemainingMs = newDuration)
                if (newDuration <= 0L) {
                    updated = updated.copy(isActive = false)
                    // Deactivate Gold Rush multiplier
                    if (updated.type == SkillType.GOLD_RUSH) {
                        goldMultiplier = 1.0f
                    }
                }
            }
            updated
        }
    }

    /**
     * Attempt to use a skill. Returns a SkillResult with updated enemies/multiplier.
     * Returns null if skill is not ready.
     */
    fun useSkill(
        type: SkillType,
        enemies: List<Enemy>,
        targetGridRow: Int? = null,
        targetGridCol: Int? = null
    ): SkillResult? {
        val skillIndex = skills.indexOfFirst { it.type == type }
        val skill = skills[skillIndex]
        if (!skill.isReady) return null

        var updatedEnemies = enemies
        var meteorPx: Float? = null
        var meteorPy: Float? = null

        when (type) {
            SkillType.METEOR_STRIKE -> {
                if (targetGridRow == null || targetGridCol == null) return null
                val centerX = (targetGridCol + 0.5f) * cellSize
                val centerY = (targetGridRow + 0.5f) * cellSize
                meteorPx = centerX
                meteorPy = centerY
                val radiusPx = SkillType.METEOR_RADIUS_CELLS * cellSize
                updatedEnemies = enemies.map { enemy ->
                    val dx = enemy.pixelX - centerX
                    val dy = enemy.pixelY - centerY
                    val dist = sqrt(dx * dx + dy * dy)
                    if (dist <= radiusPx) {
                        val dmg = enemy.calculateDamage(SkillType.METEOR_DAMAGE, bypassArmor = true)
                        enemy.copy(hp = enemy.hp - dmg)
                    } else {
                        enemy
                    }
                }
            }

            SkillType.FROZEN_TIME -> {
                val freezeEffect = StatusEffect.Slowed(
                    speedFactor = SkillType.FROZEN_SPEED_FACTOR,
                    remainingMs = type.durationMs
                )
                updatedEnemies = enemies.map { enemy ->
                    enemy.copy(
                        statusEffects = enemy.statusEffects + freezeEffect
                    )
                }
            }

            SkillType.GOLD_RUSH -> {
                goldMultiplier = SkillType.GOLD_RUSH_MULTIPLIER
            }
        }

        // Update skill state: start cooldown, activate if has duration
        val newSkill = skill.copy(
            cooldownRemainingMs = type.cooldownMs,
            isActive = type.durationMs > 0,
            durationRemainingMs = type.durationMs
        )
        skills = skills.toMutableList().apply { set(skillIndex, newSkill) }

        return SkillResult(
            skills = skills,
            enemies = updatedEnemies,
            goldMultiplier = goldMultiplier,
            skillUsedType = type,
            meteorPixelX = meteorPx,
            meteorPixelY = meteorPy
        )
    }
}
