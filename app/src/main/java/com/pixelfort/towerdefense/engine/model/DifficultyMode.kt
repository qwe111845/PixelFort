package com.pixelfort.towerdefense.engine.model

enum class DifficultyMode(
    val nameZh: String,
    val hpMultiplier: Float,
    val speedMultiplier: Float,
    val startingGoldBonus: Int,
    val startingLivesBonus: Int,
    val goldRewardMultiplier: Float,
    val rpMultiplier: Float
) {
    EASY(
        nameZh = "簡單",
        hpMultiplier = 0.75f,
        speedMultiplier = 0.9f,
        startingGoldBonus = 100,
        startingLivesBonus = 10,
        goldRewardMultiplier = 1.2f,
        rpMultiplier = 0.5f
    ),
    NORMAL(
        nameZh = "普通",
        hpMultiplier = 1.0f,
        speedMultiplier = 1.0f,
        startingGoldBonus = 0,
        startingLivesBonus = 0,
        goldRewardMultiplier = 1.0f,
        rpMultiplier = 1.0f
    ),
    HARD(
        nameZh = "困難",
        hpMultiplier = 1.5f,
        speedMultiplier = 1.15f,
        startingGoldBonus = -50,
        startingLivesBonus = -5,
        goldRewardMultiplier = 0.8f,
        rpMultiplier = 2.0f
    )
}
