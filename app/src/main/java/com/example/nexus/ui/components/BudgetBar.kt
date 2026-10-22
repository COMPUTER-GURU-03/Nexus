package com.example.nexus.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechLime
import com.example.ui.theme.FintechRed
import com.example.ui.theme.FintechSurface2
import com.example.ui.theme.FintechSurface3
import com.example.ui.theme.FintechTextMuted
import com.example.ui.theme.FintechTextPrimary
import com.example.ui.theme.FintechTextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Reusable BudgetBar component:
 * - Category icon and title
 * - Current spending vs total limit
 * - Thin lime progress bar with smooth animated fill
 */
@Composable
fun BudgetBar(
    category: String,
    spent: Double,
    limit: Double,
    currencySymbol: String = "$",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val progress = if (limit > 0) (spent / limit).coerceIn(0.0, 1.0).toFloat() else 0f
    val isOverBudget = spent > limit

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "budgetProgress"
    )

    val resolvedIcon = icon ?: getBudgetCategoryIcon(category)
    val barColor = if (isOverBudget) FintechRed else FintechLime

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("budget_bar_$category")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(FintechSurface2, RoundedCornerShape(10.dp))
                        .border(1.dp, FintechBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = resolvedIcon,
                        contentDescription = category,
                        tint = FintechLime,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FintechTextPrimary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% used",
                        fontSize = 11.sp,
                        color = FintechTextMuted
                    )
                }
            }

            // Spent / Limit text
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formatAmount(spent, currencySymbol),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverBudget) FintechRed else FintechTextPrimary
                )
                Text(
                    text = " / ${formatAmount(limit, currencySymbol)}",
                    fontSize = 12.sp,
                    color = FintechTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Thin lime progress bar (height 5.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(FintechSurface3, CircleShape)
                .clip(CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(barColor, CircleShape)
            )
        }
    }
}

private fun formatAmount(amount: Double, symbol: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }
    return "$symbol${formatter.format(amount)}"
}

private fun getBudgetCategoryIcon(category: String): ImageVector {
    val lower = category.lowercase()
    return when {
        "food" in lower || "dining" in lower || "grocer" in lower -> Icons.Outlined.Fastfood
        "shop" in lower || "retail" in lower -> Icons.Outlined.ShoppingBag
        "transport" in lower || "travel" in lower || "fuel" in lower -> Icons.Outlined.Flight
        "bill" in lower || "utilit" in lower || "power" in lower -> Icons.Outlined.Lightbulb
        else -> Icons.Outlined.Movie
    }
}
