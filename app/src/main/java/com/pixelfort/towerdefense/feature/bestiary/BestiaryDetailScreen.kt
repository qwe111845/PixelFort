package com.pixelfort.towerdefense.feature.bestiary

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelfort.towerdefense.engine.model.BestiaryEntry
import com.pixelfort.towerdefense.engine.model.EnemyType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestiaryDetailScreen(
    enemyTypeName: String,
    onBack: () -> Unit,
    viewModel: BestiaryViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val enemyType = try { EnemyType.valueOf(enemyTypeName) } catch (_: Exception) { null }
    val entry = entries.find { it.enemyType == enemyType }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = entry?.enemyType?.nameZh ?: "???",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
        },
        containerColor = Color(0xFF1A1A2E)
    ) { paddingValues ->
        if (entry == null || !entry.isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Entry not found", color = Color.Gray, fontSize = 16.sp)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large sprite placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = enemyColor(entry.enemyType),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.enemyType.nameZh.first().toString(),
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name (Chinese + English)
            Text(
                text = entry.enemyType.nameZh,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = entry.enemyType.name,
                color = Color(0xFFAAAAAA),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats section
            SectionLabel("Stats")
            StatRow("HP", entry.enemyType.baseHp.toString())
            StatRow("Speed", entry.enemyType.baseSpeed.toString())
            StatRow("Gold", "${entry.enemyType.reward}g")

            Spacer(modifier = Modifier.height(16.dp))

            // Weakness section
            SectionLabel("Weakness")
            Text(
                text = "Weak to: ${entry.weakTo.nameZh} (${entry.weakTo.name})",
                color = Color(0xFFFF9800),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            Text(
                text = entry.weaknessText,
                color = Color(0xFFCCCCCC),
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lore section
            SectionLabel("Lore")
            Text(
                text = entry.lore,
                color = Color(0xFFBBBBBB),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Battle record
            SectionLabel("Battle Record")
            StatRow("Defeated", "${entry.defeatedCount} times")
            StatRow("Max Hit Damage", entry.maxHitDamage.toString())
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFFFD700),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    )
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF999999), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
