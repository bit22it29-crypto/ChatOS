package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.theme.*

@Composable
fun ThemeBlenderModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
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
    if (!isOpen) return

    val currentTheme = LocalChatOSTheme.current
    var selectedSection by remember { mutableStateOf(WorkspaceSection.GLOBAL) }
    var blendSecondary by remember(config.blendSecondaryTheme) { mutableStateOf(config.blendSecondaryTheme) }
    var blendSliderValue by remember(config.blendIntensity) { mutableStateOf(config.blendIntensity) }
    var activeTab by remember { mutableStateOf(0) } // 0: Gallery, 1: Custom Forge, 2: Per-Section, 3: Blender, 4: Presets
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ThemeCategory.ALL) }

    val filteredThemes = remember(searchQuery, selectedCategory) {
        ThemeRegistry.ALL_THEMES.filter { th ->
            val matchesCat = selectedCategory == ThemeCategory.ALL || th.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                th.name.contains(searchQuery, ignoreCase = true) ||
                th.tagLine.contains(searchQuery, ignoreCase = true) ||
                th.badgeCode.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = currentTheme.surface,
            border = BorderStroke(1.dp, currentTheme.accentGlow.copy(alpha = 0.6f)),
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("theme_blender_modal")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(currentTheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ChatOS Theme Studio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = currentTheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onRandomizeTheme,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Randomize Theme",
                            tint = currentTheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = currentTheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "${ThemeRegistry.ALL_THEMES.size} Themes • Custom Forge • Per-Section • Live RGB Blender",
                    fontSize = 11.sp,
                    color = currentTheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = activeTab,
                    containerColor = currentTheme.surfaceVariant,
                    contentColor = currentTheme.primary,
                    edgePadding = 4.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .padding(bottom = 10.dp)
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Themes (${ThemeRegistry.ALL_THEMES.size})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Custom Forge", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Per-Section", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("Blender", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeTab == 4,
                        onClick = { activeTab = 4 },
                        text = { Text("Presets", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                // Tab Content
                when (activeTab) {
                    0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Search input
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Search themes...", fontSize = 12.sp, color = currentTheme.onSurfaceVariant)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = currentTheme.primary, modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = currentTheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentTheme.primary,
                                    unfocusedBorderColor = currentTheme.surfaceBorder,
                                    focusedTextColor = currentTheme.onSurface,
                                    unfocusedTextColor = currentTheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Category filter chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(ThemeCategory.values()) { category ->
                                    val isCat = selectedCategory == category
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCat) currentTheme.primary else currentTheme.surfaceVariant,
                                        border = BorderStroke(1.dp, if (isCat) currentTheme.primary else currentTheme.surfaceBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { selectedCategory = category }
                                    ) {
                                        Text(
                                            text = category.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCat) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCat) currentTheme.onPrimary else currentTheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Filtered Themes Grid
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredThemes, key = { it.id }) { theme ->
                                    val isSelected = config.globalTheme.id == theme.id
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) currentTheme.primaryContainer.copy(alpha = 0.5f) else currentTheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) currentTheme.primary else currentTheme.surfaceBorder
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onSetGlobalTheme(theme) }
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = theme.primary,
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = theme.badgeCode,
                                                            color = theme.onPrimary,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(6.dp))

                                                Text(
                                                    text = theme.name,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = currentTheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(theme.primary))
                                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(theme.secondary))
                                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(theme.tertiary))
                                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(theme.background).border(0.5.dp, Color.Gray, CircleShape))
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = theme.emotionalTone,
                                                fontSize = 9.sp,
                                                color = currentTheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Custom Theme Forge
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            CustomThemeForgePanel(onApplyCustomTheme = onApplyCustomTheme)
                        }
                    }

                    2 -> {
                        // Per-Section Overrides
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Target Workspace Section:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = currentTheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = onResetSections) {
                                    Text("Reset", fontSize = 11.sp, color = currentTheme.primary)
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(WorkspaceSection.values()) { section ->
                                    val isCurrent = selectedSection == section
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCurrent) currentTheme.primary else currentTheme.surfaceVariant,
                                        border = BorderStroke(1.dp, if (isCurrent) currentTheme.primary else currentTheme.surfaceBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { selectedSection = section }
                                    ) {
                                        Text(
                                            text = section.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isCurrent) currentTheme.onPrimary else currentTheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val assignedTheme = config.getThemeForSection(selectedSection)
                            Text(
                                text = "Assigned: [${assignedTheme.badgeCode}] ${assignedTheme.name}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = currentTheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(ThemeRegistry.ALL_THEMES, key = { "modal_sec_${it.id}" }) { theme ->
                                    val isMatch = assignedTheme.id == theme.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isMatch) currentTheme.primaryContainer.copy(alpha = 0.4f) else currentTheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, if (isMatch) currentTheme.primary else currentTheme.surfaceBorder),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
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
                                            modifier = Modifier.padding(8.dp)
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
                                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(theme.primary))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(theme.secondary))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Live Theme Blender
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Live Dynamic RGB Interpolator",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.primary
                                    )
                                    Text(
                                        text = "Smoothly blend base with a secondary theme",
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

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Base: [${config.globalTheme.badgeCode}] ${config.globalTheme.name}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = currentTheme.primary
                                )
                                Text(
                                    text = "Target: ${blendSecondary?.let { "[${it.badgeCode}] ${it.name}" } ?: "None"}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = blendSecondary?.primary ?: currentTheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

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
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("0% (Base)", fontSize = 10.sp, color = currentTheme.onSurfaceVariant)
                                Text("${(blendSliderValue * 100).toInt()}% Blend", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = currentTheme.primary)
                                Text("100% (Target)", fontSize = 10.sp, color = currentTheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Select Secondary Theme to Blend:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = currentTheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(ThemeRegistry.ALL_THEMES, key = { "modal_blend_${it.id}" }) { theme ->
                                    val isSecondary = blendSecondary?.id == theme.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSecondary) currentTheme.primaryContainer.copy(alpha = 0.5f) else currentTheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, if (isSecondary) currentTheme.primary else currentTheme.surfaceBorder),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                blendSecondary = theme
                                                if (blendSliderValue < 0.05f) blendSliderValue = 0.35f
                                                onSetBlend(theme, blendSliderValue)
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
                                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(theme.primary))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // Designer Presets
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(PresetRegistry.PRESETS, key = { it.id }) { preset ->
                                val isPresetActive = config.activePresetId == preset.id
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isPresetActive) currentTheme.primaryContainer.copy(alpha = 0.45f) else currentTheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(if (isPresetActive) 1.5.dp else 1.dp, if (isPresetActive) currentTheme.primary else currentTheme.surfaceBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            onApplyPreset(preset)
                                            onDismiss()
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = currentTheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = preset.name,
                                                fontSize = 13.sp,
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
        }
    }
}
