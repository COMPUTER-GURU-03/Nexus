package com.example.nexus.model

data class ProductivityMetrics(
    val productivityIndex: Int, // 0 to 100
    val tasksCompletedThisWeek: Int,
    val tasksCreatedThisWeek: Int,
    val taskCompletionRate: Double, // %
    val activeHabitStreakMax: Int,
    val habitAdherenceRate: Double, // %
    val zmwSavingsRate: Double, // %
    val totalLoggedZmwIncome: Double,
    val totalLoggedZmwExpense: Double,
    val netZmwCashflow: Double,
    val openTasksCount: Int,
    val highPriorityPendingCount: Int
)

data class DayProductivityPoint(
    val epochDay: Long,
    val dayLabel: String, // e.g. "Mon", "Tue"
    val tasksDone: Int,
    val habitsDone: Int,
    val expenseZmw: Double
)
