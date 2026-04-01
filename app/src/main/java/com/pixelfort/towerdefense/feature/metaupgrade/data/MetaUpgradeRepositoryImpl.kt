package com.pixelfort.towerdefense.feature.metaupgrade.data

import com.pixelfort.towerdefense.core.database.dao.MetaUpgradeDao
import com.pixelfort.towerdefense.core.database.entity.MetaUpgradeEntity
import com.pixelfort.towerdefense.core.database.entity.ResearchPointsEntity
import com.pixelfort.towerdefense.engine.model.MetaUpgrade
import com.pixelfort.towerdefense.engine.model.MetaUpgrades
import com.pixelfort.towerdefense.engine.model.MetaUpgradeState
import com.pixelfort.towerdefense.feature.metaupgrade.domain.MetaUpgradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class MetaUpgradeRepositoryImpl @Inject constructor(
    private val dao: MetaUpgradeDao
) : MetaUpgradeRepository {

    override fun observeState(): Flow<MetaUpgradeState> =
        combine(dao.observeAll(), dao.observePoints()) { upgrades, rp ->
            MetaUpgradeState(
                purchasedLevels = upgrades.associate { it.upgradeId to it.level },
                researchPoints = rp?.points ?: 0
            )
        }

    override suspend fun getState(): MetaUpgradeState {
        val upgrades = dao.getAll()
        val rp = dao.getPoints()
        return MetaUpgradeState(
            purchasedLevels = upgrades.associate { it.upgradeId to it.level },
            researchPoints = rp?.points ?: 0
        )
    }

    override suspend fun purchaseUpgrade(upgradeId: String) {
        val state = getState()
        val upgrade = MetaUpgrades.all.firstOrNull { it.id == upgradeId } ?: return
        val newState = state.purchase(upgradeId)
        if (newState == state) return  // no change (can't afford or max level)
        val newLevel = newState.levelOf(upgradeId)
        dao.upsert(MetaUpgradeEntity(upgradeId, newLevel))
        dao.upsertPoints(ResearchPointsEntity(points = newState.researchPoints))
    }

    override suspend fun addResearchPoints(amount: Int) {
        val current = dao.getPoints()?.points ?: 0
        dao.upsertPoints(ResearchPointsEntity(points = current + amount))
    }
}
