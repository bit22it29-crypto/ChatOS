package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgentSkill
import com.example.model.ChromaMemoryItem
import com.example.theme.ChatOSCard
import com.example.theme.LocalChatOSTheme

@Composable
fun MemorySkillsScreen(
    chromaMemories: List<ChromaMemoryItem>,
    agentSkills: List<AgentSkill>,
    onToggleSkill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var subTab by remember { mutableStateOf(0) } // 0: ChromaDB Brain, 1: Agent Skills

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(14.dp)
    ) {
        TabRow(
            selectedTabIndex = subTab,
            containerColor = theme.surfaceVariant,
            contentColor = theme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .padding(bottom = 12.dp)
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = { Text("ChromaDB Vector Brain (${chromaMemories.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = { Text("Agent Skills (${agentSkills.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (subTab) {
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chromaMemories) { memory ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = theme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, theme.surfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = memory.memoryKey,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.primary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = theme.surface
                                    ) {
                                        Text(
                                            text = "dist: ${memory.similarityDistance}",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = theme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = memory.content,
                                    fontSize = 11.sp,
                                    color = theme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = theme.primaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = memory.category,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = theme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Synced: ${memory.timestamp}",
                                        fontSize = 9.sp,
                                        color = theme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(agentSkills) { skill ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = theme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, if (skill.isInstalled) theme.primary.copy(alpha = 0.5f) else theme.surfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = skill.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = skill.version,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = theme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = skill.description,
                                        fontSize = 11.sp,
                                        color = theme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$ ${skill.command}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.primary
                                    )
                                }

                                Button(
                                    onClick = { onToggleSkill(skill.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (skill.isInstalled) Color(0xFF064E3B) else theme.primary,
                                        contentColor = if (skill.isInstalled) Color(0xFFA7F3D0) else theme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (skill.isInstalled) "Installed" else "Install",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
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
