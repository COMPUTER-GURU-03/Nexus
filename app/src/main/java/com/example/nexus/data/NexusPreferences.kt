package com.example.nexus.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexus.model.EnergyLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nexus_settings")

data class UserPreferences(
    val userName: String = "Operator",
    val energyLevel: EnergyLevel = EnergyLevel.BALANCED,
    val workStartHour: Int = 9,
    val workEndHour: Int = 17,
    val dailyAvailableMinutes: Int = 360, // 6 hours planned work capacity
    val bufferMinutes: Int = 15,
    val currencyPrefix: String = "ZMW",
    val remindersEnabled: Boolean = true
)

class NexusPreferences(private val context: Context) {
    companion object {
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_ENERGY_LEVEL = stringPreferencesKey("user_energy_level")
        val KEY_WORK_START_HOUR = intPreferencesKey("work_start_hour")
        val KEY_WORK_END_HOUR = intPreferencesKey("work_end_hour")
        val KEY_DAILY_AVAILABLE_MINUTES = intPreferencesKey("daily_available_minutes")
        val KEY_BUFFER_MINUTES = intPreferencesKey("buffer_minutes")
        val KEY_CURRENCY_PREFIX = stringPreferencesKey("currency_prefix")
        val KEY_REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val name = preferences[KEY_USER_NAME] ?: "Operator"
        val energyStr = preferences[KEY_ENERGY_LEVEL] ?: EnergyLevel.BALANCED.name
        val energy = try { EnergyLevel.valueOf(energyStr) } catch (e: Exception) { EnergyLevel.BALANCED }
        val startHour = preferences[KEY_WORK_START_HOUR] ?: 9
        val endHour = preferences[KEY_WORK_END_HOUR] ?: 17
        val dailyMinutes = preferences[KEY_DAILY_AVAILABLE_MINUTES] ?: 360
        val buffer = preferences[KEY_BUFFER_MINUTES] ?: 15
        val currency = preferences[KEY_CURRENCY_PREFIX] ?: "ZMW"
        val reminders = preferences[KEY_REMINDERS_ENABLED] ?: true

        UserPreferences(
            userName = name,
            energyLevel = energy,
            workStartHour = startHour,
            workEndHour = endHour,
            dailyAvailableMinutes = dailyMinutes,
            bufferMinutes = buffer,
            currencyPrefix = currency,
            remindersEnabled = reminders
        )
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name.trim()
        }
    }

    suspend fun setEnergyLevel(level: EnergyLevel) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ENERGY_LEVEL] = level.name
        }
    }

    suspend fun setWorkHours(startHour: Int, endHour: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_WORK_START_HOUR] = startHour
            preferences[KEY_WORK_END_HOUR] = endHour
        }
    }

    suspend fun setDailyAvailableMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DAILY_AVAILABLE_MINUTES] = minutes
        }
    }

    suspend fun setBufferMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BUFFER_MINUTES] = minutes
        }
    }

    suspend fun setCurrencyPrefix(prefix: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CURRENCY_PREFIX] = prefix
        }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_REMINDERS_ENABLED] = enabled
        }
    }
}
