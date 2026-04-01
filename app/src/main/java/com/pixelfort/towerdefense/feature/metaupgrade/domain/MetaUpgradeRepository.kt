package com.pixelfort.towerdefense.feature.metaupgrade.domain

import com.pixelfort.towerdefense.engine.model.MetaUpgradeState
import kotlinx.coroutines.flow.Flow

interface MetaUpgradeRepository {
    fun observeState(): Flow<MetaUpgradeState>
    suspend fun getState(): MetaUpgradeState
    suspend fun purchaseUpgrade(upgradeId: String)
    suspend fun addResearchPoints(amount: Int)
}
