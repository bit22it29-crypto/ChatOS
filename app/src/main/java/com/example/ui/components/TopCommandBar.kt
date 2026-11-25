package com.example.ui.components

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
import com.example.model.AIModel
import com.example.theme.LocalChatOSTheme
import com.example.theme.StatusPulseIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCommandBar(
    currentModel: AIModel,
    availableModels: List<AIModel>,
    onSelectModel: (AIModel) -> Unit,
    onOpenCommandPalette: () -> Unit,
    onOpenThemeBlender: () -> Unit,
    onToggleSidebar: () -> Unit,
    isAgentActive: Boolean,
    onToggleAgentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var showModelMenu by remember { mutableStateOf(false) }

    Surface(
        color = theme.surface,
        contentColor = theme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = theme.surfaceBorder,
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Sidebar Toggle Button
            IconButton(
                onClick = onToggleSidebar,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("toggle_sidebar_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle Sidebar",
                    tint = theme.onSurface
                )
            }

            // Brand glyph and Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(theme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Odysseus",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = theme.primary
                )
            }

            // Model Switcher Pill
            Box {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = theme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.surfaceBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showModelMenu = true }
                        .testTag("model_switcher_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        StatusPulseIndicator(
                            isActive = true,
                            activeColor = if (currentModel.isLocal) theme.secondary else theme.primary
                        )
                        Text(
                            text = currentModel.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = theme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Model",
                            tint = theme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showModelMenu,
                    onDismissRequest = { showModelMenu = false },
                    modifier = Modifier.background(theme.surface)
                ) {
                    availableModels.forEach { model ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = model.name,
                                            fontWeight = if (model.id == currentModel.id) FontWeight.Bold else FontWeight.Normal,
                                            color = if (model.id == currentModel.id) theme.primary else theme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (model.isLocal) theme.secondaryContainer else theme.primaryContainer
                                        ) {
                                            Text(
                                                text = if (model.isLocal) "LOCAL" else "API",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (model.isLocal) theme.onSecondaryContainer else theme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${model.provider} • ${model.contextWindow} • ${model.fitStatus}",
                                        fontSize = 11.sp,
                                        color = theme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onSelectModel(model)
                                showModelMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Agent Mode Toggle Chip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isAgentActive) theme.primary.copy(alpha = 0.2f) else theme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAgentActive) theme.primary else theme.surfaceBorder
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleAgentMode() }
                    .testTag("toggle_agent_mode_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isAgentActive) Icons.Default.SmartToy else Icons.Default.ChatBubbleOutline,
                        contentDescription = "Agent Mode",
                        tint = if (isAgentActive) theme.primary else theme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAgentActive) "Agent Active" else "Chat Mode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAgentActive) theme.primary else theme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Command Palette (Ctrl+K) Trigger
            IconButton(
                onClick = onOpenCommandPalette,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("command_palette_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search & Commands",
                    tint = theme.onSurface
                )
            }

            // Theme Studio / Blender Trigger
            IconButton(
                onClick = onOpenThemeBlender,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("theme_studio_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Studio & Blender",
                    tint = theme.primary
                )
            }
        }
    }
}
