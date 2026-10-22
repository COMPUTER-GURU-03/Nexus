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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus.data.UserPreferences
import com.example.ui.theme.NexusBackground
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusIndigoLight
import com.example.ui.theme.NexusIndigoSoft
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface2
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextMuted
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    onUpdateName: (String) -> Unit,
    onUpdateCurrency: (String) -> Unit,
    onUpdateDailyCapacityMinutes: (Int) -> Unit,
    onUpdateWorkHours: (Int, Int) -> Unit,
    onUpdateReminders: (Boolean) -> Unit,
    onGenerateExportJson: () -> String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var editingName by remember(userPreferences.userName) { mutableStateOf(userPreferences.userName) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportContent by remember { mutableStateOf("") }
    var copiedToClipboard by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground)
            .padding(horizontal = 20.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "PROFILE & PREFERENCES",
                    color = NexusIndigoLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "System Settings",
                    color = NexusTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Display Name Card (Surface 1)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "User Identity",
                            color = NexusTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editingName,
                            onValueChange = { editingName = it },
                            label = { Text("Display Name", color = NexusTextSecondary) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_name_input"),
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

                        Button(
                            onClick = { onUpdateName(editingName) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexusIndigo,
                                contentColor = NexusTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Save", color = NexusTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Daily Planning Capacity (Surface 1)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Planning Capacity",
                                color = NexusTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${userPreferences.dailyAvailableMinutes / 60}h ${userPreferences.dailyAvailableMinutes % 60}m",
                            color = NexusIndigoLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "The intelligent planner uses this maximum focus budget to prevent task overload and highlight unscheduled overflow.",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = userPreferences.dailyAvailableMinutes.toFloat(),
                        onValueChange = { onUpdateDailyCapacityMinutes(it.toInt()) },
                        valueRange = 120f..600f,
                        steps = 7, // 2h, 3h, 4h, 5h, 6h, 7h, 8h, 9h, 10h
                        colors = SliderDefaults.colors(
                            thumbColor = NexusIndigo,
                            activeTrackColor = NexusIndigo,
                            inactiveTrackColor = NexusSurface3
                        )
                    )
                }
            }
        }

        // Working Hours (Surface 1)
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
                        text = "Workday Time Window",
                        color = NexusTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Currently configured as ${String.format("%02d:00", userPreferences.workStartHour)} – ${String.format("%02d:00", userPreferences.workEndHour)} with a protected 12:30–13:30 mid-day recovery break.",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Currency Settings (Surface 1)
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
                        text = "Fiscal Currency Format",
                        color = NexusTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Default currency is Zambian Kwacha (ZMW).",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexusSurface3)
                            .border(1.dp, NexusBorder, RoundedCornerShape(10.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("ZMW", "ZK", "USD").forEach { curr ->
                            val isSelected = userPreferences.currencyPrefix == curr
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NexusIndigo else Color.Transparent)
                                    .clickable { onUpdateCurrency(curr) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = curr,
                                    color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reminders Toggle (Surface 1)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NexusSurface1)
                    .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Task & Routine Reminders",
                                color = NexusTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Operating-system level local alarms",
                                color = NexusTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = userPreferences.remindersEnabled,
                        onCheckedChange = { onUpdateReminders(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NexusTextPrimary,
                            checkedTrackColor = NexusIndigo,
                            uncheckedThumbColor = NexusTextMuted,
                            uncheckedTrackColor = NexusSurface3
                        )
                    )
                }
            }
        }

        // Data Export & Portability (Surface 1)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = NexusIndigoLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Local Data Portability",
                            color = NexusTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Export your local tasks, habits, and financial logs as a structured JSON summary.",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            exportContent = onGenerateExportJson()
                            showExportDialog = true
                            copiedToClipboard = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexusSurface2,
                            contentColor = NexusTextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexusBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("settings_export_btn")
                    ) {
                        Text("Generate JSON Backup Summary", color = NexusTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // App Information & Offline Privacy
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NexusSurface1)
                    .border(1.dp, NexusBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "NEXUS OS // v1.0.0",
                        color = NexusIndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Engineered as an offline-first personal operating system. Structured Room SQLite persistence, Jetpack Compose, Coroutines and Flow.",
                        color = NexusTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text("Local Backup Summary", color = NexusTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Exported JSON structure:", color = NexusTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(NexusSurface2)
                            .border(1.dp, NexusBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = exportContent,
                            color = NexusTextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportContent))
                        copiedToClipboard = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusIndigo,
                        contentColor = NexusTextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (copiedToClipboard) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        tint = NexusTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (copiedToClipboard) "Copied!" else "Copy JSON", color = NexusTextPrimary, fontSize = 11.sp)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusSurface2,
                        contentColor = NexusTextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", color = NexusTextSecondary, fontSize = 11.sp)
                }
            },
            containerColor = NexusSurface1,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
