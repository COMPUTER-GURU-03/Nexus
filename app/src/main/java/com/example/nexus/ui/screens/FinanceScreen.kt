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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus.model.BudgetStatus
import com.example.nexus.model.CategorySpend
import com.example.nexus.model.FinanceTransaction
import com.example.nexus.model.TransactionType
import com.example.nexus.model.ZmwFinanceSummary
import com.example.nexus.model.ZmwFormatter
import com.example.ui.theme.NexusBackground
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusError
import com.example.ui.theme.NexusIndigo
import com.example.ui.theme.NexusIndigoLight
import com.example.ui.theme.NexusSuccess
import com.example.ui.theme.NexusSurface1
import com.example.ui.theme.NexusSurface2
import com.example.ui.theme.NexusSurface3
import com.example.ui.theme.NexusTextMuted
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusWarning

@Composable
fun FinanceScreen(
    summary: ZmwFinanceSummary,
    currencyPrefix: String,
    onCurrencyToggled: (String) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onDeleteBudget: (Long) -> Unit,
    onOpenCreateTransaction: () -> Unit,
    onOpenCreateBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NexusBackground),
        containerColor = NexusBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenCreateTransaction,
                containerColor = NexusIndigo,
                contentColor = NexusTextPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("finance_add_tx_fab")
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Transaction", tint = NexusTextPrimary)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("finance_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Header & Currency Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PERSONAL FINANCE",
                            color = NexusIndigoLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Fiscal Ledger",
                            color = NexusTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Currency Selector [ZMW / ZK / USD] in Surface 3
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NexusSurface3)
                            .border(1.dp, NexusBorder, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("ZMW", "ZK", "USD").forEach { curr ->
                            val isSelected = currencyPrefix == curr
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) NexusIndigo else Color.Transparent)
                                    .clickable { onCurrencyToggled(curr) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = curr,
                                    color = if (isSelected) NexusTextPrimary else NexusTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Net Settled Balance Hero Card (Surface 1)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NexusSurface1)
                        .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Text(
                            text = "NET CASHFLOW BALANCE",
                            color = NexusTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ZmwFormatter.formatZmw(summary.currentBalanceZmw, currencyPrefix),
                            color = NexusTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = NexusSuccess, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("INFLOW", color = NexusTextSecondary, fontSize = 10.sp)
                                }
                                Text(
                                    text = ZmwFormatter.formatZmw(summary.totalIncomeZmw, currencyPrefix),
                                    color = NexusSuccess,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = NexusTextMuted, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("OUTFLOW", color = NexusTextSecondary, fontSize = 10.sp)
                                }
                                Text(
                                    text = ZmwFormatter.formatZmw(summary.totalExpenseZmw, currencyPrefix),
                                    color = NexusTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column {
                                Text("SAVINGS RATE", color = NexusTextSecondary, fontSize = 10.sp)
                                Text(
                                    text = "${String.format("%.1f", summary.savingsRatePercentage)}%",
                                    color = NexusIndigoLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Monthly Budgets Caps
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MONTHLY BUDGET CAPS",
                        color = NexusTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Button(
                        onClick = onOpenCreateBudget,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexusSurface2,
                            contentColor = NexusTextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexusBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("finance_add_budget_btn")
                    ) {
                        Text("+ Cap", color = NexusIndigoLight, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (summary.budgetStatuses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexusSurface1)
                            .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No spending limits set. Tap '+ Cap' to add one.",
                            color = NexusTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        summary.budgetStatuses.forEach { status ->
                            BudgetCapItem(
                                status = status,
                                currencyPrefix = currencyPrefix,
                                onDelete = { onDeleteBudget(status.budget.id) }
                            )
                        }
                    }
                }
            }

            // Category Spend Breakdown
            if (summary.topSpendingCategories.isNotEmpty()) {
                item {
                    Text(
                        text = "SPENDING BREAKDOWN",
                        color = NexusTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        summary.topSpendingCategories.take(4).forEach { cat ->
                            CategoryBar(cat, currencyPrefix)
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Text(
                    text = "TRANSACTION LEDGER (${summary.recentTransactions.size})",
                    color = NexusTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            if (summary.recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexusSurface1)
                            .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions recorded yet.",
                            color = NexusTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(summary.recentTransactions, key = { it.id }) { tx ->
                    TransactionItemRow(
                        tx = tx,
                        currencyPrefix = currencyPrefix,
                        onDelete = { onDeleteTransaction(tx.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun BudgetCapItem(
    status: BudgetStatus,
    currencyPrefix: String,
    onDelete: () -> Unit
) {
    val progress = (status.percentageUsed / 100.0).toFloat().coerceIn(0f, 1f)
    val isWarning = status.percentageUsed >= 80.0 && !status.isOverBudget

    val statusColor = when {
        status.isOverBudget -> NexusError
        isWarning -> NexusWarning
        else -> NexusIndigo
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NexusSurface1)
            .border(
                1.dp,
                if (status.isOverBudget) NexusError.copy(alpha = 0.5f) else NexusBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (status.isOverBudget) {
                        Icon(Icons.Outlined.Warning, contentDescription = "Over Budget", tint = NexusError, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                    } else if (isWarning) {
                        Icon(Icons.Outlined.Warning, contentDescription = "Approaching Limit", tint = NexusWarning, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = status.budget.category,
                        color = NexusTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${String.format("%.0f", status.percentageUsed)}%",
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete Budget", tint = NexusTextMuted, modifier = Modifier.size(15.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = NexusSurface3,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: ${ZmwFormatter.formatZmw(status.spentZmw, currencyPrefix)}",
                    color = NexusTextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "Cap: ${ZmwFormatter.formatZmw(status.budget.monthlyLimitZmw, currencyPrefix)}",
                    color = NexusTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun CategoryBar(cat: CategorySpend, currencyPrefix: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NexusSurface1)
            .border(1.dp, NexusBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = cat.category, color = NexusTextPrimary, fontSize = 12.sp)
                Text(
                    text = "${ZmwFormatter.formatZmw(cat.totalAmountZmw, currencyPrefix)} (${String.format("%.0f", cat.percentageOfTotal)}%)",
                    color = NexusIndigoLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (cat.percentageOfTotal / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NexusIndigo,
                trackColor = NexusSurface3,
            )
        }
    }
}

@Composable
private fun TransactionItemRow(
    tx: FinanceTransaction,
    currencyPrefix: String,
    onDelete: () -> Unit
) {
    val isExpense = tx.type == TransactionType.EXPENSE
    val sign = if (isExpense) "-" else "+"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NexusSurface1)
            .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("tx_item_${tx.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.description,
                    color = NexusTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = tx.category, color = NexusIndigoLight, fontSize = 11.sp)
                    Text("•", color = NexusBorder, fontSize = 11.sp)
                    Text(text = tx.account, color = NexusTextSecondary, fontSize = 11.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$sign${ZmwFormatter.formatZmw(tx.amountZmw, currencyPrefix)}",
                    color = if (isExpense) NexusTextPrimary else NexusSuccess,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete", tint = NexusTextMuted, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}
