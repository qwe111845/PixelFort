package com.pixelfort.towerdefense.feature.levelselect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelfort.towerdefense.feature.progress.domain.LevelProgress

private data class LevelInfo(
    val id: Int,
    val name: String,
    val subtitle: String,
    val waves: Int,
    val themeColor: Color
)

private val levels = listOf(
    LevelInfo(1, "翠綠草原", "初級難度 · 8×12 地圖", 5, Color(0xFF388E3C)),
    LevelInfo(2, "沙漠廢墟", "中級難度 · 9×14 地圖", 6, Color(0xFFE65100)),
    LevelInfo(3, "冰雪要塞", "高級難度 · 10×16 地圖", 7, Color(0xFF0D47A1))
)

@Composable
fun LevelSelectScreen(
    onStartLevel: (Int) -> Unit,
    onGoToUpgrades: () -> Unit,
    onBack: () -> Unit,
    viewModel: LevelSelectViewModel = hiltViewModel()
) {
    val progressList by viewModel.progressList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1A))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "◀",
                color = Color(0xFF7788AA),
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Text(
                text = "選擇關卡",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            // Right side spacer for symmetry
            Spacer(Modifier.size(36.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Level cards
        levels.forEachIndexed { index, levelInfo ->
            val progress = progressList.find { it.levelId == levelInfo.id }
            val isUnlocked = index == 0 ||
                    (progressList.any { it.levelId == levels[index - 1].id && it.completed })

            LevelCard(
                levelInfo = levelInfo,
                progress = progress,
                isUnlocked = isUnlocked,
                onClick = { if (isUnlocked) onStartLevel(levelInfo.id) }
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(8.dp))

        // Meta upgrade button
        Button(
            onClick = onGoToUpgrades,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("🔬  場外升級", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LevelCard(
    levelInfo: LevelInfo,
    progress: LevelProgress?,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val starsEarned = progress?.starsEarned ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isUnlocked) Color(0xFF161630) else Color(0xFF0D0D1A)
            )
            .border(
                width = 1.5.dp,
                color = if (isUnlocked) levelInfo.themeColor.copy(alpha = 0.6f) else Color(0xFF222244),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isUnlocked) {
                            Text("🔒 ", fontSize = 16.sp)
                        }
                        Text(
                            text = levelInfo.name,
                            color = if (isUnlocked) Color.White else Color(0xFF445566),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = levelInfo.subtitle,
                        color = if (isUnlocked) Color(0xFF8899AA) else Color(0xFF334455),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "波次：${levelInfo.waves}",
                        color = if (isUnlocked) Color(0xFF6677AA) else Color(0xFF334455),
                        fontSize = 12.sp
                    )
                }

                // Stars
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        repeat(3) { i ->
                            Text(
                                text = if (isUnlocked && i < starsEarned) "⭐" else "☆",
                                fontSize = 22.sp,
                                color = if (isUnlocked) Color.White else Color(0xFF333355)
                            )
                        }
                    }
                    if (isUnlocked && progress?.completed == true) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "✓ 已完成",
                            color = Color(0xFF4CAF50),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (isUnlocked) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .background(levelInfo.themeColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (progress?.completed == true) "再次挑戰" else "開始挑戰",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "通關前一關卡解鎖",
                    color = Color(0xFF334455),
                    fontSize = 11.sp
                )
            }
        }
    }
}
