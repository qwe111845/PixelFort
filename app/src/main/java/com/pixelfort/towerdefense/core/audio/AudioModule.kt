package com.pixelfort.towerdefense.core.audio

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for audio dependencies.
 * SoundManager, MusicManager, and AudioSettings are @Singleton @Inject classes,
 * so Hilt provides them automatically. This module exists for discoverability.
 */
@Module
@InstallIn(SingletonComponent::class)
object AudioModule
