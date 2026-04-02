package com.pixelfort.towerdefense.engine

import com.pixelfort.towerdefense.engine.action.ActionProcessor
import com.pixelfort.towerdefense.engine.action.GameAction
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.event.GameEventBus
import com.pixelfort.towerdefense.engine.level.EndlessWaveGenerator
import com.pixelfort.towerdefense.engine.level.LevelDefinition
import com.pixelfort.towerdefense.engine.model.DifficultyMode
import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.PlayerState
import com.pixelfort.towerdefense.engine.model.Projectile
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerEffect
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.engine.model.ActiveCombo
import com.pixelfort.towerdefense.engine.system.ComboSystem
import com.pixelfort.towerdefense.engine.system.EnemyDeathSystem
import com.pixelfort.towerdefense.engine.system.EnemyMovementSystem
import com.pixelfort.towerdefense.engine.system.EndReachSystem
import com.pixelfort.towerdefense.engine.system.ProjectileSystem
import com.pixelfort.towerdefense.engine.system.StatusEffectSystem
import com.pixelfort.towerdefense.engine.system.TowerTargetingSystem
import com.pixelfort.towerdefense.engine.system.WaveSpawnerSystem

class GameEngine(
    private val level: LevelDefinition,
    private val cellSize: Float,
    private val metaBonus: MetaBonus = MetaBonus(),
    private val difficulty: DifficultyMode = DifficultyMode.NORMAL,
    private val isEndless: Boolean = false
) {
    private val eventBus        = GameEventBus()
    private val actionProcessor = ActionProcessor(level.map)
    private val movementSystem  = EnemyMovementSystem(level.map.pathWaypoints, cellSize, level.cellEffects)
    private val targetingSystem = TowerTargetingSystem(cellSize)
    private val projectileSystem= ProjectileSystem(cellSize)
    private val statusSystem    = StatusEffectSystem()
    private val waveSpawner     = WaveSpawnerSystem(
        if (isEndless) listOf(EndlessWaveGenerator.generate(0))
        else level.waves
    )
    private val deathSystem     = EnemyDeathSystem()
    private val endReachSystem  = EndReachSystem()

    private var towers      = mutableListOf<Tower>()
    private var enemies     = mutableListOf<Enemy>()
    private var projectiles = mutableListOf<Projectile>()
    private var playerState = PlayerState(
        gold  = maxOf(0, level.startingGold + metaBonus.startingGoldBonus + difficulty.startingGoldBonus),
        lives = maxOf(1, level.startingLives + metaBonus.startingLivesBonus + difficulty.startingLivesBonus),
        currentWave = 0
    )
    private var state: GameState = GameState.WaitingForWave
    private var nextTowerId  = 1
    private var nextEnemyId  = 1
    private var waveSpawningComplete = false
    private var starsEarned  = -1
    private var rpEarned     = 0
    private var speedMultiplier = 1f
    private var totalKills   = 0
    private var activeCombos = listOf<ActiveCombo>()

    fun update(deltaMs: Long) {
        if (state != GameState.Playing) return
        eventBus.clear()
        val scaledDelta = (deltaMs * speedMultiplier).toLong()

        val statusResult = statusSystem.update(enemies, scaledDelta)
        enemies = statusResult.updatedEnemies.toMutableList()
        statusResult.newlyEnragedIds.forEach { id ->
            eventBus.emit(GameEvent.BossEnraged(id))
        }

        val spawnResult = waveSpawner.update(scaledDelta, nextEnemyId)
        spawnResult.spawnedEnemies.forEach { e ->
            val hpMul = difficulty.hpMultiplier * (if (isEndless) EndlessWaveGenerator.hpMultiplier(playerState.currentWave) else 1f)
            val scaledHp = maxOf(1, (e.maxHp * hpMul).toInt())
            val scaledSpeed = e.speed * difficulty.speedMultiplier
            val scaledReward = (e.reward * metaBonus.goldRewardMultiplier * difficulty.goldRewardMultiplier).toInt()
            enemies.add(e.copy(hp = scaledHp, maxHp = scaledHp, speed = scaledSpeed, reward = scaledReward))
            nextEnemyId = e.id + 1
        }
        if (spawnResult.waveSpawningComplete) waveSpawningComplete = true
        if (spawnResult.bossWarning) {
            eventBus.emit(GameEvent.BossWarning(playerState.currentWave))
        }

        val movementResult = movementSystem.moveAllWithEffects(enemies, scaledDelta)
        enemies = movementResult.enemies.toMutableList()
        movementResult.lavaDamageEvents.forEach { evt ->
            eventBus.emit(GameEvent.ProjectileHit(evt.pixelX, evt.pixelY, TowerEffect.None, evt.damage))
        }

        val targetResult = targetingSystem.update(towers, enemies, scaledDelta)
        towers = targetResult.updatedTowers.toMutableList()
        projectiles.addAll(targetResult.projectiles)

        val projResult = projectileSystem.update(projectiles, enemies, scaledDelta, statusSystem, activeCombos, towers)
        projectiles = (projResult.remainingProjectiles + projResult.newChainProjectiles).toMutableList()
        enemies = projResult.damagedEnemies.toMutableList()

        projResult.hitEvents.forEach { h ->
            eventBus.emit(GameEvent.ProjectileHit(h.pixelX, h.pixelY, h.effect, h.damage))
        }

        val deathResult = deathSystem.update(enemies)
        enemies = deathResult.survivors.toMutableList()
        if (deathResult.goldEarned > 0)
            playerState = playerState.earnGold(deathResult.goldEarned)
        totalKills += deathResult.killedEnemies.size
        deathResult.killedEnemies.forEach { killed ->
            eventBus.emit(GameEvent.EnemyKilled(
                killed.id, killed.type, killed.reward, killed.pixelX, killed.pixelY
            ))
        }

        val endResult = endReachSystem.update(enemies)
        enemies = endResult.survivors.toMutableList()
        endResult.reachedEnemies.forEach { reached ->
            playerState = playerState.loseLife()
            eventBus.emit(GameEvent.LivesLost(reached.id, playerState.lives))
        }

        checkWinLoseConditions()
    }

    fun processAction(action: GameAction): Boolean {
        if (action is GameAction.SetSpeed) {
            speedMultiplier = action.multiplier.coerceIn(1f, 3f)
            return true
        }

        val valid = actionProcessor.validate(action, playerState, towers, metaBonus)
        if (!valid.isValid) return false

        when (action) {
            is GameAction.PlaceTower -> {
                val tower = Tower(
                    id = nextTowerId++,
                    type = action.towerType,
                    level = 1,
                    gridRow = action.gridRow,
                    gridCol = action.gridCol,
                    metaBonus = metaBonus
                )
                towers.add(tower)
                playerState = playerState.spendGold(action.towerType.baseCost)
                eventBus.emit(GameEvent.TowerPlaced(tower.id, tower.type, tower.gridRow, tower.gridCol))
                recalculateCombos()
            }
            is GameAction.UpgradeTower -> {
                val idx = towers.indexOfFirst { it.id == action.towerId }
                val t = towers[idx]
                playerState = playerState.spendGold(t.upgradeCost)
                towers[idx] = t.copy(level = t.level + 1)
                eventBus.emit(GameEvent.TowerUpgraded(t.id, t.level + 1, t.gridRow, t.gridCol))
                recalculateCombos()
            }
            is GameAction.SellTower -> {
                val t = towers.first { it.id == action.towerId }
                towers.remove(t)
                playerState = playerState.earnGold(t.sellValue)
                eventBus.emit(GameEvent.TowerSold(t.id, t.sellValue))
                recalculateCombos()
            }
            is GameAction.StartWave -> {
                val maxWaves = if (isEndless) Int.MAX_VALUE else level.waves.size
                if (state == GameState.WaitingForWave && playerState.currentWave < maxWaves) {
                    waveSpawner.startWave(playerState.currentWave)
                    waveSpawningComplete = false
                    state = GameState.Playing
                }
            }
            is GameAction.SetSpeed -> { /* handled above, unreachable */ }
        }
        return true
    }

    fun pause() {
        if (state == GameState.Playing) {
            state = GameState.Paused
            speedMultiplier = 1f
        }
    }
    fun resume() { if (state == GameState.Paused)  state = GameState.Playing }

    fun snapshot() = GameSnapshot(
        state           = state,
        towers          = towers.toList(),
        enemies         = enemies.toList(),
        projectiles     = projectiles.toList(),
        playerState     = playerState,
        currentWave     = playerState.currentWave,
        totalWaves      = if (isEndless) Int.MAX_VALUE else level.waves.size,
        events          = eventBus.events,
        metaBonus       = metaBonus,
        starsEarned     = starsEarned,
        rpEarned        = rpEarned,
        speedMultiplier = speedMultiplier,
        wavePreview     = buildWavePreview(),
        difficulty      = difficulty,
        isEndless       = isEndless,
        totalKills      = totalKills,
        activeCombos    = activeCombos
    )

    /** Expose active combos for the ProjectileSystem to apply damage bonuses. */
    fun getActiveCombos(): List<ActiveCombo> = activeCombos

    private fun buildWavePreview(): List<Pair<EnemyType, Int>> {
        val waveIdx = playerState.currentWave
        if (isEndless) {
            val preview = EndlessWaveGenerator.generate(waveIdx)
            return preview.groups.map { it.enemyType to it.count }
        }
        if (waveIdx >= level.waves.size) return emptyList()
        return level.waves[waveIdx].groups.map { it.enemyType to it.count }
    }

    private fun recalculateCombos() {
        activeCombos = ComboSystem.detectCombos(towers)
    }

    private fun checkWinLoseConditions() {
        if (playerState.isGameOver) {
            state = GameState.Lost
            eventBus.emit(GameEvent.GameLost)
            return
        }
        if (waveSpawningComplete && enemies.isEmpty() && projectiles.isEmpty()) {
            val nextWave = playerState.currentWave + 1
            val maxLives = maxOf(1, level.startingLives + metaBonus.startingLivesBonus + difficulty.startingLivesBonus)
            if (metaBonus.livesPerWaveBonus > 0) {
                playerState = playerState.copy(
                    lives = minOf(playerState.lives + metaBonus.livesPerWaveBonus, maxLives)
                )
            }
            eventBus.emit(GameEvent.WaveCompleted(playerState.currentWave + 1, playerState.lives))
            playerState = playerState.copy(currentWave = nextWave)

            if (isEndless) {
                waveSpawner.addWave(EndlessWaveGenerator.generate(nextWave))
                state = GameState.WaitingForWave
                speedMultiplier = 1f
            } else if (nextWave >= level.waves.size) {
                val livePct = playerState.lives.toFloat() / maxLives
                starsEarned = when {
                    livePct >= 0.8f -> 3
                    livePct >= 0.5f -> 2
                    else            -> 1
                }
                rpEarned = (starsEarned * difficulty.rpMultiplier).toInt()
                state = GameState.Won
                eventBus.emit(GameEvent.GameWon)
            } else {
                state = GameState.WaitingForWave
                speedMultiplier = 1f
            }
        }
    }
}
