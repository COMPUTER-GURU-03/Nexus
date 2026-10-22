package com.example.nexus

import com.example.nexus.engine.SmartSchedulerEngine
import com.example.nexus.engine.TaskPrioritizationEngine
import com.example.nexus.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartSchedulerEngineTest {

    @Test
    fun dailyScheduleGeneratesNonOverlappingSlots() {
        val task1 = Task(id = 1, title = "Design System", estimatedMinutes = 30)
        val task2 = Task(id = 2, title = "API Endpoints", estimatedMinutes = 45)
        val prioritized = TaskPrioritizationEngine.prioritizeTasks(listOf(task1, task2))

        val schedule = SmartSchedulerEngine.generateDailySchedule(
            prioritizedTasks = prioritized,
            startHour = 9,
            endHour = 17,
            bufferMinutes = 15
        )

        assertTrue(schedule.size >= 2)
        val firstSlot = schedule[0]
        val secondSlot = schedule[1]

        assertEquals(9 * 60, firstSlot.startMinutes)
        assertEquals(9 * 60 + 30, firstSlot.endMinutes)
        // With 15 min buffer: second slot starts at 9:30 + 15m = 9:45
        assertEquals(9 * 60 + 45, secondSlot.startMinutes)
        assertEquals(9 * 60 + 45 + 45, secondSlot.endMinutes)
        assertFalse(firstSlot.isBreak)
        assertFalse(secondSlot.isBreak)
    }

    @Test
    fun dailyPlanRespectsAvailableTimeConstraintAndExplainsUnscheduled() {
        // 3 tasks of 60 minutes each, but user only has 100 minutes of planned capacity
        val task1 = Task(id = 1, title = "Task 1", estimatedMinutes = 60)
        val task2 = Task(id = 2, title = "Task 2", estimatedMinutes = 60)
        val task3 = Task(id = 3, title = "Task 3", estimatedMinutes = 60)
        val prioritized = TaskPrioritizationEngine.prioritizeTasks(listOf(task1, task2, task3))

        val plan = SmartSchedulerEngine.planDay(
            prioritizedTasks = prioritized,
            startHour = 9,
            endHour = 17,
            maxAvailableMinutes = 100, // Only 1 task of 60m fits!
            bufferMinutes = 15
        )

        assertEquals(1, plan.scheduledBlocks.count { !it.isBreak })
        assertEquals(60, plan.allocatedMinutes)
        assertEquals(100, plan.totalAvailableMinutes)
        assertEquals(60.0, plan.utilizationPercentage, 0.01)

        // Other 2 tasks must not be silently discarded; they must appear in unscheduled with explanation
        assertEquals(2, plan.unscheduledTasks.size)
        assertTrue(plan.unscheduledTasks.all { it.reason.contains("Exceeds daily planned focus limit") })
    }

    @Test
    fun dailyScheduleRespectsWorkdayLimits() {
        // Tasks totaling 10 hours cannot fit into a 3-hour window
        val longTasks = (1..5).map {
            Task(id = it.toLong(), title = "Task $it", estimatedMinutes = 120)
        }
        val prioritized = TaskPrioritizationEngine.prioritizeTasks(longTasks)

        val schedule = SmartSchedulerEngine.generateDailySchedule(
            prioritizedTasks = prioritized,
            startHour = 9,
            endHour = 12, // 3 hours window
            bufferMinutes = 15
        )

        assertTrue(schedule.all { it.endMinutes <= 12 * 60 })
    }
}
