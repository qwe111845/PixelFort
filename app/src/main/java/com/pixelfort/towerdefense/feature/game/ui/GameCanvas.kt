package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import com.pixelfort.towerdefense.engine.GameSnapshot
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.feature.game.renderer.EnemyRenderer.drawEnemies
import com.pixelfort.towerdefense.feature.game.renderer.FloatingTextRenderer.drawFloatingTexts
import com.pixelfort.towerdefense.feature.game.renderer.MapRenderer.drawMap
import com.pixelfort.towerdefense.feature.game.renderer.ProjectileRenderer.drawProjectiles
import com.pixelfort.towerdefense.feature.game.renderer.TowerRenderer.drawTowers
import com.pixelfort.towerdefense.feature.game.renderer.VfxRenderer.drawParticles
import com.pixelfort.towerdefense.feature.game.vfx.FloatingText
import com.pixelfort.towerdefense.feature.game.vfx.Particle
import com.pixelfort.towerdefense.feature.game.vfx.ScreenShake

@Composable
fun GameCanvas(
    snapshot: GameSnapshot,
    map: GameMap,
    cellSize: Float,
    particles: List<Particle>,
    floatingTexts: List<FloatingText> = emptyList(),
    screenShake: ScreenShake = ScreenShake.IDLE,
    selectedTowerId: Int?,
    onCellTapped: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .pointerInput(cellSize) {
                detectTapGestures { offset ->
                    val col = (offset.x / cellSize).toInt()
                    val row = (offset.y / cellSize).toInt()
                    if (map.isInBounds(row, col)) {
                        onCellTapped(row, col)
                    }
                }
            }
    ) {
        // Apply screen shake offset
        val shakeX = screenShake.offsetX
        val shakeY = screenShake.offsetY
        translate(shakeX, shakeY) {
            drawMap(map, cellSize)
            drawTowers(snapshot.towers, cellSize, selectedTowerId)
            drawEnemies(snapshot.enemies, cellSize)
            drawProjectiles(snapshot.projectiles)
            drawParticles(particles)
        }
        // Floating texts drawn without shake (stays stable)
        drawFloatingTexts(floatingTexts, textMeasurer)
    }
}
