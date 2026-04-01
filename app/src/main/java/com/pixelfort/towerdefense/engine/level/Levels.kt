package com.pixelfort.towerdefense.engine.level

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
                WaveGroup(EnemyType.DRAGON, 1, 5000L, 5000L)
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
                WaveGroup(EnemyType.DRAGON, 1, 5000L, 5000L)
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
                WaveGroup(EnemyType.TROLL, 3, 2500L, 6000L)
            ))
        ),
        startingGold = 300, startingLives = 25
    )

    val all = listOf(level1, level2, level3)
    fun getById(id: Int) = all.first { it.id == id }
}
