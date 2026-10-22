package com.example.nexus.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechLime
import com.example.ui.theme.FintechLimeDark
import com.example.ui.theme.FintechLimeGlow
import com.example.ui.theme.FintechSurface1
import com.example.ui.theme.FintechTextMuted

enum class FintechTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    WALLET("Wallet", Icons.Outlined.AccountBalanceWallet),
    INVEST("Invest", Icons.Outlined.ShowChart),
    PROFILE("Profile", Icons.Outlined.Person)
}

/**
 * Floating dark rounded bar with 4 tabs (Home, Wallet, Invest, Profile),
 * and a raised circular lime button in the center for the main action.
 */
@Composable
fun FintechBottomNav(
    activeTab: FintechTab,
    onTabSelected: (FintechTab) -> Unit,
    onCenterActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating dark rounded bar (height 68dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black)
                .background(FintechSurface1, RoundedCornerShape(32.dp))
                .border(1.dp, FintechBorder, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .testTag("fintech_bottom_nav_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                NavTabItem(
                    tab = FintechTab.HOME,
                    isSelected = activeTab == FintechTab.HOME,
                    onClick = { onTabSelected(FintechTab.HOME) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Wallet
                NavTabItem(
                    tab = FintechTab.WALLET,
                    isSelected = activeTab == FintechTab.WALLET,
                    onClick = { onTabSelected(FintechTab.WALLET) },
                    modifier = Modifier.weight(1f)
                )

                // Spacer in center for the raised button
                Spacer(modifier = Modifier.weight(1.2f))

                // Tab 3: Invest
                NavTabItem(
                    tab = FintechTab.INVEST,
                    isSelected = activeTab == FintechTab.INVEST,
                    onClick = { onTabSelected(FintechTab.INVEST) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 4: Profile
                NavTabItem(
                    tab = FintechTab.PROFILE,
                    isSelected = activeTab == FintechTab.PROFILE,
                    onClick = { onTabSelected(FintechTab.PROFILE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Raised circular lime button in the center
        val centerInteractionSource = remember { MutableInteractionSource() }
        val centerIsPressed by centerInteractionSource.collectIsPressedAsState()
        val centerScale by animateFloatAsState(
            targetValue = if (centerIsPressed) 0.90f else 1.0f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
            label = "centerBtnScale"
        )

        Box(
            modifier = Modifier
                .offset(y = (-16).dp)
                .scale(centerScale)
                .size(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = FintechLime
                )
                .background(FintechLime, CircleShape)
                .clip(CircleShape)
                .clickable(
                    interactionSource = centerInteractionSource,
                    indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                    onClick = onCenterActionClick
                )
                .testTag("center_action_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Quick Action",
                tint = FintechLimeDark,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavTabItem(
    tab: FintechTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor by animateColorAsState(
        targetValue = if (isSelected) FintechLime else FintechTextMuted,
        label = "navColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onClick
            )
            .padding(vertical = 6.dp)
            .testTag("nav_tab_${tab.label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = tintColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = tab.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = tintColor
        )
    }
}
