package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
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
import com.example.theme.ThemeMaterialStyle

data class CanvasSwatchOption(
    val name: String,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color
)

data class AccentSwatchOption(
    val name: String,
    val color: Color
)

object CustomThemeOptions {
    val CANVASES = listOf(
        CanvasSwatchOption("Obsidian Black", Color(0xFF050507), Color(0xFF0C0B0E), Color(0xFF161115)),
        CanvasSwatchOption("Vanta AMOLED", Color(0xFF000000), Color(0xFF080709), Color(0xFF130D11)),
        CanvasSwatchOption("Carbon Slate", Color(0xFF0A0A0D), Color(0xFF121216), Color(0xFF1E1E24)),
        CanvasSwatchOption("Volcanic Ash", Color(0xFF120809), Color(0xFF1D0D0F), Color(0xFF2B1417)),
        CanvasSwatchOption("Midnight Abyss", Color(0xFF040914), Color(0xFF0A1224), Color(0xFF13203A)),
        CanvasSwatchOption("Deep Basalt", Color(0xFF0C0B10), Color(0xFF15131C), Color(0xFF221E2E))
    )

    val ACCENTS = listOf(
        AccentSwatchOption("Crimson Red", Color(0xFFEF233C)),
        AccentSwatchOption("Racing Scarlet", Color(0xFFFF1E2D)),
        AccentSwatchOption("Blood Ruby", Color(0xFFDC2626)),
        AccentSwatchOption("Arterial Rose", Color(0xFFE11D48)),
        AccentSwatchOption("Ember Orange", Color(0xFFF97316)),
        AccentSwatchOption("24K Gold", Color(0xFFEAB308)),
        AccentSwatchOption("Cyber Cyan", Color(0xFF00F0FF)),
        AccentSwatchOption("Matrix Emerald", Color(0xFF10B981)),
        AccentSwatchOption("Amethyst", Color(0xFFA855F7)),
        AccentSwatchOption("Katana Silver", Color(0xFFE2E8F0))
    )

    val MATERIAL_STYLES = listOf(
        ThemeMaterialStyle.METALLIC_CHROME to "Metallic",
        ThemeMaterialStyle.NEON_GLOW to "Neon Glow",
        ThemeMaterialStyle.MINIMAL_TITANIUM to "Titanium",
        ThemeMaterialStyle.GLASSMORPHIC to "Glass",
        ThemeMaterialStyle.TERMINAL_PHOSPHOR to "Terminal",
        ThemeMaterialStyle.RETRO_CRT to "Retro CRT"
    )
}

@Composable
fun CustomThemeForgePanel(
    onApplyCustomTheme: (
        customName: String,
        canvasBackground: Color,
        surfaceColor: Color,
        surfaceVariantColor: Color,
        accentColor: Color,
        secondaryColor: Color,
        materialStyle: ThemeMaterialStyle
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var selectedCanvas by remember { mutableStateOf(CustomThemeOptions.CANVASES[0]) }
    var selectedPrimary by remember { mutableStateOf(CustomThemeOptions.ACCENTS[0]) }
    var selectedSecondary by remember { mutableStateOf(CustomThemeOptions.ACCENTS[3]) }
    var selectedStyle by remember { mutableStateOf(ThemeMaterialStyle.METALLIC_CHROME) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // Live Preview Mini-Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = selectedCanvas.surface,
            border = BorderStroke(1.5.dp, selectedPrimary.color.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(selectedPrimary.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${selectedCanvas.name} × ${selectedPrimary.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedPrimary.color
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = selectedPrimary.color
                    ) {
                        Text(
                            text = "PREVIEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = selectedCanvas.surfaceVariant,
                    border = BorderStroke(1.dp, selectedPrimary.color.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "$ chatos --theme custom",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = selectedPrimary.color
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(selectedSecondary.color)
                        )
                    }
                }
            }
        }

        // 1. Base Dark Canvas Picker
        Text(
            text = "1. Base Canvas",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurface
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CustomThemeOptions.CANVASES) { canvas ->
                val isSelected = selectedCanvas.name == canvas.name
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = canvas.surface,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) theme.primary else theme.surfaceBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedCanvas = canvas }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(canvas.background)
                                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = canvas.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Primary Accent Picker
        Text(
            text = "2. Primary Accent",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurface
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CustomThemeOptions.ACCENTS) { accent ->
                val isSelected = selectedPrimary.name == accent.name
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) accent.color.copy(alpha = 0.22f) else theme.surfaceVariant,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) accent.color else theme.surfaceBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedPrimary = accent }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(accent.color)
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = accent.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = theme.onSurface
                        )
                    }
                }
            }
        }

        // 3. Secondary Highlight Picker
        Text(
            text = "3. Secondary Highlight",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurface
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CustomThemeOptions.ACCENTS) { accent ->
                val isSelected = selectedSecondary.name == accent.name
                Surface(
                    shape = CircleShape,
                    color = accent.color,
                    border = BorderStroke(
                        if (isSelected) 2.5.dp else 1.dp,
                        if (isSelected) Color.White else Color.Transparent
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { selectedSecondary = accent }
                ) {
                    if (isSelected) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = accent.name,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Material Surface Style
        Text(
            text = "4. Material Texture",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurface
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(CustomThemeOptions.MATERIAL_STYLES) { (style, label) ->
                val isSelected = selectedStyle == style
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) theme.primaryContainer else theme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) theme.primary else theme.surfaceBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedStyle = style }
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) theme.onPrimaryContainer else theme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                onApplyCustomTheme(
                    "${selectedCanvas.name.split(" ").first()} ${selectedPrimary.name}",
                    selectedCanvas.background,
                    selectedCanvas.surface,
                    selectedCanvas.surfaceVariant,
                    selectedPrimary.color,
                    selectedSecondary.color,
                    selectedStyle
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedPrimary.color,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("apply_custom_theme_button")
        ) {
            Icon(
                imageVector = Icons.Default.AutoFixHigh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Apply Custom Forged Theme",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
