package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AIModel
import com.example.model.WorkspaceTab
import com.example.theme.ChatOSTheme
import com.example.theme.LocalChatOSTheme
import com.example.theme.ThemeRegistry

data class PaletteAction(
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val onExecute: () -> Unit
)

@Composable
fun CommandPaletteDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSelectTab: (WorkspaceTab) -> Unit,
    onSelectModel: (AIModel) -> Unit,
    onSelectTheme: (ChatOSTheme) -> Unit,
    availableModels: List<AIModel>,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val theme = LocalChatOSTheme.current
    var query by remember { mutableStateOf("") }

    val allActions = remember(availableModels) {
        val list = mutableListOf<PaletteAction>()

        // Navigation Tabs
        WorkspaceTab.values().forEach { tab ->
            list.add(
                PaletteAction(
                    title = "Go to ${tab.title}",
                    subtitle = "Navigate to workspace section",
                    category = "Navigation",
                    icon = when (tab) {
                        WorkspaceTab.CHAT -> Icons.Default.Chat
                        WorkspaceTab.DEEP_RESEARCH -> Icons.Default.TravelExplore
                        WorkspaceTab.COOKBOOK -> Icons.Default.Memory
                        WorkspaceTab.COMPARE -> Icons.Default.CompareArrows
                        WorkspaceTab.DOCUMENTS -> Icons.Default.Edit
                        WorkspaceTab.EMAIL -> Icons.Default.Mail
                        WorkspaceTab.NOTES_TASKS -> Icons.Default.EventNote
                        WorkspaceTab.MEMORY_SKILLS -> Icons.Default.Psychology
                        WorkspaceTab.MCP_INTEGRATIONS -> Icons.Default.Hub
                        WorkspaceTab.THEME_STUDIO -> Icons.Default.Palette
                    },
                    onExecute = {
                        onSelectTab(tab)
                        onDismiss()
                    }
                )
            )
        }

        // Models
        availableModels.forEach { model ->
            list.add(
                PaletteAction(
                    title = "Switch Model: ${model.name}",
                    subtitle = "${model.provider} • ${model.contextWindow} • ${model.fitStatus}",
                    category = "AI Models",
                    icon = Icons.Default.SmartToy,
                    onExecute = {
                        onSelectModel(model)
                        onDismiss()
                    }
                )
            )
        }

        // Themes
        ThemeRegistry.ALL_THEMES.forEach { th ->
            list.add(
                PaletteAction(
                    title = "Apply Theme: [${th.badgeCode}] ${th.name}",
                    subtitle = "${th.category.label} • ${th.tagLine}",
                    category = "Themes",
                    icon = Icons.Default.Brush,
                    onExecute = {
                        onSelectTheme(th)
                        onDismiss()
                    }
                )
            )
        }

        list
    }

    val filteredActions = remember(query, allActions) {
        if (query.isBlank()) allActions
        else allActions.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.subtitle.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentGlow.copy(alpha = 0.5f)),
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .testTag("command_palette_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header Search Input
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.surfaceVariant)
                        .border(1.dp, theme.surfaceBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = theme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                "Type a command, model, theme, or tab...",
                                color = theme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = theme.onSurface,
                            unfocusedTextColor = theme.onSurface
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${filteredActions.size} Results",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = theme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Divider(color = theme.surfaceBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredActions) { action ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = theme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.surfaceBorder.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { action.onExecute() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = theme.primaryContainer.copy(alpha = 0.6f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = null,
                                            tint = theme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = action.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = theme.onSurface
                                    )
                                    Text(
                                        text = action.subtitle,
                                        fontSize = 11.sp,
                                        color = theme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = theme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.surfaceBorder)
                                ) {
                                    Text(
                                        text = action.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
