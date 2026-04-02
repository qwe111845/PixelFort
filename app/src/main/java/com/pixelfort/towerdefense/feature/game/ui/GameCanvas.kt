package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import com.pixelfort.towerdefense.core.util.SpriteAssetLoader
import com.pixelfort.towerdefense.engine.GameSnapshot
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.engine.model.TowerType
import com.pixelfort.towerdefense.feature.game.renderer.EnemyRenderer.drawEnemies
import com.pixelfort.towerdefense.feature.game.renderer.FloatingTextRenderer.drawFloatingTexts
import com.pixelfort.towerdefense.feature.game.renderer.MapRenderer.drawMap
import com.pixelfort.towerdefense.feature.game.renderer.ProjectileRenderer.drawProjectiles
import com.pixelfort.towerdefense.feature.game.renderer.TowerRenderer.drawGhostTower
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
    selectedTowerType: TowerType? = null,
    spriteLoader: SpriteAssetLoader? = null,
    elapsedMs: Long = 0L,
    onCellTapped: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Track pointer position for ghost tower preview
    var pointerPos by remember { mutableStateOf<Offset?>(null) }

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
            .pointerInput(cellSize, selectedTowerType) {
                if (selectedTowerType != null) {
                    detectDragGestures(
                        onDragStart = { offset -> pointerPos = offset },
                        onDrag = { change, _ ->
                            pointerPos = change.position
                            change.consume()
                        },
                        onDragEnd = { pointerPos = null },
                        onDragCancel = { pointerPos = null }
                    )
                }
            }
    ) {
        // Apply screen shake offset
        val shakeX = screenShake.offsetX
        val shakeY = screenShake.offsetY
        translate(shakeX, shakeY) {
            drawMap(map, cellSize)
            drawTowers(snapshot.towers, cellSize, selectedTowerId, spriteLoader, elapsedMs)
            drawEnemies(snapshot.enemies, cellSize, spriteLoader, elapsedMs)
            drawProjectiles(snapshot.projectiles)
            drawParticles(particles)

            // Ghost tower preview when a tower type is selected
            if (selectedTowerType != null) {
                // Use pointer position if dragging, otherwise show at center of canvas
                val pos = pointerPos
                if (pos != null) {
                    val ghostCol = (pos.x / cellSize).toInt()
                    val ghostRow = (pos.y / cellSize).toInt()
                    if (map.isInBounds(ghostRow, ghostCol)) {
                        val isBuildable = map.isBuildable(ghostRow, ghostCol)
                        val isOccupied = snapshot.towers.any {
                            it.gridRow == ghostRow && it.gridCol == ghostCol
                        }
                        val isValid = isBuildable && !isOccupied
                        drawGhostTower(
                            towerType = selectedTowerType,
                            gridRow = ghostRow,
                            gridCol = ghostCol,
                            cellSize = cellSize,
                            isValid = isValid,
                            spriteLoader = spriteLoader
                        )
                    }
                }
            }
        }
        // Floating texts drawn without shake (stays stable)
        drawFloatingTexts(floatingTexts, textMeasurer)
    }
}
