package com.example.nexus.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Projects
    @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllActiveProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC, id DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC, id DESC")
    fun getPendingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksByProject(projectId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE scheduledDate = :epochDay ORDER BY isCompleted ASC, scheduledTimeSlot ASC")
    fun getTasksByScheduledDate(epochDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDate IS NOT NULL ORDER BY dueDate ASC")
    fun getTasksSortedByDueDate(): Flow<List<Task>>

    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 0 
        ORDER BY 
            CASE priority 
                WHEN 'CRITICAL' THEN 1 
                WHEN 'HIGH' THEN 2 
                WHEN 'MEDIUM' THEN 3 
                ELSE 4 
            END ASC, 
            dueDate ASC
    """)
    fun getTasksSortedByPriority(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND priority = :priority ORDER BY dueDate ASC")
    fun getPendingTasksByPriority(priority: TaskPriority): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDate IS NOT NULL AND dueDate < :currentTimeMillis ORDER BY dueDate ASC")
    fun getOverdueTasks(currentTimeMillis: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDate IS NOT NULL AND dueDate >= :startMillis AND dueDate <= :endMillis ORDER BY dueDate ASC")
    fun getUpcomingTasks(startMillis: Long, endMillis: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksSnapshot(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("UPDATE tasks SET isCompleted = :completed, completedAt = :completedAt WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: Long, completed: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET scheduledDate = :epochDay, scheduledTimeSlot = :timeSlot WHERE id = :taskId")
    suspend fun updateTaskSchedule(taskId: Long, epochDay: Long?, timeSlot: String?)

    @Query("UPDATE tasks SET dueDate = :dueDate WHERE id = :taskId")
    suspend fun updateTaskDueDate(taskId: Long, dueDate: Long?)

    @Query("UPDATE tasks SET priority = :priority WHERE id = :taskId")
    suspend fun updateTaskPriority(taskId: Long, priority: TaskPriority)
}
