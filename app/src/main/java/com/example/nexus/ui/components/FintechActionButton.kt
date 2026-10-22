package com.example.nexus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechLime
import com.example.ui.theme.FintechLimeDark
import com.example.ui.theme.FintechSurface2
import com.example.ui.theme.FintechTextPrimary
import com.example.ui.theme.FintechTextSecondary

/**
 * Reusable ActionButton with animated press scale and sleek fintech styling.
 */
@Composable
fun FintechActionButton(
    label: String,
    icon: ImageVector,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "action_btn_$label"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "pressScale"
    )

    val shape = RoundedCornerShape(18.dp)
    val containerColor = if (isPrimary) FintechLime else FintechSurface2
    val contentColor = if (isPrimary) FintechLimeDark else FintechTextPrimary
    val borderColor = if (isPrimary) FintechLime else FintechBorder

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = if (isPrimary) Color.Black.copy(alpha = 0.2f) else FintechLime.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(containerColor, shape)
                .border(1.dp, borderColor, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isPrimary) FintechTextPrimary else FintechTextSecondary
        )
    }
}

/**
 * 4 Quick-Action Buttons Row (Send, Receive, Cards, More)
 */
@Composable
fun QuickActionsRow(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onCardsClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FintechActionButton(
            label = "Send",
            icon = Icons.AutoMirrored.Outlined.Send,
            isPrimary = true,
            onClick = onSendClick
        )

        FintechActionButton(
            label = "Receive",
            icon = Icons.Outlined.QrCodeScanner,
            isPrimary = false,
            onClick = onReceiveClick
        )

        FintechActionButton(
            label = "Cards",
            icon = Icons.Outlined.CreditCard,
            isPrimary = false,
            onClick = onCardsClick
        )

        FintechActionButton(
            label = "More",
            icon = Icons.Outlined.MoreHoriz,
            isPrimary = false,
            onClick = onMoreClick
        )
    }
}
