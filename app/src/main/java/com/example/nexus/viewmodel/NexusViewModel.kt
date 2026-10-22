package com.example.nexus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nexus.data.NexusPreferences
import com.example.nexus.data.UserPreferences
import com.example.nexus.engine.DailyPlanningResult
import com.example.nexus.engine.SmartSchedulerEngine
import com.example.nexus.engine.TaskPrioritizationEngine
import com.example.nexus.model.EnergyLevel
import com.example.nexus.model.FinanceBudget
import com.example.nexus.model.FinanceTransaction
import com.example.nexus.model.Habit
import com.example.nexus.model.HabitWithStatus
import com.example.nexus.model.PrioritizedTask
import com.example.nexus.model.ProductivityMetrics
import com.example.nexus.model.Project
import com.example.nexus.model.Task
import com.example.nexus.model.TaskPriority
import com.example.nexus.model.TransactionType
import com.example.nexus.model.ZmwFinanceSummary
import com.example.nexus.repository.NexusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class NexusDestination(val title: String, val iconLabel: String) {
    HOME("Home", "Home"),
    TASKS("Tasks", "Tasks"),
    FINANCE("Finance", "Finance"),
    INSIGHTS("Insights", "Insights"),
    SETTINGS("Settings", "Profile")
}

enum class TaskViewTab(val label: String) {
    TODAY("Today"),
    UPCOMING("Upcoming"),
    ALL("All Tasks"),
    PROJECTS("Projects"),
    COMPLETED("Completed")
}

