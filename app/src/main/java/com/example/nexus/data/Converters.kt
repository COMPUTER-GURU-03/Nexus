package com.example.nexus.data

import androidx.room.TypeConverter
import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.HabitFrequency
import com.example.nexus.model.TaskPriority
import com.example.nexus.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority =
        try { TaskPriority.valueOf(value) } catch (e: Exception) { TaskPriority.MEDIUM }

    @TypeConverter
    fun fromEnergyLevel(value: EnergyLevel): String = value.name

    @TypeConverter
    fun toEnergyLevel(value: String): EnergyLevel =
        try { EnergyLevel.valueOf(value) } catch (e: Exception) { EnergyLevel.BALANCED }

    @TypeConverter
    fun fromHabitFrequency(value: HabitFrequency): String = value.name

    @TypeConverter
    fun toHabitFrequency(value: String): HabitFrequency =
        try { HabitFrequency.valueOf(value) } catch (e: Exception) { HabitFrequency.DAILY }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        try { TransactionType.valueOf(value) } catch (e: Exception) { TransactionType.EXPENSE }
}
