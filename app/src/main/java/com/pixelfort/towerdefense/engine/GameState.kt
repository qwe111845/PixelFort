package com.pixelfort.towerdefense.engine

sealed interface GameState {
    data object Loading : GameState
    data object Playing : GameState
    data object Paused : GameState
    data object Won : GameState
    data object Lost : GameState
    data object WaitingForWave : GameState
}
