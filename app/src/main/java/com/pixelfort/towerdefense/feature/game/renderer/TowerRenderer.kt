package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.pixelfort.towerdefense.core.util.SpriteAssetLoader
import com.pixelfort.towerdefense.engine.model.Tower
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.feature.game.renderer.PixelDraw.pixel

object TowerRenderer {

    // ── Palette constants ──────────────────────────────────────
    private val C_STONE    = Color(0xFF78909C)
    private val C_DARK     = Color(0xFF37474F)
    private val C_ARCHER   = Color(0xFF1565C0)
    private val C_ARCHER_L = Color(0xFF42A5F5)
    private val C_CANNON   = Color(0xFFB71C1C)
    private val C_CANNON_L = Color(0xFFEF9A9A)
    private val C_MAGIC    = Color(0xFF6A1B9A)
    private val C_MAGIC_L  = Color(0xFFCE93D8)
    private val C_SNIPER   = Color(0xFF1B5E20)
    private val C_SNIPER_L = Color(0xFFA5D6A7)
    private val C_FROST    = Color(0xFF0D47A1)
    private val C_FROST_L  = Color(0xFFBBDEFB)
    private val C_LIGHT    = Color(0xFFF9A825)
    private val C_LIGHT_L  = Color(0xFFFFF176)
    private val C_POISON   = Color(0xFF33691E)
    private val C_POISON_L = Color(0xFFCCFF90)
    private val C_BOMB     = Color(0xFF4E342E)
    private val C_BOMB_L   = Color(0xFFFF7043)
    private val C_SELECT   = Color(0xFFFFFF00)

    fun DrawScope.drawTowers(
        towers: List<Tower>,
        cellSize: Float,
        selectedTowerId: Int?,
        spriteLoader: SpriteAssetLoader? = null,
        elapsedMs: Long = 0L
    ) {
        for (tower in towers) {
            val cx = tower.gridCol * cellSize + cellSize / 2f
            val cy = tower.gridRow * cellSize + cellSize / 2f
            val sc = cellSize / 10f  // 1 pixel unit = sc dp

            // Range ring when selected
            if (tower.id == selectedTowerId) {
                drawCircle(
                    color = C_SELECT.copy(alpha = 0.20f),
                    radius = tower.stats.range * cellSize,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = C_SELECT,
                    radius = tower.stats.range * cellSize,
                    center = Offset(cx, cy),
                    style = Stroke(sc * 0.8f)
                )
            }

            // Convert facing angle from radians to degrees for Canvas rotation
            val rotationDegrees = Math.toDegrees(tower.facingAngle.toDouble()).toFloat()

            // Try PNG sprite first, fall back to procedural
            val sprite = spriteLoader?.getTowerSprite(tower.type, tower.level)
            if (sprite != null) {
                // Idle breathing: subtle scale oscillation
                val breathScale = 1f + 0.02f * kotlin.math.sin(elapsedMs * Math.PI / 1000.0).toFloat()

                // Attack pulse: brief scale spike on fire (check cooldown)
                val fireRateMs = tower.stats.fireRateMs.toLong()
                val cooldownPct = tower.cooldownRemainingMs.toFloat() / fireRateMs.coerceAtLeast(1)
                val justFired = cooldownPct > 0.9f
                val attackScale = if (justFired) 1.12f else 1f

                val finalScale = breathScale * attackScale
                val spriteSize = (cellSize * 0.85f * finalScale).toInt()
                rotate(degrees = rotationDegrees, pivot = Offset(cx, cy)) {
                    drawImage(
                        image = sprite,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(sprite.width, sprite.height),
                        dstOffset = IntOffset(
                            (cx - spriteSize / 2f).toInt(),
                            (cy - spriteSize / 2f).toInt()
                        ),
                        dstSize = IntSize(spriteSize, spriteSize)
                    )
                }
            } else {
                // Fallback: procedural pixel art with rotation
                rotate(degrees = rotationDegrees, pivot = Offset(cx, cy)) {
                    when (tower.type) {
                        TowerType.ARCHER    -> drawArcher(cx, cy, sc, tower.level)
                        TowerType.CANNON    -> drawCannon(cx, cy, sc, tower.level)
                        TowerType.MAGIC     -> drawMagic(cx, cy, sc, tower.level)
                        TowerType.SNIPER    -> drawSniper(cx, cy, sc, tower.level)
                        TowerType.FROST     -> drawFrost(cx, cy, sc, tower.level)
                        TowerType.LIGHTNING -> drawLightning(cx, cy, sc, tower.level)
                        TowerType.POISON    -> drawPoison(cx, cy, sc, tower.level)
                        TowerType.BOMB      -> drawBomb(cx, cy, sc, tower.level)
                    }
                }
            }

            // Level pips below (drawn without rotation so they stay readable)
            val pipColor = Color.White
            val pipSize = sc * 1.5f
            val totalW = tower.level * (pipSize + sc * 0.5f) - sc * 0.5f
            val startX = cx - totalW / 2f
            for (i in 0 until tower.level) {
                drawRect(
                    color = pipColor,
                    topLeft = Offset(startX + i * (pipSize + sc * 0.5f), cy + cellSize * 0.42f),
                    size = Size(pipSize, pipSize)
                )
            }
        }
    }

