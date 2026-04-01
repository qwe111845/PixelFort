package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelfort.towerdefense.engine.GameState
import com.pixelfort.towerdefense.engine.model.MetaBonus
import com.pixelfort.towerdefense.engine.model.PlayerState
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.feature.game.renderer.TowerRenderer.drawTowers

// Tower colors for HUD buttons (matching TowerRenderer palette)
private val towerHudColor = mapOf(
    TowerType.ARCHER    to Color(0xFF1565C0),
    TowerType.CANNON    to Color(0xFFB71C1C),
    TowerType.MAGIC     to Color(0xFF6A1B9A),
    TowerType.SNIPER    to Color(0xFF1B5E20),
    TowerType.FROST     to Color(0xFF0D47A1),
    TowerType.LIGHTNING to Color(0xFFF9A825),
    TowerType.POISON    to Color(0xFF33691E),
    TowerType.BOMB      to Color(0xFF4E342E)
)

@Composable
fun HudOverlay(
    playerState: PlayerState,
    gameState: GameState,
    currentWave: Int,
    totalWaves: Int,
    selectedTowerType: TowerType?,
    metaBonus: MetaBonus,
    onSelectTower: (TowerType?) -> Unit,
    onStartWave: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tooltipTower by remember { mutableStateOf<TowerType?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0D1A))
    ) {
        // Wave start button
        if (gameState == GameState.WaitingForWave) {
            Button(
                onClick = onStartWave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    "▶  開始第 ${currentWave + 1} 波  /$totalWaves",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Tower selection scroll bar
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(TowerType.entries) { towerType ->
                val isUnlocked = !towerType.isLockedByDefault ||
                        towerType in metaBonus.unlockedTowers
                val isSelected = selectedTowerType == towerType
                val canAfford = isUnlocked && playerState.canAfford(towerType.baseCost)

                TowerHudButton(
                    towerType = towerType,
                    isUnlocked = isUnlocked,
                    isSelected = isSelected,
                    canAfford = canAfford,
                    onSelect = {
                        if (isUnlocked) {
                            onSelectTower(if (isSelected) null else towerType)
                        }
                    },
                    onInfo = { tooltipTower = towerType }
                )
            }
        }

        // Tooltip popup
        tooltipTower?.let { tt ->
            TowerTooltip(
                towerType = tt,
                metaBonus = metaBonus,
                onDismiss = { tooltipTower = null }
            )
        }
    }
}

@Composable
private fun TowerHudButton(
    towerType: TowerType,
    isUnlocked: Boolean,
    isSelected: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onInfo: () -> Unit
) {
    val baseColor = towerHudColor[towerType] ?: Color.Gray
    val dimColor = baseColor.copy(alpha = 0.35f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> Color.White.copy(alpha = 0.15f)
                    else -> Color(0xFF1A1A2E)
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = when {
                    isSelected -> Color(0xFFFFD700)
                    isUnlocked -> baseColor.copy(alpha = 0.5f)
                    else -> Color(0xFF333355)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = isUnlocked) { onSelect() }
            .padding(4.dp)
    ) {
        // Mini tower canvas preview
        Box(modifier = Modifier.size(44.dp)) {
            if (isUnlocked) {
                Canvas(modifier = Modifier.size(44.dp)) {
                    val previewCell = size.width
                    drawTowers(
                        towers = listOf(Tower(id = 0, type = towerType, level = 1, gridRow = 0, gridCol = 0)),
                        cellSize = previewCell,
                        selectedTowerId = null
                    )
                }
                if (!canAfford) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                }
            } else {
                // Locked: dark background with lock icon
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFF111122), shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔒", fontSize = 18.sp)
                }
            }
        }

        // Tower name
        Text(
            text = towerType.nameZh,
            color = if (isUnlocked) Color.White else Color(0xFF555577),
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Cost or locked label
        if (isUnlocked) {
            Text(
                text = "${towerType.baseCost}g",
                color = if (canAfford) Color(0xFFFFD700) else Color(0xFF886600),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "未解鎖",
                color = Color(0xFF445566),
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Info button
        Text(
            text = "ⓘ",
            color = Color(0xFF7788AA),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onInfo() }
        )
    }
}

@Composable
private fun TowerTooltip(
    towerType: TowerType,
    metaBonus: MetaBonus,
    onDismiss: () -> Unit
) {
    val stats = towerType.statsForLevel(1, metaBonus)
    val isUnlocked = !towerType.isLockedByDefault || towerType in metaBonus.unlockedTowers

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(Color(0xFF1E1E3A), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF3344AA), RoundedCornerShape(10.dp))
            .clickable { onDismiss() }
            .padding(10.dp)
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isUnlocked) {
                        Spacer(Modifier.width(6.dp))
                        Text("🔒 場外升級解鎖", color = Color(0xFFFFD700), fontSize = 11.sp)
                    }
                }
                Text("✕", color = Color(0xFF7788AA), fontSize = 14.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = towerType.descZh,
                color = Color(0xFFAABBCC),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip("⚔ ${stats.damage}", Color(0xFFEF5350))
                StatChip("🎯 ${"%.1f".format(stats.range)}", Color(0xFF42A5F5))
                StatChip("⚡ ${stats.fireRateMs}ms", Color(0xFFFFEE58))
                StatChip("💰 ${towerType.baseCost}g", Color(0xFFFFD700))
            }
        }
    }
}

@Composable
private fun StatChip(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
    )
}
