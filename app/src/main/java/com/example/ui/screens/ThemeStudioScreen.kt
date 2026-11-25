package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.theme.*
import com.example.ui.components.CustomThemeForgePanel

@Composable
fun ThemeStudioScreen(
    config: MultiThemeConfig,
    onSetGlobalTheme: (ChatOSTheme) -> Unit,
    onSetSectionTheme: (WorkspaceSection, ChatOSTheme?) -> Unit,
    onResetSections: () -> Unit,
    onSetBlend: (ChatOSTheme?, Float) -> Unit,
    onApplyPreset: (ThemePreset) -> Unit,
    onApplyCustomTheme: (
        customName: String,
        canvasBackground: Color,
        surfaceColor: Color,
        surfaceVariantColor: Color,
        accentColor: Color,
        secondaryColor: Color,
        materialStyle: ThemeMaterialStyle
    ) -> Unit,
    onRandomizeTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalChatOSTheme.current
    var selectedSection by remember { mutableStateOf(WorkspaceSection.GLOBAL) }
    var blendSecondary by remember(config.blendSecondaryTheme) { mutableStateOf(config.blendSecondaryTheme) }
    var blendSliderValue by remember(config.blendIntensity) { mutableStateOf(config.blendIntensity) }
    var studioTab by remember { mutableStateOf(0) } // 0: Gallery, 1: Custom Forge, 2: Per-Section, 3: Live Blender, 4: Presets
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ThemeCategory.ALL) }

    val filteredThemes = remember(searchQuery, selectedCategory) {
        ThemeRegistry.ALL_THEMES.filter { th ->
            val matchesCat = selectedCategory == ThemeCategory.ALL || th.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                th.name.contains(searchQuery, ignoreCase = true) ||
                th.tagLine.contains(searchQuery, ignoreCase = true) ||
                th.emotionalTone.contains(searchQuery, ignoreCase = true) ||
                th.badgeCode.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .background(currentTheme.background)
    ) {
        // Hero Card
        item {
            ChatOSCard(
                glowEffect = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = currentTheme.primary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentTheme.badgeCode,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = currentTheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ChatOS Multi-Theme Studio",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            )
                            Text(
                                text = "Active: [${currentTheme.badgeCode}] ${currentTheme.name} • ${ThemeRegistry.ALL_THEMES.size} Themes",
                                fontSize = 11.sp,
                                color = currentTheme.onSurfaceVariant
                            )
                        }

                        // Randomize / Surprise Me button
                        FilledTonalIconButton(
                            onClick = onRandomizeTheme,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = currentTheme.primaryContainer,
                                contentColor = currentTheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("randomize_theme_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Randomize Theme",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Palette Swatch Strip
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ColorTokenChip("Primary", currentTheme.primary, Modifier.weight(1f))
                        ColorTokenChip("Secondary", currentTheme.secondary, Modifier.weight(1f))
                        ColorTokenChip("Tertiary", currentTheme.tertiary, Modifier.weight(1f))
                        ColorTokenChip("Surface", currentTheme.surface, Modifier.weight(1f))
                        ColorTokenChip("Prompt", currentTheme.terminalPrompt, Modifier.weight(1f))
                    }
                }
            }
        }

        // Scrollable Sub-Tab Switcher
        item {
            ScrollableTabRow(
                selectedTabIndex = studioTab,
                containerColor = currentTheme.surfaceVariant,
                contentColor = currentTheme.primary,
                edgePadding = 4.dp,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = studioTab == 0,
                    onClick = { studioTab = 0 },
                    text = { Text("Gallery (${ThemeRegistry.ALL_THEMES.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = studioTab == 1,
                    onClick = { studioTab = 1 },
                    text = { Text("Custom Forge", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = studioTab == 2,
                    onClick = { studioTab = 2 },
                    text = { Text("Per-Section", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = studioTab == 3,
                    onClick = { studioTab = 3 },
                    text = { Text("Live Blender", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = studioTab == 4,
                    onClick = { studioTab = 4 },
                    text = { Text("Presets (${PresetRegistry.PRESETS.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (studioTab) {
            0 -> {
                // Search Bar + Category Filter Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text("Search themes (e.g. Obsidian, Red, Neon, Cyber...)", fontSize = 12.sp, color = currentTheme.onSurfaceVariant)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search Themes", tint = currentTheme.primary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = currentTheme.onSurfaceVariant)
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.primary,
                                unfocusedBorderColor = currentTheme.surfaceBorder,
                                focusedTextColor = currentTheme.onSurface,
                                unfocusedTextColor = currentTheme.onSurface,
                                focusedContainerColor = currentTheme.surfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = currentTheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(ThemeCategory.values()) { category ->
                                val isSelected = selectedCategory == category
                                val count = if (category == ThemeCategory.ALL) ThemeRegistry.ALL_THEMES.size else ThemeRegistry.ALL_THEMES.count { it.category == category }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) currentTheme.primary else currentTheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) currentTheme.primary else currentTheme.surfaceBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedCategory = category }
                                ) {
                                    Text(
                                        text = "${category.label} ($count)",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) currentTheme.onPrimary else currentTheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Filtered Themes List
                items(filteredThemes, key = { it.id }) { theme ->
                    val isSelected = config.globalTheme.id == theme.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) currentTheme.primaryContainer.copy(alpha = 0.45f) else currentTheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) currentTheme.primary else currentTheme.surfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSetGlobalTheme(theme) }
                            .testTag("theme_card_${theme.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = theme.primary,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = theme.badgeCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.onPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = theme.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.onSurface
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = currentTheme.primary
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = currentTheme.onPrimary,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = theme.tagLine,
                                    fontSize = 11.sp,
                                    color = currentTheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${theme.category.label} • ${theme.materialStyle.name.replace('_', ' ')}",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = theme.primary
                                )
                            }

                            // Swatches
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.primary))
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.secondary))
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.tertiary))
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.background).border(0.5.dp, Color.Gray, CircleShape))
                            }
                        }
                    }
                }
            }

            1 -> {
                // Custom Theme Forge
                item {
                    CustomThemeForgePanel(onApplyCustomTheme = onApplyCustomTheme)
                }
            }

            2 -> {
                // Interactive Per-Section Assignment
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Select Section to Customize:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentTheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onResetSections) {
                            Text("Reset All", fontSize = 11.sp, color = currentTheme.primary)
                        }
                    }
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(WorkspaceSection.values()) { section ->
                            val isCurrent = selectedSection == section
                            val assigned = config.getThemeForSection(section)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) currentTheme.primary else currentTheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isCurrent) currentTheme.primary else currentTheme.surfaceBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedSection = section }
                            ) {
                                Text(
                                    text = "${section.displayName} [${assigned.badgeCode}]",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isCurrent) currentTheme.onPrimary else currentTheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    val assigned = config.getThemeForSection(selectedSection)
                    Text(
                        text = "${selectedSection.displayName}: [${assigned.badgeCode}] ${assigned.name}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = currentTheme.primary
                    )
                }

                items(ThemeRegistry.ALL_THEMES, key = { "sec_${it.id}" }) { theme ->
                    val assigned = config.getThemeForSection(selectedSection)
                    val isMatch = assigned.id == theme.id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isMatch) currentTheme.primaryContainer.copy(alpha = 0.4f) else currentTheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (isMatch) currentTheme.primary else currentTheme.surfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (selectedSection == WorkspaceSection.GLOBAL) {
                                    onSetGlobalTheme(theme)
                                } else {
                                    onSetSectionTheme(selectedSection, theme)
                                }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "[${theme.badgeCode}]",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = theme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = theme.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = currentTheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(theme.primary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(theme.background).border(0.5.dp, Color.Gray, CircleShape))
                        }
                    }
                }
            }

            3 -> {
                // Live Theme Blender
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = currentTheme.surface,
                        border = BorderStroke(1.dp, currentTheme.surfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Realtime Color Gradient Blender",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.primary
                                    )
                                    Text(
                                        text = "Cross-fade Base [${config.globalTheme.badgeCode}] with Secondary theme",
                                        fontSize = 11.sp,
                                        color = currentTheme.onSurfaceVariant
                                    )
                                }
                                if (blendSecondary != null && blendSliderValue > 0f) {
                                    TextButton(
                                        onClick = {
                                            blendSecondary = null
                                            blendSliderValue = 0f
                                            onSetBlend(null, 0f)
                                        }
                                    ) {
                                        Text("Clear", fontSize = 11.sp, color = currentTheme.primary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Slider(
                                value = blendSliderValue,
                                onValueChange = {
                                    blendSliderValue = it
                                    onSetBlend(blendSecondary, it)
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = currentTheme.primary,
                                    activeTrackColor = currentTheme.primary,
                                    inactiveTrackColor = currentTheme.surfaceBorder
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("0% [${config.globalTheme.name}]", fontSize = 10.sp, color = currentTheme.onSurfaceVariant)
                                Text("${(blendSliderValue * 100).toInt()}% Mix", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = currentTheme.primary)
                                Text("100% Target", fontSize = 10.sp, color = currentTheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                items(ThemeRegistry.ALL_THEMES, key = { "blend_${it.id}" }) { theme ->
                    val isSecondary = blendSecondary?.id == theme.id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSecondary) currentTheme.primaryContainer.copy(alpha = 0.5f) else currentTheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (isSecondary) currentTheme.primary else currentTheme.surfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                blendSecondary = theme
                                if (blendSliderValue < 0.05f) blendSliderValue = 0.35f
                                onSetBlend(theme, blendSliderValue)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "[${theme.badgeCode}]",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = theme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = theme.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = if (isSecondary) "ACTIVE BLEND TARGET" else "Select as Blend",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSecondary) currentTheme.primary else currentTheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            4 -> {
                // Presets
                items(PresetRegistry.PRESETS, key = { it.id }) { preset ->
                    val isPresetActive = config.activePresetId == preset.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPresetActive) currentTheme.primaryContainer.copy(alpha = 0.45f) else currentTheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(if (isPresetActive) 1.5.dp else 1.dp, if (isPresetActive) currentTheme.primary else currentTheme.surfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onApplyPreset(preset) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = currentTheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = preset.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.description,
                                fontSize = 11.sp,
                                color = currentTheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorTokenChip(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)),
        modifier = modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (color.luminance() > 0.5f) Color.Black else Color.White
            )
        }
    }
}

fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
