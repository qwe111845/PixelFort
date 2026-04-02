package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.ActiveWaveEvent
import com.pixelfort.towerdefense.engine.model.WaveEventType
import kotlin.random.Random

/**
 * SPEC-032: Decides whether a random event triggers before a wave and aggregates
 * modifier values for systems that need them each frame.
 */
object WaveEventSystem {

    /** Probability of an event firing (40 %). */
    private const val NORMAL_CHANCE = 0.40f

    /**
     * Roll for a random wave event.
     *
     * @param waveNumber 0-based wave index about to start.
     * @param isEndless  true when playing endless mode.
     * @param enabled    false when the user has toggled events off in Settings.
     * @param random     injectable [Random] for testability.
     * @return the rolled [WaveEventType], or `null` if no event triggers.
     */
    fun rollEvent(
        waveNumber: Int,
        isEndless: Boolean,
        enabled: Boolean,
        random: Random = Random
    ): WaveEventType? {
        // Events never fire on wave 0 (the very first wave) or when disabled
        if (!enabled || waveNumber < 1) return null

        val chance = if (isEndless && waveNumber >= 5) 1.0f else NORMAL_CHANCE

        if (random.nextFloat() >= chance) return null

        val types = WaveEventType.entries
        return types[random.nextInt(types.size)]
    }

    /**
     * Aggregate all active events into a single [EventModifiers] value.
     * Multipliers are multiplied together so multiple events stack multiplicatively.
     */
    fun getModifiers(activeEvents: List<ActiveWaveEvent>): EventModifiers {
        if (activeEvents.isEmpty()) return EventModifiers.NONE
        var projSpeed = 1f
        var gold = 1f
        var enemyHp = 1f
        var range = 1f
        var fireRate = 1f
        var enemySpeed = 1f
        for (event in activeEvents) {
            projSpeed *= event.type.projectileSpeedMult
            gold *= event.type.goldMult
            enemyHp *= event.type.enemyHpMult
            range *= event.type.rangeMult
            fireRate *= event.type.fireRateMult
            enemySpeed *= event.type.enemySpeedMult
        }
        return EventModifiers(projSpeed, gold, enemyHp, range, fireRate, enemySpeed)
    }
}

/**
 * Aggregated multipliers from all active wave events for the current frame.
 * All values default to 1.0 (no change).
 */
data class EventModifiers(
    val projectileSpeedMult: Float = 1f,
    val goldMult: Float = 1f,
    val enemyHpMult: Float = 1f,
    val rangeMult: Float = 1f,
    val fireRateMult: Float = 1f,
    val enemySpeedMult: Float = 1f
) {
    companion object {
        val NONE = EventModifiers()
    }
}
