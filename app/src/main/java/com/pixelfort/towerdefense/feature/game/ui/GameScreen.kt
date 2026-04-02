package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape as RCS
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelfort.towerdefense.core.util.SpriteAssetLoader
import com.pixelfort.towerdefense.engine.GameState
import com.pixelfort.towerdefense.engine.level.Levels
import com.pixelfort.towerdefense.engine.model.ActiveCombo
import com.pixelfort.towerdefense.engine.model.ActiveWaveEvent
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.engine.system.ComboSystem
import com.pixelfort.towerdefense.core.datastore.GameplaySettingsData
import com.pixelfort.towerdefense.feature.game.tutorial.TutorialOverlay
import com.pixelfort.towerdefense.feature.game.viewmodel.GameUiState
import com.pixelfort.towerdefense.feature.game.viewmodel.GameViewModel
import android.widget.Toast
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    levelId: Int,
    difficulty: String = "NORMAL",
    isEndless: Boolean = false,
    onBack: () -> Unit,
    onGoToUpgrades: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameplaySettings by viewModel.gameplaySettingsFlow.collectAsStateWithLifecycle(initialValue = GameplaySettingsData())
    val bestiaryToast by viewModel.bestiaryToast.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SPEC-031: Show bestiary unlock toast
    LaunchedEffect(bestiaryToast) {
        bestiaryToast?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearBestiaryToast()
        }
    }

    val levelDef = Levels.getById(levelId)
    val map = levelDef.map
    val cellEffects = levelDef.cellEffects
    val density = LocalDensity.current

    // FPS counter state
    var fpsText by remember { mutableStateOf("") }
    var frameCount by remember { mutableStateOf(0) }
    var lastFpsTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(uiState) {
        if (gameplaySettings.showFpsCounter) {
            frameCount++
            val now = System.currentTimeMillis()
            if (now - lastFpsTime >= 1000L) {
                fpsText = "$frameCount FPS"
                frameCount = 0
                lastFpsTime = now
            }
        }
    }

    // Floating tooltip state
    var tooltipTower by remember { mutableStateOf<TowerType?>(null) }
    var tooltipMetaBonus by remember { mutableStateOf(MetaBonus()) }

    // Auto-dismiss tooltip after 4 seconds
    LaunchedEffect(tooltipTower) {
        if (tooltipTower != null) {
            delay(4000L)
            tooltipTower = null
        }
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1A))
            .padding(top = statusBarPadding, bottom = navBarPadding)
    ) {
        // Calculate cell size so map fits on screen with HUD space reserved
        val hudHeightDp = 160.dp
        val topBarHeightDp = 52.dp
        val gameAreaHeight = maxHeight - hudHeightDp - topBarHeightDp
        val cellSizeDp = minOf(
            maxWidth / map.cols,
            gameAreaHeight / map.rows
        )
        val cellSizePx = with(density) { cellSizeDp.toPx() }
        val gameWidthDp = cellSizeDp * map.cols
        val gameHeightDp = cellSizeDp * map.rows

        LaunchedEffect(cellSizePx) {
            if (cellSizePx > 0f) viewModel.initGame(cellSizePx)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top info bar ─────────────────────────────────────
            when (val state = uiState) {
                is GameUiState.Loading -> {
                    TopInfoBarPlaceholder(topBarHeightDp = topBarHeightDp, onPause = {})
                }
                is GameUiState.Playing -> {
                    TopInfoBar(
                        gold = state.snapshot.playerState.gold,
                        lives = state.snapshot.playerState.lives,
                        currentWave = state.snapshot.currentWave,
                        totalWaves = state.snapshot.totalWaves,
                        speedMultiplier = state.snapshot.speedMultiplier,
                        onPause = viewModel::pause,
                        onToggleSpeed = viewModel::toggleSpeed,
                        modifier = Modifier.height(topBarHeightDp)
                    )
                }
            }

            // ── Game canvas ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is GameUiState.Loading -> {
                        CircularProgressIndicator(
                            color = Color(0xFFFFD700),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    is GameUiState.Playing -> {
                        Box {
                            GameCanvas(
                                snapshot = state.snapshot,
                                map = map,
                                cellSize = state.cellSize,
                                cellEffects = cellEffects,
                                particles = state.particles,
                                floatingTexts = state.floatingTexts,
                                screenShake = state.screenShake,
                                trailSystem = state.trailSystem,
                                ambientParticles = state.ambientParticles,
                                deathFlashes = state.deathFlashes,
                                sellEffects = state.sellEffects,
                                selectedTowerId = state.selectedTowerId,
                                selectedTowerType = state.selectedTowerType,
                                spriteLoader = viewModel.spriteLoader,
                                elapsedMs = state.elapsedMs,
                                onCellTapped = viewModel::onCellTapped,
                                modifier = Modifier.size(gameWidthDp, gameHeightDp)
                            )

                            // SPEC-029: Skill bar at top-right of game area
                            if (state.snapshot.skills.isNotEmpty()) {
                                SkillBar(
                                    skills = state.snapshot.skills,
                                    isMeteorTargeting = state.isMeteorTargeting,
                                    onSkillTapped = viewModel::onSkillTapped,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }
            }

            // ── Bottom HUD ────────────────────────────────────────
            when (val state = uiState) {
                is GameUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hudHeightDp)
                            .background(Color(0xFF0D0D1A))
                    )
                }
                is GameUiState.Playing -> {
                    HudOverlay(
                        playerState = state.snapshot.playerState,
                        gameState = state.snapshot.state,
                        currentWave = state.snapshot.currentWave,
                        totalWaves = state.snapshot.totalWaves,
                        selectedTowerType = state.selectedTowerType,
                        metaBonus = state.metaBonus,
                        wavePreview = state.snapshot.wavePreview,
                        placedTowers = state.snapshot.towers,
                        onSelectTower = viewModel::selectTowerType,
                        onStartWave = viewModel::startWave,
                        onPause = viewModel::pause,
                        onShowTooltip = { towerType ->
                            tooltipTower = towerType
                            tooltipMetaBonus = state.metaBonus
                        },
                        spriteLoader = viewModel.spriteLoader
                    )
                }
            }
        }

        // ── Overlays (float on top of everything) ─────────────────
        if (uiState is GameUiState.Playing) {
            val state = uiState as GameUiState.Playing
            val snapshot = state.snapshot

            // Pause overlay
            if (snapshot.state == GameState.Paused) {
                PauseOverlay(
                    onResume = viewModel::resume,
                    onQuit = onBack
                )
            }

            // Boss warning banner
            if (state.bossWarningActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, Color(0xFFFF1744))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = statusBarPadding + topBarHeightDp + 16.dp)
                            .background(Color(0xDD8B0000), RoundedCornerShape(12.dp))
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "BOSS INCOMING",
                            color = Color(0xFFFFD700),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Flash effect overlay
            if (state.flashEffect.isActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(state.flashEffect.color.copy(alpha = state.flashEffect.alpha))
                )
            }

            // SPEC-032: Screen tint overlay for active wave events
            if (snapshot.activeWaveEvents.isNotEmpty()) {
                for (event in snapshot.activeWaveEvents) {
                    val tintColor = Color(event.type.tintColor)
                    if (tintColor.alpha > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(tintColor)
                        )
                    }
                }
            }

            // SPEC-032: Wave event banner
            EventBanner(
                event = state.waveEventBanner,
                visible = state.waveEventBannerRemainingMs > 0L,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding + topBarHeightDp + 8.dp)
            )

            // FPS counter overlay
            if (gameplaySettings.showFpsCounter && fpsText.isNotEmpty()) {
                Text(
                    text = fpsText,
                    color = Color(0xFF00FF00),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = statusBarPadding + 4.dp, end = 8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Victory overlay
            if (snapshot.state == GameState.Won) {
                GameEndOverlay(
                    isVictory = true,
                    starsEarned = snapshot.starsEarned,
                    rpEarned = snapshot.rpEarned,
                    onBack = onBack,
                    onGoToUpgrades = onGoToUpgrades
                )
            }

            // Defeat overlay
            if (snapshot.state == GameState.Lost) {
                GameEndOverlay(
                    isVictory = false,
                    starsEarned = 0,
                    rpEarned = 0,
                    isEndless = snapshot.isEndless,
                    wavesReached = snapshot.currentWave,
                    totalKills = snapshot.totalKills,
                    onBack = onBack,
                    onGoToUpgrades = onGoToUpgrades
                )
            }

            // Tower info popup (when a placed tower is selected)
            val selectedTower = state.selectedTowerId?.let { id ->
                snapshot.towers.find { it.id == id }
            }
            if (selectedTower != null) {
                val towerCombos = ComboSystem.combosForTower(selectedTower.id, snapshot.activeCombos)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = (hudHeightDp + 8.dp))
                ) {
                    TowerInfoPopup(
                        tower = selectedTower,
                        canAffordUpgrade = snapshot.playerState.canAfford(selectedTower.upgradeCost),
                        activeCombos = towerCombos,
                        isSellConfirming = state.sellConfirmTowerId == selectedTower.id,
                        onUpgrade = viewModel::upgradeTower,
                        onSell = viewModel::onSellTapped,
                        onDismiss = { viewModel.selectTowerType(null) }
                    )
                }
            }
        }

        // ── Floating Tower Tooltip (over everything, does NOT affect layout) ──
        tooltipTower?.let { tt ->
            FloatingTowerTooltip(
                towerType = tt,
                metaBonus = tooltipMetaBonus,
                spriteLoader = (uiState as? GameUiState.Playing)?.let { viewModel.spriteLoader },
                onDismiss = { tooltipTower = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = hudHeightDp + 4.dp)
            )
        }

        // ── Tutorial overlay (topmost layer, pauses game) ──
        if (uiState is GameUiState.Playing) {
            val playingState = uiState as GameUiState.Playing
            if (playingState.tutorialState.isActive) {
                TutorialOverlay(
                    tutorialState = playingState.tutorialState,
                    onNext = viewModel::advanceTutorial,
                    onSkip = viewModel::skipTutorial
                )
            }
        }
    }
}

