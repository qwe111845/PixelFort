package com.pixelfort.towerdefense.feature.game.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Full-screen tutorial overlay with instruction text, Next and Skip buttons.
 * Uses a semi-transparent dark background. The spotlight concept is represented
 * by the instructional text describing what to look at; actual UI highlight
 * rectangles would require measuring real layout coordinates at runtime and
 * are kept simple for this implementation.
 */
@Composable
fun TutorialOverlay(
    tutorialState: TutorialState,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = tutorialState.currentStep ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* consume taps so game doesn't receive them */ }
    ) {
        // Content: text + buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = if (step.textAtBottom) Arrangement.Bottom else Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (step.textAtBottom) {
                Spacer(Modifier.weight(1f))
            }

            // Spotlight target hint
            if (step.spotlightTarget != SpotlightTarget.FULL_SCREEN) {
                Text(
                    text = spotlightHint(step.spotlightTarget),
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Step indicator
            Text(
                text = "Step ${step.index + 1} / ${tutorialState.totalSteps}",
                color = Color(0xFF88AACC),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            // Main instruction text
            Text(
                text = step.text,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color(0xFF1A1A2E).copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Skip Tutorial", fontSize = 14.sp)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (step.index == tutorialState.totalSteps - 1) "Done" else "Next",
                        fontSize = 14.sp
                    )
                }
            }

            if (step.textAtBottom) {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

private fun spotlightHint(target: SpotlightTarget): String = when (target) {
    SpotlightTarget.MAP_AREA -> "[ Look at the map area ]"
    SpotlightTarget.TOWER_BAR -> "[ Look at the tower bar below ]"
    SpotlightTarget.BUILDABLE_CELLS -> "[ Look at the green cells on the map ]"
    SpotlightTarget.START_BUTTON -> "[ Look at the Start Wave button ]"
    SpotlightTarget.PLACED_TOWER -> "[ Look at your placed tower on the map ]"
    SpotlightTarget.FULL_SCREEN -> ""
}
