package com.pixelfort.towerdefense.feature.game.vfx

import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

/**
 * Level-themed ambient particles (fireflies, sand wisps, snowflakes).
 *
 * Emits particles from random screen positions that drift gently,
 * creating atmospheric ambiance appropriate to each level theme.
 */
class AmbientSystem(
    private val canvasWidth: Float,
    private val canvasHeight: Float,
    private val levelId: Int
) {
    private val particles = mutableListOf<AmbientParticle>()
    private var nextId = 0
    private var emitAccumulatorMs = 0L

    /** How often (ms) to spawn a new ambient particle. */
    private val emitIntervalMs: Long = when (levelId) {
        1    -> 300L   // fireflies: moderate density
        2    -> 200L   // sand wisps: denser
        else -> 250L   // snowflakes: moderate
    }

    /** Max ambient particles alive at once. */
    private val maxParticles: Int = 40

    val activeParticles: List<AmbientParticle> get() = particles

    fun update(deltaMs: Long) {
        // Update existing particles
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.update(deltaMs, canvasWidth, canvasHeight)
            if (p.isDead) iterator.remove()
        }

        // Emit new particles
        emitAccumulatorMs += deltaMs
        while (emitAccumulatorMs >= emitIntervalMs && particles.size < maxParticles) {
            emitAccumulatorMs -= emitIntervalMs
            emit()
        }
    }

    private fun emit() {
        val x = Random.nextFloat() * canvasWidth
        val y = when (levelId) {
            3    -> -5f  // snowflakes fall from top
            else -> Random.nextFloat() * canvasHeight
        }

        when (levelId) {
            1 -> emitFirefly(x, y)
            2 -> emitSandWisp(x, y)
            else -> emitSnowflake(x, y)
        }
    }

    private fun emitFirefly(x: Float, y: Float) {
        particles.add(
            AmbientParticle(
                id = nextId++,
                x = x,
                y = y,
                vx = Random.nextFloat() * 20f - 10f,
                vy = Random.nextFloat() * 20f - 10f,
                lifeMs = 3000L + (Random.nextFloat() * 2000L).toLong(),
                maxLifeMs = 5000L,
                color = Color(0xFFFFEB3B),
                size = 2f + Random.nextFloat() * 2f,
                type = AmbientType.FIREFLY
            )
        )
    }

    private fun emitSandWisp(x: Float, y: Float) {
        particles.add(
            AmbientParticle(
                id = nextId++,
                x = x,
                y = y,
                vx = 15f + Random.nextFloat() * 25f,  // blow to the right
                vy = Random.nextFloat() * 10f - 5f,
                lifeMs = 2000L + (Random.nextFloat() * 1500L).toLong(),
                maxLifeMs = 3500L,
                color = Color(0xFFD7CCC8),
                size = 1.5f + Random.nextFloat() * 2f,
                type = AmbientType.SAND_WISP
            )
        )
    }

    private fun emitSnowflake(x: Float, y: Float) {
        particles.add(
            AmbientParticle(
                id = nextId++,
                x = x,
                y = y,
                vx = Random.nextFloat() * 10f - 5f,
                vy = 15f + Random.nextFloat() * 15f,  // fall downward
                lifeMs = 4000L + (Random.nextFloat() * 2000L).toLong(),
                maxLifeMs = 6000L,
                color = Color.White,
                size = 1.5f + Random.nextFloat() * 2.5f,
                type = AmbientType.SNOWFLAKE
            )
        )
    }
}

enum class AmbientType { FIREFLY, SAND_WISP, SNOWFLAKE }

class AmbientParticle(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var lifeMs: Long,
    val maxLifeMs: Long,
    val color: Color,
    val size: Float,
    val type: AmbientType
) {
    val alpha: Float get() {
        val lifeFraction = (lifeMs.toFloat() / maxLifeMs).coerceIn(0f, 1f)
        // Fade in during first 20%, fade out during last 30%
        return when {
            lifeFraction > 0.8f -> (1f - lifeFraction) / 0.2f  // fade in
            lifeFraction < 0.3f -> lifeFraction / 0.3f          // fade out
            else -> 1f
        }.coerceIn(0f, 1f) * when (type) {
            AmbientType.FIREFLY -> 0.7f
            AmbientType.SAND_WISP -> 0.4f
            AmbientType.SNOWFLAKE -> 0.6f
        }
    }

    val isDead: Boolean get() = lifeMs <= 0

    fun update(deltaMs: Long, canvasWidth: Float, canvasHeight: Float) {
        val dt = deltaMs / 1000f
        lifeMs -= deltaMs

        when (type) {
            AmbientType.FIREFLY -> {
                // Gentle sinusoidal drift
                val time = (maxLifeMs - lifeMs).toFloat() / 1000f
                x += vx * dt + sin(time * 2f).toFloat() * 8f * dt
                y += vy * dt + sin(time * 1.5f + 1f).toFloat() * 6f * dt
            }
            AmbientType.SAND_WISP -> {
                x += vx * dt
                y += vy * dt
            }
            AmbientType.SNOWFLAKE -> {
                // Gentle side-to-side sway
                val time = (maxLifeMs - lifeMs).toFloat() / 1000f
                x += vx * dt + sin(time * 1.2f).toFloat() * 12f * dt
                y += vy * dt
            }
        }

        // Kill if off-screen
        if (x < -20f || x > canvasWidth + 20f || y < -20f || y > canvasHeight + 20f) {
            lifeMs = 0
        }
    }
}
