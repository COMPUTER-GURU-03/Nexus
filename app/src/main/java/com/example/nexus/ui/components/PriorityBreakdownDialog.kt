package com.example.nexus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.nexus.model.PrioritizedTask
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
fun PriorityBreakdownDialog(
    prioritizedTask: PrioritizedTask,
    onDismiss: () -> Unit
) {
    val b = prioritizedTask.breakdown
    val task = prioritizedTask.task

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                .testTag("priority_breakdown_dialog"),
            color = NexusSurface1
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRIORITIZATION TELEMETRY",
                            color = NexusIndigoLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Rank #${b.rank} • Score ${String.format("%.1f", b.totalScore)}",
                            color = NexusTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_breakdown_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = NexusTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Task Details Box (Surface 2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusSurface2)
                        .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = task.title,
                            color = NexusTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (prioritizedTask.projectName != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Project: ${prioritizedTask.projectName}",
                                color = NexusTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "FACTOR WEIGHTS",
                    color = NexusTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                FactorRow(label = "Base Priority (${task.priority.label})", value = b.priorityScore, max = 40.0)
                FactorRow(label = "Deadline Closeness & Urgency", value = b.urgencyScore, max = 35.0)
                FactorRow(label = "Strategic Impact (${task.impactRating}/5)", value = b.impactScore, max = 25.0)
                FactorRow(label = "Energy Resonance (${task.energyRequired.label})", value = b.energyMatchScore, max = 15.0)
                FactorRow(label = "Duration Momentum Boost", value = b.durationScore, max = 10.0)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "EXPLANATORY LOG",
                    color = NexusTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(b.reasons) { reason ->
                        val isNegative = reason.startsWith("-")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NexusSurface2)
                                .border(
                                    1.dp,
                                    if (isNegative) NexusError.copy(alpha = 0.5f) else NexusBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isNegative) NexusError else NexusIndigo)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = reason,
                                    color = if (isNegative) NexusError else NexusTextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_breakdown_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusIndigo,
                        contentColor = NexusTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Dismiss",
                        color = NexusTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun FactorRow(
    label: String,
    value: Double,
    max: Double
) {
    val progress = (value / max).toFloat().coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = NexusTextSecondary, fontSize = 11.sp)
            Text(
                text = "${String.format("%.1f", value)} / ${max.toInt()} pts",
                color = NexusIndigoLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = NexusIndigo,
            trackColor = NexusSurface3,
        )
    }
}
