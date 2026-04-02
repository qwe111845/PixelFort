package com.pixelfort.towerdefense.core.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import com.pixelfort.towerdefense.core.datastore.AudioSettings
import com.pixelfort.towerdefense.core.datastore.AudioSettingsData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioSettings: AudioSettings
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var settings = AudioSettingsData()

    private var currentPlayer: MediaPlayer? = null
    private var currentTrack: MusicTrack? = null
    private var crossfadeJob: Job? = null

    private companion object {
        const val CROSSFADE_DURATION_MS = 500L
        const val CROSSFADE_STEPS = 10
    }

    init {
        scope.launch {
            audioSettings.settingsFlow.collectLatest { newSettings ->
                settings = newSettings
                updateVolume()
            }
        }
    }

    fun play(track: MusicTrack) {
        if (track == currentTrack && currentPlayer?.isPlaying == true) return
        crossfadeTo(track)
    }

    fun stop() {
        crossfadeJob?.cancel()
        currentPlayer?.let { player ->
            try {
                player.stop()
                player.release()
            } catch (_: Exception) { }
        }
        currentPlayer = null
        currentTrack = null
    }

    private fun crossfadeTo(track: MusicTrack) {
        crossfadeJob?.cancel()
        crossfadeJob = scope.launch {
            val oldPlayer = currentPlayer
            val newPlayer = createPlayer(track)

            if (newPlayer == null) {
                // Asset not found — just stop old
                oldPlayer?.let {
                    try { it.stop(); it.release() } catch (_: Exception) { }
                }
                currentPlayer = null
                currentTrack = null
                return@launch
            }

            val targetVolume = effectiveVolume()
            val stepDelayMs = CROSSFADE_DURATION_MS / CROSSFADE_STEPS

            // Start new player at 0 volume
            newPlayer.setVolume(0f, 0f)
            try {
                newPlayer.start()
            } catch (_: Exception) {
                newPlayer.release()
                return@launch
            }

            // Crossfade
            for (i in 1..CROSSFADE_STEPS) {
                val fraction = i.toFloat() / CROSSFADE_STEPS
                val fadeInVol = targetVolume * fraction
                val fadeOutVol = targetVolume * (1f - fraction)
                newPlayer.setVolume(fadeInVol, fadeInVol)
                oldPlayer?.let {
                    try { it.setVolume(fadeOutVol, fadeOutVol) } catch (_: Exception) { }
                }
                delay(stepDelayMs)
            }

            // Clean up old player
            oldPlayer?.let {
                try { it.stop(); it.release() } catch (_: Exception) { }
            }

            currentPlayer = newPlayer
            currentTrack = track
        }
    }

    private fun createPlayer(track: MusicTrack): MediaPlayer? {
        return try {
            val afd: AssetFileDescriptor = context.assets.openFd(track.assetPath)
            MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                isLooping = true
                prepare()
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun updateVolume() {
        val vol = effectiveVolume()
        currentPlayer?.let {
            try { it.setVolume(vol, vol) } catch (_: Exception) { }
        }
    }

    private fun effectiveVolume(): Float {
        if (settings.isMuted) return 0f
        return settings.masterVolume * settings.musicVolume
    }

    fun release() {
        crossfadeJob?.cancel()
        currentPlayer?.let {
            try { it.stop(); it.release() } catch (_: Exception) { }
        }
        currentPlayer = null
        currentTrack = null
    }
}
