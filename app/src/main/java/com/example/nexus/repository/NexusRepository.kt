package com.example.nexus.repository

import com.example.nexus.data.FinanceDao
import com.example.nexus.data.HabitDao
import com.example.nexus.data.TaskDao
import com.example.nexus.model.BudgetStatus
import com.example.nexus.model.CategorySpend
import com.example.nexus.model.FinanceBudget
import com.example.nexus.model.FinanceTransaction
import com.example.nexus.model.Habit
import com.example.nexus.model.HabitLog
import com.example.nexus.model.HabitWithStatus
import com.example.nexus.model.ProductivityMetrics
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TransactionType
import com.example.nexus.model.ZmwFinanceSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import kotlin.math.max

class NexusRepository(
    private val taskDao: TaskDao,
    private val habitDao: HabitDao,
    private val financeDao: FinanceDao,
    private val taskRepository: TaskRepository = TaskRepositoryImpl(taskDao)
) : TaskRepository by taskRepository {

    // Habits
    val activeHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits()

    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun toggleHabitToday(habitId: Long, currentCount: Int, target: Int) {
        val today = LocalDate.now().toEpochDay()
        val existing = habitDao.getLogForHabitAndDay(habitId, today)
        if (existing == null) {
            habitDao.insertOrUpdateLog(
                HabitLog(
                    habitId = habitId,
                    epochDay = today,
                    count = target,
                    completed = true
                )
            )
        } else {
            if (existing.completed) {
                // Toggle off
                habitDao.deleteLogForDay(habitId, today)
            } else {
                habitDao.insertOrUpdateLog(existing.copy(completed = true, count = target))
            }
        }
    }

    /**
     * Combines habits with real persistent logs to calculate actual streaks and 30-day rates.
     */
    fun getHabitsWithStatusFlow(): Flow<List<HabitWithStatus>> {
        val today = LocalDate.now().toEpochDay()
        val thirtyDaysAgo = today - 30

        return combine(
            activeHabits,
            habitDao.getLogsSince(thirtyDaysAgo)
        ) { habits, logs ->
            val logsByHabit = logs.groupBy { it.habitId }

            habits.map { habit ->
                val habitLogs = logsByHabit[habit.id] ?: emptyList()
                val logMap = habitLogs.associateBy { it.epochDay }

                val completedToday = logMap[today]?.completed == true

                // Calculate current streak
                var currentStreak = 0
                var checkDay = if (completedToday) today else today - 1
                while (logMap[checkDay]?.completed == true) {
                    currentStreak++
                    checkDay--
                }

                // Calculate best streak over recorded logs
                var bestStreak = 0
                var tempStreak = 0
                val sortedDays = habitLogs.filter { it.completed }.map { it.epochDay }.sorted()
                var prevDay: Long? = null
                for (day in sortedDays) {
                    if (prevDay == null || day == prevDay + 1) {
                        tempStreak++
                    } else if (day > prevDay + 1) {
                        tempStreak = 1
                    }
                    if (tempStreak > bestStreak) bestStreak = tempStreak
                    prevDay = day
                }
                if (currentStreak > bestStreak) bestStreak = currentStreak

                // 7-day sparkline (from 6 days ago up to today)
                val last7Days = (6 downTo 0).map { offset ->
                    logMap[today - offset]?.completed == true
                }

                // 30 days completion rate
                val totalIn30Days = habitLogs.count { it.completed }
                val rate30 = if (totalIn30Days > 0) (totalIn30Days / 30.0) * 100.0 else 0.0

                HabitWithStatus(
                    habit = habit,
                    completedToday = completedToday,
                    currentStreak = currentStreak,
                    bestStreak = max(bestStreak, currentStreak),
                    completionsLast7Days = last7Days,
                    totalCompletions = habitLogs.size,
                    completionRate30Days = rate30.coerceIn(0.0, 100.0)
                )
            }
        }
    }

    // Finance (ZMW)
    val allTransactions: Flow<List<FinanceTransaction>> = financeDao.getAllTransactions()
    val allBudgets: Flow<List<FinanceBudget>> = financeDao.getAllBudgets()

    suspend fun insertTransaction(tx: FinanceTransaction): Long = financeDao.insertTransaction(tx)
    suspend fun deleteTransaction(id: Long) = financeDao.deleteTransactionById(id)

    suspend fun insertBudget(budget: FinanceBudget): Long = financeDao.insertBudget(budget)
    suspend fun deleteBudget(id: Long) = financeDao.deleteBudgetById(id)

    fun getFinanceSummaryFlow(): Flow<ZmwFinanceSummary> {
        return combine(allTransactions, allBudgets) { transactions, budgets ->
            var income = 0.0
            var expense = 0.0

            transactions.forEach { tx ->
                when (tx.type) {
                    TransactionType.INCOME -> income += tx.amountZmw
                    TransactionType.EXPENSE -> expense += tx.amountZmw
                    TransactionType.TRANSFER -> { /* transfers don't change net equity */ }
                }
            }

            val balance = income - expense
            val savings = if (income > 0) max(0.0, income - expense) else 0.0
            val savingsRate = if (income > 0) (savings / income) * 100.0 else 0.0

            // Category spend breakdown
            val expensesByCategory = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .map { (cat, list) ->
                    val sum = list.sumOf { it.amountZmw }
                    val pct = if (expense > 0) (sum / expense) * 100.0 else 0.0
                    CategorySpend(cat, sum, pct)
                }
                .sortedByDescending { it.totalAmountZmw }

            // Budget statuses
            val budgetStatuses = budgets.map { b ->
                val spent = transactions
                    .filter { it.type == TransactionType.EXPENSE && it.category.equals(b.category, ignoreCase = true) }
                    .sumOf { it.amountZmw }
                val remaining = b.monthlyLimitZmw - spent
                val pct = if (b.monthlyLimitZmw > 0) (spent / b.monthlyLimitZmw) * 100.0 else 0.0
                BudgetStatus(
                    budget = b,
                    spentZmw = spent,
                    remainingZmw = remaining,
                    percentageUsed = pct,
                    isOverBudget = spent > b.monthlyLimitZmw
                )
            }

            ZmwFinanceSummary(
                currentBalanceZmw = balance,
                totalIncomeZmw = income,
                totalExpenseZmw = expense,
                netSavingsZmw = savings,
                savingsRatePercentage = savingsRate,
                topSpendingCategories = expensesByCategory,
                budgetStatuses = budgetStatuses,
                recentTransactions = transactions.take(15)
            )
        }
    }

    // Telemetry & Productivity Metrics
    fun getProductivityMetricsFlow(): Flow<ProductivityMetrics> {
        val today = LocalDate.now().toEpochDay()
        val sevenDaysAgo = today - 7

        return combine(
            allTasks,
            getHabitsWithStatusFlow(),
            getFinanceSummaryFlow()
        ) { tasks, habits, finance ->
            val totalTasks = tasks.size
            val completedTasks = tasks.filter { it.isCompleted }
            val pending = tasks.filter { !it.isCompleted }
            val highPriorityPending = pending.count { it.priority == com.example.nexus.model.TaskPriority.CRITICAL || it.priority == com.example.nexus.model.TaskPriority.HIGH }

            val completedThisWeek = completedTasks.count {
                it.completedAt != null && it.completedAt >= System.currentTimeMillis() - (7 * 24 * 3600 * 1000L)
            }
            val createdThisWeek = tasks.count {
                it.createdAt >= System.currentTimeMillis() - (7 * 24 * 3600 * 1000L)
            }

            val taskRate = if (totalTasks > 0) (completedTasks.size.toDouble() / totalTasks) * 100.0 else 0.0

            val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
            val habitAvg = if (habits.isNotEmpty()) habits.map { it.completionRate30Days }.average() else 0.0

            // Productivity Index Formula:
            // 40% task completion weight + 30% habit adherence + 15% streak momentum + 15% financial savings rate
            val pIndex = (
                (taskRate * 0.40) +
                (habitAvg * 0.30) +
                ((maxStreak.coerceAtMost(14) / 14.0 * 100.0) * 0.15) +
                (finance.savingsRatePercentage.coerceIn(0.0, 100.0) * 0.15)
            ).toInt().coerceIn(10, 100)

            ProductivityMetrics(
                productivityIndex = if (tasks.isEmpty() && habits.isEmpty()) 75 else pIndex,
                tasksCompletedThisWeek = completedThisWeek,
                tasksCreatedThisWeek = createdThisWeek,
                taskCompletionRate = taskRate,
                activeHabitStreakMax = maxStreak,
                habitAdherenceRate = habitAvg,
                zmwSavingsRate = finance.savingsRatePercentage,
                totalLoggedZmwIncome = finance.totalIncomeZmw,
                totalLoggedZmwExpense = finance.totalExpenseZmw,
                netZmwCashflow = finance.currentBalanceZmw,
                openTasksCount = pending.size,
                highPriorityPendingCount = highPriorityPending
            )
        }
    }
}
