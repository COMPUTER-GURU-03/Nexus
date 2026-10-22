package com.example.nexus.engine

import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.PriorityBreakdown
import com.example.nexus.model.PrioritizedTask
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import kotlin.math.max

object TaskPrioritizationEngine {

    /**
     * Computes transparent, explainable priority breakdown for a single task
     * considering user's current energy state, deadlines, dependencies, and ratings.
     */
    fun evaluateTask(
        task: Task,
        currentEnergy: EnergyLevel = EnergyLevel.BALANCED,
        currentTimeMillis: Long = System.currentTimeMillis(),
        allTasksMap: Map<Long, Task> = emptyMap()
    ): PriorityBreakdown {
        val reasons = mutableListOf<String>()

        // 1. Base Priority Tier Weight (8 to 40 pts)
        val priorityScore = when (task.priority) {
            TaskPriority.CRITICAL -> {
                reasons.add("+40 Base Tier: Critical Mission")
                40.0
            }
            TaskPriority.HIGH -> {
                reasons.add("+28 Base Tier: High Priority")
                28.0
            }
            TaskPriority.MEDIUM -> {
                reasons.add("+16 Base Tier: Medium Priority")
                16.0
            }
            TaskPriority.LOW -> {
                reasons.add("+8 Base Tier: Low Priority")
                8.0
            }
        }

        // 2. Urgency Score (up to 35 pts)
        var urgencyScore = 0.0
        val dueDate = task.dueDate
        if (dueDate != null) {
            val millisRemaining = dueDate - currentTimeMillis
            val hoursRemaining = millisRemaining / (3600 * 1000.0)
            when {
                hoursRemaining < 0 -> {
                    urgencyScore = 35.0
                    reasons.add("+35 ALARM: Task is Overdue!")
                }
                hoursRemaining <= 6.0 -> {
                    urgencyScore = 32.0
                    reasons.add("+32 Imminent Deadline (${String.format("%.1f", hoursRemaining)} hrs remaining)")
                }
                hoursRemaining <= 24.0 -> {
                    urgencyScore = 26.0
                    reasons.add("+26 Deadline Today (${String.format("%.1f", hoursRemaining)} hrs remaining)")
                }
                hoursRemaining <= 72.0 -> {
                    urgencyScore = 18.0
                    reasons.add("+18 Approaching Deadline within 3 days")
                }
                hoursRemaining <= 168.0 -> { // 7 days
                    urgencyScore = 10.0
                    reasons.add("+10 Due This Week")
                }
                else -> {
                    urgencyScore = (task.urgencyRating * 3.0)
                    reasons.add("+${urgencyScore.toInt()} Urgency Rating (${task.urgencyRating}/5)")
                }
            }
        } else {
            urgencyScore = (task.urgencyRating * 4.0)
            reasons.add("+${urgencyScore.toInt()} Stated Urgency Rating (${task.urgencyRating}/5)")
        }

        // 3. Strategic Impact Score (5 to 25 pts)
        val impactScore = (task.impactRating.coerceIn(1, 5) * 5.0)
        reasons.add("+${impactScore.toInt()} Strategic Impact Rating (${task.impactRating}/5)")

        // 4. Energy Resonance Score (-10 to +15 pts)
        var energyMatchScore = 0.0
        if (task.energyRequired == currentEnergy) {
            energyMatchScore = 15.0
            reasons.add("+15 Optimal Energy Resonance (${currentEnergy.label})")
        } else {
            val dist = kotlin.math.abs(task.energyRequired.ordinal - currentEnergy.ordinal)
            if (dist == 1) {
                energyMatchScore = 6.0
                reasons.add("+6 Moderate Energy Alignment")
            } else {
                // High energy task while low energy, or vice versa
                if (currentEnergy == EnergyLevel.LOW && task.energyRequired == EnergyLevel.HIGH) {
                    energyMatchScore = -10.0
                    reasons.add("-10 Energy Friction: Deep Focus task during Low Energy state")
                } else {
                    energyMatchScore = 2.0
                    reasons.add("+2 Low Energy Task available")
                }
            }
        }

        // 5. Duration / Quick-Win Velocity Score (-4 to +10 pts)
        val durationScore = when {
            task.estimatedMinutes <= 15 -> {
                reasons.add("+10 Quick Win Momentum (<= 15 min)")
                10.0
            }
            task.estimatedMinutes <= 30 -> {
                reasons.add("+6 Swift Execution (<= 30 min)")
                6.0
            }
            task.estimatedMinutes >= 90 -> {
                reasons.add("-4 Heavy Time Commitment (>= 90 min)")
                -4.0
            }
            else -> 0.0
        }

        // 6. Dependency / Blocker Check (-30 to +5 pts)
        var dependencyPenalty = 0.0
        val depId = task.dependencyTaskId
        if (depId != null) {
            val parentTask = allTasksMap[depId]
            if (parentTask != null && !parentTask.isCompleted) {
                dependencyPenalty = -30.0
                reasons.add("-30 Blocked by unresolved dependency: \"${parentTask.title}\"")
            } else if (parentTask != null && parentTask.isCompleted) {
                dependencyPenalty = 5.0
                reasons.add("+5 Dependency Cleared: \"${parentTask.title}\"")
            }
        }

        val totalRaw = priorityScore + urgencyScore + impactScore + energyMatchScore + durationScore + dependencyPenalty
        val totalScore = max(0.0, totalRaw)

        return PriorityBreakdown(
            taskId = task.id,
            totalScore = totalScore,
            urgencyScore = urgencyScore,
            impactScore = impactScore,
            priorityScore = priorityScore,
            energyMatchScore = energyMatchScore,
            durationScore = durationScore,
            dependencyPenalty = dependencyPenalty,
            reasons = reasons
        )
    }

    /**
     * Prioritizes and ranks a collection of tasks with projects
     */
    fun prioritizeTasks(
        tasks: List<Task>,
        projects: List<Project> = emptyList(),
        currentEnergy: EnergyLevel = EnergyLevel.BALANCED,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): List<PrioritizedTask> {
        val projectMap = projects.associateBy { it.id }
        val taskMap = tasks.associateBy { it.id }

        val evaluated = tasks.map { task ->
            val breakdown = evaluateTask(
                task = task,
                currentEnergy = currentEnergy,
                currentTimeMillis = currentTimeMillis,
                allTasksMap = taskMap
            )
            val project = task.projectId?.let { projectMap[it] }
            PrioritizedTask(
                task = task,
                projectName = project?.name,
                projectColor = project?.colorHex,
                breakdown = breakdown
            )
        }

        // Sort descending by score, but keep completed tasks at the bottom
        val sorted = evaluated.sortedWith(
            compareBy<PrioritizedTask> { it.task.isCompleted }
                .thenByDescending { it.breakdown.totalScore }
                .thenBy { it.task.dueDate ?: Long.MAX_VALUE }
        )

        // Assign rank numbers
        return sorted.mapIndexed { index, item ->
            item.copy(breakdown = item.breakdown.copy(rank = index + 1))
        }
    }
}
