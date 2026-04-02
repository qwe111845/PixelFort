package com.pixelfort.towerdefense.engine.system

import com.pixelfort.towerdefense.engine.model.ActiveCombo
import com.pixelfort.towerdefense.engine.model.ComboType
import com.pixelfort.towerdefense.engine.model.Tower
import kotlin.math.abs

/**
 * SPEC-028: Detects active combos among placed towers.
 *
 * Two towers form a combo when:
 *  1. They are in adjacent cells (8-directional: abs(rowDiff) <= 1 && abs(colDiff) <= 1)
 *  2. Their types match one of the [ComboType] definitions (order-independent)
 *  3. Both towers are level 2 or higher
 *
 * A single tower may participate in multiple combos simultaneously.
 */
object ComboSystem {

    /**
     * Scans all towers and returns every active combo.
     * Pairs are deduplicated: each (towerA, towerB) combo appears only once.
     */
    fun detectCombos(towers: List<Tower>): List<ActiveCombo> {
        if (towers.size < 2) return emptyList()

        val combos = mutableListOf<ActiveCombo>()

        for (i in towers.indices) {
            for (j in i + 1 until towers.size) {
                val a = towers[i]
                val b = towers[j]

                // Both must be level 2+
                if (a.level < 2 || b.level < 2) continue

                // Must be adjacent (8-directional)
                if (!isAdjacent(a, b)) continue

                // Check all combo definitions
                for (comboType in ComboType.entries) {
                    if (comboType.matches(a.type, b.type)) {
                        combos.add(ActiveCombo(comboType, a.id, b.id))
                    }
                }
            }
        }

        return combos
    }

    /**
     * Checks whether tower [a] has a specific [comboType] active in the given combo list.
     */
    fun towerHasCombo(towerId: Int, comboType: ComboType, combos: List<ActiveCombo>): Boolean =
        combos.any { it.comboType == comboType && (it.towerIdA == towerId || it.towerIdB == towerId) }

    /**
     * Returns all combos that a specific tower participates in.
     */
    fun combosForTower(towerId: Int, combos: List<ActiveCombo>): List<ActiveCombo> =
        combos.filter { it.towerIdA == towerId || it.towerIdB == towerId }

    private fun isAdjacent(a: Tower, b: Tower): Boolean {
        val rowDiff = abs(a.gridRow - b.gridRow)
        val colDiff = abs(a.gridCol - b.gridCol)
        return rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0)
    }
}
