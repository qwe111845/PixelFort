package com.pixelfort.towerdefense.feature.settings

import androidx.lifecycle.ViewModel
import com.pixelfort.towerdefense.core.datastore.AudioSettings
import com.pixelfort.towerdefense.core.datastore.AudioSettingsData
import com.pixelfort.towerdefense.core.datastore.GameplaySettingsData
import com.pixelfort.towerdefense.core.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val audioSettings: AudioSettings,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val audioState: Flow<AudioSettingsData> = audioSettings.settingsFlow
    val gameplayState: Flow<GameplaySettingsData> = settingsDataStore.settingsFlow

    suspend fun setMasterVolume(volume: Float) = audioSettings.setMasterVolume(volume)
    suspend fun setSfxVolume(volume: Float) = audioSettings.setSfxVolume(volume)
    suspend fun setMusicVolume(volume: Float) = audioSettings.setMusicVolume(volume)
    suspend fun setMuted(muted: Boolean) = audioSettings.setMuted(muted)

    suspend fun setScreenShakeEnabled(enabled: Boolean) = settingsDataStore.setScreenShakeEnabled(enabled)
    suspend fun setDamageNumbersEnabled(enabled: Boolean) = settingsDataStore.setDamageNumbersEnabled(enabled)
    suspend fun setShowFpsCounter(enabled: Boolean) = settingsDataStore.setShowFpsCounter(enabled)

    suspend fun setRandomEventsEnabled(enabled: Boolean) = settingsDataStore.setRandomEventsEnabled(enabled)

    suspend fun resetTutorial() = settingsDataStore.setTutorialCompleted(false)
}
