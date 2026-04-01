package com.pixelfort.towerdefense.engine.model

enum class EnemyType(
    val baseHp: Int,
    val baseSpeed: Float,
    val reward: Int,
    val nameZh: String,
    val size: Float = 1.0f   // visual scale multiplier
) {
    GOBLIN(baseHp = 50,  baseSpeed = 2.0f, reward = 10, nameZh = "哥布林"),
    ORC(baseHp = 150,    baseSpeed = 1.0f, reward = 25, nameZh = "獸人", size = 1.3f),
    DRAGON(baseHp = 400, baseSpeed = 1.5f, reward = 50, nameZh = "龍", size = 1.6f),
    TROLL(baseHp = 250,  baseSpeed = 0.8f, reward = 35, nameZh = "巨魔", size = 1.4f),
    SPECTER(baseHp = 80, baseSpeed = 3.0f, reward = 20, nameZh = "幽靈", size = 0.9f)
}

data class Enemy(
    val id: Int,
    val type: EnemyType,
    val hp: Int,
    val maxHp: Int,
    val pathProgress: Float,
    val pixelX: Float,
    val pixelY: Float,
    val speed: Float,
    val reward: Int,
    val statusEffects: List<StatusEffect> = emptyList()
) {
    val isDead: Boolean get() = hp <= 0
    val hasReachedEnd: Boolean get() = pathProgress >= 1.0f
    val hpPercentage: Float get() = (hp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)

    val effectiveSpeed: Float
        get() {
            val slowEffect = statusEffects.filterIsInstance<StatusEffect.Slowed>().minByOrNull { it.speedFactor }
            return speed * (slowEffect?.speedFactor ?: 1f)
        }

    val isSlowed: Boolean get() = statusEffects.any { it is StatusEffect.Slowed }
    val isPoisoned: Boolean get() = statusEffects.any { it is StatusEffect.Poisoned }

    companion object {
        fun create(id: Int, type: EnemyType, goldMultiplier: Float = 1f): Enemy = Enemy(
            id = id,
            type = type,
            hp = type.baseHp,
            maxHp = type.baseHp,
            pathProgress = 0f,
            pixelX = 0f,
            pixelY = 0f,
            speed = type.baseSpeed,
            reward = (type.reward * goldMultiplier).toInt()
        )
    }
}
