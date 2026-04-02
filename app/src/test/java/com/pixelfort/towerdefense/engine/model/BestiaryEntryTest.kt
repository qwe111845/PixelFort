package com.pixelfort.towerdefense.engine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class BestiaryEntryTest {

    @Nested
    inner class AllEntries {
        @Test
        fun `allEntries returns one entry per EnemyType`() {
            val entries = BestiaryEntry.allEntries()
            assertEquals(EnemyType.entries.size, entries.size)
            assertEquals(
                EnemyType.entries.toSet(),
                entries.map { it.enemyType }.toSet()
            )
        }

        @Test
        fun `allEntries are initially locked`() {
            val entries = BestiaryEntry.allEntries()
            entries.forEach { entry ->
                assertFalse(entry.isUnlocked, "${entry.enemyType} should be locked initially")
                assertEquals(0, entry.defeatedCount)
            }
        }
    }

    @Nested
    inner class UnlockLogic {
        @Test
        fun `entry with defeatedCount 0 is locked`() {
            val entry = BestiaryEntry.allEntries().first()
            assertFalse(entry.isUnlocked)
        }

        @Test
        fun `entry with defeatedCount greater than 0 is unlocked`() {
            val entry = BestiaryEntry.allEntries().first().copy(defeatedCount = 1)
            assertTrue(entry.isUnlocked)
        }
    }

    @Nested
    inner class Weaknesses {
        @Test
        fun `GOBLIN is weak to ARCHER`() {
            assertEquals(TowerType.ARCHER, BestiaryEntry.weaknessFor(EnemyType.GOBLIN))
        }

        @Test
        fun `ORC is weak to CANNON`() {
            assertEquals(TowerType.CANNON, BestiaryEntry.weaknessFor(EnemyType.ORC))
        }

        @Test
        fun `DRAGON is weak to FROST`() {
            assertEquals(TowerType.FROST, BestiaryEntry.weaknessFor(EnemyType.DRAGON))
        }

        @Test
        fun `TROLL is weak to MAGIC`() {
            assertEquals(TowerType.MAGIC, BestiaryEntry.weaknessFor(EnemyType.TROLL))
        }

        @Test
        fun `SPECTER is weak to LIGHTNING`() {
            assertEquals(TowerType.LIGHTNING, BestiaryEntry.weaknessFor(EnemyType.SPECTER))
        }

        @Test
        fun `BOSS_DRAGON is weak to POISON`() {
            assertEquals(TowerType.POISON, BestiaryEntry.weaknessFor(EnemyType.BOSS_DRAGON))
        }
    }

    @Nested
    inner class WeaknessText {
        @Test
        fun `GOBLIN weakness text matches spec`() {
            assertEquals(
                "Small and quick \u2014 arrows pick them off easily",
                BestiaryEntry.weaknessTextFor(EnemyType.GOBLIN)
            )
        }

        @Test
        fun `ORC weakness text matches spec`() {
            assertEquals(
                "Tough but slow \u2014 explosions shatter their armor",
                BestiaryEntry.weaknessTextFor(EnemyType.ORC)
            )
        }

        @Test
        fun `DRAGON weakness text matches spec`() {
            assertEquals(
                "Fire-breathers hate the cold",
                BestiaryEntry.weaknessTextFor(EnemyType.DRAGON)
            )
        }

        @Test
        fun `TROLL weakness text matches spec`() {
            assertEquals(
                "Regenerating flesh dissolves under arcane energy",
                BestiaryEntry.weaknessTextFor(EnemyType.TROLL)
            )
        }

        @Test
        fun `SPECTER weakness text matches spec`() {
            assertEquals(
                "Ghosts fear the spark of lightning",
                BestiaryEntry.weaknessTextFor(EnemyType.SPECTER)
            )
        }

        @Test
        fun `BOSS_DRAGON weakness text matches spec`() {
            assertEquals(
                "Even dragon kings succumb to persistent toxins",
                BestiaryEntry.weaknessTextFor(EnemyType.BOSS_DRAGON)
            )
        }
    }

    @Nested
    inner class Lore {
        @ParameterizedTest
        @EnumSource(EnemyType::class)
        fun `every enemy type has non-empty lore`(type: EnemyType) {
            val lore = BestiaryEntry.loreFor(type)
            assertTrue(lore.isNotBlank(), "Lore for $type should not be blank")
        }
    }

    @Nested
    inner class Completeness {
        @ParameterizedTest
        @EnumSource(EnemyType::class)
        fun `every enemy type has a weakness mapping`(type: EnemyType) {
            // Should not throw
            val weakness = BestiaryEntry.weaknessFor(type)
            assertTrue(TowerType.entries.contains(weakness))
        }

        @ParameterizedTest
        @EnumSource(EnemyType::class)
        fun `every enemy type has non-empty weakness text`(type: EnemyType) {
            val text = BestiaryEntry.weaknessTextFor(type)
            assertTrue(text.isNotBlank(), "Weakness text for $type should not be blank")
        }

        @Test
        fun `allEntries includes all 6 types`() {
            val entries = BestiaryEntry.allEntries()
            assertEquals(6, entries.size)
            val types = entries.map { it.enemyType }
            assertTrue(types.contains(EnemyType.GOBLIN))
            assertTrue(types.contains(EnemyType.ORC))
            assertTrue(types.contains(EnemyType.DRAGON))
            assertTrue(types.contains(EnemyType.TROLL))
            assertTrue(types.contains(EnemyType.SPECTER))
            assertTrue(types.contains(EnemyType.BOSS_DRAGON))
        }
    }
}
