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
}
