package com.pixelfort.towerdefense.engine.model

data class TowerStats(
    val damage: Int,
    val range: Float,
    val fireRateMs: Long,
    val cost: Int,
    val effect: TowerEffect = TowerEffect.None
)

enum class TowerType(
    private val baseDamage: Int,
    private val baseRange: Float,
    private val baseFireRateMs: Long,
    val baseCost: Int,
    private val baseEffect: TowerEffect,
    val nameZh: String,
    val descZh: String,
    val isLockedByDefault: Boolean = false
) {
    ARCHER(
        baseDamage = 20, baseRange = 3.5f, baseFireRateMs = 800L, baseCost = 100,
        baseEffect = TowerEffect.None,
        nameZh = "弓箭手", descZh = "快速單體攻擊，適合守住入口"
    ),
    CANNON(
        baseDamage = 40, baseRange = 2.5f, baseFireRateMs = 1500L, baseCost = 150,
        baseEffect = TowerEffect.AoeSplash(radiusCells = 1.5f),
        nameZh = "砲台", descZh = "爆炸傷害波及周圍敵人，對密集敵人有奇效"
    ),
    MAGIC(
        baseDamage = 10, baseRange = 3.0f, baseFireRateMs = 1000L, baseCost = 200,
        baseEffect = TowerEffect.Slow(speedFactor = 0.5f, durationMs = 2500L),
        nameZh = "魔法塔", descZh = "減速敵人50%，配合其他塔效果加倍"
    ),
    SNIPER(
        baseDamage = 80, baseRange = 6.0f, baseFireRateMs = 2500L, baseCost = 175,
        baseEffect = TowerEffect.None,
        nameZh = "狙擊手", descZh = "超遠距離，高單體傷害，優先攻擊最遠敵人",
        isLockedByDefault = true
    ),
    FROST(
        baseDamage = 12, baseRange = 2.8f, baseFireRateMs = 1100L, baseCost = 225,
        baseEffect = TowerEffect.AoeWithSlow(
            radiusCells = 1.2f, speedFactor = 0.35f, durationMs = 3000L
        ),
        nameZh = "冰霜塔", descZh = "範圍冰凍，大幅減速周圍敵人65%",
        isLockedByDefault = true
    ),
    LIGHTNING(
        baseDamage = 25, baseRange = 3.5f, baseFireRateMs = 900L, baseCost = 250,
        baseEffect = TowerEffect.Chain(jumps = 3, damageDecay = 0.6f),
        nameZh = "閃電塔", descZh = "閃電鏈傷害3個敵人，每跳衰減40%",
        isLockedByDefault = true
    ),
    POISON(
        baseDamage = 5, baseRange = 3.0f, baseFireRateMs = 800L, baseCost = 175,
        baseEffect = TowerEffect.Poison(
            damagePerTick = 8, tickIntervalMs = 500L, totalDurationMs = 3000L
        ),
        nameZh = "毒素塔", descZh = "中毒持續掉血3秒，對高血量Boss尤其有效",
        isLockedByDefault = true
    ),
    BOMB(
        baseDamage = 120, baseRange = 2.0f, baseFireRateMs = 4000L, baseCost = 300,
        baseEffect = TowerEffect.AoeSplash(radiusCells = 2.5f),
        nameZh = "炸彈塔", descZh = "超大範圍爆炸，清場利器，冷卻時間長",
        isLockedByDefault = true
    );

    fun statsForLevel(level: Int, metaBonus: MetaBonus = MetaBonus()): TowerStats {
        val damageMultiplier = Math.pow(1.5, (level - 1).toDouble()).toFloat()
        val rangeBonus = 0.3f * (level - 1)
        val fireRateMultiplier = Math.pow(0.9, (level - 1).toDouble()).toFloat()
        return TowerStats(
            damage = (baseDamage * damageMultiplier * metaBonus.damageMultiplier).toInt(),
            range = baseRange + rangeBonus + metaBonus.rangeBonus,
            fireRateMs = (baseFireRateMs * fireRateMultiplier * metaBonus.fireRateMultiplier).toLong(),
            cost = baseCost,
            effect = baseEffect
        )
    }

    // Overload for tests (no meta bonus)
    fun statsForLevel(level: Int): TowerStats = statsForLevel(level, MetaBonus())
}

data class Tower(
    val id: Int,
    val type: TowerType,
    val level: Int,
    val gridRow: Int,
    val gridCol: Int,
    val cooldownRemainingMs: Long = 0,
    val metaBonus: MetaBonus = MetaBonus()
) {
    val stats: TowerStats get() = type.statsForLevel(level, metaBonus)

    val upgradeCost: Int get() = type.baseCost * level

    val sellValue: Int
        get() {
            var totalInvested = type.baseCost
            for (l in 1 until level) totalInvested += type.baseCost * l
            return (totalInvested * metaBonus.sellRefundRate).toInt()
        }

    val isMaxLevel: Boolean get() = level >= MAX_LEVEL

    companion object {
        const val MAX_LEVEL = 3
    }
}
