package com.pixelfort.towerdefense.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.audioDataStore: DataStore<Preferences> by preferencesDataStore(name = "audio_settings")

data class AudioSettingsData(
    val masterVolume: Float = 1f,
    val sfxVolume: Float = 1f,
    val musicVolume: Float = 1f,
    val isMuted: Boolean = false
)

@Singleton
class AudioSettings @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterVolumeKey = floatPreferencesKey("master_volume")
    private val sfxVolumeKey = floatPreferencesKey("sfx_volume")
    private val musicVolumeKey = floatPreferencesKey("music_volume")
    private val isMutedKey = booleanPreferencesKey("is_muted")

    val settingsFlow: Flow<AudioSettingsData> = context.audioDataStore.data.map { prefs ->
        AudioSettingsData(
            masterVolume = prefs[masterVolumeKey] ?: 1f,
            sfxVolume = prefs[sfxVolumeKey] ?: 1f,
            musicVolume = prefs[musicVolumeKey] ?: 1f,
            isMuted = prefs[isMutedKey] ?: false
        )
    }

    suspend fun setMasterVolume(volume: Float) {
        context.audioDataStore.edit { it[masterVolumeKey] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setSfxVolume(volume: Float) {
        context.audioDataStore.edit { it[sfxVolumeKey] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setMusicVolume(volume: Float) {
        context.audioDataStore.edit { it[musicVolumeKey] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setMuted(muted: Boolean) {
        context.audioDataStore.edit { it[isMutedKey] = muted }
    }
}
