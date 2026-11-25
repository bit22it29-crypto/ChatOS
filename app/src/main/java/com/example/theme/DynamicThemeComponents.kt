package com.example.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatOSCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    glowEffect: Boolean = false,
    content: @Composable () -> Unit
) {
    val theme = LocalChatOSTheme.current
    val animatedBg by animateColorAsState(
        targetValue = backgroundColor ?: theme.surface,
        animationSpec = tween(350),
        label = "card_bg"
    )
    val animatedBorder by animateColorAsState(
        targetValue = borderColor ?: theme.surfaceBorder,
        animationSpec = tween(350),
        label = "card_border"
    )

    val cardModifier = if (glowEffect) {
        modifier
            .clip(shape)
            .border(
                BorderStroke(
                    borderWidth,
                    Brush.linearGradient(
                        colors = listOf(
                            theme.primary.copy(alpha = 0.8f),
                            animatedBorder.copy(alpha = 0.4f),
                            theme.secondary.copy(alpha = 0.6f)
                        )
                    )
                ),
                shape
            )
            .background(animatedBg, shape)
    } else {
        modifier
            .clip(shape)
            .border(BorderStroke(borderWidth, animatedBorder), shape)
            .background(animatedBg, shape)
    }

    Box(modifier = cardModifier) {
        // Subtle Retro CRT Scanline overlay if Retro CRT theme is active
        if (theme.materialStyle == ThemeMaterialStyle.RETRO_CRT) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val step = 4.dp.toPx()
                for (y in 0..size.height.toInt() step step.toInt()) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.15f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = 1f
                    )
                }
            }
        }
        content()
    }
}

@Composable
fun ChatOSTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalChatOSTheme.current.primary,
    backgroundColor: Color = LocalChatOSTheme.current.primaryContainer.copy(alpha = 0.4f),
    fontFamily: FontFamily = FontFamily.Monospace
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatusPulseIndicator(
    isActive: Boolean = true,
    activeColor: Color = LocalChatOSTheme.current.primary,
    inactiveColor: Color = Color.Gray,
    size: Dp = 8.dp
) {
    Canvas(modifier = Modifier.padding(end = 6.dp)) {
        drawCircle(
            color = if (isActive) activeColor else inactiveColor,
            radius = size.toPx() / 2
        )
        if (isActive) {
            drawCircle(
                color = activeColor.copy(alpha = 0.35f),
                radius = size.toPx()
            )
        }
    }
}
