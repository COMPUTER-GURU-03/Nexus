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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nexus.model.TransactionType
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusError
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface2
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun CreateFinanceDialog(
    currencyPrefix: String = "ZMW",
    onDismiss: () -> Unit,
    onConfirm: (type: TransactionType, amount: Double, category: String, description: String, account: String) -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Groceries & Food") }
    var description by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("Mobile Money (Airtel)") }

    var amountError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Groceries & Food",
        "Utilities & Bills",
        "Transport & Fuel",
        "Tech & Hardware",
        "Salary & Revenue",
        "Healthcare",
        "Consulting Income",
        "Investments & Savings",
        "Leisure & Personal"
    )

    val accounts = listOf(
        "Mobile Money (Airtel)",
        "Mobile Money (MTN)",
        "Bank Account",
        "Cash Wallet"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                .testTag("create_finance_dialog"),
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
                        text = "Record Transaction",
                        color = NexusTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close", tint = NexusTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Type Toggle (Expense, Income, Transfer)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionType.entries.forEach { t ->
                        val isSelected = type == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusIndigo else NexusSurface2)
                                .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                .clickable { type = t }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t.label,
                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Input in ZMW
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        if (it.toDoubleOrNull() != null && it.toDouble() > 0) amountError = false
                    },
                    label = { Text("Amount ($currencyPrefix) *", color = NexusTextSecondary) },
                    placeholder = { Text("0.00", color = NexusTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = {
                        if (amountError) Text("Enter a valid positive amount", color = NexusError)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("finance_amount_input"),
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

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (it.isNotBlank()) descError = false
                    },
                    label = { Text("Description *", color = NexusTextSecondary) },
                    isError = descError,
                    supportingText = {
                        if (descError) Text("Description is required", color = NexusError)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("finance_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedBorderColor = NexusIndigo,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusSurface3,
                        unfocusedContainerColor = NexusSurface3
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category chips
                Text(
                    text = "CATEGORY",
                    color = NexusTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.chunked(2).forEach { rowList ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowList.forEach { cat ->
                                val isSelected = category == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NexusIndigo else NexusSurface2)
                                        .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                        .clickable { category = cat }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Account Selector
                Text(
                    text = "ACCOUNT",
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
                    accounts.forEach { acc ->
                        val isSelected = account == acc
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusIndigo else NexusSurface2)
                                .border(1.dp, if (isSelected) NexusIndigo else NexusBorder, RoundedCornerShape(8.dp))
                                .clickable { account = acc }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = acc.replace("Mobile Money ", "MoMo "),
                                color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amountVal = amountText.toDoubleOrNull()
                        if (amountVal == null || amountVal <= 0.0) {
                            amountError = true
                        } else if (description.isBlank()) {
                            descError = true
                        } else {
                            onConfirm(type, amountVal, category, description, account)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_finance_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusIndigo,
                        contentColor = NexusTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Save Transaction",
                        color = NexusTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
