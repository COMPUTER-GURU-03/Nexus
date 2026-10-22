package com.example.nexus.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskPriority(val weight: Double, val label: String) {
    LOW(8.0, "Low"),
    MEDIUM(16.0, "Medium"),
    HIGH(28.0, "High"),
    CRITICAL(40.0, "Critical")
}

enum class EnergyLevel(val label: String, val icon: String) {
    LOW("Low Friction", "🔋"),
    BALANCED("Balanced Focus", "⚖️"),
    HIGH("Deep Focus", "⚡")
}

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val colorHex: String = "#00F0FF",
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["priority"]),
        Index(value = ["dueDate"]),
        Index(value = ["isCompleted"])
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long? = null,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val urgencyRating: Int = 3, // 1 (Relaxed) to 5 (Extremely urgent)
    val impactRating: Int = 3,  // 1 (Minor) to 5 (Transformative)
    val estimatedMinutes: Int = 30,
    val energyRequired: EnergyLevel = EnergyLevel.BALANCED,
    val dueDate: Long? = null,
    val scheduledDate: Long? = null, // epoch day (e.g. LocalDate.toEpochDay())
    val scheduledTimeSlot: String? = null, // e.g. "09:00 - 09:45"
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val dependencyTaskId: Long? = null // ID of task that must be completed first
)

/**
 * Explainable prioritization result showing transparent score and reasons
 */
data class PriorityBreakdown(
    val taskId: Long,
    val totalScore: Double, // 0.0 to 100.0+
    val urgencyScore: Double,
    val impactScore: Double,
    val priorityScore: Double,
    val energyMatchScore: Double,
    val durationScore: Double,
    val dependencyPenalty: Double,
    val reasons: List<String>,
    val rank: Int = 0
)

data class PrioritizedTask(
    val task: Task,
    val projectName: String? = null,
    val projectColor: String? = null,
    val breakdown: PriorityBreakdown
)
