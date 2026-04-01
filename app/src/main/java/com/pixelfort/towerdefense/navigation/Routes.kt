package com.pixelfort.towerdefense.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object MainMenu : Routes

    @Serializable
    data object LevelSelect : Routes

    @Serializable
    data class Game(val levelId: Int) : Routes

    @Serializable
    data object Settings : Routes

    @Serializable
    data object MetaUpgrade : Routes
}
