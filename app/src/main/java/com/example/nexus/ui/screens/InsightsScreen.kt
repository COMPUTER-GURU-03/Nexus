package com.example.nexus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus.model.ProductivityMetrics
import com.example.nexus.model.ZmwFinanceSummary
import com.example.nexus.model.ZmwFormatter
import com.example.ui.theme.NexusBackground
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusError
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusIndigoLight
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface2
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextMuted
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun InsightsScreen(
    metrics: ProductivityMetrics,
    financeSummary: ZmwFinanceSummary,
    currencyPrefix: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground)
            .padding(horizontal = 20.dp)
            .testTag("insights_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Column {
                Text(
                    text = "INSIGHTS & ANALYTICS",
                    color = NexusIndigoLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "System Telemetry",
                    color = NexusTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Composite Productivity Score (Surface 1)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NexusSurface1)
                    .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Speed, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Productivity Index",
                                color = NexusTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${metrics.productivityIndex} / 100",
                            color = NexusIndigoLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (metrics.productivityIndex / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NexusIndigo,
                        trackColor = NexusSurface3,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Calculated from completed tasks, habit consistency adherence, and Kwacha savings rate.",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Task Velocity Breakdown (Surface 1)
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
                    Text(
                        text = "TASK EXECUTION VELOCITY",
                        color = NexusTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryMetricItem("Done (7 Days)", "${metrics.tasksCompletedThisWeek}")
                        TelemetryMetricItem("Created (7 Days)", "${metrics.tasksCreatedThisWeek}")
                        TelemetryMetricItem("Completion %", "${String.format("%.0f", metrics.taskCompletionRate)}%")
                        TelemetryMetricItem("Critical Open", "${metrics.highPriorityPendingCount}", isCritical = metrics.highPriorityPendingCount > 0)
                    }
                }
            }
        }

        // Habit Consistency Telemetry (Surface 1)
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
                    Text(
                        text = "HABIT CONSISTENCY & ROUTINES",
                        color = NexusTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryMetricItem("30-Day Rate", "${String.format("%.1f", metrics.habitAdherenceRate)}%")
                        TelemetryMetricItem("Longest Streak", "${metrics.activeHabitStreakMax} Days")
                        TelemetryMetricItem("Active Routines", "${metrics.openTasksCount}")
                    }
                }
            }
        }

        // Financial Ratio Telemetry (Surface 1)
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
                    Text(
                        text = "FISCAL LIQUIDITY METRICS ($currencyPrefix)",
                        color = NexusTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryMetricItem("Total Inflow", ZmwFormatter.formatZmw(financeSummary.totalIncomeZmw, currencyPrefix))
                        TelemetryMetricItem("Total Outflow", ZmwFormatter.formatZmw(financeSummary.totalExpenseZmw, currencyPrefix))
                        TelemetryMetricItem("Savings Rate", "${String.format("%.1f", metrics.zmwSavingsRate)}%")
                    }
                }
            }
        }

        // Offline Data Integrity Note (Surface 1)
        item {
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Storage, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Persistent Local Storage (Room SQLite)",
                            color = NexusTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "All telemetry computed from on-device records. Zero remote telemetry.",
                            color = NexusTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun TelemetryMetricItem(label: String, value: String, isCritical: Boolean = false) {
    Column {
        Text(
            text = value,
            color = if (isCritical) NexusError else NexusTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = NexusTextSecondary,
            fontSize = 10.sp
        )
    }
}
