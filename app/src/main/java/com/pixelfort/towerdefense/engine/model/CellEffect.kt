package com.pixelfort.towerdefense.engine.model

/**
 * Effects that can be applied to specific grid cells.
 * When an enemy occupies a cell with an effect, the effect is applied.
 */
sealed interface CellEffect {
    /** Deals [damage] HP to any enemy passing through the cell. */
    data class LavaDamage(val damage: Int = 10) : CellEffect

    /** Teleports the enemy forward to [targetWaypointIndex] in the path. */
    data class Teleport(val targetWaypointIndex: Int) : CellEffect
}
