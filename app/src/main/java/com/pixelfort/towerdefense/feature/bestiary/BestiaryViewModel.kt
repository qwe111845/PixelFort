package com.pixelfort.towerdefense.feature.bestiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelfort.towerdefense.core.database.dao.BestiaryDao
import com.pixelfort.towerdefense.engine.model.BestiaryEntry
import com.pixelfort.towerdefense.engine.model.EnemyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BestiaryViewModel @Inject constructor(
    bestiaryDao: BestiaryDao
) : ViewModel() {

    val entries: StateFlow<List<BestiaryEntry>> = bestiaryDao.observeAll()
        .map { entities ->
            val entityMap = entities.associateBy { it.enemyType }
            BestiaryEntry.allEntries().map { entry ->
                val saved = entityMap[entry.enemyType.name]
                if (saved != null) {
                    entry.copy(
                        defeatedCount = saved.defeatedCount,
                        firstDefeatedAt = saved.firstDefeatedAt,
                        maxHitDamage = saved.maxHitDamage
                    )
                } else {
                    entry
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), BestiaryEntry.allEntries())
}
