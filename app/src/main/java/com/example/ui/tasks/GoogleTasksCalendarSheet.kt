package com.example.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskItemEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TaskFilter {
    ALL,
    GOOGLE_TASKS,
    GOOGLE_CALENDAR,
    DUE_TODAY,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleTasksCalendarSheet(
    tasks: List<TaskItemEntity>,
    isSyncing: Boolean,
    syncStatusMessage: String,
    onDismiss: () -> Unit,
    onSyncNow: () -> Unit,
    onAddTaskClicked: () -> Unit,
    onToggleTaskCompleted: (TaskItemEntity) -> Unit,
    onDeleteTask: (String) -> Unit,
    onAskPetAboutTask: (TaskItemEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentFilter by remember { mutableStateOf(TaskFilter.ALL) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFDFCF4),
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("google_tasks_calendar_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Sync",
                            tint = Color(0xFF556500),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Sync & Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1C17)
                        )
                    }
                    Text(
                        text = "Connected with Google Tasks & Calendar",
                        fontSize = 12.sp,
                        color = Color(0xFF5C6146)
                    )
                }

                Row {
                    Button(
                        onClick = onSyncNow,
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF556500)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("sync_google_now_btn")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Syncing...", fontSize = 12.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (syncStatusMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Status: $syncStatusMessage",
                    fontSize = 11.sp,
                    color = Color(0xFF386663),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Add task button
            Button(
                onClick = onAddTaskClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_new_task_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF386663)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add New Deadline / Task", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = currentFilter == TaskFilter.ALL,
                        onClick = { currentFilter = TaskFilter.ALL },
                        label = { Text("All (${tasks.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF556500),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == TaskFilter.GOOGLE_TASKS,
                        onClick = { currentFilter = TaskFilter.GOOGLE_TASKS },
                        label = { Text("Google Tasks", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF386663),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == TaskFilter.GOOGLE_CALENDAR,
                        onClick = { currentFilter = TaskFilter.GOOGLE_CALENDAR },
                        label = { Text("Calendar Events", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF5C6146),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == TaskFilter.DUE_TODAY,
                        onClick = { currentFilter = TaskFilter.DUE_TODAY },
                        label = { Text("Due Soon", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF9E472A),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == TaskFilter.COMPLETED,
                        onClick = { currentFilter = TaskFilter.COMPLETED },
                        label = { Text("Completed", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF76786C),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filtered tasks list
            val filteredTasks = remember(tasks, currentFilter) {
                val now = System.currentTimeMillis()
                when (currentFilter) {
                    TaskFilter.ALL -> tasks
                    TaskFilter.GOOGLE_TASKS -> tasks.filter { it.source == TaskSource.GOOGLE_TASKS }
                    TaskFilter.GOOGLE_CALENDAR -> tasks.filter { it.source == TaskSource.GOOGLE_CALENDAR }
                    TaskFilter.DUE_TODAY -> tasks.filter {
                        !it.isCompleted && it.dueTimestamp != null && it.dueTimestamp - now <= 24 * 3600 * 1000L
                    }
                    TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
                }
            }

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks found in this section! (＾▽＾) Enjoy your free time!",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggleCompleted = { onToggleTaskCompleted(task) },
                            onDelete = { onDeleteTask(task.id) },
                            onAskPet = { onAskPetAboutTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskItemEntity,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
    onAskPet: () -> Unit
) {
    val priorityColor = when (task.priority) {
        TaskPriority.URGENT -> Color(0xFF9E472A)
        TaskPriority.HIGH -> Color(0xFFD48B47)
        TaskPriority.MEDIUM -> Color(0xFF556500)
        TaskPriority.LOW -> Color(0xFF5C6146)
    }

    val sourceBadge = when (task.source) {
        TaskSource.GOOGLE_TASKS -> "Google Tasks" to Color(0xFF386663)
        TaskSource.GOOGLE_CALENDAR -> "Calendar Event" to Color(0xFF556500)
        TaskSource.MANUAL -> "Local Task" to Color(0xFF5C6146)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Color(0xFFF5F4EB) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 1.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (task.isCompleted) Color(0xFFDDDBCF) else priorityColor.copy(alpha = 0.4f),
                RoundedCornerShape(16.dp)
            )
            .testTag("task_card_${task.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Checkbox
            IconButton(
                onClick = onToggleCompleted,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("toggle_task_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) Color(0xFF556500) else Color(0xFF76786C),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Task info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Source badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(sourceBadge.second.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sourceBadge.first,
                            color = sourceBadge.second,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Priority badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(priorityColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority.name,
                            color = priorityColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (task.isCompleted) Color(0xFF76786C) else Color(0xFF1B1C17),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.notes.isNotBlank()) {
                    Text(
                        text = task.notes,
                        fontSize = 12.sp,
                        color = Color(0xFF5C6146),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (task.calendarEventLocation.isNotBlank()) {
                    Text(
                        text = "📍 ${task.calendarEventLocation}",
                        fontSize = 11.sp,
                        color = Color(0xFF386663)
                    )
                }

                if (task.dueTimestamp != null) {
                    val sdf = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault())
                    val dueStr = sdf.format(Date(task.dueTimestamp))
                    Text(
                        text = "⏰ Due: $dueStr",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.dueTimestamp < System.currentTimeMillis() && !task.isCompleted) Color(0xFF9E472A) else Color(0xFF556500)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Pet Reminder trigger button
                IconButton(
                    onClick = onAskPet,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Pet Reminder Dialogue",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
