package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.pixelfort.towerdefense.engine.GameSnapshot
import com.pixelfort.towerdefense.engine.model.GameMap
import com.pixelfort.towerdefense.feature.game.renderer.EnemyRenderer.drawEnemies
import com.pixelfort.towerdefense.feature.game.renderer.MapRenderer.drawMap
import com.pixelfort.towerdefense.feature.game.renderer.ProjectileRenderer.drawProjectiles
import com.pixelfort.towerdefense.feature.game.renderer.TowerRenderer.drawTowers
import com.pixelfort.towerdefense.feature.game.renderer.VfxRenderer.drawParticles
import com.pixelfort.towerdefense.feature.game.vfx.Particle

@Composable
fun GameCanvas(
    snapshot: GameSnapshot,
    map: GameMap,
    cellSize: Float,
    particles: List<Particle>,
    selectedTowerId: Int?,
    onCellTapped: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
    }
}
