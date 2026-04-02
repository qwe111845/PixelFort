package com.pixelfort.towerdefense.engine.model

/**
 * SPEC-032: Random Wave Events.
 *
 * Each [WaveEventType] defines a single modifier that affects gameplay for one wave.
 * [ActiveWaveEvent] wraps the type with a human-readable description shown on the banner.
 */
enum class WaveEventType(
    val displayName: String,
    val description: String,
    val projectileSpeedMult: Float = 1f,
    val goldMult: Float = 1f,
    val enemyHpMult: Float = 1f,
    val rangeMult: Float = 1f,
    val fireRateMult: Float = 1f,
    val enemySpeedMult: Float = 1f,
    /** ARGB tint colour applied as a screen overlay during the wave */
    val tintColor: Long = 0x00000000
) {
    STORM(
        displayName = "Storm",
        description = "Projectile speed -30%",
        projectileSpeedMult = 0.70f,
        tintColor = 0x18_5C6B_C0   // blue-grey
    ),
    GOLDEN_HOUR(
        displayName = "Golden Hour",
        description = "Kill gold x2",
        goldMult = 2.0f,
        tintColor = 0x20_FFD7_00    // gold
    ),
    REINFORCED(
        displayName = "Reinforced",
        description = "Enemy HP +50%, gold +50%",
        enemyHpMult = 1.50f,
        goldMult = 1.50f,
        tintColor = 0x18_B71C_1C    // dark red
    ),
    FOG_OF_WAR(
        displayName = "Fog of War",
        description = "Tower range -20%",
        rangeMult = 0.80f,
        tintColor = 0x22_9E9E_9E    // grey fog
    ),
    TAILWIND(
        displayName = "Tailwind",
        description = "Tower attack speed +25%",
        fireRateMult = 0.75f,        // lower fire-rate ms = faster attacks
        tintColor = 0x18_4CAF_50     // green
    ),
    BLOOD_MOON(
        displayName = "Blood Moon",
        description = "Enemy speed +30%, gold +30%",
        enemySpeedMult = 1.30f,
        goldMult = 1.30f,
        tintColor = 0x20_D500_00     // crimson
    )
}

data class ActiveWaveEvent(
    val type: WaveEventType,
    val description: String = type.description
)
