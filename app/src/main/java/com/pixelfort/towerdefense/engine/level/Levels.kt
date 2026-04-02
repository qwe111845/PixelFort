package com.pixelfort.towerdefense.engine.level

import com.pixelfort.towerdefense.engine.model.CellEffect
import com.pixelfort.towerdefense.engine.model.CellType.BLOCKED
import com.pixelfort.towerdefense.engine.model.CellType.BUILDABLE
import com.pixelfort.towerdefense.engine.model.CellType.PATH
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.GridPoint
import com.pixelfort.towerdefense.engine.model.Wave
import com.pixelfort.towerdefense.engine.model.WaveGroup

object Levels {

    // ──────────────────────────────────────────────────────────
    // LEVEL 1 — Green Meadow  (8×12, S-shaped path)
    // ──────────────────────────────────────────────────────────
    val level1 = LevelDefinition(
        id = 1, name = "翠綠草原",
        map = GameMap(
            rows = 12, cols = 8,
            grid = listOf(
                listOf(BLOCKED,  BLOCKED,  PATH,     PATH,     PATH,     BLOCKED,  BLOCKED,  BLOCKED),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     BUILDABLE),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE),
                listOf(BUILDABLE,PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BUILDABLE),
                listOf(BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BUILDABLE,PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  PATH,     BLOCKED,  BLOCKED,  BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,2), GridPoint(0,4), GridPoint(3,4), GridPoint(3,6),
                GridPoint(6,6), GridPoint(6,1), GridPoint(9,1), GridPoint(9,4), GridPoint(11,4)
            )
        ),
        waves = listOf(
            Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 6, 1200L))),
            Wave(2, listOf(WaveGroup(EnemyType.GOBLIN, 10, 1000L))),
            Wave(3, listOf(
                WaveGroup(EnemyType.GOBLIN, 6, 1000L),
                WaveGroup(EnemyType.ORC, 2, 2000L, 3000L)
            )),
            Wave(4, listOf(
                WaveGroup(EnemyType.ORC, 5, 1500L),
                WaveGroup(EnemyType.GOBLIN, 8, 800L, 2000L)
            )),
            Wave(5, listOf(
                WaveGroup(EnemyType.ORC, 4, 1500L),
                WaveGroup(EnemyType.DRAGON, 1, 5000L, 5000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 1, 5000L, 5000L)
            ))
        ),
        startingGold = 200, startingLives = 20
    )

    // ──────────────────────────────────────────────────────────
    // LEVEL 2 — Desert Ruins  (9×14, U-shaped path)
    //   Entry: (0,1) → right → down → left → down → right → down → Exit: (13,4)
    // ──────────────────────────────────────────────────────────
    val level2 = LevelDefinition(
        id = 2, name = "沙漠廢墟",
        map = GameMap(
            rows = 14, cols = 9,
            grid = listOf(
                /*r0 */ listOf(BLOCKED,  PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED),
                /*r1 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED),
                /*r2 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED),
                /*r3 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED),
                /*r4 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED),
                /*r5 */ listOf(PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED),
                /*r6 */ listOf(PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r7 */ listOf(PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r8 */ listOf(PATH,     PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r9 */ listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r10*/ listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r11*/ listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                /*r12*/ listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                /*r13*/ listOf(BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  PATH,     BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,1), GridPoint(0,7), GridPoint(5,7), GridPoint(5,0),
                GridPoint(8,0), GridPoint(8,4), GridPoint(13,4)
            )
        ),
        waves = listOf(
            Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 8, 1000L))),
            Wave(2, listOf(
                WaveGroup(EnemyType.GOBLIN, 8, 900L),
                WaveGroup(EnemyType.SPECTER, 3, 1500L, 2000L)
            )),
            Wave(3, listOf(
                WaveGroup(EnemyType.SPECTER, 5, 1200L),
                WaveGroup(EnemyType.ORC, 3, 1800L, 2000L)
            )),
            Wave(4, listOf(
                WaveGroup(EnemyType.ORC, 6, 1500L),
                WaveGroup(EnemyType.TROLL, 2, 3000L, 3000L)
            )),
            Wave(5, listOf(
                WaveGroup(EnemyType.TROLL, 3, 2500L),
                WaveGroup(EnemyType.SPECTER, 6, 900L, 2000L)
            )),
            Wave(6, listOf(
                WaveGroup(EnemyType.ORC, 5, 1200L),
                WaveGroup(EnemyType.TROLL, 2, 3000L, 2000L),
                WaveGroup(EnemyType.DRAGON, 1, 5000L, 5000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 1, 5000L, 5000L)
            ))
        ),
        startingGold = 250, startingLives = 20
    )

    // ──────────────────────────────────────────────────────────
    // LEVEL 3 — Frozen Fortress  (10×16, spiral path)
    //   Entry: (0,2) → right → down → left → down → right → down → right → down → Exit: (15,8)
    // ──────────────────────────────────────────────────────────
    val level3 = LevelDefinition(
        id = 3, name = "冰雪要塞",
        map = GameMap(
            rows = 16, cols = 10,
            grid = listOf(
                /*r0 */ listOf(BLOCKED,  BLOCKED,  PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED,  BLOCKED),
                /*r1 */ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED,  BLOCKED),
                /*r2 */ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED,  BLOCKED),
                /*r3 */ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED,  BLOCKED),
                /*r4 */ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED,  BLOCKED),
                /*r5 */ listOf(PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED,  BLOCKED),
                /*r6 */ listOf(PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED,  BLOCKED),
                /*r7 */ listOf(PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED,  BLOCKED),
                /*r8 */ listOf(PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED,  BLOCKED),
                /*r9 */ listOf(BLOCKED,  BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED,  BLOCKED),
                /*r10*/ listOf(BLOCKED,  BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED,  BLOCKED),
                /*r11*/ listOf(BLOCKED,  BLOCKED,  PATH,     PATH,     PATH,     PATH,     PATH,     BUILDABLE,BLOCKED,  BLOCKED),
                /*r12*/ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BLOCKED,  BLOCKED),
                /*r13*/ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     BLOCKED),
                /*r14*/ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED),
                /*r15*/ listOf(BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  PATH,     BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,2), GridPoint(0,7), GridPoint(5,7), GridPoint(5,0),
                GridPoint(8,0), GridPoint(8,2), GridPoint(11,2), GridPoint(11,6),
                GridPoint(13,6), GridPoint(13,8), GridPoint(15,8)
            )
        ),
        waves = listOf(
            Wave(1, listOf(WaveGroup(EnemyType.GOBLIN, 10, 900L))),
            Wave(2, listOf(
                WaveGroup(EnemyType.GOBLIN, 8, 800L),
                WaveGroup(EnemyType.SPECTER, 4, 1200L, 1500L)
            )),
            Wave(3, listOf(
                WaveGroup(EnemyType.ORC, 6, 1400L),
                WaveGroup(EnemyType.SPECTER, 6, 900L, 2000L)
            )),
            Wave(4, listOf(
                WaveGroup(EnemyType.TROLL, 4, 2200L),
                WaveGroup(EnemyType.ORC, 6, 1200L, 2000L)
            )),
            Wave(5, listOf(
                WaveGroup(EnemyType.TROLL, 5, 2000L),
                WaveGroup(EnemyType.DRAGON, 1, 5000L, 4000L)
            )),
            Wave(6, listOf(
                WaveGroup(EnemyType.ORC, 8, 1000L),
                WaveGroup(EnemyType.TROLL, 4, 2000L, 2000L),
                WaveGroup(EnemyType.DRAGON, 2, 5000L, 5000L)
            )),
            Wave(7, listOf(
                WaveGroup(EnemyType.SPECTER, 10, 700L),
                WaveGroup(EnemyType.DRAGON, 3, 4000L, 4000L),
                WaveGroup(EnemyType.TROLL, 3, 2500L, 6000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 1, 5000L, 5000L)
            ))
        ),
        startingGold = 300, startingLives = 25
    )

    // ──────────────────────────────────────────────────────────
    // LEVEL 4 — Lava Cavern  (10×14, winding path with lava cells)
    //   Entry: (0,1) → right → down → left → down → right → down → Exit: (13,8)
    //   Lava cells deal 10 damage when enemies pass through
    // ──────────────────────────────────────────────────────────
    val level4 = LevelDefinition(
        id = 4, name = "熔岩洞窟",
        map = GameMap(
            rows = 14, cols = 10,
            grid = listOf(
                /*r0 */ listOf(BLOCKED,  PATH,     PATH,     PATH,     PATH,     BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED),
                /*r1 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r2 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r3 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     PATH,     BUILDABLE,BLOCKED),
                /*r4 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BLOCKED),
                /*r5 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BLOCKED),
                /*r6 */ listOf(BLOCKED,  PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BUILDABLE,BLOCKED),
                /*r7 */ listOf(BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r8 */ listOf(BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r9 */ listOf(BLOCKED,  PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r10*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r11*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED),
                /*r12*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED),
                /*r13*/ listOf(BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  PATH,     BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,1), GridPoint(0,4), GridPoint(3,4), GridPoint(3,7),
                GridPoint(6,7), GridPoint(6,1), GridPoint(9,1), GridPoint(9,4),
                GridPoint(11,4), GridPoint(11,8), GridPoint(13,8)
            )
        ),
        waves = listOf(
            Wave(1, listOf(WaveGroup(EnemyType.ORC, 8, 1200L))),
            Wave(2, listOf(
                WaveGroup(EnemyType.ORC, 6, 1100L),
                WaveGroup(EnemyType.TROLL, 3, 2000L, 2000L)
            )),
            Wave(3, listOf(
                WaveGroup(EnemyType.TROLL, 5, 1800L),
                WaveGroup(EnemyType.SPECTER, 6, 900L, 2000L)
            )),
            Wave(4, listOf(
                WaveGroup(EnemyType.TROLL, 6, 1600L),
                WaveGroup(EnemyType.ORC, 8, 1000L, 2000L)
            )),
            Wave(5, listOf(
                WaveGroup(EnemyType.TROLL, 8, 1400L),
                WaveGroup(EnemyType.DRAGON, 2, 4000L, 3000L)
            )),
            Wave(6, listOf(
                WaveGroup(EnemyType.DRAGON, 3, 3500L),
                WaveGroup(EnemyType.TROLL, 6, 1500L, 2000L)
            )),
            Wave(7, listOf(
                WaveGroup(EnemyType.TROLL, 10, 1200L),
                WaveGroup(EnemyType.DRAGON, 3, 3000L, 3000L),
                WaveGroup(EnemyType.SPECTER, 8, 700L, 2000L)
            )),
            Wave(8, listOf(
                WaveGroup(EnemyType.TROLL, 8, 1400L),
                WaveGroup(EnemyType.DRAGON, 4, 3000L, 2000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 1, 5000L, 5000L)
            ))
        ),
        startingGold = 350, startingLives = 25,
        cellEffects = mapOf(
            // Lava cells along path segments — enemies take 10 damage per lava cell
            GridPoint(3, 5) to CellEffect.LavaDamage(10),
            GridPoint(3, 6) to CellEffect.LavaDamage(10),
            GridPoint(6, 3) to CellEffect.LavaDamage(10),
            GridPoint(6, 4) to CellEffect.LavaDamage(10),
            GridPoint(6, 5) to CellEffect.LavaDamage(10),
            GridPoint(9, 2) to CellEffect.LavaDamage(10),
            GridPoint(9, 3) to CellEffect.LavaDamage(10),
            GridPoint(11, 6) to CellEffect.LavaDamage(10),
            GridPoint(11, 7) to CellEffect.LavaDamage(10),
        )
    )

    // ──────────────────────────────────────────────────────────
    // LEVEL 5 — Sky Temple  (12×16, complex path with teleport portal)
    //   Entry: (0,2) → right → down → left → down → right → teleport → down → right → Exit: (15,10)
    //   Teleport portal at (7,1) skips enemies ahead to waypoint index 7
    //   Final boss has 2000 HP (BOSS_DRAGON × difficulty scaling)
    // ──────────────────────────────────────────────────────────
    val level5 = LevelDefinition(
        id = 5, name = "天空神殿",
        map = GameMap(
            rows = 16, cols = 12,
            grid = listOf(
                /*r0 */ listOf(BLOCKED,  BLOCKED,  PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED,  BLOCKED,  BLOCKED),
                /*r1 */ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r2 */ listOf(BLOCKED,  BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r3 */ listOf(BLOCKED,  PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r4 */ listOf(BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r5 */ listOf(BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r6 */ listOf(BLOCKED,  PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r7 */ listOf(BLOCKED,  PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r8 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r9 */ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r10*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r11*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r12*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,BLOCKED),
                /*r13*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BLOCKED),
                /*r14*/ listOf(BLOCKED,  BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED),
                /*r15*/ listOf(BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  PATH,     BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,2), GridPoint(0,8), GridPoint(3,8), GridPoint(3,1),
                GridPoint(7,1), GridPoint(7,4), GridPoint(9,4), GridPoint(9,8),
                GridPoint(12,8), GridPoint(12,5), GridPoint(14,5), GridPoint(14,10),
                GridPoint(15,10)
            )
        ),
        waves = listOf(
            Wave(1, listOf(
                WaveGroup(EnemyType.GOBLIN, 10, 800L),
                WaveGroup(EnemyType.ORC, 4, 1500L, 2000L)
            )),
            Wave(2, listOf(
                WaveGroup(EnemyType.SPECTER, 8, 900L),
                WaveGroup(EnemyType.TROLL, 4, 1800L, 2000L)
            )),
            Wave(3, listOf(
                WaveGroup(EnemyType.ORC, 8, 1100L),
                WaveGroup(EnemyType.DRAGON, 2, 4000L, 3000L)
            )),
            Wave(4, listOf(
                WaveGroup(EnemyType.TROLL, 6, 1500L),
                WaveGroup(EnemyType.SPECTER, 10, 600L, 2000L)
            )),
            Wave(5, listOf(
                WaveGroup(EnemyType.DRAGON, 3, 3000L),
                WaveGroup(EnemyType.TROLL, 6, 1400L, 2000L),
                WaveGroup(EnemyType.ORC, 6, 1000L, 2000L)
            )),
            Wave(6, listOf(
                WaveGroup(EnemyType.TROLL, 8, 1200L),
                WaveGroup(EnemyType.DRAGON, 4, 2500L, 3000L)
            )),
            Wave(7, listOf(
                WaveGroup(EnemyType.SPECTER, 12, 500L),
                WaveGroup(EnemyType.DRAGON, 3, 3000L, 2000L),
                WaveGroup(EnemyType.TROLL, 5, 1800L, 3000L)
            )),
            Wave(8, listOf(
                WaveGroup(EnemyType.DRAGON, 5, 2500L),
                WaveGroup(EnemyType.TROLL, 8, 1200L, 2000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 1, 5000L, 5000L)
            )),
            Wave(9, listOf(
                WaveGroup(EnemyType.SPECTER, 15, 400L),
                WaveGroup(EnemyType.DRAGON, 4, 2500L, 2000L),
                WaveGroup(EnemyType.TROLL, 6, 1500L, 3000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 1, 5000L, 5000L)
            )),
            Wave(10, listOf(
                WaveGroup(EnemyType.TROLL, 10, 1000L),
                WaveGroup(EnemyType.DRAGON, 6, 2000L, 2000L),
                WaveGroup(EnemyType.BOSS_DRAGON, 2, 4000L, 5000L)
            ))
        ),
        startingGold = 400, startingLives = 30,
        cellEffects = mapOf(
            // Teleport portal: enemy at (7,1) skips ahead to waypoint 7 (GridPoint(9,8))
            GridPoint(7, 1) to CellEffect.Teleport(targetWaypointIndex = 7)
        )
    )

    val all = listOf(level1, level2, level3, level4, level5)
    fun getById(id: Int) = all.first { it.id == id }
}
