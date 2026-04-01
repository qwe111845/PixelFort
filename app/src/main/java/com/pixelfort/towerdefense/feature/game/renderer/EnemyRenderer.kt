package com.pixelfort.towerdefense.feature.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.pixelfort.towerdefense.engine.model.Enemy
import com.pixelfort.towerdefense.engine.model.EnemyType

object EnemyRenderer {

    fun DrawScope.drawEnemies(enemies: List<Enemy>, cellSize: Float) {
        for (enemy in enemies) {
            val sc = cellSize / 10f * enemy.type.size
            val cx = enemy.pixelX
            val cy = enemy.pixelY

            when (enemy.type) {
                EnemyType.GOBLIN  -> drawGoblin(cx, cy, sc)
                EnemyType.ORC     -> drawOrc(cx, cy, sc)
                EnemyType.DRAGON  -> drawDragon(cx, cy, sc)
                EnemyType.TROLL   -> drawTroll(cx, cy, sc)
                EnemyType.SPECTER -> drawSpecter(cx, cy, sc)
            }

            // Slow frost tint overlay
            if (enemy.isSlowed) {
                drawCircle(Color(0x4480DEEA), sc * 3.5f, Offset(cx, cy))
            }
            // Poison bubble overlay
            if (enemy.isPoisoned) {
                drawCircle(Color(0x449CCC65), sc * 3f, Offset(cx, cy))
            }

            // HP bar
            val barW = sc * 7f
            val barH = sc * 1.2f
            val barX = cx - barW / 2f
            val barY = cy - sc * 5.5f
            drawRect(Color(0xFF222222), Offset(barX, barY), Size(barW, barH))
            val hpColor = when {
                enemy.hpPercentage > 0.6f -> Color(0xFF4CAF50)
                enemy.hpPercentage > 0.3f -> Color(0xFFFFEB3B)
                else                      -> Color(0xFFE53935)
            }
            drawRect(hpColor, Offset(barX, barY), Size(barW * enemy.hpPercentage, barH))
        }
    }

