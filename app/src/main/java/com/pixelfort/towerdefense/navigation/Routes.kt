package com.pixelfort.towerdefense.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object MainMenu : Routes

    @Serializable
    data object LevelSelect : Routes

    @Serializable
    data class Game(
        val levelId: Int,
        val difficulty: String = "NORMAL",
        val isEndless: Boolean = false
    ) : Routes

    @Serializable
    data object Settings : Routes

    @Serializable
    data object MetaUpgrade : Routes

    @Serializable
    data object Bestiary : Routes

    @Serializable
    data class BestiaryDetail(val enemyType: String) : Routes
}
