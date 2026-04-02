package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.model.TowerEffect

class FloatingTextSystem {

    private val texts = mutableListOf<FloatingText>()
    private var nextId = 0

    val activeTexts: List<FloatingText> get() = texts.toList()

    fun update(deltaMs: Long) {
        texts.removeAll { it.isDead }
        val dt = deltaMs / 1000f
        for (i in texts.indices) {
            val t = texts[i]
            texts[i] = t.copy(
                y = t.y + t.vy * dt,
                lifeMs = t.lifeMs - deltaMs
            )
        }
    }

    fun processEvents(events: List<GameEvent>) {
        for (event in events) {
            when (event) {
                is GameEvent.ProjectileHit -> emitDamageText(event)
                is GameEvent.EnemyKilled -> emitGoldText(event)
                is GameEvent.WaveCompleted -> emitWaveCompleteText(event)
                is GameEvent.LivesLost -> emitLivesLostText(event)
                else -> Unit
            }
        }
    }

    private fun emitDamageText(event: GameEvent.ProjectileHit) {
        if (event.damage <= 0) return
        val color = when (event.effect) {
            is TowerEffect.AoeSplash, is TowerEffect.AoeWithSlow -> Color(0xFFFF8F00)
            is TowerEffect.Slow -> Color(0xFF80DEEA)
            is TowerEffect.Poison -> Color(0xFF9CCC65)
            is TowerEffect.Chain -> Color(0xFFFFEE58)
            else -> Color.White
        }
        emit(event.pixelX, event.pixelY - 10f, "${event.damage}", color, 13f, 800L)
    }

    private fun emitGoldText(event: GameEvent.EnemyKilled) {
        emit(
            event.pixelX, event.pixelY - 20f,
            "+${event.reward}g",
            Color(0xFFFFD700), 15f, 1000L,
            vy = -60f
        )
    }

    private fun emitWaveCompleteText(event: GameEvent.WaveCompleted) {
        emit(
            0f, 0f,
            "Wave ${event.waveNumber} Complete!",
            Color(0xFF4FC3F7), 24f, 2000L,
            vy = -20f, centered = true
        )
    }

    private fun emitLivesLostText(event: GameEvent.LivesLost) {
        emit(
            0f, 0f,
            "-1 Life!",
            Color(0xFFEF5350), 18f, 1200L,
            vy = -30f, centered = true
        )
    }

    /** SPEC-030: Emit a sell refund text like "+60g" at the sold tower position. */
    fun emitSellText(x: Float, y: Float, refundAmount: Int) {
        emit(
            x, y - 10f,
            "+${refundAmount}g",
            Color(0xFFFFD700), 16f, 1200L,
            vy = -70f
        )
    }

    private fun emit(
        x: Float, y: Float, text: String, color: Color,
        fontSize: Float, lifeMs: Long,
        vy: Float = -80f, centered: Boolean = false
    ) {
        if (texts.size >= MAX_TEXTS) return
        texts.add(
            FloatingText(
                id = nextId++, x = x, y = y, text = text,
                color = color, fontSize = fontSize,
                lifeMs = lifeMs, maxLifeMs = lifeMs,
                vy = vy, centered = centered
            )
        )
    }

    companion object {
        const val MAX_TEXTS = 50
    }
}
