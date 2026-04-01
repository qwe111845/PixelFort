package com.pixelfort.towerdefense.feature.metaupgrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelfort.towerdefense.engine.model.MetaUpgradeState
import com.pixelfort.towerdefense.feature.metaupgrade.domain.MetaUpgradeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetaUpgradeViewModel @Inject constructor(
    private val repository: MetaUpgradeRepository
) : ViewModel() {

    val state: StateFlow<MetaUpgradeState> =
        repository.observeState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MetaUpgradeState()
            )

    fun purchase(upgradeId: String) {
        viewModelScope.launch {
            repository.purchaseUpgrade(upgradeId)
        }
    }
}
