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
    // LEVEL 2 — Desert Ruins  (9×14, U + fork path)
    // ──────────────────────────────────────────────────────────
    val level2 = LevelDefinition(
        id = 2, name = "沙漠廢墟",
        map = GameMap(
            rows = 14, cols = 9,
            grid = listOf(
                listOf(BLOCKED, PATH,     PATH,     PATH,     PATH,     PATH,     PATH,     BLOCKED, BLOCKED),
                listOf(BLOCKED, PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED, BLOCKED),
                listOf(BLOCKED, PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     BLOCKED, BLOCKED),
                listOf(BLOCKED, PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,     PATH,    PATH),
                listOf(BLOCKED, PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH),
                listOf(PATH,    PATH,     BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH),
                listOf(PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH),
                listOf(PATH,    BUILDABLE,BUILDABLE,PATH,     PATH,     PATH,     BUILDABLE,BUILDABLE,PATH),
                listOf(PATH,    BUILDABLE,BUILDABLE,PATH,     BUILDABLE,PATH,     BUILDABLE,BUILDABLE,PATH),
                listOf(PATH,    PATH,     PATH,     PATH,     BUILDABLE,PATH,     PATH,     PATH,    PATH),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BLOCKED, BLOCKED,  PATH,     PATH,     PATH,     PATH,     BLOCKED,  BLOCKED, BLOCKED),
                listOf(BLOCKED, BLOCKED,  BLOCKED,  BLOCKED,  PATH,     BLOCKED,  BLOCKED,  BLOCKED, BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,1), GridPoint(0,6), GridPoint(5,6), GridPoint(5,0),
                GridPoint(9,0), GridPoint(9,8), GridPoint(3,8), GridPoint(3,6),
                GridPoint(7,6), GridPoint(7,3), GridPoint(9,3), GridPoint(9,4),
                GridPoint(12,4), GridPoint(13,4)
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
    // LEVEL 3 — Frozen Fortress  (10×16, spiral + multiple straights)
    // ──────────────────────────────────────────────────────────
    val level3 = LevelDefinition(
        id = 3, name = "冰雪要塞",
        map = GameMap(
            rows = 16, cols = 10,
            grid = listOf(
                listOf(BLOCKED, BLOCKED, PATH,    PATH,    PATH,    PATH,    PATH,    BLOCKED, BLOCKED, BLOCKED),
                listOf(BLOCKED, BLOCKED, BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,BLOCKED, BLOCKED, BLOCKED),
                listOf(BLOCKED, BLOCKED, BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,BLOCKED, BLOCKED, BLOCKED),
                listOf(PATH,    PATH,    PATH,    BUILDABLE,BUILDABLE,BUILDABLE,PATH, PATH,    PATH,    BLOCKED),
                listOf(PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,BLOCKED),
                listOf(PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH,BLOCKED),
                listOf(PATH,    PATH,    PATH,    PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH, BLOCKED),
                listOf(BLOCKED, BLOCKED, BLOCKED, PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,PATH, BLOCKED),
                listOf(BLOCKED, BLOCKED, BLOCKED, PATH,    PATH,    PATH,    BUILDABLE,BUILDABLE,PATH,  BLOCKED),
                listOf(BLOCKED, PATH,    PATH,    PATH,    BUILDABLE,PATH,    BUILDABLE,BUILDABLE,PATH,  BLOCKED),
                listOf(BLOCKED, PATH,    BUILDABLE,BUILDABLE,BUILDABLE,PATH,  PATH,    PATH,    PATH,    BLOCKED),
                listOf(BLOCKED, PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BLOCKED, PATH,    PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BLOCKED, BLOCKED, PATH,    PATH,    PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BLOCKED, BLOCKED, BLOCKED, BLOCKED, PATH,    BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE,BUILDABLE),
                listOf(BLOCKED, BLOCKED, BLOCKED, BLOCKED, PATH,    BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED,  BLOCKED),
            ),
            pathWaypoints = listOf(
                GridPoint(0,2),  GridPoint(0,6),  GridPoint(3,6),  GridPoint(3,0),
                GridPoint(6,0),  GridPoint(6,3),  GridPoint(9,3),  GridPoint(9,1),
                GridPoint(12,1), GridPoint(12,2), GridPoint(13,2), GridPoint(13,4),
                GridPoint(10,4), GridPoint(10,6), GridPoint(8,6),  GridPoint(8,8),
                GridPoint(3,8),  GridPoint(3,7),  GridPoint(4,7),  GridPoint(4,8),
                GridPoint(11,8), GridPoint(11,9), GridPoint(15,4)
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
