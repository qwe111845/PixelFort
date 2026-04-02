package com.pixelfort.towerdefense.engine.model

sealed interface StatusEffect {
    data class Slowed(
        val speedFactor: Float,
        val remainingMs: Long
    ) : StatusEffect

    data class Poisoned(
        val damagePerTick: Int,
        val tickIntervalMs: Long,
        val remainingMs: Long,
        val timeSinceLastTickMs: Long = 0L
    ) : StatusEffect

    /** Boss enrage: permanent speed boost when HP drops below 30% */
    data class Enraged(
        val speedBoost: Float = 0.5f
    ) : StatusEffect
}
