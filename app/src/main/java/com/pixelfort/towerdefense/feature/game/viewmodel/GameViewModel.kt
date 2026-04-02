package com.pixelfort.towerdefense.feature.game.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelfort.towerdefense.core.datastore.GameplaySettingsData
import com.pixelfort.towerdefense.core.datastore.SettingsDataStore
import com.pixelfort.towerdefense.engine.GameEngine
import com.pixelfort.towerdefense.engine.GameState
import com.pixelfort.towerdefense.engine.action.GameAction
import com.pixelfort.towerdefense.engine.level.Levels
import com.pixelfort.towerdefense.engine.model.DifficultyMode
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.model.TowerEffect
import com.pixelfort.towerdefense.feature.game.tutorial.TutorialState
import com.pixelfort.towerdefense.feature.game.vfx.AmbientSystem
import com.pixelfort.towerdefense.feature.game.vfx.DeathFlash
import com.pixelfort.towerdefense.feature.game.vfx.FlashEffect
import com.pixelfort.towerdefense.feature.game.vfx.FloatingTextSystem
import com.pixelfort.towerdefense.feature.game.vfx.ParticleSystem
import com.pixelfort.towerdefense.feature.game.vfx.ScreenShake
import com.pixelfort.towerdefense.feature.game.vfx.TrailSystem
import com.pixelfort.towerdefense.core.util.SpriteAssetLoader
import com.pixelfort.towerdefense.core.database.dao.EndlessHighScoreDao
import com.pixelfort.towerdefense.core.database.entity.EndlessHighScoreEntity
import com.pixelfort.towerdefense.feature.metaupgrade.domain.MetaUpgradeRepository
import com.pixelfort.towerdefense.feature.progress.domain.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val metaUpgradeRepository: MetaUpgradeRepository,
    private val progressRepository: ProgressRepository,
    private val endlessHighScoreDao: EndlessHighScoreDao,
    val spriteLoader: SpriteAssetLoader,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val levelId: Int = savedStateHandle.get<Int>("levelId") ?: 1
    private val difficulty: DifficultyMode = try {
        DifficultyMode.valueOf(savedStateHandle.get<String>("difficulty") ?: "NORMAL")
    } catch (_: Exception) { DifficultyMode.NORMAL }
    private val isEndless: Boolean = savedStateHandle.get<Boolean>("isEndless") ?: false

    private val particleSystem = ParticleSystem()
    private val floatingTextSystem = FloatingTextSystem()
    private val trailSystem = TrailSystem()
    private var ambientSystem: AmbientSystem? = null
    private val deathFlashes = mutableListOf<DeathFlash>()
    private var screenShake = ScreenShake.IDLE
    private var flashEffect = FlashEffect.NONE
    private var metaBonus = MetaBonus()
    private var engine: GameEngine? = null
    private var currentCellSize: Float = 80f
    private var bossWarningRemainingMs: Long = 0L
    private var tutorialState = TutorialState(isActive = false, isCompleted = true)
    private var tutorialStateInitialized = false

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var selectedTowerType: TowerType? = null
    private var selectedTowerId: Int? = null
    private var gameEndHandled = false
    private var gameElapsedMs: Long = 0L

    val gameplaySettingsFlow = settingsDataStore.settingsFlow
    private var _currentGameplaySettings = GameplaySettingsData()

    init {
        viewModelScope.launch {
            settingsDataStore.settingsFlow.collect { settings ->
                _currentGameplaySettings = settings
                if (!tutorialStateInitialized) {
                    tutorialStateInitialized = true
                    tutorialState = TutorialState.initial(
                        alreadyCompleted = settings.tutorialCompleted || levelId != 1
                    )
                }
            }
        }
    }

    /** Called by UI once the canvas size is known (via BoxWithConstraints). */
    fun initGame(cellSize: Float) {
        if (engine != null) return  // already initialized
        currentCellSize = cellSize
        viewModelScope.launch {
            spriteLoader.loadAll()
            metaBonus = MetaBonus.from(metaUpgradeRepository.getState())
            val level = Levels.getById(levelId)
            engine = GameEngine(level, cellSize, metaBonus, difficulty, isEndless)
            // Initialize ambient particle system sized to the game map
            ambientSystem = AmbientSystem(
                canvasWidth = level.map.cols * cellSize,
                canvasHeight = level.map.rows * cellSize,
                levelId = levelId
            )
            emitState()
        }
    }

    fun startWave() {
        engine?.processAction(GameAction.StartWave)
        startGameLoop()
    }

    fun toggleSpeed() {
        val eng = engine ?: return
        val current = eng.snapshot().speedMultiplier
        val next = when {
            current < 1.5f -> 2f
            current < 2.5f -> 3f
            else -> 1f
        }
        eng.processAction(GameAction.SetSpeed(next))
        emitState()
    }

    fun selectTowerType(type: TowerType?) {
        selectedTowerType = type
        selectedTowerId = null
        emitState()
    }

    fun onCellTapped(gridRow: Int, gridCol: Int) {
        val eng = engine ?: return
        val towerType = selectedTowerType
        if (towerType != null) {
            val placed = eng.processAction(GameAction.PlaceTower(towerType, gridRow, gridCol))
            if (placed) selectedTowerType = null
        } else {
            val snapshot = eng.snapshot()
            val tower = snapshot.towers.find { it.gridRow == gridRow && it.gridCol == gridCol }
            selectedTowerId = tower?.id
        }
        emitState()
    }

    fun upgradeTower() {
        val towerId = selectedTowerId ?: return
        engine?.processAction(GameAction.UpgradeTower(towerId))
        emitState()
    }

    fun sellTower() {
        val towerId = selectedTowerId ?: return
        engine?.processAction(GameAction.SellTower(towerId))
        selectedTowerId = null
        emitState()
    }

    fun advanceTutorial() {
        tutorialState = tutorialState.advance()
        if (tutorialState.isCompleted) {
            viewModelScope.launch { settingsDataStore.setTutorialCompleted(true) }
        }
        emitState()
    }

    fun skipTutorial() {
        tutorialState = tutorialState.skip()
        viewModelScope.launch { settingsDataStore.setTutorialCompleted(true) }
        emitState()
    }

    fun pause() {
        engine?.pause()
        gameLoopJob?.cancel()
        gameLoopJob = null
        emitState()
    }

    fun resume() {
        engine?.resume()
        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastFrameTimeMs = System.currentTimeMillis()

            while (isActive) {
                kotlinx.coroutines.delay(16L)
                val now = System.currentTimeMillis()
                val deltaMs = (now - lastFrameTimeMs).coerceIn(1L, 100L)
                lastFrameTimeMs = now

                val eng = engine ?: break
                eng.update(deltaMs)
                gameElapsedMs += deltaMs

                val snapshot = eng.snapshot()

                // Process VFX events
                particleSystem.processEvents(snapshot.events, currentCellSize)
                particleSystem.update(deltaMs)

                // Update projectile trails
                trailSystem.update(snapshot.projectiles)

                // Update ambient particles
                ambientSystem?.update(deltaMs)

                // Process death flashes from EnemyKilled events
                for (event in snapshot.events) {
                    if (event is GameEvent.EnemyKilled) {
                        deathFlashes.add(
                            DeathFlash(
                                x = event.pixelX,
                                y = event.pixelY,
                                size = event.enemyType.size,
                                remainingMs = DeathFlash.FLASH_DURATION_MS
                            )
                        )
                    }
                }
                // Update and prune death flashes
                val updatedFlashes = deathFlashes.map { it.update(deltaMs) }.filter { !it.isDead }
                deathFlashes.clear()
                deathFlashes.addAll(updatedFlashes)

                // Only process floating texts (damage numbers) if enabled
                if (_currentGameplaySettings.damageNumbersEnabled) {
                    floatingTextSystem.processEvents(snapshot.events)
                }
                floatingTextSystem.update(deltaMs)

                processShakeAndFlash(snapshot.events, deltaMs)

                emitState()

                // Handle game end (save progress, award RP) -- only once
                val endState = snapshot.state
                if ((endState == GameState.Won || endState == GameState.Lost) && !gameEndHandled) {
                    gameEndHandled = true
                    if (isEndless && endState == GameState.Lost) {
                        endlessHighScoreDao.insert(
                            EndlessHighScoreEntity(
                                wavesReached = snapshot.currentWave,
                                totalKills = snapshot.totalKills,
                                date = System.currentTimeMillis()
                            )
                        )
                    } else if (endState == GameState.Won && !isEndless) {
                        progressRepository.saveProgress(levelId, snapshot.starsEarned)
                        metaUpgradeRepository.addResearchPoints(snapshot.rpEarned)
                    }
                    break
                }
                if (endState == GameState.WaitingForWave) {
                    break
                }
            }
        }
    }

    private fun processShakeAndFlash(events: List<GameEvent>, deltaMs: Long) {
        val shakeEnabled = _currentGameplaySettings.screenShakeEnabled
        for (event in events) {
            when (event) {
                is GameEvent.LivesLost -> {
                    if (shakeEnabled) screenShake = screenShake.trigger(12f, 300L)
                    flashEffect = flashEffect.trigger(
                        androidx.compose.ui.graphics.Color(0xFFEF5350), 200L
                    )
                }
                is GameEvent.ProjectileHit -> {
                    if (event.effect is TowerEffect.AoeSplash && event.damage >= 80) {
                        if (shakeEnabled) screenShake = screenShake.trigger(8f, 200L)
                        flashEffect = flashEffect.trigger(
                            androidx.compose.ui.graphics.Color.White, 50L
                        )
                    }
                }
                is GameEvent.EnemyKilled -> {
                    if (event.enemyType.isBoss) {
                        if (shakeEnabled) screenShake = screenShake.trigger(20f, 600L)
                        flashEffect = flashEffect.trigger(
                            androidx.compose.ui.graphics.Color.White, 150L
                        )
                    } else if (event.reward >= 50) {
                        if (shakeEnabled) screenShake = screenShake.trigger(16f, 400L)
                    }
                }
                is GameEvent.BossWarning -> {
                    bossWarningRemainingMs = 3000L
                    if (shakeEnabled) screenShake = screenShake.trigger(6f, 500L)
                    flashEffect = flashEffect.trigger(
                        androidx.compose.ui.graphics.Color(0xFFFF1744), 300L
                    )
                }
                is GameEvent.BossEnraged -> {
                    if (shakeEnabled) screenShake = screenShake.trigger(14f, 400L)
                    flashEffect = flashEffect.trigger(
                        androidx.compose.ui.graphics.Color(0xFFFF6F00), 200L
                    )
                }
                else -> Unit
            }
        }
        // Update boss warning timer
        if (bossWarningRemainingMs > 0) {
            bossWarningRemainingMs = (bossWarningRemainingMs - deltaMs).coerceAtLeast(0L)
        }
        screenShake = screenShake.update(deltaMs)
        flashEffect = flashEffect.update(deltaMs)
    }

    private fun emitState() {
        val eng = engine ?: return
        _uiState.value = GameUiState.Playing(
            snapshot = eng.snapshot(),
            selectedTowerType = selectedTowerType,
            selectedTowerId = selectedTowerId,
            particles = particleSystem.activeParticles,
            floatingTexts = floatingTextSystem.activeTexts,
            screenShake = screenShake,
            flashEffect = flashEffect,
            metaBonus = metaBonus,
            cellSize = currentCellSize,
            elapsedMs = gameElapsedMs,
            bossWarningActive = bossWarningRemainingMs > 0,
            tutorialState = tutorialState,
            trailSystem = trailSystem,
            ambientParticles = ambientSystem?.activeParticles ?: emptyList(),
            deathFlashes = deathFlashes.toList()
        )
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
