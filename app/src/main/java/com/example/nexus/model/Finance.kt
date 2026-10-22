package com.example.nexus.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.Locale

enum class TransactionType(val label: String) {
    INCOME("Income"),
    EXPENSE("Expense"),
    TRANSFER("Transfer")
}

@Entity(tableName = "finance_transactions")
data class FinanceTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountZmw: Double, // Value in Zambian Kwacha
    val category: String, // e.g. "Housing", "Groceries", "Transport", "Tech/Dev", "Utilities", "Health", "Savings", "Business"
    val description: String,
    val date: Long = System.currentTimeMillis(),
    val account: String = "Mobile Money (Airtel/MTN)", // e.g. "Mobile Money", "Zanaco/Stanbic Bank", "Cash Wallet"
    val notes: String = ""
)

@Entity(tableName = "finance_budgets")
data class FinanceBudget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val monthlyLimitZmw: Double,
    val monthYear: String // Format: "YYYY-MM" e.g. "2026-10"
)

data class BudgetStatus(
    val budget: FinanceBudget,
    val spentZmw: Double,
    val remainingZmw: Double,
    val percentageUsed: Double, // 0.0 to 100.0+
    val isOverBudget: Boolean
)

data class CategorySpend(
    val category: String,
    val totalAmountZmw: Double,
    val percentageOfTotal: Double
)

data class ZmwFinanceSummary(
    val currentBalanceZmw: Double,
    val totalIncomeZmw: Double,
    val totalExpenseZmw: Double,
    val netSavingsZmw: Double,
    val savingsRatePercentage: Double,
    val topSpendingCategories: List<CategorySpend>,
    val budgetStatuses: List<BudgetStatus>,
    val recentTransactions: List<FinanceTransaction>
)

object ZmwFormatter {
    fun formatZmw(amount: Double, prefix: String = "ZMW"): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "$prefix ${formatter.format(amount)}"
    }
}
