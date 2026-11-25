package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AIModel
import com.example.model.HardwareScanResult
import com.example.theme.ChatOSCard
import com.example.theme.LocalChatOSTheme
import com.example.theme.StatusPulseIndicator

@Composable
fun CookbookScreen(
    hardwareScan: HardwareScanResult,
    availableModels: List<AIModel>,
    selectedModel: AIModel,
    onSelectModel: (AIModel) -> Unit,
    activeEngine: String,
    tokensPerSec: Float,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var selectedServingEngine by remember { mutableStateOf(activeEngine) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Hardware Scan Telemetry Dashboard
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.secondaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = "Hardware Scanner",
                                    tint = theme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Local Hardware Telemetry",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                            Text(
                                text = "${hardwareScan.cpuModel} • ${hardwareScan.npuTops} TOPS NPU",
                                fontSize = 11.sp,
                                color = theme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = theme.surfaceVariant
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                StatusPulseIndicator(isActive = true, activeColor = theme.secondary)
                                Text(
                                    text = "${hardwareScan.temperatureCelsius}°C",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = theme.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hardware Specs Metrics Grid
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MetricBlock(
                            label = "RAM AVAILABLE",
                            value = "${hardwareScan.availableRamGB} / ${hardwareScan.totalRamGB} GB",
                            modifier = Modifier.weight(1f)
                        )
                        MetricBlock(
                            label = "GPU VRAM",
                            value = "${hardwareScan.vramGB} GB Vulkan",
                            modifier = Modifier.weight(1f)
                        )
                        MetricBlock(
                            label = "SERVING SPEED",
                            value = "$tokensPerSec tok/s",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Serving Engines Switcher
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = theme.surface,
                border = BorderStroke(1.dp, theme.surfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LOCAL SERVING RUNTIME ENGINE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = theme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val engines = listOf(
                        Triple("llama.cpp", "GGUF Quantization • CPU/GPU Offloading", Icons.Default.Terminal),
                        Triple("vLLM", "PagedAttention • High-Concurrency Serving", Icons.Default.Speed),
                        Triple("SGLang", "RadixAttention • Ultra-Fast Structured Output", Icons.Default.Bolt)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        engines.forEach { (engineName, subtitle, icon) ->
                            val isSelected = selectedServingEngine == engineName
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) theme.primary.copy(alpha = 0.15f) else theme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) theme.primary else theme.surfaceBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedServingEngine = engineName }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) theme.primary else theme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = engineName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) theme.primary else theme.onSurface
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 9.sp,
                                        color = theme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Model Catalog & Hardware Fit Scoring
        item {
            Text(
                text = "Model Fit Catalog & Quantizations",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onSurface
            )
        }

        items(availableModels) { model ->
            val isSelected = model.id == selectedModel.id
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) theme.primaryContainer.copy(alpha = 0.35f) else theme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, if (isSelected) theme.primary else theme.surfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectModel(model) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) theme.primary else theme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
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

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${model.provider} • ${model.parameters} • Context: ${model.contextWindow}",
                            fontSize = 11.sp,
                            color = theme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (model.fitStatus.contains("Ideal")) Color(0xFF064E3B) else Color(0xFF78350F)
                        ) {
                            Text(
                                text = model.fitStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (model.fitStatus.contains("Ideal")) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Engine: ${model.defaultServing}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = theme.surfaceVariant,
        border = BorderStroke(1.dp, theme.surfaceBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = theme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primary
            )
        }
    }
}
