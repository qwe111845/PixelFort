package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.TowerEffect
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ParticleSystem {

    private val particles = mutableListOf<Particle>()
    private var nextId = 0

    val activeParticles: List<Particle> get() = particles.toList()

    fun update(deltaMs: Long) {
        particles.removeAll { it.isDead }
        val updated = particles.map { p ->
            val dt = deltaMs / 1000f
            p.copy(
                x = p.x + p.vx * dt,
                y = p.y + p.vy * dt,
                lifeMs = p.lifeMs - deltaMs,
                vy = p.vy + 60f * dt   // gravity
            )
        }
        particles.clear()
        particles.addAll(updated.filter { !it.isDead })
    }

    fun processEvents(events: List<GameEvent>, cellSize: Float = 80f) {
        val half = cellSize / 2f
        for (event in events) {
            when (event) {
                is GameEvent.EnemyKilled ->
                    emitDeathBurst(event.pixelX, event.pixelY, event.enemyType)
                is GameEvent.ProjectileHit ->
                    emitHitEffect(event.pixelX, event.pixelY, event.effect)
                is GameEvent.TowerPlaced ->
                    emitSmoke(event.gridCol * cellSize + half, event.gridRow * cellSize + half)
                is GameEvent.TowerUpgraded ->
                    emitSparkle(event.gridCol * cellSize + half, event.gridRow * cellSize + half)
                else -> Unit
            }
        }
    }

    private fun emitDeathBurst(x: Float, y: Float, type: EnemyType) {
        val color = when (type) {
            EnemyType.GOBLIN  -> Color(0xFF66BB6A)
            EnemyType.ORC     -> Color(0xFF8D6E63)
            EnemyType.DRAGON  -> Color(0xFFEF5350)
            EnemyType.TROLL   -> Color(0xFF78909C)
            EnemyType.SPECTER -> Color(0xFFCE93D8)
        }
        repeat(12) {
            val angle = Random.nextFloat() * 360f
            val speed = Random.nextFloat() * 120f + 60f
            emit(x, y,
                cos(Math.toRadians(angle.toDouble())).toFloat() * speed,
                sin(Math.toRadians(angle.toDouble())).toFloat() * speed,
                color, Random.nextFloat() * 4f + 2f, 600L, ParticleType.SQUARE)
        }
        // Gold coin sparkle
        repeat(4) {
            val angle = Random.nextFloat() * 360f
            emit(x, y - 10f,
                cos(Math.toRadians(angle.toDouble())).toFloat() * 50f,
                sin(Math.toRadians(angle.toDouble())).toFloat() * 50f - 80f,
                Color(0xFFFFD700), 5f, 800L, ParticleType.SQUARE)
        }
    }

    private fun emitHitEffect(x: Float, y: Float, effect: TowerEffect) {
        when (effect) {
            is TowerEffect.AoeSplash, is TowerEffect.AoeWithSlow -> {
                repeat(16) {
                    val angle = it * (360f / 16)
                    val speed = 90f
                    emit(x, y,
                        cos(Math.toRadians(angle.toDouble())).toFloat() * speed,
                        sin(Math.toRadians(angle.toDouble())).toFloat() * speed,
                        Color(0xFFFF8F00), 4f, 400L, ParticleType.SQUARE)
                }
            }
            is TowerEffect.Slow, is TowerEffect.Chain -> {
                repeat(8) {
                    val angle = Random.nextFloat() * 360f
                    emit(x, y,
                        cos(Math.toRadians(angle.toDouble())).toFloat() * 60f,
                        sin(Math.toRadians(angle.toDouble())).toFloat() * 60f,
                        Color(0xFF80DEEA), 3f, 350L, ParticleType.CIRCLE)
                }
            }
            is TowerEffect.Poison -> {
                repeat(6) {
                    val angle = Random.nextFloat() * 360f
                    emit(x, y,
                        cos(Math.toRadians(angle.toDouble())).toFloat() * 40f,
                        sin(Math.toRadians(angle.toDouble())).toFloat() * 40f - 30f,
                        Color(0xFF9CCC65), 4f, 500L, ParticleType.CIRCLE)
                }
            }
            else -> {
                repeat(4) {
                    val angle = Random.nextFloat() * 360f
                    emit(x, y,
                        cos(Math.toRadians(angle.toDouble())).toFloat() * 50f,
                        sin(Math.toRadians(angle.toDouble())).toFloat() * 50f,
                        Color.White, 2f, 250L, ParticleType.SQUARE)
                }
            }
        }
    }

    private fun emitSmoke(x: Float, y: Float) {
        repeat(6) {
            emit(x + Random.nextFloat() * 20f - 10f, y,
                Random.nextFloat() * 30f - 15f, -Random.nextFloat() * 40f - 20f,
                Color(0xFF9E9E9E), 5f, 500L, ParticleType.CIRCLE)
        }
    }

    private fun emitSparkle(x: Float, y: Float) {
        repeat(10) {
            val angle = Random.nextFloat() * 360f
            val speed = Random.nextFloat() * 80f + 40f
            emit(x, y,
                cos(Math.toRadians(angle.toDouble())).toFloat() * speed,
                sin(Math.toRadians(angle.toDouble())).toFloat() * speed - 60f,
                Color(0xFFFFD700), 3f, 600L, ParticleType.STAR)
        }
    }

    private fun emit(
        x: Float, y: Float, vx: Float, vy: Float,
        color: Color, size: Float, lifeMs: Long, type: ParticleType
    ) {
        if (particles.size >= MAX_PARTICLES) return
        particles.add(Particle(nextId++, x, y, vx, vy, lifeMs, lifeMs, color, size, type))
    }

    companion object {
        const val MAX_PARTICLES = 300
    }
}
