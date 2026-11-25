package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.EmailMessage
import com.example.theme.ChatOSCard
import com.example.theme.LocalChatOSTheme

@Composable
fun EmailScreen(
    emails: List<EmailMessage>,
    selectedEmailId: String?,
    onSelectEmail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    val activeEmail = emails.find { it.id == selectedEmailId } ?: emails.firstOrNull()
    val clipboardManager = LocalClipboardManager.current
    var isDraftingReply by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Inbox List
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight()
                .border(BorderStroke(1.dp, theme.surfaceBorder))
        ) {
            Surface(
                color = theme.surface,
                border = BorderStroke(1.dp, theme.surfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = "Inbox",
                        tint = theme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IMAP Inbox & Triage",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(emails) { email ->
                    val isSelected = email.id == selectedEmailId
                    Surface(
                        color = if (isSelected) theme.primaryContainer.copy(alpha = 0.4f) else theme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) BorderStroke(1.dp, theme.primary.copy(alpha = 0.5f)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectEmail(email.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = email.senderName,
                                    fontSize = 12.sp,
                                    fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) theme.primary else theme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = email.timestamp,
                                    fontSize = 10.sp,
                                    color = theme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = email.subject,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = theme.onSurface,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (email.category) {
                                    "URGENT" -> Color(0xFF7F1D1D)
                                    "ACTION_REQUIRED" -> Color(0xFF78350F)
                                    else -> theme.surface
                                }
                            ) {
                                Text(
                                    text = email.category,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = when (email.category) {
                                        "URGENT" -> Color(0xFFFECACA)
                                        "ACTION_REQUIRED" -> Color(0xFFFDE68A)
                                        else -> theme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Email Detail Reader & AI Summary
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            if (activeEmail != null) {
                ChatOSCard(
                    glowEffect = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = activeEmail.subject,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "From: ${activeEmail.senderName} <${activeEmail.senderAddress}>",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // AI Summary Callout
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.surfaceVariant,
                            border = BorderStroke(1.dp, theme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = theme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI TRIAGE SUMMARY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeEmail.aiSummary,
                                    fontSize = 11.sp,
                                    color = theme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = activeEmail.body,
                            fontSize = 12.sp,
                            color = theme.onSurface,
                            lineHeight = 18.sp
                        )

                        if (activeEmail.suggestedReply != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = theme.surfaceBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "AI Generated Draft Reply:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = theme.codeBackground,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = activeEmail.suggestedReply,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = theme.terminalPrompt,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(activeEmail.suggestedReply))
                                    isDraftingReply = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.onPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_reply_button")
                            ) {
                                Text(if (isDraftingReply) "Reply Copied!" else "Copy AI Draft to Mailer", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
