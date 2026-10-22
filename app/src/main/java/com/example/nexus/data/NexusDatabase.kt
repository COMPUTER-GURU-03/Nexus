package com.example.nexus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.FinanceBudget
import com.example.nexus.model.FinanceTransaction
import com.example.nexus.model.Habit
import com.example.nexus.model.HabitFrequency
import com.example.nexus.model.HabitLog
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import com.example.nexus.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [
        Project::class,
        Task::class,
        Habit::class,
        HabitLog::class,
        FinanceTransaction::class,
        FinanceBudget::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NexusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: NexusDatabase? = null

        fun getInstance(context: Context): NexusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexusDatabase::class.java,
                    "nexus_core.db"
                )
                    .addCallback(NexusDatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Test helper for in-memory database
        fun createInMemory(context: Context): NexusDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                NexusDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }

    private class NexusDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                getInstance(context).seedInitialData()
            }
        }
    }

    suspend fun seedInitialData() {
        val today = LocalDate.now().toEpochDay()
        val now = System.currentTimeMillis()

        // Seed initial Projects
        val project1Id = taskDao().insertProject(
            Project(
                name = "AeroNexus Architecture",
                description = "Core system engine and offline-first reactive pipeline",
                colorHex = "#4F46E5",
                category = "Engineering"
            )
        )
        val project2Id = taskDao().insertProject(
            Project(
                name = "FinTech Zambia Deployment",
                description = "Payment gateway rollout and regulatory compliance",
                colorHex = "#4F46E5",
                category = "Fintech"
            )
        )
        val project3Id = taskDao().insertProject(
            Project(
                name = "Quantum BioHacking",
                description = "Cognitive training, sleep optimization, and longevity",
                colorHex = "#4F46E5",
                category = "Health"
            )
        )

        // Seed Initial Tasks
        taskDao().insertTask(
            Task(
                projectId = project1Id,
                title = "Finalize Explainable Prioritizer Weights",
                description = "Tune dynamic urgency coefficients and energy matching curve",
                priority = TaskPriority.CRITICAL,
                urgencyRating = 5,
                impactRating = 5,
                estimatedMinutes = 45,
                energyRequired = EnergyLevel.HIGH,
                dueDate = now + (4 * 3600 * 1000L), // in 4 hours
                scheduledDate = today,
                scheduledTimeSlot = "09:00 - 09:45"
            )
        )

        taskDao().insertTask(
            Task(
                projectId = project2Id,
                title = "Audit Mobile Money ZMW Reconciliation",
                description = "Check MTN & Airtel Money settlement ledger and fee structures",
                priority = TaskPriority.HIGH,
                urgencyRating = 4,
                impactRating = 4,
                estimatedMinutes = 30,
                energyRequired = EnergyLevel.BALANCED,
                dueDate = now + (24 * 3600 * 1000L),
                scheduledDate = today,
                scheduledTimeSlot = "10:30 - 11:00"
            )
        )

        taskDao().insertTask(
            Task(
                projectId = project1Id,
                title = "Run JVM Robolectric Test Suite",
                description = "Verify local state persistence and DAO constraints",
                priority = TaskPriority.HIGH,
                urgencyRating = 3,
                impactRating = 4,
                estimatedMinutes = 20,
                energyRequired = EnergyLevel.BALANCED,
                dueDate = now + (12 * 3600 * 1000L)
            )
        )

        taskDao().insertTask(
            Task(
                projectId = project3Id,
                title = "Review Weekly Cognitive & Biometric Telemetry",
                description = "Analyze sleep tracking, HRV trends, and deep focus hours",
                priority = TaskPriority.MEDIUM,
                urgencyRating = 2,
                impactRating = 3,
                estimatedMinutes = 25,
                energyRequired = EnergyLevel.LOW,
                dueDate = now + (48 * 3600 * 1000L)
            )
        )

        // Seed Initial Habits
        val habit1Id = habitDao().insertHabit(
            Habit(
                title = "Deep Focus Matrix",
                description = "90 minutes of zero-distraction deep work",
                frequency = HabitFrequency.DAILY,
                targetPerDay = 1,
                unit = "sessions",
                colorHex = "#4F46E5",
                iconName = "focus"
            )
        )

        val habit2Id = habitDao().insertHabit(
            Habit(
                title = "Neural Hydration",
                description = "Drink 3 liters of mineral-balanced water",
                frequency = HabitFrequency.DAILY,
                targetPerDay = 8,
                unit = "glasses",
                colorHex = "#4F46E5",
                iconName = "water"
            )
        )

        val habit3Id = habitDao().insertHabit(
            Habit(
                title = "High-Intensity Physical Training",
                description = "Cardio & resistance workout",
                frequency = HabitFrequency.DAILY,
                targetPerDay = 1,
                unit = "workouts",
                colorHex = "#4F46E5",
                iconName = "fitness"
            )
        )

        val habit4Id = habitDao().insertHabit(
            Habit(
                title = "Algorithmic & Tech Reading",
                description = "Read 20 pages of technical whitepapers/books",
                frequency = HabitFrequency.DAILY,
                targetPerDay = 20,
                unit = "pages",
                colorHex = "#4F46E5",
                iconName = "book"
            )
        )

        // Seed Habit Logs for streaks
        for (i in 1..4) {
            habitDao().insertOrUpdateLog(
                HabitLog(
                    habitId = habit1Id,
                    epochDay = today - i,
                    count = 1,
                    completed = true
                )
            )
            habitDao().insertOrUpdateLog(
                HabitLog(
                    habitId = habit2Id,
                    epochDay = today - i,
                    count = 8,
                    completed = true
                )
            )
        }
        // Habit 1 done today
        habitDao().insertOrUpdateLog(
            HabitLog(
                habitId = habit1Id,
                epochDay = today,
                count = 1,
                completed = true
            )
        )

        // Seed ZMW Finance Transactions
        val currentMonthStr = "%04d-%02d".format(LocalDate.now().year, LocalDate.now().monthValue)
        financeDao().insertTransaction(
            FinanceTransaction(
                type = TransactionType.INCOME,
                amountZmw = 42500.0,
                category = "Consulting/Salary",
                description = "Software Architecture Retainer Q4",
                account = "Bank Account (Stanbic)",
                date = now - (3 * 24 * 3600 * 1000L)
            )
        )

        financeDao().insertTransaction(
            FinanceTransaction(
                type = TransactionType.EXPENSE,
                amountZmw = 3850.0,
                category = "Utilities & Cloud",
                description = "AWS & Cloud Servers + Fiber Internet",
                account = "Mobile Money (Airtel)",
                date = now - (2 * 24 * 3600 * 1000L)
            )
        )

        financeDao().insertTransaction(
            FinanceTransaction(
                type = TransactionType.EXPENSE,
                amountZmw = 2400.0,
                category = "Groceries & Nutrition",
                description = "Weekly organics and nutrition supply",
                account = "Mobile Money (MTN)",
                date = now - (1 * 24 * 3600 * 1000L)
            )
        )

        financeDao().insertTransaction(
            FinanceTransaction(
                type = TransactionType.EXPENSE,
                amountZmw = 950.0,
                category = "Transport & Fuel",
                description = "Fuel replenishment Lusaka CBD",
                account = "Cash Wallet",
                date = now - (12 * 3600 * 1000L)
            )
        )

        // Seed Budgets for current month
        financeDao().insertBudget(
            FinanceBudget(
                category = "Utilities & Cloud",
                monthlyLimitZmw = 6000.0,
                monthYear = currentMonthStr
            )
        )
        financeDao().insertBudget(
            FinanceBudget(
                category = "Groceries & Nutrition",
                monthlyLimitZmw = 8000.0,
                monthYear = currentMonthStr
            )
        )
        financeDao().insertBudget(
            FinanceBudget(
                category = "Transport & Fuel",
                monthlyLimitZmw = 4000.0,
                monthYear = currentMonthStr
            )
        )
    }
}
