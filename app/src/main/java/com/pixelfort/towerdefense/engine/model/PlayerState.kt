package com.pixelfort.towerdefense.engine.model

data class PlayerState(
    val gold: Int,
    val lives: Int,
    val currentWave: Int
) {
    val isGameOver: Boolean get() = lives <= 0

    fun canAfford(cost: Int): Boolean = gold >= cost

    fun spendGold(amount: Int): PlayerState = copy(gold = gold - amount)

    fun earnGold(amount: Int): PlayerState = copy(gold = gold + amount)

    fun loseLife(): PlayerState = copy(lives = lives - 1)
}
