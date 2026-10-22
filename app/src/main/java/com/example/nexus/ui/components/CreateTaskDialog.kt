package com.example.nexus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.Project
import com.example.nexus.model.TaskPriority
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusError
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusIndigoLight
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface2
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun CreateTaskDialog(
    projects: List<Project>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        projectId: Long?,
        priority: TaskPriority,
        urgency: Int,
        impact: Int,
        estimatedMinutes: Int,
        energy: EnergyLevel,
        dueDate: Long?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedProjectId by remember { mutableStateOf<Long?>(projects.firstOrNull()?.id) }
    var priority by remember { mutableStateOf(TaskPriority.HIGH) }
    var urgencyRating by remember { mutableIntStateOf(4) }
    var impactRating by remember { mutableIntStateOf(4) }
    var estimatedMinutes by remember { mutableIntStateOf(30) }
    var energyRequired by remember { mutableStateOf(EnergyLevel.BALANCED) }
    var dueHoursAhead by remember { mutableIntStateOf(24) }

    var titleError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                .testTag("create_task_dialog"),
            color = NexusSurface1
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Task",
                        color = NexusTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close", tint = NexusTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Task Title *", color = NexusTextSecondary) },
                    isError = titleError,
                    supportingText = {
                        if (titleError) Text("Title is required", color = NexusError)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedBorderColor = NexusIndigo,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusSurface3,
                        unfocusedContainerColor = NexusSurface3
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)", color = NexusTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_desc_input"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedBorderColor = NexusIndigo,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusSurface3,
                        unfocusedContainerColor = NexusSurface3
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Project Selector
                if (projects.isNotEmpty()) {
                    Text(
                        text = "PROJECT",
                        color = NexusTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        projects.forEach { proj ->
                            val isSelected = selectedProjectId == proj.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NexusIndigo else NexusSurface2)
                                    .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedProjectId = proj.id }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = proj.name.take(16),
                                    color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Priority Tier
                Text(
                    text = "PRIORITY",
                    color = NexusTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskPriority.entries.forEach { p ->
                        val isSelected = priority == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusIndigo else NexusSurface2)
                                .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                .clickable { priority = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.label,
                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Urgency & Impact Sliders
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Urgency: $urgencyRating/5", color = NexusTextPrimary, fontSize = 11.sp)
                    Text("Impact: $impactRating/5", color = NexusIndigoLight, fontSize = 11.sp)
                }
                Slider(
                    value = urgencyRating.toFloat(),
                    onValueChange = { urgencyRating = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = NexusIndigo,
                        activeTrackColor = NexusIndigo,
                        inactiveTrackColor = NexusSurface3
                    )
                )
                Slider(
                    value = impactRating.toFloat(),
                    onValueChange = { impactRating = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = NexusIndigo,
                        activeTrackColor = NexusIndigo,
                        inactiveTrackColor = NexusSurface3
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Duration Selector
                Text(
                    text = "ESTIMATED DURATION: $estimatedMinutes MINS",
                    color = NexusTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 30, 45, 60, 90).forEach { mins ->
                        val isSelected = estimatedMinutes == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusIndigo else NexusSurface2)
                                .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                .clickable { estimatedMinutes = mins }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Energy Required
                Text(
                    text = "FOCUS ENERGY",
                    color = NexusTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EnergyLevel.entries.forEach { eng ->
                        val isSelected = energyRequired == eng
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusIndigo else NexusSurface2)
                                .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                .clickable { energyRequired = eng }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = eng.label,
                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                        } else {
                            val due = System.currentTimeMillis() + (dueHoursAhead * 3600 * 1000L)
                            onConfirm(
                                title,
                                description,
                                selectedProjectId,
                                priority,
                                urgencyRating,
                                impactRating,
                                estimatedMinutes,
                                energyRequired,
                                due
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_task_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusIndigo,
                        contentColor = NexusTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Create Task",
                        color = NexusTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
