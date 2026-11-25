package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeepResearchSession
import com.example.theme.ChatOSCard
import com.example.theme.ChatOSTag
import com.example.theme.LocalChatOSTheme

@Composable
fun DeepResearchScreen(
    session: DeepResearchSession,
    onStartResearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var queryText by remember { mutableStateOf(session.topic) }
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Hero Header
        item {
            ChatOSCard(
                glowEffect = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TravelExplore,
                                    contentDescription = "Deep Research",
                                    tint = theme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Deep Research Engine",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                            Text(
                                text = "SearXNG Multi-Source Crawl & Comprehensive Report Generation",
                                fontSize = 11.sp,
                                color = theme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Research Query Input
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = theme.surfaceVariant,
                        border = BorderStroke(1.dp, theme.surfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            TextField(
                                value = queryText,
                                onValueChange = { queryText = it },
                                placeholder = {
                                    Text("Enter research topic or hypothesis...", fontSize = 13.sp, color = theme.onSurfaceVariant)
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = theme.onSurface,
                                    unfocusedTextColor = theme.onSurface
                                ),
                                modifier = Modifier.weight(1f).testTag("deep_research_input")
                            )

                            Button(
                                onClick = { onStartResearch(queryText) },
                                enabled = !session.isRunning && queryText.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.onPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("start_research_button")
                            ) {
                                if (session.isRunning) {
                                    CircularProgressIndicator(
                                        color = theme.onPrimary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text("Research", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4-Step Pipeline Status
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = theme.surface,
                border = BorderStroke(1.dp, theme.surfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RESEARCH EXECUTION PIPELINE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.primary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    val steps = listOf(
                        "1. Query Deconstruction",
                        "2. SearXNG Web Federation",
                        "3. Evidence Extraction",
                        "4. Synthesis & Citations"
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        steps.forEachIndexed { index, stepName ->
                            val isCompleted = session.stepIndex > index + 1
                            val isCurrent = session.stepIndex == index + 1
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCompleted -> Color(0xFF10B981)
                                                isCurrent -> theme.primary
                                                else -> theme.surfaceBorder
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) theme.onPrimary else theme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stepName,
                                    fontSize = 9.sp,
                                    color = if (isCurrent) theme.primary else theme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    if (session.isRunning || session.statusMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = theme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = session.statusMessage,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = theme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sources Discovered
        if (session.sources.isNotEmpty()) {
            item {
                Text(
                    text = "Discovered Sources (${session.sources.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.onSurface
                )
            }

            items(session.sources) { source ->
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
                                text = source.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF064E3B)
                            ) {
                                Text(
                                    text = "${source.credibilityScore}% Confidence",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = source.url,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = source.snippet,
                            fontSize = 11.sp,
                            color = theme.onSurface
                        )
                    }
                }
            }
        }

        // Synthesized Report
        if (session.synthesisReport.isNotBlank()) {
            item {
                ChatOSCard(
                    glowEffect = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Synthesis Report",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(session.synthesisReport)) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Report",
                                    tint = theme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = session.synthesisReport,
                            fontSize = 12.sp,
                            color = theme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
