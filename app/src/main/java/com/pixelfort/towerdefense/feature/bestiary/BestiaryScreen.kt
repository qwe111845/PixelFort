package com.pixelfort.towerdefense.feature.bestiary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelfort.towerdefense.engine.model.BestiaryEntry
import com.pixelfort.towerdefense.engine.model.EnemyType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestiaryScreen(
    onBack: () -> Unit,
    onEntryClick: (EnemyType) -> Unit,
    viewModel: BestiaryViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bestiary", color = Color.White, fontWeight = FontWeight.Bold) },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entries, key = { it.enemyType.name }) { entry ->
                BestiaryCard(
                    entry = entry,
                    onClick = {
                        if (entry.isUnlocked) onEntryClick(entry.enemyType)
                    }
                )
            }
        }
    }
}

@Composable
private fun BestiaryCard(
    entry: BestiaryEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = entry.isUnlocked, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isUnlocked) Color(0xFF2A2A4E) else Color(0xFF1E1E38)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Sprite placeholder: colored circle for unlocked, dark silhouette for locked
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (entry.isUnlocked) enemyColor(entry.enemyType) else Color(0xFF333355),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (entry.isUnlocked) entry.enemyType.nameZh.first().toString() else "?",
                    color = if (entry.isUnlocked) Color.White else Color(0xFF555577),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name
            Text(
                text = if (entry.isUnlocked) entry.enemyType.nameZh else "???",
                color = if (entry.isUnlocked) Color.White else Color(0xFF555577),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            // English name
            Text(
                text = if (entry.isUnlocked) entry.enemyType.name else "???",
                color = if (entry.isUnlocked) Color(0xFFAAAAAA) else Color(0xFF444466),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Defeated count
            if (entry.isUnlocked) {
                Text(
                    text = "Defeated: ${entry.defeatedCount}",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

internal fun enemyColor(type: EnemyType): Color = when (type) {
    EnemyType.GOBLIN -> Color(0xFF4CAF50)
    EnemyType.ORC -> Color(0xFF795548)
    EnemyType.DRAGON -> Color(0xFFFF5722)
    EnemyType.TROLL -> Color(0xFF607D8B)
    EnemyType.SPECTER -> Color(0xFF9C27B0)
    EnemyType.BOSS_DRAGON -> Color(0xFFD32F2F)
}
