package com.example.ui.navigation

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
import androidx.compose.runtime.Composable
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
import com.example.model.WorkspaceTab
import com.example.theme.ChatOSTheme
import com.example.theme.LocalChatOSTheme
import com.example.theme.ThemeRegistry

data class DrawerMenuItem(
    val tab: WorkspaceTab,
    val icon: ImageVector,
    val label: String,
    val badge: String? = null
)

@Composable
fun ChatOSDrawer(
    currentTab: WorkspaceTab,
    activeTheme: ChatOSTheme,
    onSelectTab: (WorkspaceTab) -> Unit,
    onNewChat: () -> Unit,
    onOpenSearch: () -> Unit,
    onQuickSelectTheme: (ChatOSTheme) -> Unit,
    onOpenThemeBlender: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current

    val menuItems = listOf(
        DrawerMenuItem(WorkspaceTab.CHAT, Icons.Default.Chat, "Chat & Agents", "Live"),
        DrawerMenuItem(WorkspaceTab.DEEP_RESEARCH, Icons.Default.TravelExplore, "Deep Research", "SearXNG"),
        DrawerMenuItem(WorkspaceTab.COOKBOOK, Icons.Default.Memory, "Cookbook", "Local"),
        DrawerMenuItem(WorkspaceTab.COMPARE, Icons.Default.CompareArrows, "Model Compare", "Blind"),
        DrawerMenuItem(WorkspaceTab.DOCUMENTS, Icons.Default.Edit, "Documents", null),
        DrawerMenuItem(WorkspaceTab.EMAIL, Icons.Default.Mail, "Email & Triage", "IMAP"),
        DrawerMenuItem(WorkspaceTab.NOTES_TASKS, Icons.Default.EventNote, "Notes & Cal", null),
        DrawerMenuItem(WorkspaceTab.MEMORY_SKILLS, Icons.Default.Psychology, "Chroma & Skills", "DB"),
        DrawerMenuItem(WorkspaceTab.MCP_INTEGRATIONS, Icons.Default.Hub, "MCP Servers", "RPC"),
        DrawerMenuItem(WorkspaceTab.THEME_STUDIO, Icons.Default.Palette, "Theme Studio", "${ThemeRegistry.ALL_THEMES.size}+")
    )

    Surface(
        color = theme.surface,
        contentColor = theme.onSurface,
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .border(
                width = 1.dp,
                color = theme.surfaceBorder,
                shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            )
            .testTag("chatos_sidebar_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 18.dp)
        ) {
            // Odysseus Logo Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = theme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "ChatOS Core",
                            tint = theme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Odysseus",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.primary
                    )
                    Text(
                        text = "Local-First ChatOS",
                        fontSize = 10.sp,
                        color = theme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "+ New Chat" Action Button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = theme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.surfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNewChat()
                        onSelectTab(WorkspaceTab.CHAT)
                        onCloseDrawer()
                    }
                    .testTag("new_chat_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = theme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Chat",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // "Search" Action Button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onOpenSearch()
                        onCloseDrawer()
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = theme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search (Ctrl+K)",
                        fontSize = 12.sp,
                        color = theme.onSurfaceVariant
                    )
                }
            }

            Divider(
                color = theme.surfaceBorder,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Main Menu List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(menuItems) { item ->
                    val isSelected = currentTab == item.tab
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) theme.primary.copy(alpha = 0.15f) else Color.Transparent,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.4f)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onSelectTab(item.tab)
                                onCloseDrawer()
                            }
                            .testTag("menu_tab_${item.tab.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) theme.primary else theme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = item.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) theme.primary else theme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (item.badge != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) theme.primary else theme.surfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Text(
                                        text = item.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isSelected) theme.onPrimary else theme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(
                color = theme.surfaceBorder,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Quick Theme Switcher Strip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "QUICK THEMES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = theme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "All ${ThemeRegistry.ALL_THEMES.size} →",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            onOpenThemeBlender()
                            onCloseDrawer()
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                items(ThemeRegistry.QUICK_FAVORITES) { fav ->
                    val isCurrent = activeTheme.id == fav.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrent) fav.primary.copy(alpha = 0.22f) else theme.surfaceVariant,
                        border = BorderStroke(
                            if (isCurrent) 1.5.dp else 1.dp,
                            if (isCurrent) fav.primary else theme.surfaceBorder
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onQuickSelectTheme(fav) }
                            .testTag("drawer_quick_theme_${fav.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(fav.primary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = fav.badgeCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isCurrent) fav.primary else theme.onSurface
                            )
                        }
                    }
                }
            }

            Divider(
                color = theme.surfaceBorder,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // User Identity & Settings footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = theme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "N",
                            fontWeight = FontWeight.Bold,
                            color = theme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "nav993671",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.onSurface
                    )
                    Text(
                        text = "Local Host 127.0.0.1:7000",
                        fontSize = 10.sp,
                        color = theme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = {
                        onSelectTab(WorkspaceTab.THEME_STUDIO)
                        onCloseDrawer()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = theme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
