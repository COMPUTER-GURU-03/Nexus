package com.example.nexus.engine

import com.example.nexus.model.PrioritizedTask
import com.example.nexus.model.Task
import java.util.Locale

data class ScheduledTimeBlock(
    val timeRange: String, // e.g. "09:00 - 09:45"
    val startMinutes: Int, // minutes from midnight
    val endMinutes: Int,
    val task: Task? = null,
    val projectColor: String? = null,
    val priorityScore: Double = 0.0,
    val isBreak: Boolean = false,
    val label: String = ""
)

data class UnscheduledTaskReason(
    val prioritizedTask: PrioritizedTask,
    val reason: String
)

data class DailyPlanningResult(
    val scheduledBlocks: List<ScheduledTimeBlock>,
    val unscheduledTasks: List<UnscheduledTaskReason>,
    val totalAvailableMinutes: Int,
    val allocatedMinutes: Int,
    val utilizationPercentage: Double
)

object SmartSchedulerEngine {

    /**
     * Builds a feasible, conflict-free daily schedule within the user's available planning time.
     * Never silently discards tasks; explains why unselected tasks cannot fit.
     */
    fun planDay(
        prioritizedTasks: List<PrioritizedTask>,
        startHour: Int = 9,
        endHour: Int = 17,
        maxAvailableMinutes: Int = 360,
        bufferMinutes: Int = 15,
        lunchStartHour: Int = 12,
        lunchDurationMinutes: Int = 60
    ): DailyPlanningResult {
        val blocks = mutableListOf<ScheduledTimeBlock>()
        val unscheduled = mutableListOf<UnscheduledTaskReason>()

        val workStartMinutes = startHour * 60
        val workEndMinutes = endHour * 60
        val lunchStartMinutes = lunchStartHour * 60 + 30 // 12:30
        val lunchEndMinutes = lunchStartMinutes + lunchDurationMinutes // 13:30

        var currentMinute = workStartMinutes
        var accumulatedTaskMinutes = 0

        // Filter uncompleted tasks
        val uncompleted = prioritizedTasks.filter { !it.task.isCompleted }

        for (item in uncompleted) {
            val task = item.task
            val duration = task.estimatedMinutes.coerceIn(15, 240)

            // Check if task is blocked by a dependency
            if (item.breakdown.dependencyPenalty < 0) {
                unscheduled.add(
                    UnscheduledTaskReason(
                        prioritizedTask = item,
                        reason = "Blocked by unfinished prerequisite task"
                    )
                )
                continue
            }

            // Check if adding this task exceeds user's daily available capacity
            if (accumulatedTaskMinutes + duration > maxAvailableMinutes) {
                unscheduled.add(
                    UnscheduledTaskReason(
                        prioritizedTask = item,
                        reason = "Exceeds daily planned focus limit of ${maxAvailableMinutes}m (${accumulatedTaskMinutes}m already planned)"
                    )
                )
                continue
            }

            // Check if we already reached or passed workday end
            if (currentMinute >= workEndMinutes) {
                unscheduled.add(
                    UnscheduledTaskReason(
                        prioritizedTask = item,
                        reason = "Extends beyond workday ending hour (${String.format(Locale.US, "%02d:00", endHour)})"
                    )
                )
                continue
            }

            // Handle lunch break collision only if lunch occurs within the workday
            if (lunchStartMinutes < workEndMinutes && currentMinute + duration > lunchStartMinutes && currentMinute < lunchEndMinutes) {
                if (currentMinute < lunchStartMinutes && (lunchStartMinutes - currentMinute) >= 15) {
                    blocks.add(
                        ScheduledTimeBlock(
                            timeRange = formatTimeRange(currentMinute, lunchStartMinutes),
                            startMinutes = currentMinute,
                            endMinutes = lunchStartMinutes,
                            isBreak = true,
                            label = "Buffer / Re-alignment"
                        )
                    )
                }
                if (lunchEndMinutes <= workEndMinutes) {
                    blocks.add(
                        ScheduledTimeBlock(
                            timeRange = formatTimeRange(lunchStartMinutes, lunchEndMinutes),
                            startMinutes = lunchStartMinutes,
                            endMinutes = lunchEndMinutes,
                            isBreak = true,
                            label = "Mid-Day Break & Recovery"
                        )
                    )
                    currentMinute = lunchEndMinutes
                } else {
                    currentMinute = workEndMinutes
                }
            }

            // Check if slot exceeds workday end
            if (currentMinute + duration > workEndMinutes) {
                unscheduled.add(
                    UnscheduledTaskReason(
                        prioritizedTask = item,
                        reason = "Extends beyond workday ending hour (${String.format(Locale.US, "%02d:00", endHour)})"
                    )
                )
                continue
            }

            val slotEnd = currentMinute + duration
            blocks.add(
                ScheduledTimeBlock(
                    timeRange = formatTimeRange(currentMinute, slotEnd),
                    startMinutes = currentMinute,
                    endMinutes = slotEnd,
                    task = task,
                    projectColor = item.projectColor ?: "#8175FF",
                    priorityScore = item.breakdown.totalScore,
                    isBreak = false,
                    label = task.title
                )
            )

            accumulatedTaskMinutes += duration
            currentMinute = slotEnd + bufferMinutes
        }

        val utilization = if (maxAvailableMinutes > 0) {
            (accumulatedTaskMinutes.toDouble() / maxAvailableMinutes) * 100.0
        } else 0.0

        return DailyPlanningResult(
            scheduledBlocks = blocks,
            unscheduledTasks = unscheduled,
            totalAvailableMinutes = maxAvailableMinutes,
            allocatedMinutes = accumulatedTaskMinutes,
            utilizationPercentage = utilization.coerceIn(0.0, 100.0)
        )
    }

    /**
     * Backward-compatible convenience function
     */
    fun generateDailySchedule(
        prioritizedTasks: List<PrioritizedTask>,
        startHour: Int = 9,
        endHour: Int = 17,
        bufferMinutes: Int = 15,
        lunchStartHour: Int = 12,
        lunchDurationMinutes: Int = 60
    ): List<ScheduledTimeBlock> {
        return planDay(
            prioritizedTasks = prioritizedTasks,
            startHour = startHour,
            endHour = endHour,
            maxAvailableMinutes = 480,
            bufferMinutes = bufferMinutes,
            lunchStartHour = lunchStartHour,
            lunchDurationMinutes = lunchDurationMinutes
        ).scheduledBlocks
    }

    private fun formatTimeRange(startMinutes: Int, endMinutes: Int): String {
        val startH = startMinutes / 60
        val startM = startMinutes % 60
        val endH = endMinutes / 60
        val endM = endMinutes % 60
        return String.format(Locale.US, "%02d:%02d – %02d:%02d", startH, startM, endH, endM)
    }
}
