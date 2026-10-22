package com.example.nexus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus.viewmodel.NexusDestination
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusIndigoSoft
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun NexusBottomNav(
    currentDestination: NexusDestination,
    onNavigate: (NexusDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NexusSurface1)
            .border(
                width = 1.dp,
                color = NexusBorder,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .padding(vertical = 6.dp)
            .testTag("nexus_bottom_nav")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(
                destination = NexusDestination.HOME,
                icon = Icons.Outlined.Home,
                isSelected = currentDestination == NexusDestination.HOME,
                onClick = { onNavigate(NexusDestination.HOME) }
            )
            NavButton(
                destination = NexusDestination.TASKS,
                icon = Icons.Outlined.CheckCircle,
                isSelected = currentDestination == NexusDestination.TASKS,
                onClick = { onNavigate(NexusDestination.TASKS) }
            )
            NavButton(
                destination = NexusDestination.FINANCE,
                icon = Icons.Outlined.AccountBalanceWallet,
                isSelected = currentDestination == NexusDestination.FINANCE,
                onClick = { onNavigate(NexusDestination.FINANCE) }
            )
            NavButton(
                destination = NexusDestination.INSIGHTS,
                icon = Icons.Outlined.Insights,
                isSelected = currentDestination == NexusDestination.INSIGHTS,
                onClick = { onNavigate(NexusDestination.INSIGHTS) }
            )
            NavButton(
                destination = NexusDestination.SETTINGS,
                icon = Icons.Outlined.Person,
                isSelected = currentDestination == NexusDestination.SETTINGS,
                onClick = { onNavigate(NexusDestination.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavButton(
    destination: NexusDestination,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) NexusIndigo else NexusTextSecondary,
        label = "navIconTint"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) NexusTextPrimary else NexusTextSecondary,
        label = "navLabelColor"
    )
    val pillBg by animateColorAsState(
        targetValue = if (isSelected) NexusIndigoSoft else androidx.compose.ui.graphics.Color.Transparent,
        label = "navPillBg"
    )

    Column(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NexusIndigo),
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("nav_btn_${destination.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(pillBg)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = destination.title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = destination.iconLabel,
            color = labelColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            letterSpacing = 0.2.sp
        )
    }
}
