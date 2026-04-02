package com.pixelfort.towerdefense.core.util

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.pixelfort.towerdefense.engine.model.EnemyType
import com.pixelfort.towerdefense.engine.model.TowerType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads and caches PNG sprites from assets/sprites/.
 * Returns null (with warning log) on missing files — callers should fall back to procedural drawing.
 */
@Singleton
class SpriteAssetLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = mutableMapOf<String, ImageBitmap>()
    private var loaded = false

    fun loadAll() {
        if (loaded) return
        loaded = true

        // Tower sprites: 8 types × 3 levels
        for (type in TowerType.entries) {
            for (level in 1..3) {
                val key = towerKey(type, level)
                loadSprite("sprites/towers/tower_${type.name.lowercase()}_lv$level.png", key)
            }
        }

        // Enemy sprites: each type × 2 walk frames
        for (type in EnemyType.entries) {
            for (frame in 1..2) {
                val key = enemyKey(type, frame)
                loadSprite("sprites/enemies/enemy_${type.name.lowercase()}_$frame.png", key)
            }
        }

        // Boss enrage sprite
        loadSprite("sprites/enemies/enemy_boss_dragon_enrage.png", BOSS_ENRAGE_KEY)

        // Extra assets
        loadSprite("sprites/extras/menu_background.png", "extra_menu_background")
        loadSprite("sprites/extras/result_victory.png", "extra_result_victory")
        loadSprite("sprites/extras/result_defeat.png", "extra_result_defeat")
        loadSprite("sprites/extras/app_icon.png", "extra_app_icon")

        Log.i(TAG, "Loaded ${cache.size} sprites")
    }

    fun getTowerSprite(type: TowerType, level: Int): ImageBitmap? =
        cache[towerKey(type, level)]

    fun getEnemySprite(type: EnemyType, frame: Int): ImageBitmap? =
        cache[enemyKey(type, frame)]

    fun getBossEnrageSprite(): ImageBitmap? =
        cache[BOSS_ENRAGE_KEY]

    fun getExtraAsset(name: String): ImageBitmap? =
        cache["extra_$name"]

    fun isLoaded(): Boolean = loaded

    private fun loadSprite(assetPath: String, cacheKey: String) {
        try {
            context.assets.open(assetPath).use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    cache[cacheKey] = bitmap.asImageBitmap()
                } else {
                    Log.w(TAG, "Failed to decode: $assetPath")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sprite not found: $assetPath (${e.message})")
        }
    }

    private fun towerKey(type: TowerType, level: Int) = "tower_${type.name}_$level"
    private fun enemyKey(type: EnemyType, frame: Int) = "enemy_${type.name}_$frame"

    companion object {
        private const val TAG = "SpriteAssetLoader"
        private const val BOSS_ENRAGE_KEY = "boss_enrage"
    }
}
