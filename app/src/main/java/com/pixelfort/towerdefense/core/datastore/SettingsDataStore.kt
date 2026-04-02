package com.pixelfort.towerdefense.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "gameplay_settings")

data class GameplaySettingsData(
    val screenShakeEnabled: Boolean = true,
    val damageNumbersEnabled: Boolean = true,
<<<<<<< HEAD
    val showFpsCounter: Boolean = false,
    val tutorialCompleted: Boolean = false
=======
    val showFpsCounter: Boolean = false
>>>>>>> origin/master
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val screenShakeKey = booleanPreferencesKey("screen_shake_enabled")
    private val damageNumbersKey = booleanPreferencesKey("damage_numbers_enabled")
    private val showFpsKey = booleanPreferencesKey("show_fps_counter")
<<<<<<< HEAD
    private val tutorialCompletedKey = booleanPreferencesKey("tutorial_completed")
=======
>>>>>>> origin/master

    val settingsFlow: Flow<GameplaySettingsData> = context.settingsDataStore.data.map { prefs ->
        GameplaySettingsData(
            screenShakeEnabled = prefs[screenShakeKey] ?: true,
            damageNumbersEnabled = prefs[damageNumbersKey] ?: true,
<<<<<<< HEAD
            showFpsCounter = prefs[showFpsKey] ?: false,
            tutorialCompleted = prefs[tutorialCompletedKey] ?: false
=======
            showFpsCounter = prefs[showFpsKey] ?: false
>>>>>>> origin/master
        )
    }

    suspend fun setScreenShakeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[screenShakeKey] = enabled }
    }

    suspend fun setDamageNumbersEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[damageNumbersKey] = enabled }
    }

    suspend fun setShowFpsCounter(enabled: Boolean) {
        context.settingsDataStore.edit { it[showFpsKey] = enabled }
    }
<<<<<<< HEAD

    suspend fun setTutorialCompleted(completed: Boolean) {
        context.settingsDataStore.edit { it[tutorialCompletedKey] = completed }
    }
=======
>>>>>>> origin/master
}
