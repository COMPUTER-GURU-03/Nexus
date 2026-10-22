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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus.model.EnergyLevel
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusDivider
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextMuted
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun NexusHUDHeader(
    currentEnergy: EnergyLevel,
    productivityIndex: Int,
    onEnergySelected: (EnergyLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NexusSurface1)
            .border(
                width = 1.dp,
                color = NexusDivider,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("nexus_hud_header")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding & Operating System Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NexusIndigo)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NEXUS",
                            color = NexusTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "•",
                            color = NexusTextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${productivityIndex}% SYS",
                            color = NexusIndigo,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Personal Operating System",
                        color = NexusTextSecondary,
                        fontSize = 10.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }

            // Energy Level Mode Switcher: Segmented Control Track in Surface 3
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NexusSurface3)
                    .border(width = 1.dp, color = NexusBorder, shape = RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnergySegmentItem(
                    label = "Deep",
                    isSelected = currentEnergy == EnergyLevel.HIGH,
                    onClick = { onEnergySelected(EnergyLevel.HIGH) }
                )
                EnergySegmentItem(
                    label = "Balanced",
                    isSelected = currentEnergy == EnergyLevel.BALANCED,
                    onClick = { onEnergySelected(EnergyLevel.BALANCED) }
                )
                EnergySegmentItem(
                    label = "Low",
                    isSelected = currentEnergy == EnergyLevel.LOW,
                    onClick = { onEnergySelected(EnergyLevel.LOW) }
                )
            }
        }
    }
}

@Composable
private fun EnergySegmentItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) NexusIndigo else androidx.compose.ui.graphics.Color.Transparent,
        label = "segmentBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) NexusTextPrimary else NexusTextSecondary,
        label = "segmentText"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NexusTextPrimary),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("energy_chip_${label.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
