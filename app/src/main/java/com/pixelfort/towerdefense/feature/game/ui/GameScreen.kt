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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelfort.towerdefense.engine.GameState
import com.pixelfort.towerdefense.engine.level.Levels
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.feature.game.viewmodel.GameUiState
import com.pixelfort.towerdefense.feature.game.viewmodel.GameViewModel

@Composable
fun GameScreen(
    levelId: Int,
    onBack: () -> Unit,
    onGoToUpgrades: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val map = Levels.getById(levelId).map
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1A))
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
                        GameCanvas(
                            snapshot = state.snapshot,
                            map = map,
                            cellSize = state.cellSize,
                            particles = state.particles,
                            selectedTowerId = state.selectedTowerId,
                            onCellTapped = viewModel::onCellTapped,
                            modifier = Modifier.size(gameWidthDp, gameHeightDp)
                        )
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
                        onSelectTower = viewModel::selectTowerType,
                        onStartWave = viewModel::startWave,
                        onPause = viewModel::pause
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
                    onBack = onBack,
                    onGoToUpgrades = onGoToUpgrades
                )
            }

            // Tower info popup (when a placed tower is selected)
            val selectedTower = state.selectedTowerId?.let { id ->
                snapshot.towers.find { it.id == id }
            }
            if (selectedTower != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = (hudHeightDp + 8.dp))
                ) {
                    TowerInfoPopup(
                        tower = selectedTower,
                        canAffordUpgrade = snapshot.playerState.canAfford(selectedTower.upgradeCost),
                        onUpgrade = viewModel::upgradeTower,
                        onSell = viewModel::sellTower,
                        onDismiss = { viewModel.selectTowerType(null) }
                    )
                }
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
            text = "第 ${currentWave + 1}/$totalWaves 波",
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
    onBack: () -> Unit,
    onGoToUpgrades: () -> Unit
) {
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
            Text(
                text = if (isVictory) "🏆 勝利！" else "💀 失敗",
                color = if (isVictory) Color(0xFFFFD700) else Color(0xFFEF5350),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) { Text("出售 ${tower.sellValue}g", fontSize = 13.sp) }
        }
    }
}
