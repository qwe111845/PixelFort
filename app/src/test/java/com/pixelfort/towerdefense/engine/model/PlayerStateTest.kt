package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerStateTest {

    @Test
    fun `PlayerState stores gold and lives`() {
        val state = PlayerState(gold = 200, lives = 20, currentWave = 0)
        assertEquals(200, state.gold)
        assertEquals(20, state.lives)
        assertEquals(0, state.currentWave)
    }

    @Test
    fun `canAfford returns true when gold is sufficient`() {
        val state = PlayerState(gold = 100, lives = 20, currentWave = 0)
        assertTrue(state.canAfford(100))
        assertTrue(state.canAfford(50))
    }

    @Test
    fun `canAfford returns false when gold is insufficient`() {
        val state = PlayerState(gold = 50, lives = 20, currentWave = 0)
        assertFalse(state.canAfford(51))
    }

    @Test
    fun `spendGold reduces gold`() {
        val state = PlayerState(gold = 100, lives = 20, currentWave = 0)
        val newState = state.spendGold(60)
        assertEquals(40, newState.gold)
    }

    @Test
    fun `earnGold increases gold`() {
        val state = PlayerState(gold = 100, lives = 20, currentWave = 0)
        val newState = state.earnGold(25)
        assertEquals(125, newState.gold)
    }

    @Test
    fun `loseLife reduces lives by 1`() {
        val state = PlayerState(gold = 100, lives = 5, currentWave = 0)
        val newState = state.loseLife()
        assertEquals(4, newState.lives)
    }

    @Test
    fun `isGameOver when lives reach zero`() {
        val state = PlayerState(gold = 100, lives = 0, currentWave = 0)
        assertTrue(state.isGameOver)
    }

    @Test
    fun `isGameOver is false when lives are positive`() {
        val state = PlayerState(gold = 0, lives = 1, currentWave = 0)
        assertFalse(state.isGameOver)
    }
}
