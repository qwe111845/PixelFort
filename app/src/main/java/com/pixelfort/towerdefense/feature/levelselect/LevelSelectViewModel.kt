package com.pixelfort.towerdefense.feature.levelselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelfort.towerdefense.feature.progress.domain.LevelProgress
import com.pixelfort.towerdefense.feature.progress.domain.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LevelSelectViewModel @Inject constructor(
    progressRepository: ProgressRepository
) : ViewModel() {

    val progressList: StateFlow<List<LevelProgress>> =
        progressRepository.observeAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}
