package com.example.nexus.repository

import com.example.nexus.data.TaskDao
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Task Management feature.
 * Encapsulates data access and provides reactive flows with priority and due date filtering.
 */
interface TaskRepository {
    val allTasks: Flow<List<Task>>
    val pendingTasks: Flow<List<Task>>
    val activeProjects: Flow<List<Project>>

    fun getTasksSortedByDueDate(): Flow<List<Task>>
    fun getTasksSortedByPriority(): Flow<List<Task>>
    fun getPendingTasksByPriority(priority: TaskPriority): Flow<List<Task>>
    fun getOverdueTasks(currentTimeMillis: Long = System.currentTimeMillis()): Flow<List<Task>>
    fun getUpcomingTasks(startMillis: Long, endMillis: Long): Flow<List<Task>>
    fun getTasksByProject(projectId: Long): Flow<List<Task>>
    fun getTasksByScheduledDate(epochDay: Long): Flow<List<Task>>

    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: Long)
    suspend fun toggleTaskCompleted(taskId: Long, currentCompleted: Boolean)
    suspend fun updateTaskDueDate(taskId: Long, dueDate: Long?)
    suspend fun updateTaskPriority(taskId: Long, priority: TaskPriority)
    suspend fun updateTaskSchedule(taskId: Long, epochDay: Long?, timeSlot: String?)

    suspend fun insertProject(project: Project): Long
    suspend fun deleteProject(project: Project)
}

/**
 * Default implementation of [TaskRepository] backed by Room [TaskDao].
 */
class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    override val pendingTasks: Flow<List<Task>> = taskDao.getPendingTasks()
    override val activeProjects: Flow<List<Project>> = taskDao.getAllActiveProjects()

    override fun getTasksSortedByDueDate(): Flow<List<Task>> =
        taskDao.getTasksSortedByDueDate()

    override fun getTasksSortedByPriority(): Flow<List<Task>> =
        taskDao.getTasksSortedByPriority()

    override fun getPendingTasksByPriority(priority: TaskPriority): Flow<List<Task>> =
        taskDao.getPendingTasksByPriority(priority)

    override fun getOverdueTasks(currentTimeMillis: Long): Flow<List<Task>> =
        taskDao.getOverdueTasks(currentTimeMillis)

    override fun getUpcomingTasks(startMillis: Long, endMillis: Long): Flow<List<Task>> =
        taskDao.getUpcomingTasks(startMillis, endMillis)

    override fun getTasksByProject(projectId: Long): Flow<List<Task>> =
        taskDao.getTasksByProject(projectId)

    override fun getTasksByScheduledDate(epochDay: Long): Flow<List<Task>> =
        taskDao.getTasksByScheduledDate(epochDay)

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)

    override suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(task)

    override suspend fun updateTask(task: Task) =
        taskDao.updateTask(task)

    override suspend fun deleteTask(taskId: Long) =
        taskDao.deleteTaskById(taskId)

    override suspend fun toggleTaskCompleted(taskId: Long, currentCompleted: Boolean) {
        val newCompleted = !currentCompleted
        val completedAt = if (newCompleted) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(taskId, newCompleted, completedAt)
    }

    override suspend fun updateTaskDueDate(taskId: Long, dueDate: Long?) =
        taskDao.updateTaskDueDate(taskId, dueDate)

    override suspend fun updateTaskPriority(taskId: Long, priority: TaskPriority) =
        taskDao.updateTaskPriority(taskId, priority)

    override suspend fun updateTaskSchedule(taskId: Long, epochDay: Long?, timeSlot: String?) =
        taskDao.updateTaskSchedule(taskId, epochDay, timeSlot)

    override suspend fun insertProject(project: Project): Long =
        taskDao.insertProject(project)

    override suspend fun deleteProject(project: Project) =
        taskDao.deleteProject(project)
}
