package com.pixelfort.towerdefense.engine.model

/**
 * SPEC-028: Tower Combo System
 *
 * Defines the six combo types and the data class for an active combo instance.
 */
enum class ComboType(
    val towerTypeA: TowerType,
    val towerTypeB: TowerType,
    val nameZh: String,
    val descZh: String
) {
    SHATTER_ICE(
        TowerType.FROST, TowerType.LIGHTNING,
        "碎冰", "被凍結的敵人受到閃電塔 2x 傷害"
    ),
    TOXIC_BLAST(
        TowerType.POISON, TowerType.CANNON,
        "毒爆", "砲台擊中時毒雲範圍 +50%"
    ),
    CROSSFIRE(
        TowerType.SNIPER, TowerType.ARCHER,
        "交叉火力", "攻擊同一敵人時兩者傷害 +30%"
    ),
    INFERNO(
        TowerType.CANNON, TowerType.BOMB,
        "煉獄", "兩者爆炸範圍 +40%"
    ),
    ARCANE_STORM(
        TowerType.MAGIC, TowerType.LIGHTNING,
        "奧術風暴", "魔法投射物額外連鎖 1 個目標"
    ),
    FROSTBITE(
        TowerType.FROST, TowerType.POISON,
        "凍傷", "減速敵人受到 2x 毒素持續傷害"
    );

    /** Returns true if the two tower types match this combo (order-independent). */
    fun matches(typeX: TowerType, typeY: TowerType): Boolean =
        (typeX == towerTypeA && typeY == towerTypeB) ||
        (typeX == towerTypeB && typeY == towerTypeA)
}

/**
 * Represents a currently active combo between two specific placed towers.
 */
data class ActiveCombo(
    val comboType: ComboType,
    val towerIdA: Int,
    val towerIdB: Int
)
