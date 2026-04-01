package com.pixelfort.towerdefense.feature.game.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelfort.towerdefense.engine.GameEngine
import com.pixelfort.towerdefense.engine.GameState
import com.pixelfort.towerdefense.engine.action.GameAction
import com.pixelfort.towerdefense.engine.level.Levels
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.engine.event.GameEvent
import com.pixelfort.towerdefense.engine.model.TowerEffect
import com.pixelfort.towerdefense.feature.game.vfx.FlashEffect
import com.pixelfort.towerdefense.feature.game.vfx.FloatingTextSystem
import com.pixelfort.towerdefense.feature.game.vfx.ParticleSystem
import com.pixelfort.towerdefense.feature.game.vfx.ScreenShake
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
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val levelId: Int = savedStateHandle.get<Int>("levelId") ?: 1

    private val particleSystem = ParticleSystem()
    private val floatingTextSystem = FloatingTextSystem()
    private var screenShake = ScreenShake.IDLE
    private var flashEffect = FlashEffect.NONE
    private var metaBonus = MetaBonus()
    private var engine: GameEngine? = null
    private var currentCellSize: Float = 80f

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var selectedTowerType: TowerType? = null
    private var selectedTowerId: Int? = null
    private var gameEndHandled = false

    /** Called by UI once the canvas size is known (via BoxWithConstraints). */
    fun initGame(cellSize: Float) {
        if (engine != null) return  // already initialized
        currentCellSize = cellSize
        viewModelScope.launch {
            metaBonus = MetaBonus.from(metaUpgradeRepository.getState())
            engine = GameEngine(Levels.getById(levelId), cellSize, metaBonus)
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

                val snapshot = eng.snapshot()

                // Process VFX events
                particleSystem.processEvents(snapshot.events, currentCellSize)
                particleSystem.update(deltaMs)
                floatingTextSystem.processEvents(snapshot.events)
                floatingTextSystem.update(deltaMs)
                processShakeAndFlash(snapshot.events, deltaMs)

                emitState()

                // Handle game end (save progress, award RP) — only once
                val endState = snapshot.state
                if ((endState == GameState.Won || endState == GameState.Lost) && !gameEndHandled) {
                    gameEndHandled = true
                    if (endState == GameState.Won) {
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
        for (event in events) {
            when (event) {
                is GameEvent.LivesLost -> {
                    screenShake = screenShake.trigger(12f, 300L)
                    flashEffect = flashEffect.trigger(
                        androidx.compose.ui.graphics.Color(0xFFEF5350), 200L
                    )
                }
                is GameEvent.ProjectileHit -> {
                    if (event.effect is TowerEffect.AoeSplash && event.damage >= 80) {
                        screenShake = screenShake.trigger(8f, 200L)
                        flashEffect = flashEffect.trigger(
                            androidx.compose.ui.graphics.Color.White, 50L
                        )
                    }
                }
                is GameEvent.EnemyKilled -> {
                    if (event.reward >= 50) {
                        screenShake = screenShake.trigger(16f, 400L)
                    }
                }
                else -> Unit
            }
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
            cellSize = currentCellSize
        )
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
