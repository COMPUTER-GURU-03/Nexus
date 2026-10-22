package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.nexus.data.NexusDatabase
import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: NexusDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = NexusDatabase.createInMemory(context)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NEXUS", appName)
    }

    @Test
    fun verifyRoomDatabaseEntityInsertionAndQuery() = runBlocking {
        val project = Project(name = "Cybernetics", description = "Test project", colorHex = "#4F46E5")
        val projectId = database.taskDao().insertProject(project)

        val task = Task(
            projectId = projectId,
            title = "Test Directive",
            priority = TaskPriority.CRITICAL,
            energyRequired = EnergyLevel.HIGH,
            estimatedMinutes = 45
        )
        val taskId = database.taskDao().insertTask(task)

        val retrievedTask = database.taskDao().getTaskById(taskId)
        assertNotNull(retrievedTask)
        assertEquals("Test Directive", retrievedTask?.title)
        assertEquals(TaskPriority.CRITICAL, retrievedTask?.priority)
        assertEquals(EnergyLevel.HIGH, retrievedTask?.energyRequired)
    }

    @Test
    fun verifyTaskPriorityAndDueDateQueries() = runBlocking {
        val now = 1000000L
        val lowTask = Task(title = "Low Later", priority = TaskPriority.LOW, dueDate = now + 50000)
        val critTask = Task(title = "Crit Sooner", priority = TaskPriority.CRITICAL, dueDate = now + 10000)
        val highOverdue = Task(title = "High Overdue", priority = TaskPriority.HIGH, dueDate = now - 5000)

        database.taskDao().insertTask(lowTask)
        database.taskDao().insertTask(critTask)
        database.taskDao().insertTask(highOverdue)

        // Test Overdue Query
        val overdueTasks = database.taskDao().getOverdueTasks(now).first()
        assertEquals(1, overdueTasks.size)
        assertEquals("High Overdue", overdueTasks.first().title)

        // Test Priority Ordering
        val priorityOrdered = database.taskDao().getTasksSortedByPriority().first()
        assertEquals(3, priorityOrdered.size)
        assertEquals(TaskPriority.CRITICAL, priorityOrdered[0].priority)
        assertEquals(TaskPriority.HIGH, priorityOrdered[1].priority)
        assertEquals(TaskPriority.LOW, priorityOrdered[2].priority)

        // Test Due Date Ordering
        val dueDateOrdered = database.taskDao().getTasksSortedByDueDate().first()
        assertEquals(3, dueDateOrdered.size)
        assertEquals("High Overdue", dueDateOrdered[0].title)
        assertEquals("Crit Sooner", dueDateOrdered[1].title)
        assertEquals("Low Later", dueDateOrdered[2].title)
    }

    @Test
    fun verifyTaskRepositoryImplementation() = runBlocking {
        val repository: com.example.nexus.repository.TaskRepository =
            com.example.nexus.repository.TaskRepositoryImpl(database.taskDao())

        val task = Task(title = "Repository Pattern Task", priority = TaskPriority.HIGH)
        val id = repository.insertTask(task)

        repository.updateTaskDueDate(id, 999999L)
        repository.updateTaskPriority(id, TaskPriority.CRITICAL)

        val retrieved = repository.getTaskById(id)
        assertEquals(TaskPriority.CRITICAL, retrieved?.priority)
        assertEquals(999999L, retrieved?.dueDate)

        repository.toggleTaskCompleted(id, false)
        val completedTask = repository.getTaskById(id)
        assertEquals(true, completedTask?.isCompleted)
    }
}
