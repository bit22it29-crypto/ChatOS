package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.LocalChatOSTheme
import com.example.theme.StatusPulseIndicator

@Composable
fun FloatingActionIsland(
    isGenerating: Boolean,
    onOpenThemeStudio: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var isListening by remember { mutableStateOf(false) }

    // Waveform animation when listening
    val infiniteTransition = rememberInfiniteTransition(label = "island_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = theme.surface.copy(alpha = 0.92f),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentGlow.copy(alpha = 0.45f)),
        shadowElevation = 8.dp,
        modifier = modifier
            .testTag("floating_action_island")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Agent Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                StatusPulseIndicator(
                    isActive = isGenerating || isListening,
                    activeColor = if (isListening) theme.tertiary else theme.primary
                )
                Text(
                    text = when {
                        isListening -> "Listening..."
                        isGenerating -> "Agent Working"
                        else -> "Agent Standby"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = theme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Quick Tool Triggers
            IconButton(
                onClick = { onQuickPrompt("Perform deep web research on latest local LLM developments") },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(theme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.TravelExplore,
                    contentDescription = "Quick Search",
                    tint = theme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = { onQuickPrompt("Scan local hardware and check model compatibility scores") },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(theme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "Hardware Scan",
                    tint = theme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Mic / Voice Island Button
            IconButton(
                onClick = {
                    isListening = !isListening
                    if (isListening) {
                        onQuickPrompt("Summarize all pending agent tasks and email drafts")
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isListening) theme.primary.copy(alpha = pulseAlpha) else theme.primary)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Command",
                    tint = theme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Active Theme Badge & Switcher
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = theme.primaryContainer.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenThemeStudio() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "[${theme.badgeCode}]",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = theme.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
