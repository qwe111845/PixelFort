package com.pixelfort.towerdefense.engine.model

enum class UpgradeCategory { COMBAT, DEFENSE, ECONOMY, UNLOCK }

data class MetaUpgrade(
    val id: String,
    val nameZh: String,
    val descriptionZh: String,
    val category: UpgradeCategory,
    val maxLevel: Int,
    val costPerLevel: Int
)

object MetaUpgrades {
    // COMBAT
    val TOWER_DAMAGE    = MetaUpgrade("tower_dmg",   "塔傷害強化",   "所有塔傷害 +5%",      UpgradeCategory.COMBAT,  5, 2)
    val TOWER_RANGE     = MetaUpgrade("tower_rng",   "射程延伸",     "所有塔射程 +0.2",     UpgradeCategory.COMBAT,  5, 2)
    val ATTACK_SPEED    = MetaUpgrade("atk_spd",     "攻擊加速",     "攻擊間隔縮短 5%",     UpgradeCategory.COMBAT,  5, 2)

    // DEFENSE
    val EXTRA_LIVES     = MetaUpgrade("extra_lives", "強化防線",     "初始生命值 +5",        UpgradeCategory.DEFENSE, 5, 2)
    val WAVE_HEAL       = MetaUpgrade("wave_heal",   "波次回復",     "每波完成恢復 1 生命",  UpgradeCategory.DEFENSE, 3, 3)

    // ECONOMY
    val START_GOLD      = MetaUpgrade("start_gold",  "充裕儲備",     "初始金幣 +50",         UpgradeCategory.ECONOMY, 5, 1)
    val GOLD_BONUS      = MetaUpgrade("gold_bonus",  "豐厚賞金",     "擊殺金幣 +10%",        UpgradeCategory.ECONOMY, 5, 2)
    val SELL_BONUS      = MetaUpgrade("sell_bonus",  "回收增益",     "出售返金比例 +5%",     UpgradeCategory.ECONOMY, 3, 2)

    // UNLOCK
    val UNLOCK_SNIPER   = MetaUpgrade("unlock_sniper",    "解鎖：狙擊塔",   "解鎖遠距狙擊塔",  UpgradeCategory.UNLOCK, 1, 3)
    val UNLOCK_FROST    = MetaUpgrade("unlock_frost",     "解鎖：冰霜塔",   "解鎖冰霜減速塔",  UpgradeCategory.UNLOCK, 1, 3)
    val UNLOCK_LIGHTNING= MetaUpgrade("unlock_lightning", "解鎖：閃電塔",   "解鎖鏈式閃電塔", UpgradeCategory.UNLOCK, 1, 4)
    val UNLOCK_POISON   = MetaUpgrade("unlock_poison",    "解鎖：毒素塔",   "解鎖毒素持傷塔",  UpgradeCategory.UNLOCK, 1, 3)
    val UNLOCK_BOMB     = MetaUpgrade("unlock_bomb",      "解鎖：炸彈塔",   "解鎖超大範圍炸彈塔", UpgradeCategory.UNLOCK, 1, 5)

    val all = listOf(
        TOWER_DAMAGE, TOWER_RANGE, ATTACK_SPEED,
        EXTRA_LIVES, WAVE_HEAL,
        START_GOLD, GOLD_BONUS, SELL_BONUS,
        UNLOCK_SNIPER, UNLOCK_FROST, UNLOCK_LIGHTNING, UNLOCK_POISON, UNLOCK_BOMB
    )
}

data class MetaUpgradeState(
    val purchasedLevels: Map<String, Int> = emptyMap(),
    val researchPoints: Int = 0
) {
    fun levelOf(upgradeId: String): Int = purchasedLevels[upgradeId] ?: 0
    fun canAfford(upgrade: MetaUpgrade): Boolean {
        val currentLevel = levelOf(upgrade.id)
        return currentLevel < upgrade.maxLevel && researchPoints >= upgrade.costPerLevel
    }
    fun purchase(upgradeId: String): MetaUpgradeState {
        val current = levelOf(upgradeId)
        val upgrade = MetaUpgrades.all.first { it.id == upgradeId }
        if (current >= upgrade.maxLevel || researchPoints < upgrade.costPerLevel) return this
        return copy(
            purchasedLevels = purchasedLevels + (upgradeId to current + 1),
            researchPoints = researchPoints - upgrade.costPerLevel
        )
    }
}

data class MetaBonus(
    val damageMultiplier: Float = 1.0f,
    val rangeBonus: Float = 0.0f,
    val fireRateMultiplier: Float = 1.0f,
    val startingGoldBonus: Int = 0,
    val goldRewardMultiplier: Float = 1.0f,
    val sellRefundRate: Float = 0.6f,
    val startingLivesBonus: Int = 0,
    val livesPerWaveBonus: Int = 0,
    val unlockedTowers: Set<TowerType> = emptySet()
) {
    companion object {
        fun from(state: MetaUpgradeState): MetaBonus {
            val lvl = { id: String -> state.levelOf(id) }
            val unlocked = buildSet<TowerType> {
                if (lvl(MetaUpgrades.UNLOCK_SNIPER.id) > 0)    add(TowerType.SNIPER)
                if (lvl(MetaUpgrades.UNLOCK_FROST.id) > 0)     add(TowerType.FROST)
                if (lvl(MetaUpgrades.UNLOCK_LIGHTNING.id) > 0) add(TowerType.LIGHTNING)
                if (lvl(MetaUpgrades.UNLOCK_POISON.id) > 0)    add(TowerType.POISON)
                if (lvl(MetaUpgrades.UNLOCK_BOMB.id) > 0)      add(TowerType.BOMB)
            }
            return MetaBonus(
                damageMultiplier    = 1f + lvl(MetaUpgrades.TOWER_DAMAGE.id) * 0.05f,
                rangeBonus          = lvl(MetaUpgrades.TOWER_RANGE.id) * 0.2f,
                fireRateMultiplier  = 1f - lvl(MetaUpgrades.ATTACK_SPEED.id) * 0.05f,
                startingGoldBonus   = lvl(MetaUpgrades.START_GOLD.id) * 50,
                goldRewardMultiplier= 1f + lvl(MetaUpgrades.GOLD_BONUS.id) * 0.10f,
                sellRefundRate      = 0.6f + lvl(MetaUpgrades.SELL_BONUS.id) * 0.05f,
                startingLivesBonus  = lvl(MetaUpgrades.EXTRA_LIVES.id) * 5,
                livesPerWaveBonus   = lvl(MetaUpgrades.WAVE_HEAL.id),
                unlockedTowers      = unlocked
            )
        }
    }
}
