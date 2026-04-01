package com.pixelfort.towerdefense.engine.model

sealed interface TowerEffect {
    data object None : TowerEffect
    data class AoeSplash(val radiusCells: Float) : TowerEffect
    data class Slow(val speedFactor: Float, val durationMs: Long) : TowerEffect
    data class Poison(
        val damagePerTick: Int,
        val tickIntervalMs: Long,
        val totalDurationMs: Long
    ) : TowerEffect
    data class Chain(val jumps: Int, val damageDecay: Float = 0.6f) : TowerEffect
    data class AoeWithSlow(
        val radiusCells: Float,
        val speedFactor: Float,
        val durationMs: Long
    ) : TowerEffect
}
