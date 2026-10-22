package com.example.nexus.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nexus.model.Habit
import com.example.nexus.model.HabitLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id ASC")
    fun getAllActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Habit Logs
    @Query("SELECT * FROM habit_logs WHERE epochDay = :epochDay")
    fun getLogsForDay(epochDay: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY epochDay DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND epochDay = :epochDay LIMIT 1")
    suspend fun getLogForHabitAndDay(habitId: Long, epochDay: Long): HabitLog?

    @Query("SELECT * FROM habit_logs WHERE epochDay >= :startEpochDay ORDER BY epochDay ASC")
    fun getLogsSince(startEpochDay: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs")
    suspend fun getAllLogsSnapshot(): List<HabitLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: HabitLog): Long

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteLogForDay(habitId: Long, epochDay: Long)
}