@Composable
private fun TopInfoBar(
    gold: Int,
    lives: Int,
    currentWave: Int,
    totalWaves: Int,
    speedMultiplier: Float = 1f,
    onPause: () -> Unit,
    onToggleSpeed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val speedLabel = when {
        speedMultiplier >= 2.5f -> "▶▶▶"
        speedMultiplier >= 1.5f -> "▶▶"
        else -> "▶"
    }
    val speedColor = when {
        speedMultiplier >= 2.5f -> Color(0xFFFF5722)
        speedMultiplier >= 1.5f -> Color(0xFFFFD700)
        else -> Color(0xFFBBCCDD)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0D1A))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Gold
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("💰", fontSize = 14.sp)
            Spacer(Modifier.width(2.dp))
            Text(
                text = "$gold",
                color = Color(0xFFFFD700),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Lives
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("❤️", fontSize = 14.sp)
            Spacer(Modifier.width(2.dp))
            Text(
                text = "$lives",
                color = Color(0xFFEF5350),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Wave indicator
        Text(
            text = if (totalWaves == Int.MAX_VALUE) "第 ${currentWave + 1} 波" else "第 ${currentWave + 1}/$totalWaves 波",
            color = Color(0xFFBBCCDD),
            fontSize = 13.sp
        )
        // Speed button
        Button(
            onClick = onToggleSpeed,
            modifier = Modifier.size(36.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3050)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(speedLabel, fontSize = 11.sp, color = speedColor)
        }
        // Pause button
        Button(
            onClick = onPause,
            modifier = Modifier.size(36.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334466)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("⏸", fontSize = 14.sp)
        }
    }
}

