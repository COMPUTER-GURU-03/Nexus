package com.example.nexus.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusError
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, colorHex: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Core") }
    var nameError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                .testTag("create_project_dialog"),
            color = NexusSurface1
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Project",
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
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Project Name *", color = NexusTextSecondary) },
                    isError = nameError,
                    supportingText = {
                        if (nameError) Text("Name is required", color = NexusError)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedBorderColor = NexusIndigo,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusSurface3,
                        unfocusedContainerColor = NexusSurface3
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Objective / Scope (Optional)", color = NexusTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedBorderColor = NexusIndigo,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusSurface3,
                        unfocusedContainerColor = NexusSurface3
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            nameError = true
                        } else {
                            onConfirm(name, description, "#4F46E5", category)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_project_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusIndigo,
                        contentColor = NexusTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Create Project",
                        color = NexusTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
