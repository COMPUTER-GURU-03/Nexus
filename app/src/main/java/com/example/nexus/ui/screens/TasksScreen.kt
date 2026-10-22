package com.example.nexus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus.model.PrioritizedTask
import com.example.nexus.model.Project
import com.example.nexus.model.TaskPriority
import com.example.nexus.viewmodel.TaskViewTab
import com.example.ui.theme.NexusBackground
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusError
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusIndigoLight
import com.example.ui.theme.NexusIndigoSoft
import com.example.ui.theme.NexusSuccess
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface2
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextMuted
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusWarning

@Composable
fun TasksScreen(
    tasks: List<PrioritizedTask>,
    projects: List<Project>,
    currentTab: TaskViewTab,
    searchQuery: String,
    onTabSelected: (TaskViewTab) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectTaskForBreakdown: (PrioritizedTask) -> Unit,
    onToggleTask: (taskId: Long, currentCompleted: Boolean) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit,
    onDeleteProject: (Project) -> Unit,
    onOpenCreateTask: () -> Unit,
    onOpenCreateProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overdueCount = tasks.count { !it.task.isCompleted && it.task.dueDate != null && it.task.dueDate < System.currentTimeMillis() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground),
        containerColor = NexusBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenCreateTask,
                containerColor = NexusIndigo,
                contentColor = NexusTextPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("tasks_add_fab")
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Task", tint = NexusTextPrimary)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("tasks_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Title & Project Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TASKS & PROJECTS",
                            color = NexusIndigoLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Directive Queue",
                            color = NexusTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onOpenCreateProject,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexusSurface2,
                            contentColor = NexusTextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexusBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("tasks_add_project_btn")
                    ) {
                        Icon(Icons.Outlined.Folder, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Project", color = NexusTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Search Bar (Surface 3 with Subtle Border)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Search tasks and projects...", color = NexusTextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = NexusTextSecondary, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tasks_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedBorderColor = NexusIndigo,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusSurface3,
                        unfocusedContainerColor = NexusSurface3
                    ),
                    singleLine = true
                )
            }

            // Views: Segmented Tab Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexusSurface3)
                        .border(1.dp, NexusBorder, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TaskViewTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusIndigo else Color.Transparent)
                                .clickable { onTabSelected(tab) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.label,
                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Overdue Warning Banner (Functional Status: Crimson)
            if (overdueCount > 0 && currentTab != TaskViewTab.COMPLETED) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexusSurface1)
                            .border(1.dp, NexusError.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Warning, contentDescription = null, tint = NexusError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$overdueCount task(s) past scheduled deadline",
                                color = NexusError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Projects List if in Projects Tab (Surface 1)
            if (currentTab == TaskViewTab.PROJECTS) {
                if (projects.isEmpty()) {
                    item {
                        EmptyStateCard("No projects created yet.")
                    }
                } else {
                    items(projects, key = { it.id }) { proj ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(NexusSurface1)
                                .border(1.dp, NexusBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = proj.name,
                                        color = NexusTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (proj.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = proj.description,
                                            color = NexusTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                IconButton(onClick = { onDeleteProject(proj) }) {
                                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete Project", tint = NexusTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                // Task Items (Surface 1)
                if (tasks.isEmpty()) {
                    item {
                        EmptyStateCard("No tasks in this view.")
                    }
                } else {
                    items(tasks, key = { it.task.id }) { item ->
                        TaskCardItem(
                            item = item,
                            onToggle = { onToggleTask(item.task.id, item.task.isCompleted) },
                            onExplain = { onSelectTaskForBreakdown(item) },
                            onDelete = { onDeleteTask(item.task.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TaskCardItem(
    item: PrioritizedTask,
    onToggle: () -> Unit,
    onExplain: () -> Unit,
    onDelete: () -> Unit
) {
    val task = item.task
    val b = item.breakdown
    val isOverdue = !task.isCompleted && task.dueDate != null && task.dueDate < System.currentTimeMillis()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NexusSurface1)
            .border(
                1.dp,
                if (isOverdue) NexusError.copy(alpha = 0.5f) else NexusBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
            .testTag("task_item_${task.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NexusIndigoSoft)
                            .border(1.dp, NexusIndigo.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#${b.rank}",
                            color = NexusIndigoLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    val (badgeBg, badgeBorder, badgeText) = when (task.priority) {
                        TaskPriority.CRITICAL -> Triple(NexusError.copy(alpha = 0.15f), NexusError, NexusError)
                        TaskPriority.HIGH -> Triple(NexusWarning.copy(alpha = 0.15f), NexusWarning, NexusWarning)
                        else -> Triple(NexusSurface2, NexusBorder, NexusTextSecondary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .border(1.dp, badgeBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority.label.uppercase(),
                            color = badgeText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${String.format("%.1f", b.totalScore)} pts",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )

                    if (isOverdue) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OVERDUE",
                            color = NexusError,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onExplain, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Info, contentDescription = "Explain", tint = NexusIndigoLight, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete", tint = NexusTextMuted, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = "Toggle Complete",
                            tint = if (task.isCompleted) NexusSuccess else NexusTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                color = if (task.isCompleted) NexusTextMuted else NexusTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    color = NexusTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.projectName != null) {
                        Text(
                            text = item.projectName,
                            color = NexusIndigoLight,
                            fontSize = 11.sp
                        )
                        Text("•", color = NexusBorder, fontSize = 11.sp)
                    }

                    Text(
                        text = "${task.estimatedMinutes}m",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )

                    Text("•", color = NexusBorder, fontSize = 11.sp)

                    Text(
                        text = task.energyRequired.label,
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (task.scheduledTimeSlot != null) {
                    Text(
                        text = task.scheduledTimeSlot,
                        color = NexusIndigoLight,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NexusSurface1)
            .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = NexusTextSecondary,
            fontSize = 13.sp
        )
    }
}
