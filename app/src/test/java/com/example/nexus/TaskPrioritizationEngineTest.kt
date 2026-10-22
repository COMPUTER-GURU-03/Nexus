package com.example.nexus

import com.example.nexus.engine.TaskPrioritizationEngine
import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskPrioritizationEngineTest {

    @Test
    fun criticalTaskGetsHigherScoreThanLowTask() {
        val criticalTask = Task(
            id = 1,
            title = "Critical Mission",
            priority = TaskPriority.CRITICAL,
            urgencyRating = 3,
            impactRating = 3,
            estimatedMinutes = 30
        )
        val lowTask = Task(
            id = 2,
            title = "Low Priority Task",
            priority = TaskPriority.LOW,
            urgencyRating = 3,
            impactRating = 3,
            estimatedMinutes = 30
        )

        val criticalBreakdown = TaskPrioritizationEngine.evaluateTask(criticalTask)
        val lowBreakdown = TaskPrioritizationEngine.evaluateTask(lowTask)

        assertTrue(
            "Critical task score (${criticalBreakdown.totalScore}) should exceed low task score (${lowBreakdown.totalScore})",
            criticalBreakdown.totalScore > lowBreakdown.totalScore
        )
        assertEquals(40.0, criticalBreakdown.priorityScore, 0.01)
        assertEquals(8.0, lowBreakdown.priorityScore, 0.01)
    }

    @Test
    fun energyResonanceBoostsMatchingTasks() {
        val highEnergyTask = Task(
            id = 1,
            title = "Deep Architectural Math",
            energyRequired = EnergyLevel.HIGH,
            estimatedMinutes = 30
        )

        val breakdownInHighMode = TaskPrioritizationEngine.evaluateTask(
            task = highEnergyTask,
            currentEnergy = EnergyLevel.HIGH
        )
        val breakdownInLowMode = TaskPrioritizationEngine.evaluateTask(
            task = highEnergyTask,
            currentEnergy = EnergyLevel.LOW
        )

        assertEquals(15.0, breakdownInHighMode.energyMatchScore, 0.01)
        assertEquals(-10.0, breakdownInLowMode.energyMatchScore, 0.01)
        assertTrue(breakdownInHighMode.totalScore > breakdownInLowMode.totalScore)
    }

    @Test
    fun blockedTaskSuffersDependencyPenalty() {
        val dependencyTask = Task(id = 10, title = "Backend Migration", isCompleted = false)
        val blockedTask = Task(
            id = 11,
            title = "Client Integration",
            dependencyTaskId = 10,
            estimatedMinutes = 30
        )

        val taskMap = mapOf(10L to dependencyTask, 11L to blockedTask)
        val breakdown = TaskPrioritizationEngine.evaluateTask(
            task = blockedTask,
            allTasksMap = taskMap
        )

        assertEquals(-30.0, breakdown.dependencyPenalty, 0.01)
        assertTrue(breakdown.reasons.any { it.contains("Blocked by unresolved dependency") })
    }

    @Test
    fun quickWinBoostsShortTasks() {
        val quickTask = Task(id = 1, title = "Fix typo", estimatedMinutes = 15)
        val longTask = Task(id = 2, title = "Write monolith", estimatedMinutes = 120)

        val quickBreakdown = TaskPrioritizationEngine.evaluateTask(quickTask)
        val longBreakdown = TaskPrioritizationEngine.evaluateTask(longTask)

        assertEquals(10.0, quickBreakdown.durationScore, 0.01)
        assertEquals(-4.0, longBreakdown.durationScore, 0.01)
    }

    @Test
    fun prioritizeTasksRanksCorrectly() {
        val urgentTask = Task(id = 1, title = "Urgent", priority = TaskPriority.CRITICAL, urgencyRating = 5, impactRating = 5)
        val minorTask = Task(id = 2, title = "Minor", priority = TaskPriority.LOW, urgencyRating = 1, impactRating = 1)

        val prioritized = TaskPrioritizationEngine.prioritizeTasks(listOf(minorTask, urgentTask))

        assertEquals(1L, prioritized.first().task.id)
        assertEquals(1, prioritized.first().breakdown.rank)
        assertEquals(2L, prioritized.last().task.id)
        assertEquals(2, prioritized.last().breakdown.rank)
    }
}
