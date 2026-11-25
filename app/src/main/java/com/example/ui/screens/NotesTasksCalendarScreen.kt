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
import com.example.model.CalendarEventItem
import com.example.model.NoteItem
import com.example.model.TaskItem
import com.example.theme.ChatOSCard
import com.example.theme.LocalChatOSTheme

@Composable
fun NotesTasksCalendarScreen(
    notes: List<NoteItem>,
    tasks: List<TaskItem>,
    calendarEvents: List<CalendarEventItem>,
    onToggleTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalChatOSTheme.current
    var subTab by remember { mutableStateOf(0) } // 0: Notes, 1: Tasks, 2: Calendar

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(14.dp)
    ) {
        // Sub-Tab Switcher
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
                text = { Text("Notes (${notes.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = { Text("Agent Tasks (${tasks.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = subTab == 2,
                onClick = { subTab = 2 },
                text = { Text("Calendar (${calendarEvents.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (subTab) {
            0 -> {
                // Notes List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes) { note ->
                        ChatOSCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = note.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.primary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = note.date,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = note.content,
                                    fontSize = 12.sp,
                                    color = theme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    note.tags.forEach { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = theme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "#$tag",
                                                fontSize = 9.sp,
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

            1 -> {
                // Tasks List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tasks) { task ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = theme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, theme.surfaceBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleTask(task.id) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { onToggleTask(task.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = theme.primary,
                                        uncheckedColor = theme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (task.isCompleted) theme.onSurfaceVariant else theme.onSurface
                                    )
                                    if (task.assignedAgent != null) {
                                        Text(
                                            text = "Assigned: ${task.assignedAgent} • Due ${task.dueDate}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = theme.primary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (task.priority) {
                                        "HIGH" -> Color(0xFF7F1D1D)
                                        "MEDIUM" -> Color(0xFF78350F)
                                        else -> theme.surface
                                    }
                                ) {
                                    Text(
                                        text = task.priority,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (task.priority) {
                                            "HIGH" -> Color(0xFFFECACA)
                                            "MEDIUM" -> Color(0xFFFDE68A)
                                            else -> theme.onSurfaceVariant
                                        },
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Calendar Events
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(calendarEvents) { event ->
                        ChatOSCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = if (event.isAgentScheduled) Icons.Default.SmartToy else Icons.Default.Event,
                                        contentDescription = null,
                                        tint = if (event.isAgentScheduled) theme.primary else theme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = event.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.onSurface
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = event.time,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = theme.primary
                                    )
                                }

                                if (event.agentTaskDetails != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚡ ${event.agentTaskDetails}",
                                        fontSize = 11.sp,
                                        color = theme.onSurfaceVariant
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
