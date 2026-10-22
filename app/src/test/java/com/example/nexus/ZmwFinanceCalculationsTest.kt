package com.example.nexus

import com.example.nexus.model.FinanceBudget
import com.example.nexus.model.FinanceTransaction
import com.example.nexus.model.TransactionType
import com.example.nexus.model.ZmwFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ZmwFinanceCalculationsTest {

    @Test
    fun zmwFormattingWithStandardAndZkPrefix() {
        val amount = 12500.50
        val formattedZmw = ZmwFormatter.formatZmw(amount, "ZMW")
        val formattedZk = ZmwFormatter.formatZmw(amount, "ZK")

        assertEquals("ZMW 12,500.50", formattedZmw)
        assertEquals("ZK 12,500.50", formattedZk)
    }

    @Test
    fun cashflowAndSavingsCalculation() {
        val incomeTx = FinanceTransaction(
            type = TransactionType.INCOME,
            amountZmw = 20000.0,
            category = "Salary",
            description = "Monthly Salary"
        )
        val expense1 = FinanceTransaction(
            type = TransactionType.EXPENSE,
            amountZmw = 5000.0,
            category = "Rent",
            description = "Apartment"
        )
        val expense2 = FinanceTransaction(
            type = TransactionType.EXPENSE,
            amountZmw = 3000.0,
            category = "Groceries",
            description = "Food"
        )

        val totalIncome = incomeTx.amountZmw
        val totalExpense = expense1.amountZmw + expense2.amountZmw
        val balance = totalIncome - totalExpense
        val savingsRate = ((totalIncome - totalExpense) / totalIncome) * 100.0

        assertEquals(12000.0, balance, 0.01)
        assertEquals(60.0, savingsRate, 0.01)
    }

    @Test
    fun budgetThresholdAlertValidation() {
        val budget = FinanceBudget(id = 1, category = "Utilities", monthlyLimitZmw = 4000.0, monthYear = "2026-10")
        val spent = 4500.0
        val isOver = spent > budget.monthlyLimitZmw
        val pctUsed = (spent / budget.monthlyLimitZmw) * 100.0

        assertTrue(isOver)
        assertEquals(112.5, pctUsed, 0.01)
    }
}