    // ── Goblin: tiny green square head + ears ─────────────────
    private fun DrawScope.drawGoblin(cx: Float, cy: Float, sc: Float) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Body
        pix(ox, oy, 2, 3, 4, 4, Color(0xFF388E3C), sc)
        // Head
        pix(ox, oy, 2, 1, 4, 3, Color(0xFF66BB6A), sc)
        // Ears
        pix(ox, oy, 1, 1, 1, 2, Color(0xFF66BB6A), sc)
        pix(ox, oy, 6, 1, 1, 2, Color(0xFF66BB6A), sc)
        // Eyes
        pix(ox, oy, 2, 2, 1, 1, Color(0xFFFFFF00), sc)
        pix(ox, oy, 5, 2, 1, 1, Color(0xFFFFFF00), sc)
        // Teeth
        pix(ox, oy, 3, 4, 1, 1, Color.White, sc)
        pix(ox, oy, 5, 4, 1, 1, Color.White, sc)
    }

    // ── Orc: big brown brute ──────────────────────────────────
    private fun DrawScope.drawOrc(cx: Float, cy: Float, sc: Float) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Armour body
        pix(ox, oy, 1, 4, 6, 4, Color(0xFF5D4037), sc)
        pix(ox, oy, 2, 4, 4, 4, Color(0xFF795548), sc)
        // Head
        pix(ox, oy, 1, 1, 6, 3, Color(0xFF8D6E63), sc)
        // Horns
        pix(ox, oy, 1, 0, 1, 2, Color(0xFFF5F5F5), sc)
        pix(ox, oy, 6, 0, 1, 2, Color(0xFFF5F5F5), sc)
        // Eyes
        pix(ox, oy, 2, 2, 1, 1, Color(0xFFE53935), sc)
        pix(ox, oy, 5, 2, 1, 1, Color(0xFFE53935), sc)
        // Metal chest plate
        pix(ox, oy, 2, 5, 4, 2, Color(0xFF78909C), sc)
    }

    // ── Dragon: large red beast ──────────────────────────────
    private fun DrawScope.drawDragon(cx: Float, cy: Float, sc: Float) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Body
        pix(ox, oy, 0, 4, 8, 4, Color(0xFFC62828), sc)
        pix(ox, oy, 1, 3, 6, 5, Color(0xFFE53935), sc)
        // Wings hint
        pix(ox, oy, 0, 2, 2, 3, Color(0xFFEF9A9A), sc)
        pix(ox, oy, 6, 2, 2, 3, Color(0xFFEF9A9A), sc)
        // Head
        pix(ox, oy, 2, 0, 4, 3, Color(0xFFE53935), sc)
        // Horns
        pix(ox, oy, 2, 0, 1, 1, Color(0xFFF57F17), sc)
        pix(ox, oy, 5, 0, 1, 1, Color(0xFFF57F17), sc)
        // Eyes (glowing gold)
        pix(ox, oy, 2, 1, 1, 1, Color(0xFFFFD600), sc)
        pix(ox, oy, 5, 1, 1, 1, Color(0xFFFFD600), sc)
        // Scales
        pix(ox, oy, 2, 5, 1, 1, Color(0xFFB71C1C), sc)
        pix(ox, oy, 4, 5, 1, 1, Color(0xFFB71C1C), sc)
        pix(ox, oy, 6, 5, 1, 1, Color(0xFFB71C1C), sc)
    }

    // ── Troll: grey bulky rock creature ───────────────────────
    private fun DrawScope.drawTroll(cx: Float, cy: Float, sc: Float) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Body (wide)
        pix(ox, oy, 0, 4, 8, 4, Color(0xFF546E7A), sc)
        pix(ox, oy, 1, 3, 6, 5, Color(0xFF607D8B), sc)
        // Head (square)
        pix(ox, oy, 1, 0, 6, 4, Color(0xFF78909C), sc)
        // Club weapon
        pix(ox, oy, 7, 2, 1, 5, Color(0xFF5D4037), sc)
        pix(ox, oy, 7, 1, 1, 2, Color(0xFF8D6E63), sc)
        // Eyes (small yellow)
        pix(ox, oy, 2, 1, 1, 1, Color(0xFFFFEE58), sc)
        pix(ox, oy, 5, 1, 1, 1, Color(0xFFFFEE58), sc)
        // Nose
        pix(ox, oy, 3, 2, 2, 1, Color(0xFF546E7A), sc)
    }

    // ── Specter: translucent ghost ────────────────────────────
    private fun DrawScope.drawSpecter(cx: Float, cy: Float, sc: Float) {
        val ox = cx - 4 * sc; val oy = cy - 4 * sc
        // Wispy body (semi-transparent)
        pix(ox, oy, 1, 2, 6, 5, Color(0xAAB39DDB), sc)
        pix(ox, oy, 2, 1, 4, 6, Color(0xCCB39DDB), sc)
        // Wavy bottom (dark gaps = transparent pattern)
        pix(ox, oy, 1, 6, 2, 1, Color.Transparent, sc)
        pix(ox, oy, 5, 6, 2, 1, Color.Transparent, sc)
        // Eyes (glowing)
        pix(ox, oy, 2, 3, 1, 1, Color(0xFFE040FB), sc)
        pix(ox, oy, 5, 3, 1, 1, Color(0xFFE040FB), sc)
        // Inner glow
        pix(ox, oy, 3, 2, 2, 4, Color(0x55EDE7F6), sc)
    }

    private fun DrawScope.pix(ox: Float, oy: Float, col: Int, row: Int, w: Int, h: Int, color: Color, sc: Float) {
        if (color == Color.Transparent) return
        drawRect(color, Offset(ox + col * sc, oy + row * sc), Size(w * sc, h * sc))
    }
}
