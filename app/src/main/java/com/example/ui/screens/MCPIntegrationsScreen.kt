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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MCPServer
import com.example.theme.ChatOSCard
import com.example.theme.LocalChatOSTheme
import com.example.theme.StatusPulseIndicator

@Composable
fun MCPIntegrationsScreen(
    mcpServers: List<MCPServer>,
    onToggleServer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
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
                            color = theme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Hub,
                                    contentDescription = "MCP Protocol",
                                    tint = theme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Model Context Protocol (MCP)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                            Text(
                                text = "Stdio & HTTP-SSE Tool Servers for Autonomous Agent Dispatch",
                                fontSize = 11.sp,
                                color = theme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Registered Tool Servers (${mcpServers.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onSurface
            )
        }

        items(mcpServers) { server ->
            val isConnected = server.status == "CONNECTED"
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = theme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, if (isConnected) theme.primary.copy(alpha = 0.4f) else theme.surfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatusPulseIndicator(
                            isActive = isConnected,
                            activeColor = Color(0xFF10B981),
                            inactiveColor = Color.Gray
                        )
                        Text(
                            text = server.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = theme.surface
                        ) {
                            Text(
                                text = server.protocol.uppercase(),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = theme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "${server.latencyMs}ms latency",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onToggleServer(server.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConnected) Color(0xFF064E3B) else theme.surfaceVariant,
                                contentColor = if (isConnected) Color(0xFFA7F3D0) else theme.onSurface
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text(if (isConnected) "Connected" else "Connect", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "URI: ${server.uri}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = theme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Exposed Tools
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tools:",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.primary
                        )
                        server.toolsProvided.forEach { tool ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = theme.codeBackground
                            ) {
                                Text(
                                    text = tool,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = theme.terminalPrompt,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
