package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.model.AIModel
import com.example.model.ChatMessage
import com.example.model.MessageRole
import com.example.model.ToolCall
import com.example.model.ToolStatus
import com.example.theme.ChatOSCard
import com.example.theme.ChatOSTag
import com.example.theme.LocalChatOSTheme
import com.example.theme.StatusPulseIndicator
import kotlinx.coroutines.launch

@Composable
fun ChatAndAgentsScreen(
    messages: List<ChatMessage>,
    selectedModel: AIModel,
    isAgentMode: Boolean,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onApproveTool: (messageId: String, toolId: String) -> Unit,
    onRejectTool: (messageId: String, toolId: String) -> Unit,
    onToggleAgentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // Auto-scroll on new message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Main Message Stream
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                // Empty Hero View matching Odysseus
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = theme.surfaceVariant,
                        border = BorderStroke(1.dp, theme.surfaceBorder),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Odysseus",
                                tint = theme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Odysseus",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.primary
                    )

                    Text(
                        text = "Yours for the voyage.",
                        fontSize = 13.sp,
                        color = theme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Quick Starter Prompt Chips
                    val quickPrompts = listOf(
                        "🔍 Research latest local-first LLM architectures",
                        "⚡ Benchmark llama.cpp vs SGLang on Snapdragon",
                        "🛡️ Configure ChromaDB vector memory retrieval",
                        "🎨 Switch workspace theme to Terminal Pro"
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.widthIn(max = 480.dp)
                    ) {
                        quickPrompts.forEach { prompt ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = theme.surfaceVariant.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, theme.surfaceBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSendMessage(prompt.substring(3)) }
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 12.sp,
                                    color = theme.onSurface,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages, key = { it.id }) { message ->
                        ChatMessageItem(
                            message = message,
                            onApproveTool = { toolId -> onApproveTool(message.id, toolId) },
                            onRejectTool = { toolId -> onRejectTool(message.id, toolId) },
                            onCopyText = { text -> clipboardManager.setText(AnnotatedString(text)) }
                        )
                    }

                    if (isGenerating) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = theme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, theme.surfaceBorder),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = theme.primary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${selectedModel.name} is formulating response...",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Input Area (Matching Odysseus sleek dock input)
        Surface(
            color = theme.surface,
            border = BorderStroke(1.dp, theme.surfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Quick context chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAgentMode) theme.primary.copy(alpha = 0.2f) else theme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isAgentMode) theme.primary else theme.surfaceBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggleAgentMode() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAgentMode) Icons.Default.SmartToy else Icons.Default.ChatBubbleOutline,
                                contentDescription = "Mode",
                                tint = if (isAgentMode) theme.primary else theme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAgentMode) "Agent: ON" else "Chat: Direct",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAgentMode) theme.primary else theme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Using ${selectedModel.name}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = theme.onSurfaceVariant
                    )
                }

                // Input Box with Controls
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = theme.surfaceVariant,
                    border = BorderStroke(1.dp, theme.surfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { inputText += " /searxng " },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Insert Command",
                                tint = theme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (isAgentMode) "Message Odysseus agent (shell, web, files enabled)..." else "Message ${selectedModel.name}...",
                                    fontSize = 13.sp,
                                    color = theme.onSurfaceVariant
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
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field")
                        )

                        // Send Button
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isGenerating) {
                                    val textToSend = inputText
                                    inputText = ""
                                    onSendMessage(textToSend)
                                }
                            },
                            enabled = inputText.isNotBlank() && !isGenerating,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank()) theme.primary else theme.surfaceBorder)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) theme.onPrimary else theme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onApproveTool: (toolId: String) -> Unit,
    onRejectTool: (toolId: String) -> Unit,
    onCopyText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    val isUser = message.role == MessageRole.USER

    Column(
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        // Message Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "You" else message.modelName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isUser) theme.secondary else theme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = message.timestamp,
                fontSize = 10.sp,
                color = theme.onSurfaceVariant
            )
        }

        // Message Bubble Card
        ChatOSCard(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 14.dp
            ),
            backgroundColor = if (isUser) theme.primaryContainer.copy(alpha = 0.35f) else theme.surfaceVariant.copy(alpha = 0.7f),
            borderColor = if (isUser) theme.primary.copy(alpha = 0.5f) else theme.surfaceBorder,
            glowEffect = !isUser,
            modifier = Modifier.widthIn(max = 520.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Main Text / Code Block Renderer
                RenderFormattedContent(content = message.text, onCopy = onCopyText)

                // Tool Calls with Approval Gate
                if (message.toolCalls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TOOL CALL APPROVAL GATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    message.toolCalls.forEach { tool ->
                        ToolCallGateCard(
                            tool = tool,
                            onApprove = { onApproveTool(tool.id) },
                            onReject = { onRejectTool(tool.id) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCallGateCard(
    tool: ToolCall,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = theme.surface,
        border = BorderStroke(
            1.dp,
            when (tool.status) {
                ToolStatus.PENDING_APPROVAL -> theme.primary
                ToolStatus.RUNNING -> theme.secondary
                ToolStatus.COMPLETED -> Color(0xFF10B981)
                ToolStatus.REJECTED -> Color(0xFFEF4444)
            }
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = when (tool.toolName) {
                        "searxng_web_search" -> Icons.Default.TravelExplore
                        "filesystem_workspace" -> Icons.Default.Folder
                        "chromadb_vector_memory" -> Icons.Default.Psychology
                        else -> Icons.Default.Terminal
                    },
                    contentDescription = null,
                    tint = theme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tool.toolName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = theme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (tool.status) {
                        ToolStatus.PENDING_APPROVAL -> theme.primaryContainer
                        ToolStatus.RUNNING -> theme.secondaryContainer
                        ToolStatus.COMPLETED -> Color(0xFF064E3B)
                        ToolStatus.REJECTED -> Color(0xFF7F1D1D)
                    }
                ) {
                    Text(
                        text = tool.status.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = when (tool.status) {
                            ToolStatus.PENDING_APPROVAL -> theme.onPrimaryContainer
                            ToolStatus.RUNNING -> theme.onSecondaryContainer
                            ToolStatus.COMPLETED -> Color(0xFFA7F3D0)
                            ToolStatus.REJECTED -> Color(0xFFFECACA)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Command / Arguments
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = theme.codeBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$ ${tool.command}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = theme.terminalPrompt,
                    modifier = Modifier.padding(6.dp)
                )
            }

            // Output if executed
            if (tool.output != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = theme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tool.output,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = theme.onSurface,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            // Action Buttons when pending approval
            if (tool.status == ToolStatus.PENDING_APPROVAL) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Deny", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.onPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Authorize & Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RenderFormattedContent(
    content: String,
    onCopy: (String) -> Unit
) {
    val theme = LocalChatOSTheme.current
    val parts = content.split("```")

    parts.forEachIndexed { index, part ->
        if (index % 2 == 1) {
            // Code block
            val lines = part.trim().lines()
            val language = lines.firstOrNull()?.trim() ?: "code"
            val codeBody = lines.drop(1).joinToString("\n").ifEmpty { part }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = theme.codeBackground,
                border = BorderStroke(1.dp, theme.surfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(theme.surfaceVariant.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = language.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = theme.primary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Copy",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onCopy(codeBody) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = codeBody,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = theme.terminalPrompt,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        } else {
            // Normal text
            if (part.isNotBlank()) {
                Text(
                    text = part.trim(),
                    fontSize = 13.sp,
                    color = theme.onSurface,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
