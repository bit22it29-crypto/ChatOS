package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.DocumentTab
import com.example.theme.LocalChatOSTheme

@Composable
fun DocumentsEditorScreen(
    documents: List<DocumentTab>,
    activeDocId: String,
    onSelectDocument: (String) -> Unit,
    onUpdateContent: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    val activeDoc = documents.find { it.id == activeDocId } ?: documents.firstOrNull()
    var editorContent by remember(activeDoc?.content) { mutableStateOf(activeDoc?.content ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Document Tabs Bar
        Surface(
            color = theme.surface,
            border = BorderStroke(1.dp, theme.surfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(documents) { doc ->
                    val isSelected = doc.id == activeDocId
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) theme.primaryContainer.copy(alpha = 0.5f) else theme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) theme.primary else theme.surfaceBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectDocument(doc.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = when (doc.language) {
                                    "markdown" -> Icons.Default.Description
                                    "kotlin" -> Icons.Default.Code
                                    "csv" -> Icons.Default.TableChart
                                    else -> Icons.Default.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = if (isSelected) theme.primary else theme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = doc.filename,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) theme.primary else theme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // AI Copilot Action Strip
        Surface(
            color = theme.surfaceVariant,
            border = BorderStroke(1.dp, theme.surfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "AI COPILOT:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = theme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))

                CopilotChip(label = "Format Code", onClick = { /* AI Format */ })
                Spacer(modifier = Modifier.width(6.dp))
                CopilotChip(label = "Explain Logic", onClick = { /* AI Explain */ })
                Spacer(modifier = Modifier.width(6.dp))
                CopilotChip(label = "Generate Unit Tests", onClick = { /* AI Tests */ })

                Spacer(modifier = Modifier.weight(1f))

                // Stats
                val lines = editorContent.lines().size
                val words = editorContent.split("\\s+".toRegex()).size
                Text(
                    text = "$lines lines • $words words",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = theme.onSurfaceVariant
                )
            }
        }

        // Code / Document Editor Body
        TextField(
            value = editorContent,
            onValueChange = {
                editorContent = it
                activeDoc?.let { doc -> onUpdateContent(doc.id, it) }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = theme.codeBackground,
                unfocusedContainerColor = theme.codeBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = theme.terminalPrompt,
                unfocusedTextColor = theme.terminalPrompt
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .testTag("document_editor_field")
        )
    }
}

@Composable
fun CopilotChip(label: String, onClick: () -> Unit) {
    val theme = LocalChatOSTheme.current
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = theme.surface,
        border = BorderStroke(1.dp, theme.surfaceBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = theme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