@Composable
private fun TopInfoBarPlaceholder(topBarHeightDp: Dp, @Suppress("UNUSED_PARAMETER") onPause: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(topBarHeightDp)
            .background(Color(0xFF0D0D1A))
    )
}

@Composable
private fun PauseOverlay(onResume: () -> Unit, onQuit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0xFF1A1A2E), RoundedCornerShape(16.dp))
                .padding(32.dp)
        ) {
            Text("⏸ 暫停", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onResume,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) { Text("繼續遊戲", fontSize = 16.sp) }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onQuit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) { Text("退出關卡", fontSize = 16.sp) }
        }
    }
}

@Composable
private fun GameEndOverlay(
    isVictory: Boolean,
    starsEarned: Int,
    rpEarned: Int,
    isEndless: Boolean = false,
    wavesReached: Int = 0,
    totalKills: Int = 0,
    onBack: () -> Unit,
    onGoToUpgrades: () -> Unit
) {
    val context = LocalContext.current
    val illustration = remember(isVictory) {
        val filename = if (isVictory) "sprites/extras/result_victory.png" else "sprites/extras/result_defeat.png"
        try {
            context.assets.open(filename).use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (_: Exception) { null }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color(0xFF1A1A2E), RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            // Result illustration
            illustration?.let { bmp ->
                Image(
                    bitmap = bmp,
                    contentDescription = if (isVictory) "Victory" else "Defeat",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(12.dp))
            }

            Text(
                text = if (isEndless) "無盡模式結束" else if (isVictory) "勝利！" else "失敗",
                color = if (isVictory) Color(0xFFFFD700) else if (isEndless) Color(0xFFCE93D8) else Color(0xFFEF5350),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            if (isVictory) {
                // Stars display
                val starsFilled = starsEarned.coerceIn(0, 3)
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(3) { i ->
                        Text(
                            text = if (i < starsFilled) "⭐" else "☆",
                            fontSize = 36.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                // RP earned
                if (rpEarned > 0) {
                    Text(
                        text = "+ $rpEarned 🔬 研究點",
                        color = Color(0xFF80DEEA),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    onClick = onGoToUpgrades,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2))
                ) { Text("🔬 場外升級", fontSize = 15.sp) }
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVictory) Color(0xFF37474F) else Color(0xFF1565C0)
                )
            ) {
                Text(if (isVictory) "返回選單" else "再試一次", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun TowerInfoPopup(
    tower: Tower,
    canAffordUpgrade: Boolean,
    activeCombos: List<ActiveCombo> = emptyList(),
    isSellConfirming: Boolean = false,
    onUpgrade: () -> Unit,
    onSell: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF1A1A2E), RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${tower.type.nameZh}  Lv.${tower.level}",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onDismiss,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF445566)),
                shape = RoundedCornerShape(6.dp)
            ) { Text("✕", fontSize = 12.sp) }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = tower.type.descZh,
            color = Color(0xFF99AABB),
            fontSize = 12.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("⚔ ${tower.stats.damage}", color = Color(0xFFEF5350), fontSize = 12.sp)
            Text("🎯 ${"%.1f".format(tower.stats.range)}", color = Color(0xFF42A5F5), fontSize = 12.sp)
            Text("⚡ ${tower.stats.fireRateMs}ms", color = Color(0xFFFFEE58), fontSize = 12.sp)
        }
        // SPEC-028: Show active combo names
        if (activeCombos.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = activeCombos.joinToString(" | ") { it.comboType.nameZh },
                    color = Color(0xFFFFD740),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            for (combo in activeCombos) {
                Text(
                    text = combo.comboType.descZh,
                    color = Color(0xFF90CAF9),
                    fontSize = 10.sp
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!tower.isMaxLevel) {
                Button(
                    onClick = onUpgrade,
                    enabled = canAffordUpgrade,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) { Text("升級 ${tower.upgradeCost}g", fontSize = 13.sp) }
            }
            Button(
                onClick = onSell,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSellConfirming) Color(0xFFFF6F00) else Color(0xFFC62828)
                )
            ) {
                Text(
                    text = if (isSellConfirming) "確認出售?" else "出售 ${tower.sellValue}g",
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun FloatingTowerTooltip(
    towerType: TowerType,
    metaBonus: MetaBonus,
    spriteLoader: SpriteAssetLoader? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = towerType.statsForLevel(1, metaBonus)
    val isUnlocked = !towerType.isLockedByDefault || towerType in metaBonus.unlockedTowers

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(Color(0xEE1A1A2E), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF3355AA), RoundedCornerShape(12.dp))
            .clickable { onDismiss() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = towerType.nameZh,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isUnlocked) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "🔒 需要場外升級解鎖",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    "✕",
                    color = Color(0xFF7788AA),
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = towerType.descZh,
                color = Color(0xFFAABBCC),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("⚔ ${stats.damage}", color = Color(0xFFEF5350), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("🎯 ${"%.1f".format(stats.range)}", color = Color(0xFF42A5F5), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("⚡ ${stats.fireRateMs}ms", color = Color(0xFFFFEE58), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("💰 ${towerType.baseCost}g", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // Level preview: show Lv1 → Lv2 → Lv3 sprites
            if (spriteLoader != null) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (lv in 1..3) {
                        val lvSprite = spriteLoader.getTowerSprite(towerType, lv)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (lvSprite != null) {
                                Image(
                                    bitmap = lvSprite,
                                    contentDescription = "Lv$lv",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(56.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFF222244), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Lv$lv", color = Color.Gray, fontSize = 10.sp)
                                }
                            }
                            Text(
                                text = "Lv$lv",
                                color = Color(0xFFAABBCC),
                                fontSize = 10.sp
                            )
                        }
                        if (lv < 3) {
                            Text("→", color = Color(0xFF556677), fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