class NexusViewModel(
    private val repository: NexusRepository,
    private val preferences: NexusPreferences
) : ViewModel() {

    // Current Navigation Screen
    private val _currentDestination = MutableStateFlow(NexusDestination.HOME)
    val currentDestination: StateFlow<NexusDestination> = _currentDestination.asStateFlow()

    fun navigateTo(destination: NexusDestination) {
        _currentDestination.value = destination
    }

    // Active Task View Tab
    private val _taskViewTab = MutableStateFlow(TaskViewTab.TODAY)
    val taskViewTab: StateFlow<TaskViewTab> = _taskViewTab.asStateFlow()

    fun setTaskViewTab(tab: TaskViewTab) {
        _taskViewTab.value = tab
    }

    // Search Query in Tasks
    private val _taskSearchQuery = MutableStateFlow("")
    val taskSearchQuery: StateFlow<String> = _taskSearchQuery.asStateFlow()

    fun setTaskSearchQuery(query: String) {
        _taskSearchQuery.value = query
    }

    // User Preferences (Energy Level, Work Hours, Currency, Available Minutes)
    val userPreferences: StateFlow<UserPreferences> = preferences.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setEnergyLevel(level: EnergyLevel) {
        viewModelScope.launch { preferences.setEnergyLevel(level) }
    }

    fun setCurrencyPrefix(prefix: String) {
        viewModelScope.launch { preferences.setCurrencyPrefix(prefix) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { preferences.setUserName(name) }
    }

    fun setDailyAvailableMinutes(minutes: Int) {
        viewModelScope.launch { preferences.setDailyAvailableMinutes(minutes) }
    }

    fun setWorkHours(startHour: Int, endHour: Int) {
        viewModelScope.launch { preferences.setWorkHours(startHour, endHour) }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setRemindersEnabled(enabled) }
    }

    // Projects
    val projects: StateFlow<List<Project>> = repository.activeProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All Prioritized Tasks
    val allPrioritizedTasks: StateFlow<List<PrioritizedTask>> = combine(
        repository.allTasks,
        repository.activeProjects,
        preferences.userPreferencesFlow
    ) { tasks, projects, prefs ->
        TaskPrioritizationEngine.prioritizeTasks(
            tasks = tasks,
            projects = projects,
            currentEnergy = prefs.energyLevel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered Tasks for Tasks View
    val filteredTasks: StateFlow<List<PrioritizedTask>> = combine(
        allPrioritizedTasks,
        _taskViewTab,
        _taskSearchQuery
    ) { tasks, tab, query ->
        val queryLower = query.trim().lowercase()
        val searched = if (queryLower.isBlank()) tasks else tasks.filter {
            it.task.title.lowercase().contains(queryLower) ||
            it.task.description.lowercase().contains(queryLower) ||
            (it.projectName?.lowercase()?.contains(queryLower) == true)
        }

        val todayEpoch = LocalDate.now().toEpochDay()
        val now = System.currentTimeMillis()

        when (tab) {
            TaskViewTab.TODAY -> searched.filter {
                !it.task.isCompleted && (
                    it.task.scheduledDate == todayEpoch ||
                    (it.task.dueDate != null && it.task.dueDate <= now + 86400000L)
                )
            }
            TaskViewTab.UPCOMING -> searched.filter {
                !it.task.isCompleted && it.task.dueDate != null && it.task.dueDate > now
            }
            TaskViewTab.ALL -> searched.filter { !it.task.isCompleted }
            TaskViewTab.PROJECTS -> searched.filter { it.task.projectId != null }
            TaskViewTab.COMPLETED -> searched.filter { it.task.isCompleted }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Smart Daily Planning Result (Includes scheduled blocks & unscheduled tasks)
    val dailyPlanningResult: StateFlow<DailyPlanningResult> = combine(
        allPrioritizedTasks,
        userPreferences
    ) { tasks, prefs ->
        SmartSchedulerEngine.planDay(
            prioritizedTasks = tasks,
            startHour = prefs.workStartHour,
            endHour = prefs.workEndHour,
            maxAvailableMinutes = prefs.dailyAvailableMinutes,
            bufferMinutes = prefs.bufferMinutes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyPlanningResult(emptyList(), emptyList(), 360, 0, 0.0)
    )

    // Habits with Streaks & 7-day sparklines
    val habitsWithStatus: StateFlow<List<HabitWithStatus>> = repository.getHabitsWithStatusFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ZMW Finance Summary & Budgets
    val financeSummary: StateFlow<ZmwFinanceSummary> = repository.getFinanceSummaryFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ZmwFinanceSummary(
                currentBalanceZmw = 0.0,
                totalIncomeZmw = 0.0,
                totalExpenseZmw = 0.0,
                netSavingsZmw = 0.0,
                savingsRatePercentage = 0.0,
                topSpendingCategories = emptyList(),
                budgetStatuses = emptyList(),
                recentTransactions = emptyList()
            )
        )

    // Persistent Telemetry & Productivity Metrics
    val productivityMetrics: StateFlow<ProductivityMetrics> = repository.getProductivityMetricsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProductivityMetrics(
                productivityIndex = 80,
                tasksCompletedThisWeek = 0,
                tasksCreatedThisWeek = 0,
                taskCompletionRate = 0.0,
                activeHabitStreakMax = 0,
                habitAdherenceRate = 0.0,
                zmwSavingsRate = 0.0,
                totalLoggedZmwIncome = 0.0,
                totalLoggedZmwExpense = 0.0,
                netZmwCashflow = 0.0,
                openTasksCount = 0,
                highPriorityPendingCount = 0
            )
        )

    // Selected Task for Explainability Modal
    private val _selectedTaskForBreakdown = MutableStateFlow<PrioritizedTask?>(null)
    val selectedTaskForBreakdown: StateFlow<PrioritizedTask?> = _selectedTaskForBreakdown.asStateFlow()

    fun selectTaskForBreakdown(task: PrioritizedTask?) {
        _selectedTaskForBreakdown.value = task
    }

    // CRUD Actions
    fun createTask(
        title: String,
        description: String,
        projectId: Long?,
        priority: TaskPriority,
        urgency: Int,
        impact: Int,
        estimatedMinutes: Int,
        energy: EnergyLevel,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = title.trim(),
                    description = description.trim(),
                    projectId = projectId,
                    priority = priority,
                    urgencyRating = urgency,
                    impactRating = impact,
                    estimatedMinutes = estimatedMinutes,
                    energyRequired = energy,
                    dueDate = dueDate,
                    scheduledDate = LocalDate.now().toEpochDay()
                )
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTask(taskId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(taskId, currentCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun createProject(name: String, description: String, colorHex: String, category: String) {
        viewModelScope.launch {
            repository.insertProject(
                Project(
                    name = name.trim(),
                    description = description.trim(),
                    colorHex = colorHex,
                    category = category.ifBlank { "General" }
                )
            )
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun createHabit(title: String, description: String, targetPerDay: Int, unit: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertHabit(
                Habit(
                    title = title.trim(),
                    description = description.trim(),
                    targetPerDay = targetPerDay,
                    unit = unit.ifBlank { "times" },
                    colorHex = colorHex
                )
            )
        }
    }

    fun toggleHabit(habitId: Long, currentCount: Int, target: Int) {
        viewModelScope.launch {
            repository.toggleHabitToday(habitId, currentCount, target)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun createTransaction(
        type: TransactionType,
        amountZmw: Double,
        category: String,
        description: String,
        account: String
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                FinanceTransaction(
                    type = type,
                    amountZmw = amountZmw,
                    category = category.trim(),
                    description = description.trim(),
                    account = account.ifBlank { "Mobile Money" }
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun createBudget(category: String, monthlyLimitZmw: Double) {
        viewModelScope.launch {
            val monthYear = "%04d-%02d".format(LocalDate.now().year, LocalDate.now().monthValue)
            repository.insertBudget(
                FinanceBudget(
                    category = category.trim(),
                    monthlyLimitZmw = monthlyLimitZmw,
                    monthYear = monthYear
                )
            )
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(id)
        }
    }

    // Export Data to JSON String
    fun exportDataSummary(): String {
        val tasks = allPrioritizedTasks.value.map {
            mapOf(
                "title" to it.task.title,
                "priority" to it.task.priority.name,
                "completed" to it.task.isCompleted,
                "estimatedMinutes" to it.task.estimatedMinutes
            )
        }
        val habits = habitsWithStatus.value.map {
            mapOf(
                "title" to it.habit.title,
                "streak" to it.currentStreak,
                "completedToday" to it.completedToday
            )
        }
        val finance = financeSummary.value
        val summaryMap = mapOf(
            "nexus_version" to "1.0",
            "export_date" to LocalDate.now().toString(),
            "currency" to userPreferences.value.currencyPrefix,
            "tasks_count" to tasks.size,
            "habits_count" to habits.size,
            "current_balance_zmw" to finance.currentBalanceZmw,
            "total_income_zmw" to finance.totalIncomeZmw,
            "total_expense_zmw" to finance.totalExpenseZmw
        )
        return summaryMap.entries.joinToString(",\n  ", prefix = "{\n  ", postfix = "\n}") { (k, v) ->
            "\"$k\": \"$v\""
        }
    }
}

class NexusViewModelFactory(
    private val repository: NexusRepository,
    private val preferences: NexusPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NexusViewModel::class.java)) {
            return NexusViewModel(repository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
