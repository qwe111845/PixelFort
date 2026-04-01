package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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

@Composable
fun GameCanvas(
    snapshot: GameSnapshot,
    map: GameMap,
    cellSize: Float,
    particles: List<Particle>,
    floatingTexts: List<FloatingText> = emptyList(),
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
        drawMap(map, cellSize)
        drawTowers(snapshot.towers, cellSize, selectedTowerId)
        drawEnemies(snapshot.enemies, cellSize)
        drawProjectiles(snapshot.projectiles)
        drawParticles(particles)
        drawFloatingTexts(floatingTexts, textMeasurer)
    }
}
