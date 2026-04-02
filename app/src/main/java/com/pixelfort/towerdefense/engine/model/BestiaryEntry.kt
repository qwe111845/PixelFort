package com.pixelfort.towerdefense.engine.model

/**
 * Domain model for an enemy bestiary entry containing static data
 * (weakness, lore) and runtime tracking (defeated count, first defeated, max damage).
 */
data class BestiaryEntry(
    val enemyType: EnemyType,
    val weakTo: TowerType,
    val weaknessText: String,
    val lore: String,
    val defeatedCount: Int = 0,
    val firstDefeatedAt: Long = 0L,
    val maxHitDamage: Int = 0
) {
    val isUnlocked: Boolean get() = defeatedCount > 0

    companion object {
        /** All bestiary entries with static weakness/lore data, initially locked. */
        fun allEntries(): List<BestiaryEntry> = EnemyType.entries.map { type ->
            BestiaryEntry(
                enemyType = type,
                weakTo = weaknessFor(type),
                weaknessText = weaknessTextFor(type),
                lore = loreFor(type)
            )
        }

        fun weaknessFor(type: EnemyType): TowerType = when (type) {
            EnemyType.GOBLIN -> TowerType.ARCHER
            EnemyType.ORC -> TowerType.CANNON
            EnemyType.DRAGON -> TowerType.FROST
            EnemyType.TROLL -> TowerType.MAGIC
            EnemyType.SPECTER -> TowerType.LIGHTNING
            EnemyType.BOSS_DRAGON -> TowerType.POISON
        }

        fun weaknessTextFor(type: EnemyType): String = when (type) {
            EnemyType.GOBLIN -> "Small and quick \u2014 arrows pick them off easily"
            EnemyType.ORC -> "Tough but slow \u2014 explosions shatter their armor"
            EnemyType.DRAGON -> "Fire-breathers hate the cold"
            EnemyType.TROLL -> "Regenerating flesh dissolves under arcane energy"
            EnemyType.SPECTER -> "Ghosts fear the spark of lightning"
            EnemyType.BOSS_DRAGON -> "Even dragon kings succumb to persistent toxins"
        }

        fun loreFor(type: EnemyType): String = when (type) {
            EnemyType.GOBLIN ->
                "Goblins are the most common invaders. Small, green, and cunning, " +
                "they swarm in packs and overwhelm careless defenders."
            EnemyType.ORC ->
                "Orcs march in heavy plate forged in volcanic furnaces. " +
                "Their slow gait belies devastating strength."
            EnemyType.DRAGON ->
                "Ancient fire-drakes that soar above the battlefield. " +
                "Their scales deflect most attacks, and their breath chars stone."
            EnemyType.TROLL ->
                "Trolls regenerate wounds almost instantly. " +
                "Only sustained magical damage can overcome their healing."
            EnemyType.SPECTER ->
                "Phantoms of fallen warriors, specters phase through walls " +
                "and resist physical projectiles. Only energy can harm them."
            EnemyType.BOSS_DRAGON ->
                "The Dragon King commands all lesser drakes. " +
                "Its armored hide and ferocious attacks make it the ultimate threat."
        }
    }
}
