package com.example.nexus.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HabitFrequency(val label: String) {
    DAILY("Every Day"),
    WEEKDAYS("Mon - Fri"),
    WEEKENDS("Weekends Only")
}

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val targetPerDay: Int = 1,
    val unit: String = "times", // e.g. "times", "mins", "glasses", "pages"
    val colorHex: String = "#00F0FF",
    val iconName: String = "fitness", // key for icon display
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["habitId", "epochDay"], unique = true)
    ]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val epochDay: Long, // java.time.LocalDate.toEpochDay()
    val count: Int = 1,
    val completed: Boolean = true,
    val loggedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class HabitWithStatus(
    val habit: Habit,
    val completedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionsLast7Days: List<Boolean>, // 7-day sparkline
    val totalCompletions: Int,
    val completionRate30Days: Double // 0.0 to 100.0
)
