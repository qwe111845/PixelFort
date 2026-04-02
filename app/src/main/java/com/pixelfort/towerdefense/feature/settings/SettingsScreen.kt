package com.pixelfort.towerdefense.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelfort.towerdefense.core.datastore.AudioSettingsData
import com.pixelfort.towerdefense.core.datastore.GameplaySettingsData
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val audioState by viewModel.audioState.collectAsStateWithLifecycle(initialValue = AudioSettingsData())
    val gameplayState by viewModel.gameplayState.collectAsStateWithLifecycle(initialValue = GameplaySettingsData())
    val scope = rememberCoroutineScope()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1A))
            .padding(top = statusBarPadding, bottom = navBarPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            color = Color(0xFFFFD700),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // ── Audio ──
            SectionHeader("Audio")
            Spacer(Modifier.height(12.dp))

            SettingsToggle(
                label = "Mute All",
                checked = audioState.isMuted,
                onCheckedChange = { scope.launch { viewModel.setMuted(it) } },
                checkedThumbColor = Color(0xFFEF5350),
                checkedTrackColor = Color(0xFF8B0000)
            )
            Spacer(Modifier.height(12.dp))

            VolumeSlider(
                label = "Master Volume",
                value = audioState.masterVolume,
                onValueChange = { scope.launch { viewModel.setMasterVolume(it) } }
            )
            Spacer(Modifier.height(8.dp))

            VolumeSlider(
                label = "SFX Volume",
                value = audioState.sfxVolume,
                onValueChange = { scope.launch { viewModel.setSfxVolume(it) } }
            )
            Spacer(Modifier.height(8.dp))

            VolumeSlider(
                label = "Music Volume",
                value = audioState.musicVolume,
                onValueChange = { scope.launch { viewModel.setMusicVolume(it) } }
            )

            SectionDivider()

            // ── Gameplay ──
            SectionHeader("Gameplay")
            Spacer(Modifier.height(12.dp))

            SettingsToggle(
                label = "Screen Shake",
                checked = gameplayState.screenShakeEnabled,
                onCheckedChange = { scope.launch { viewModel.setScreenShakeEnabled(it) } }
            )
            Spacer(Modifier.height(8.dp))

            SettingsToggle(
                label = "Damage Numbers",
                checked = gameplayState.damageNumbersEnabled,
                onCheckedChange = { scope.launch { viewModel.setDamageNumbersEnabled(it) } }
            )
            Spacer(Modifier.height(8.dp))

            SettingsToggle(
                label = "Show FPS Counter",
                checked = gameplayState.showFpsCounter,
                onCheckedChange = { scope.launch { viewModel.setShowFpsCounter(it) } }
            )
            Spacer(Modifier.height(8.dp))

            SettingsToggle(
                label = "Random Wave Events",
                checked = gameplayState.randomEventsEnabled,
                onCheckedChange = { scope.launch { viewModel.setRandomEventsEnabled(it) } }
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { scope.launch { viewModel.resetTutorial() } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3050)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (gameplayState.tutorialCompleted) "Replay Tutorial" else "Tutorial will show on Level 1",
                    fontSize = 14.sp
                )
            }

            SectionDivider()

            // ── Display ──
            SectionHeader("Display")
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Language", color = Color(0xFFBBCCDD), fontSize = 16.sp)
                Text("English", color = Color(0xFF888888), fontSize = 14.sp)
            }

            SectionDivider()

            // ── About ──
            SectionHeader("About")
            Spacer(Modifier.height(12.dp))

            AboutRow(label = "Version", value = "1.0.0")
            Spacer(Modifier.height(8.dp))
            AboutRow(label = "Credits", value = "PixelFort Team")
            Spacer(Modifier.height(8.dp))
            AboutRow(label = "Licenses", value = "Apache 2.0, MIT")

            Spacer(Modifier.height(24.dp))
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back", fontSize = 16.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SectionDivider() {
    Spacer(Modifier.height(20.dp))
    HorizontalDivider(color = Color(0xFF2A2A40), thickness = 1.dp)
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedThumbColor: Color = Color(0xFFFFD700),
    checkedTrackColor: Color = Color(0xFF665500)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFBBCCDD), fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = checkedThumbColor,
                checkedTrackColor = checkedTrackColor,
                uncheckedThumbColor = Color(0xFF888888),
                uncheckedTrackColor = Color(0xFF334466)
            )
        )
    }
}

@Composable
private fun VolumeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color(0xFFBBCCDD), fontSize = 14.sp)
            Text("${(value * 100).toInt()}%", color = Color(0xFFFFD700), fontSize = 14.sp)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFFD700),
                activeTrackColor = Color(0xFFFFD700),
                inactiveTrackColor = Color(0xFF334466)
            )
        )
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFBBCCDD), fontSize = 16.sp)
        Text(value, color = Color(0xFF888888), fontSize = 14.sp)
    }
}
