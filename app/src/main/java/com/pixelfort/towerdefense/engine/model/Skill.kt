package com.pixelfort.towerdefense.engine.model

enum class SkillType(
    val cooldownMs: Long,
    val durationMs: Long,
    val nameZh: String,
    val descZh: String,
    val icon: String
) {
    METEOR_STRIKE(
        cooldownMs = 60_000L,
        durationMs = 0L,
        nameZh = "隕石打擊",
        descZh = "指定區域造成300傷害（2格半徑）",
        icon = "☄"
    ),
    FROZEN_TIME(
        cooldownMs = 90_000L,
        durationMs = 5_000L,
        nameZh = "時間凍結",
        descZh = "所有敵人凍結5秒",
        icon = "❄"
    ),
    GOLD_RUSH(
        cooldownMs = 45_000L,
        durationMs = 10_000L,
        nameZh = "黃金潮",
        descZh = "10秒內擊殺金幣×2",
        icon = "💎"
    );

    companion object {
        const val INITIAL_COOLDOWN_MS = 15_000L
        const val METEOR_DAMAGE = 300
        const val METEOR_RADIUS_CELLS = 2
        const val FROZEN_SPEED_FACTOR = 0.0f
        const val GOLD_RUSH_MULTIPLIER = 2.0f
    }
}

data class SkillState(
    val type: SkillType,
    val cooldownRemainingMs: Long = SkillType.INITIAL_COOLDOWN_MS,
    val isActive: Boolean = false,
    val durationRemainingMs: Long = 0L
) {
    val isReady: Boolean get() = cooldownRemainingMs <= 0L && !isActive
    val cooldownFraction: Float
        get() = if (type.cooldownMs > 0)
            (cooldownRemainingMs.toFloat() / type.cooldownMs).coerceIn(0f, 1f)
        else 0f
}