    /**
     * Draw a semi-transparent ghost tower at the given grid position,
     * optionally with a range circle. If [isValid] is false the ghost tints red.
     */
    fun DrawScope.drawGhostTower(
        towerType: TowerType,
        gridRow: Int,
        gridCol: Int,
        cellSize: Float,
        isValid: Boolean,
        spriteLoader: SpriteAssetLoader? = null
    ) {
        val cx = gridCol * cellSize + cellSize / 2f
        val cy = gridRow * cellSize + cellSize / 2f
        val sc = cellSize / 10f
        val stats = towerType.statsForLevel(1)
        val ghostAlpha = 0.4f
        val rangeColor = if (isValid) Color.White.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
        val rangeBorderColor = if (isValid) Color.White.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.5f)

        // Range circle
        drawCircle(color = rangeColor, radius = stats.range * cellSize, center = Offset(cx, cy))
        drawCircle(
            color = rangeBorderColor,
            radius = stats.range * cellSize,
            center = Offset(cx, cy),
            style = Stroke(sc * 0.6f)
        )

        // Draw the tower sprite/procedural art with reduced alpha
        val sprite = spriteLoader?.getTowerSprite(towerType, 1)
        if (sprite != null) {
            val spriteSize = (cellSize * 0.85f).toInt()
            withTransform({
                // No built-in alpha transform for drawImage, so we use a color filter approach
            }) {
                drawImage(
                    image = sprite,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(sprite.width, sprite.height),
                    dstOffset = IntOffset(
                        (cx - spriteSize / 2f).toInt(),
                        (cy - spriteSize / 2f).toInt()
                    ),
                    dstSize = IntSize(spriteSize, spriteSize),
                    alpha = ghostAlpha,
                    colorFilter = if (!isValid) androidx.compose.ui.graphics.ColorFilter.tint(
                        Color.Red.copy(alpha = 0.5f),
                        blendMode = androidx.compose.ui.graphics.BlendMode.SrcAtop
                    ) else null
                )
            }
        } else {
            // Procedural fallback: draw a filled semi-transparent circle as placeholder
            val towerColor = when (towerType) {
                TowerType.ARCHER -> C_ARCHER
                TowerType.CANNON -> C_CANNON
                TowerType.MAGIC -> C_MAGIC
                TowerType.SNIPER -> C_SNIPER
                TowerType.FROST -> C_FROST
                TowerType.LIGHTNING -> C_LIGHT
                TowerType.POISON -> C_POISON
                TowerType.BOMB -> C_BOMB
            }
            val ghostColor = if (isValid) towerColor.copy(alpha = ghostAlpha)
            else Color.Red.copy(alpha = ghostAlpha)
            drawCircle(color = ghostColor, radius = cellSize * 0.35f, center = Offset(cx, cy))
        }
    }

    // ── Archer: blue tower with arrow slit ────────────────────
    private fun DrawScope.drawArcher(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Base stones
        pixRow(ox, oy, 1, 6, 2, 2, C_STONE, sc)
        pixRow(ox, oy, 1, 4, 2, 2, C_STONE, sc)
        pixRow(ox, oy, 1, 2, 2, 2, C_STONE, sc)
        pixRow(ox, oy, 5, 6, 2, 2, C_STONE, sc)
        pixRow(ox, oy, 5, 4, 2, 2, C_STONE, sc)
        pixRow(ox, oy, 5, 2, 2, 2, C_STONE, sc)
        // Fill
        pixRow(ox, oy, 2, 6, 4, 2, C_ARCHER, sc)
        pixRow(ox, oy, 2, 4, 4, 2, C_ARCHER, sc)
        pixRow(ox, oy, 2, 2, 4, 2, C_ARCHER, sc)
        // Arrow slit (dark gap)
        pixRow(ox, oy, 3, 3, 2, 1, C_DARK, sc)
        // Battlements top
        pixRow(ox, oy, 1, 0, 2, 2, C_STONE, sc)
        pixRow(ox, oy, 5, 0, 2, 2, C_STONE, sc)
        if (lv >= 2) {
            pixRow(ox, oy, 3, 0, 2, 1, C_ARCHER_L, sc) // flag
        }
        if (lv >= 3) {
            pixRow(ox, oy, 1, 7, 6, 1, C_ARCHER_L, sc) // base
        }
    }

    // ── Cannon: squat red turret ───────────────────────────────
    private fun DrawScope.drawCannon(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        pixRow(ox, oy, 1, 5, 6, 3, C_STONE, sc)
        pixRow(ox, oy, 2, 5, 4, 3, C_CANNON, sc)
        pixRow(ox, oy, 2, 3, 4, 2, C_CANNON, sc)
        // Barrel
        pixRow(ox, oy, 3, 0, 2, 3, C_DARK, sc)
        pixRow(ox, oy, 0, 5, 1, 3, C_DARK, sc)
        pixRow(ox, oy, 7, 5, 1, 3, C_DARK, sc)
        // Muzzle flash slot
        pixRow(ox, oy, 3, 1, 2, 1, C_CANNON_L, sc)
        if (lv >= 2) pixRow(ox, oy, 2, 2, 4, 1, C_CANNON_L, sc)
        if (lv >= 3) pixRow(ox, oy, 0, 7, 8, 1, C_CANNON_L, sc)
    }

    // ── Magic: purple crystal spire ────────────────────────────
    private fun DrawScope.drawMagic(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Base
        pixRow(ox, oy, 1, 6, 6, 2, C_STONE, sc)
        // Shaft
        pixRow(ox, oy, 2, 2, 4, 4, C_MAGIC, sc)
        pixRow(ox, oy, 3, 2, 2, 4, C_MAGIC_L, sc)
        // Crystal top
        pixRow(ox, oy, 3, 0, 2, 2, C_MAGIC_L, sc)
        pixRow(ox, oy, 3, 1, 2, 1, Color.White, sc)
        // Orb glow
        if (lv >= 2) pixRow(ox, oy, 2, 1, 4, 1, C_MAGIC_L, sc)
        if (lv >= 3) {
            pixRow(ox, oy, 1, 3, 1, 2, C_MAGIC_L, sc)
            pixRow(ox, oy, 6, 3, 1, 2, C_MAGIC_L, sc)
        }
    }

    // ── Sniper: tall green watchtower ─────────────────────────
    private fun DrawScope.drawSniper(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Thin tower
        pixRow(ox, oy, 3, 1, 2, 7, C_STONE, sc)
        pixRow(ox, oy, 3, 1, 2, 7, C_SNIPER, sc)
        // Observation platform
        pixRow(ox, oy, 1, 1, 6, 2, C_STONE, sc)
        pixRow(ox, oy, 2, 1, 4, 2, C_SNIPER, sc)
        // Rifle barrel
        pixRow(ox, oy, 4, 0, 1, 1, C_DARK, sc)
        // Scope glint
        pixRow(ox, oy, 2, 2, 1, 1, C_SNIPER_L, sc)
        if (lv >= 2) pixRow(ox, oy, 5, 2, 1, 1, C_SNIPER_L, sc)
        if (lv >= 3) pixRow(ox, oy, 3, 7, 2, 1, C_SNIPER_L, sc)
    }

    // ── Frost: icy blue tower ─────────────────────────────────
    private fun DrawScope.drawFrost(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        pixRow(ox, oy, 2, 5, 4, 3, C_STONE, sc)
        pixRow(ox, oy, 2, 2, 4, 3, C_FROST, sc)
        // Ice crystals top
        pixRow(ox, oy, 2, 0, 1, 3, C_FROST_L, sc)
        pixRow(ox, oy, 4, 0, 1, 3, C_FROST_L, sc)
        pixRow(ox, oy, 6, 0, 1, 3, C_FROST_L, sc)
        pixRow(ox, oy, 3, 1, 1, 1, Color.White, sc)
        pixRow(ox, oy, 5, 1, 1, 1, Color.White, sc)
        if (lv >= 2) pixRow(ox, oy, 1, 3, 6, 1, C_FROST_L, sc)
        if (lv >= 3) pixRow(ox, oy, 0, 7, 8, 1, C_FROST_L, sc)
    }

    // ── Lightning: yellow tesla coil ──────────────────────────
    private fun DrawScope.drawLightning(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        pixRow(ox, oy, 3, 4, 2, 4, C_STONE, sc)
        pixRow(ox, oy, 3, 4, 2, 4, C_DARK, sc)
        // Coil rings
        pixRow(ox, oy, 2, 4, 4, 1, C_LIGHT_L, sc)
        pixRow(ox, oy, 2, 6, 4, 1, C_LIGHT_L, sc)
        // Top sphere
        pixRow(ox, oy, 2, 1, 4, 3, C_LIGHT, sc)
        pixRow(ox, oy, 3, 1, 2, 3, C_LIGHT_L, sc)
        // Bolt
        pixRow(ox, oy, 4, 0, 1, 1, Color.White, sc)
        if (lv >= 2) {
            pixRow(ox, oy, 1, 2, 1, 1, C_LIGHT_L, sc)
            pixRow(ox, oy, 6, 2, 1, 1, C_LIGHT_L, sc)
        }
        if (lv >= 3) pixRow(ox, oy, 0, 7, 8, 1, C_LIGHT, sc)
    }

    // ── Poison: green flask tower ─────────────────────────────
    private fun DrawScope.drawPoison(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        pixRow(ox, oy, 2, 5, 4, 3, C_STONE, sc)
        // Flask body
        pixRow(ox, oy, 1, 3, 6, 2, C_POISON, sc)
        pixRow(ox, oy, 2, 3, 4, 2, C_POISON_L, sc)
        // Neck
        pixRow(ox, oy, 3, 1, 2, 2, C_DARK, sc)
        // Stopper
        pixRow(ox, oy, 3, 0, 2, 1, C_POISON, sc)
        // Bubbles
        pixRow(ox, oy, 2, 4, 1, 1, C_POISON_L, sc)
        pixRow(ox, oy, 5, 3, 1, 1, Color.White.copy(alpha = 0.7f), sc)
        if (lv >= 2) pixRow(ox, oy, 1, 5, 1, 2, C_POISON_L, sc)
        if (lv >= 3) pixRow(ox, oy, 0, 7, 8, 1, C_POISON_L, sc)
    }

    // ── Bomb: heavy dark turret ────────────────────────────────
    private fun DrawScope.drawBomb(cx: Float, cy: Float, sc: Float, lv: Int) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Wide base
        pixRow(ox, oy, 0, 6, 8, 2, C_STONE, sc)
        pixRow(ox, oy, 1, 4, 6, 2, C_BOMB, sc)
        pixRow(ox, oy, 2, 3, 4, 1, C_BOMB, sc)
        // Bomb sphere
        pixRow(ox, oy, 1, 1, 6, 3, C_DARK, sc)
        pixRow(ox, oy, 2, 1, 4, 3, C_BOMB, sc)
        // Fuse
        pixRow(ox, oy, 4, 0, 1, 1, C_BOMB_L, sc)
        // Rivets
        pixRow(ox, oy, 1, 2, 1, 1, C_BOMB_L, sc)
        pixRow(ox, oy, 6, 2, 1, 1, C_BOMB_L, sc)
        if (lv >= 2) pixRow(ox, oy, 3, 2, 2, 1, C_BOMB_L, sc)
        if (lv >= 3) pixRow(ox, oy, 0, 7, 8, 1, C_BOMB_L, sc)
    }

    // Helper: draw a filled rectangle in pixel-grid coords
    private fun DrawScope.pixRow(
        ox: Float, oy: Float,
        col: Int, row: Int, w: Int, h: Int,
        color: Color, sc: Float
    ) {
        drawRect(color, Offset(ox + col * sc, oy + row * sc), Size(w * sc, h * sc))
    }
}
