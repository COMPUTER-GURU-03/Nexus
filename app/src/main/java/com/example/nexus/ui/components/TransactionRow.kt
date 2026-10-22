package com.example.nexus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechLime
import com.example.ui.theme.FintechRed
import com.example.ui.theme.FintechSurface2
import com.example.ui.theme.FintechTextMuted
import com.example.ui.theme.FintechTextPrimary
import com.example.ui.theme.FintechTextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Reusable TransactionRow component:
 * - Rounded icon tile
 * - Bold title
 * - Gray category subtitle
 * - Amount on the right (Green for +, Red for -)
 * - Small percentage or secondary timestamp/method value beneath it
 */
@Composable
fun TransactionRow(
    title: String,
    category: String,
    amount: Double,
    isIncome: Boolean,
    secondaryInfo: String,
    currencySymbol: String = "$",
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val resolvedIcon = icon ?: getCategoryIcon(category)
    val formattedAmount = formatCurrency(amount, currencySymbol)
    val amountText = if (isIncome) "+$formattedAmount" else "-$formattedAmount"
    val amountColor = if (isIncome) FintechLime else FintechRed

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("tx_row_$title"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rounded icon tile
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(FintechSurface2, RoundedCornerShape(14.dp))
                .border(1.dp, FintechBorder, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = resolvedIcon,
                contentDescription = category,
                tint = if (isIncome) FintechLime else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Category Subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = FintechTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = category,
                color = FintechTextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Amount & Secondary Value
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amountText,
                color = amountColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = secondaryInfo,
                color = FintechTextMuted,
                fontSize = 11.sp
            )
        }
    }
}

private fun formatCurrency(amount: Double, currencySymbol: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$currencySymbol${formatter.format(amount)}"
}

private fun getCategoryIcon(category: String): ImageVector {
    val lower = category.lowercase()
    return when {
        "food" in lower || "grocer" in lower || "dining" in lower || "coffee" in lower -> Icons.Outlined.Fastfood
        "salary" in lower || "income" in lower || "freelance" in lower || "revenue" in lower -> Icons.Outlined.Work
        "shopping" in lower || "store" in lower || "retail" in lower -> Icons.Outlined.ShoppingBag
        "travel" in lower || "flight" in lower || "hotel" in lower || "uber" in lower -> Icons.Outlined.Flight
        "movie" in lower || "entertainment" in lower || "netflix" in lower -> Icons.Outlined.Movie
        "invest" in lower || "dividend" in lower -> Icons.Outlined.Paid
        "bank" in lower || "transfer" in lower -> Icons.Outlined.AccountBalance
        else -> Icons.Outlined.Payments
    }
}
