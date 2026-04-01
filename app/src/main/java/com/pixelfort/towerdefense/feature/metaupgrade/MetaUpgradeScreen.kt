package com.pixelfort.towerdefense.feature.metaupgrade

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
import com.pixelfort.towerdefense.engine.model.MetaUpgrade
import com.pixelfort.towerdefense.engine.model.MetaUpgradeState
import com.pixelfort.towerdefense.engine.model.MetaUpgrades
import com.pixelfort.towerdefense.engine.model.UpgradeCategory

private val categoryLabel = mapOf(
    UpgradeCategory.COMBAT  to "⚔ 戰鬥強化",
    UpgradeCategory.DEFENSE to "🛡 防禦強化",
    UpgradeCategory.ECONOMY to "💰 經濟增益",
    UpgradeCategory.UNLOCK  to "🔓 解鎖進階塔"
)

private val categoryColor = mapOf(
    UpgradeCategory.COMBAT  to Color(0xFFEF5350),
    UpgradeCategory.DEFENSE to Color(0xFF42A5F5),
    UpgradeCategory.ECONOMY to Color(0xFFFFD700),
    UpgradeCategory.UNLOCK  to Color(0xFFCE93D8)
)

@Composable
fun MetaUpgradeScreen(
    onBack: () -> Unit,
    viewModel: MetaUpgradeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                text = "場外升級",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            // Research points badge
            Box(
                modifier = Modifier
                    .background(Color(0xFF1E1E3A), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF3355AA), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔬 ${state.researchPoints} RP",
                    color = Color(0xFF80DEEA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "完成關卡後獲得研究點，用於永久強化你的塔防能力",
            color = Color(0xFF5566AA),
            fontSize = 12.sp
        )

        Spacer(Modifier.height(16.dp))

        // Group upgrades by category
        UpgradeCategory.values().forEach { category ->
            val upgradesInCategory = MetaUpgrades.all.filter { it.category == category }
            val color = categoryColor[category] ?: Color.White

            // Category header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(3.dp, 20.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = categoryLabel[category] ?: category.name,
                    color = color,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            upgradesInCategory.forEach { upgrade ->
                UpgradeCard(
                    upgrade = upgrade,
                    state = state,
                    categoryColor = color,
                    onPurchase = { viewModel.purchase(upgrade.id) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun UpgradeCard(
    upgrade: MetaUpgrade,
    state: MetaUpgradeState,
    categoryColor: Color,
    onPurchase: () -> Unit
) {
    val currentLevel = state.levelOf(upgrade.id)
    val isMaxed = currentLevel >= upgrade.maxLevel
    val canAfford = state.canAfford(upgrade)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131325))
            .border(
                width = 1.dp,
                color = if (isMaxed) categoryColor.copy(alpha = 0.6f)
                else if (canAfford) categoryColor.copy(alpha = 0.3f)
                else Color(0xFF1E1E3A),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = upgrade.nameZh,
                        color = if (isMaxed) categoryColor else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isMaxed) {
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = "MAX",
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = upgrade.descriptionZh,
                    color = Color(0xFF778899),
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(6.dp))
                // Level stars
                LevelDots(
                    current = currentLevel,
                    max = upgrade.maxLevel,
                    color = categoryColor
                )
            }

            Spacer(Modifier.size(8.dp))

            // Purchase button or maxed indicator
            if (isMaxed) {
                Box(
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text("✓ 已滿", color = categoryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = categoryColor.copy(alpha = 0.8f),
                        disabledContainerColor = Color(0xFF1E2030)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 10.dp, vertical = 6.dp
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("升級", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${upgrade.costPerLevel} RP",
                            fontSize = 10.sp,
                            color = if (canAfford) Color.White.copy(alpha = 0.8f) else Color(0xFF445566)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelDots(current: Int, max: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(max) { i ->
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 6.dp)
                    .background(
                        if (i < current) color else color.copy(alpha = 0.2f),
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
