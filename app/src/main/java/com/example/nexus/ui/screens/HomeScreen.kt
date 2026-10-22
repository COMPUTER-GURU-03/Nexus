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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.nexus.engine.DailyPlanningResult
import com.example.nexus.model.HabitWithStatus
import com.example.nexus.model.PrioritizedTask
import com.example.nexus.model.ProductivityMetrics
import com.example.nexus.model.ZmwFinanceSummary
import com.example.nexus.model.ZmwFormatter
import com.example.nexus.viewmodel.NexusDestination
import com.example.ui.theme.NexusBackground
import com.example.ui.theme.NexusBorder
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    productivityMetrics: ProductivityMetrics,
    prioritizedTasks: List<PrioritizedTask>,
    planningResult: DailyPlanningResult,
    habits: List<HabitWithStatus>,
    financeSummary: ZmwFinanceSummary,
    currencyPrefix: String,
    onNavigate: (NexusDestination) -> Unit,
    onSelectTaskForBreakdown: (PrioritizedTask) -> Unit,
    onToggleTask: (taskId: Long, currentCompleted: Boolean) -> Unit,
    onToggleHabit: (habitId: Long, currentCount: Int, target: Int) -> Unit,
    onQuickAddTask: () -> Unit,
    onQuickAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topTask = prioritizedTasks.firstOrNull { !it.task.isCompleted }
    val todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH))

    val completedTasksToday = prioritizedTasks.count { it.task.isCompleted }
    val totalTasksToday = prioritizedTasks.size
    val taskProgress = if (totalTasksToday > 0) completedTasksToday.toFloat() / totalTasksToday else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground)
            .padding(horizontal = 20.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Greeting & Date Header
            Column {
                Text(
                    text = todayFormatted.uppercase(),
                    color = NexusIndigoLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Welcome, $userName",
                    color = NexusTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onQuickAddTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusIndigo,
                        contentColor = NexusTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("home_quick_add_task_btn")
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = NexusTextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Task", color = NexusTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onQuickAddTransaction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusSurface2,
                        contentColor = NexusTextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexusBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("home_quick_add_finance_btn")
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = NexusTextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Expense", color = NexusTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Today's Progress Card (Surface 1)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NexusSurface1)
                    .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Completion",
                            color = NexusTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$completedTasksToday of $totalTasksToday tasks",
                            color = NexusTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { taskProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NexusIndigo,
                        trackColor = NexusSurface3,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Planning Capacity: ${planningResult.allocatedMinutes}m / ${planningResult.totalAvailableMinutes}m (${String.format("%.0f", planningResult.utilizationPercentage)}%)",
                            color = NexusTextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${productivityMetrics.openTasksCount} pending",
                            color = NexusIndigoLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Recommended Next Task (Explainable Prioritization Card - Surface 1)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECOMMENDED NEXT ACTION",
                        color = NexusTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "View All",
                        color = NexusIndigoLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onNavigate(NexusDestination.TASKS) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (topTask != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NexusSurface1)
                            .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                            .testTag("home_recommended_task_card")
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
                                            .border(1.dp, NexusIndigo, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Rank #${topTask.breakdown.rank}",
                                            color = NexusIndigoLight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${String.format("%.1f", topTask.breakdown.totalScore)} pts",
                                        color = NexusTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NexusSurface2)
                                        .border(1.dp, NexusBorder, RoundedCornerShape(6.dp))
                                        .clickable { onSelectTaskForBreakdown(topTask) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Info, contentDescription = "Explain", tint = NexusIndigoLight, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Why?", color = NexusIndigoLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = topTask.task.title,
                                color = NexusTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (topTask.task.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = topTask.task.description,
                                    color = NexusTextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${topTask.task.estimatedMinutes} min • ${topTask.task.priority.label} Priority",
                                    color = NexusTextSecondary,
                                    fontSize = 11.sp
                                )

                                Button(
                                    onClick = { onToggleTask(topTask.task.id, topTask.task.isCompleted) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NexusIndigo,
                                        contentColor = NexusTextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Mark Complete", color = NexusTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NexusSurface1)
                            .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending tasks. You are all caught up.",
                            color = NexusTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Proposed Daily Schedule
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROPOSED SCHEDULE",
                        color = NexusTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${planningResult.scheduledBlocks.count { !it.isBreak }} scheduled",
                        color = NexusIndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (planningResult.scheduledBlocks.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        planningResult.scheduledBlocks.take(3).forEach { block ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (block.isBreak) NexusSurface2 else NexusSurface1)
                                    .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Schedule,
                                            contentDescription = null,
                                            tint = if (block.isBreak) NexusTextMuted else NexusIndigo,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = block.label,
                                                color = if (block.isBreak) NexusTextSecondary else NexusTextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = block.timeRange,
                                                color = NexusTextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (planningResult.unscheduledTasks.isNotEmpty()) {
                            Text(
                                text = "• ${planningResult.unscheduledTasks.size} tasks unscheduled (exceeds ${planningResult.totalAvailableMinutes}m capacity)",
                                color = NexusTextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Habit Progress Preview
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HABITS & ROUTINES",
                        color = NexusTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Manage",
                        color = NexusIndigoLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onNavigate(NexusDestination.TASKS) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(habits) { h ->
                        val isDone = h.completedToday
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(NexusSurface1)
                                .border(
                                    1.dp,
                                    if (isDone) NexusSuccess.copy(alpha = 0.6f) else NexusBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onToggleHabit(h.habit.id, if (isDone) h.habit.targetPerDay else 0, h.habit.targetPerDay)
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${h.currentStreak}d streak",
                                        color = if (isDone) NexusSuccess else NexusIndigoLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = "Toggle",
                                        tint = if (isDone) NexusSuccess else NexusTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = h.habit.title,
                                    color = NexusTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${h.habit.targetPerDay} ${h.habit.unit} daily",
                                    color = NexusTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Financial Summary Glance
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NexusSurface1)
                    .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                    .clickable { onNavigate(NexusDestination.FINANCE) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FINANCIAL BALANCE ($currencyPrefix)",
                            color = NexusTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ZmwFormatter.formatZmw(financeSummary.currentBalanceZmw, currencyPrefix),
                            color = NexusTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "View Finance",
                        tint = NexusIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
