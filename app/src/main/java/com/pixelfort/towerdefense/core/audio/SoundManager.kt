package com.pixelfort.towerdefense.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.pixelfort.towerdefense.core.datastore.AudioSettings
import com.pixelfort.towerdefense.core.datastore.AudioSettingsData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioSettings: AudioSettings
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var settings = AudioSettingsData()

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds = mutableMapOf<SoundEffect, Int>()
    private var loaded = false

    init {
        scope.launch {
            audioSettings.settingsFlow.collectLatest { settings = it }
        }
    }

    fun loadAll() {
        if (loaded) return
        SoundEffect.entries.forEach { sfx ->
            soundIds[sfx] = soundPool.load(context, sfx.resId, sfx.priority)
        }
        loaded = true
    }

    fun play(sfx: SoundEffect) {
        if (settings.isMuted) return
        val id = soundIds[sfx] ?: return
        val volume = settings.masterVolume * settings.sfxVolume
        if (volume <= 0f) return
        soundPool.play(id, volume, volume, sfx.priority, 0, 1f)
    }

    fun release() {
        soundPool.release()
        soundIds.clear()
        loaded = false
    }
}
