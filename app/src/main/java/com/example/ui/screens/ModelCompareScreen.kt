package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.CompareEvaluation
import com.example.theme.ChatOSCard
import com.example.theme.LocalChatOSTheme

@Composable
fun ModelCompareScreen(
    evaluation: CompareEvaluation,
    onCastVote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Header
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
                                    imageVector = Icons.Default.CompareArrows,
                                    contentDescription = "Model Compare",
                                    tint = theme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Model Arena & Blind Compare",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                            Text(
                                text = "Side-by-side synthesis with latency and token throughput benchmark",
                                fontSize = 11.sp,
                                color = theme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.surfaceVariant,
                        border = BorderStroke(1.dp, theme.surfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Test Prompt: \"${evaluation.prompt}\"",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Model A vs Model B Responses
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Model A
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.surface,
                        border = BorderStroke(1.dp, if (evaluation.userVote == "A") theme.primary else theme.surfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (evaluation.isRevealed) evaluation.modelA.name else "Model Alpha (Blind)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.primary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${evaluation.latencyMsA}ms • ${evaluation.tokPerSecA} tok/s",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = theme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            RenderFormattedContent(content = evaluation.responseA, onCopy = {})
                        }
                    }
                }

                // Model B
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.surface,
                        border = BorderStroke(1.dp, if (evaluation.userVote == "B") theme.secondary else theme.surfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (evaluation.isRevealed) evaluation.modelB.name else "Model Beta (Blind)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.secondary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${evaluation.latencyMsB}ms • ${evaluation.tokPerSecB} tok/s",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = theme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            RenderFormattedContent(content = evaluation.responseB, onCopy = {})
                        }
                    }
                }
            }
        }

        // Voting & Reveal Arena Bar
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = theme.surface,
                border = BorderStroke(1.dp, theme.surfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = if (evaluation.isRevealed) "Evaluation Completed" else "Cast Your Blind Judgment",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onCastVote("A") },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.onPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("vote_model_a_button")
                        ) {
                            Text("Model A is Better", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onCastVote("TIE") },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.surfaceVariant, contentColor = theme.onSurface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("vote_tie_button")
                        ) {
                            Text("Tie / Both Good", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onCastVote("B") },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.secondary, contentColor = theme.onSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("vote_model_b_button")
                        ) {
                            Text("Model B is Better", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (evaluation.isRevealed) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Revealed: Model A was [${evaluation.modelA.name}] and Model B was [${evaluation.modelB.name}].",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.primary
                        )
                    }
                }
            }
        }
    }
}
