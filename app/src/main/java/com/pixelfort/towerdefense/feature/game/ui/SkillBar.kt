package com.pixelfort.towerdefense.feature.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelfort.towerdefense.engine.model.SkillState
import com.pixelfort.towerdefense.engine.model.SkillType

@Composable
fun SkillBar(
    skills: List<SkillState>,
    isMeteorTargeting: Boolean,
    onSkillTapped: (SkillType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End
    ) {
        skills.forEach { skill ->
            SkillButton(
                skill = skill,
                isTargeting = isMeteorTargeting && skill.type == SkillType.METEOR_STRIKE,
                onTap = { onSkillTapped(skill.type) }
            )
        }
    }
}

@Composable
private fun SkillButton(
    skill: SkillState,
    isTargeting: Boolean,
    onTap: () -> Unit
) {
    val isReady = skill.isReady
    val isOnCooldown = skill.cooldownRemainingMs > 0 && !skill.isActive
    val cooldownSec = (skill.cooldownRemainingMs / 1000L).toInt()
    val sweepAngle = skill.cooldownFraction * 360f

    val borderColor = when {
        isTargeting -> Color(0xFFFF5722)
        skill.isActive -> when (skill.type) {
            SkillType.FROZEN_TIME -> Color(0xFF00BCD4)
            SkillType.GOLD_RUSH -> Color(0xFFFFD700)
            else -> Color(0xFF4CAF50)
        }
        isReady -> Color(0xFF4CAF50)
        else -> Color(0xFF444466)
    }
    val bgColor = when {
        isTargeting -> Color(0xFF3D1400)
        skill.isActive -> Color(0xFF1A2A2A)
        isReady -> Color(0xFF1A2A1A)
        else -> Color(0xFF1A1A2E)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .alpha(if (isOnCooldown) 0.6f else 1f)
            .clickable(enabled = isReady || isTargeting) { onTap() }
            .then(
                if (isOnCooldown) {
                    Modifier.drawBehind {
                        // Radial sweep cooldown overlay
                        drawArc(
                            color = Color.Black.copy(alpha = 0.6f),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset.Zero,
                            size = Size(size.width, size.height)
                        )
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = skill.type.icon,
                fontSize = 18.sp
            )
            if (isOnCooldown) {
                Text(
                    text = "${cooldownSec}s",
                    color = Color(0xFFAABBCC),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            } else if (skill.isActive) {
                val durSec = (skill.durationRemainingMs / 1000L).toInt()
                Text(
                    text = "${durSec}s",
                    color = Color(0xFF00FF88),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
