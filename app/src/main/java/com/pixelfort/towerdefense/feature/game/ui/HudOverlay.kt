package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import com.pixelfort.towerdefense.engine.model.EnemyType
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
    wavePreview: List<Pair<EnemyType, Int>> = emptyList(),
    onSelectTower: (TowerType?) -> Unit,
    onStartWave: () -> Unit,
    onPause: () -> Unit,
    onShowTooltip: (TowerType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0D1A))
    ) {
        // Wave preview
        if (gameState == GameState.WaitingForWave && wavePreview.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .background(Color(0xFF161630), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "下一波：",
                    color = Color(0xFF8899AA),
                    fontSize = 12.sp
                )
                Spacer(Modifier.width(4.dp))
                wavePreview.forEachIndexed { index, (enemyType, count) ->
                    if (index > 0) {
                        Text("  ", fontSize = 12.sp)
                    }
                    Text(
                        text = "${enemyType.nameZh} x$count",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

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
                    onLongPress = { onShowTooltip(towerType) },
                    onInfoTap = { onShowTooltip(towerType) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TowerHudButton(
    towerType: TowerType,
    isUnlocked: Boolean,
    isSelected: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onLongPress: () -> Unit,
    onInfoTap: () -> Unit
) {
    val baseColor = towerHudColor[towerType] ?: Color.Gray

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
            .combinedClickable(
                enabled = true,
                onClick = { onSelect() },
                onLongClick = { onLongPress() }
            )
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

        // Info button (tap shows tooltip without selecting tower)
        Text(
            text = "ⓘ",
            color = Color(0xFF7788AA),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onInfoTap() }
        )
    }
}
