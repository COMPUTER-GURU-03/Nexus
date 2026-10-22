package com.example.nexus.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechLime
import com.example.ui.theme.FintechLimeDark
import com.example.ui.theme.FintechSurface1
import com.example.ui.theme.FintechSurface2
import com.example.ui.theme.FintechSurface3
import com.example.ui.theme.FintechTextMuted
import com.example.ui.theme.FintechTextPrimary
import com.example.ui.theme.FintechTextSecondary

enum class QuickActionType {
    SEND,
    RECEIVE,
    CARDS,
    MENU
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBottomSheet(
    initialAction: QuickActionType = QuickActionType.MENU,
    onDismiss: () -> Unit,
    onSendSuccess: (amount: Double, recipient: String, note: String) -> Unit = { _, _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentMode by remember { mutableStateOf(initialAction) }

    var amountText by remember { mutableStateOf("") }
    var recipientText by remember { mutableStateOf("Elena Rostova") }
    var noteText by remember { mutableStateOf("Dinner bill") }
    var isSubmitted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FintechSurface1,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .background(FintechSurface3, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 36.dp)
                .testTag("quick_action_sheet")
        ) {
            when (currentMode) {
                QuickActionType.MENU -> {
                    Text(
                        text = "Transfer & Payments",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FintechTextPrimary
                    )
                    Text(
                        text = "Choose an instant action",
                        fontSize = 13.sp,
                        color = FintechTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ActionOptionItem(
                        title = "Send Money",
                        subtitle = "Instant wire to contacts or bank IBAN",
                        icon = Icons.AutoMirrored.Outlined.Send,
                        iconTint = FintechLime,
                        onClick = { currentMode = QuickActionType.SEND }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ActionOptionItem(
                        title = "Receive & QR Code",
                        subtitle = "Show your payment QR or payment handle",
                        icon = Icons.Outlined.QrCodeScanner,
                        iconTint = FintechLime,
                        onClick = { currentMode = QuickActionType.RECEIVE }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ActionOptionItem(
                        title = "Virtual Cards",
                        subtitle = "Manage digital Apple Pay & Google Wallet cards",
                        icon = Icons.Outlined.CreditCard,
                        iconTint = FintechTextPrimary,
                        onClick = { currentMode = QuickActionType.CARDS }
                    )
                }

                QuickActionType.SEND -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Send Money",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = FintechTextSecondary)
                        }
                    }

                    if (isSubmitted) {
                        // Success state
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(FintechLime, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Check, contentDescription = "Success", tint = FintechLimeDark, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Transfer Sent!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = FintechTextPrimary
                            )
                            Text(
                                text = "$$amountText sent to $recipientText",
                                fontSize = 14.sp,
                                color = FintechTextSecondary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Large balance entry
                        Text(
                            text = "AMOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            color = FintechTextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = { Text("0.00", color = FintechTextMuted, fontSize = 28.sp) },
                            prefix = { Text("$", color = FintechLime, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FintechTextPrimary,
                                unfocusedTextColor = FintechTextPrimary,
                                focusedBorderColor = FintechLime,
                                unfocusedBorderColor = FintechBorder,
                                focusedContainerColor = FintechSurface2,
                                unfocusedContainerColor = FintechSurface2
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Recipient chips
                        Text(
                            text = "QUICK RECIPIENTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            color = FintechTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val recipients = listOf("Elena R.", "Marcus V.", "Sarah K.", "Devin P.")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recipients.forEach { rec ->
                                val isSelected = recipientText.startsWith(rec.take(4))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) FintechLime else FintechSurface2)
                                        .border(1.dp, if (isSelected) FintechLime else FintechBorder, RoundedCornerShape(12.dp))
                                        .clickable { recipientText = rec }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = rec,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) FintechLimeDark else FintechTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 100.0
                                onSendSuccess(amt, recipientText, noteText)
                                isSubmitted = true
                            },
                            enabled = amountText.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("confirm_send_money_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FintechLime,
                                contentColor = FintechLimeDark,
                                disabledContainerColor = FintechSurface3,
                                disabledContentColor = FintechTextMuted
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Confirm & Send",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                QuickActionType.RECEIVE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Receive via QR",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = FintechTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FintechSurface2, RoundedCornerShape(20.dp))
                            .border(1.dp, FintechBorder, RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCode,
                                contentDescription = "QR Code",
                                tint = Color.Black,
                                modifier = Modifier.size(116.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "@alex.nexus",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary
                        )
                        Text(
                            text = "Scan or share handle to receive instantly with zero fees",
                            fontSize = 12.sp,
                            color = FintechTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                QuickActionType.CARDS -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Virtual Card Controls",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close", tint = FintechTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ActionOptionItem(
                        title = "Freeze Physical Card",
                        subtitle = "Temporarily block new online and offline charges",
                        icon = Icons.Outlined.CreditCard,
                        iconTint = FintechLime,
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ActionOptionItem(
                        title = "View PIN & CVV",
                        subtitle = "Biometric check required",
                        icon = Icons.Outlined.AccountBalance,
                        iconTint = FintechTextPrimary,
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FintechSurface2)
            .border(1.dp, FintechBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(FintechSurface1, RoundedCornerShape(12.dp))
                .border(1.dp, FintechBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FintechTextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = FintechTextSecondary)
        }
    }
}
