package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus.data.NexusDatabase
import com.example.nexus.data.NexusPreferences
import com.example.nexus.repository.NexusRepository
import com.example.nexus.ui.components.CreateBudgetDialog
import com.example.nexus.ui.components.CreateFinanceDialog
import com.example.nexus.ui.components.CreateHabitDialog
import com.example.nexus.ui.components.CreateProjectDialog
import com.example.nexus.ui.components.CreateTaskDialog
import com.example.nexus.ui.components.NexusBottomNav
import com.example.nexus.ui.components.NexusHUDHeader
import com.example.nexus.ui.components.PriorityBreakdownDialog
import com.example.nexus.ui.screens.FinanceScreen
import com.example.nexus.ui.screens.HomeScreen
import com.example.nexus.ui.screens.InsightsScreen
import com.example.nexus.ui.screens.SettingsScreen
import com.example.nexus.ui.screens.TasksScreen
import com.example.nexus.viewmodel.NexusDestination
import com.example.nexus.viewmodel.NexusViewModel
import com.example.nexus.viewmodel.NexusViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NexusBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = NexusDatabase.getInstance(applicationContext)
        val repository = NexusRepository(
            taskDao = db.taskDao(),
            habitDao = db.habitDao(),
            financeDao = db.financeDao()
        )
        val preferences = NexusPreferences(applicationContext)
        val factory = NexusViewModelFactory(repository, preferences)

        setContent {
            MyApplicationTheme {
                val viewModel: NexusViewModel = viewModel(factory = factory)
                NexusAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NexusAppRoot(viewModel: NexusViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val allPrioritizedTasks by viewModel.allPrioritizedTasks.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val planningResult by viewModel.dailyPlanningResult.collectAsStateWithLifecycle()
    val habits by viewModel.habitsWithStatus.collectAsStateWithLifecycle()
    val financeSummary by viewModel.financeSummary.collectAsStateWithLifecycle()
    val productivityMetrics by viewModel.productivityMetrics.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val taskViewTab by viewModel.taskViewTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.taskSearchQuery.collectAsStateWithLifecycle()
    val selectedTaskForBreakdown by viewModel.selectedTaskForBreakdown.collectAsStateWithLifecycle()

    // Dialog Visibility State
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var showCreateHabitDialog by remember { mutableStateOf(false) }
    var showCreateFinanceDialog by remember { mutableStateOf(false) }
    var showCreateBudgetDialog by remember { mutableStateOf(false) }

    // Android Predictive Back Navigation: if on secondary screen, navigate back to HOME
    BackHandler(enabled = currentDestination != NexusDestination.HOME) {
        viewModel.navigateTo(NexusDestination.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 700.dp)
                .testTag("nexus_root_scaffold"),
            containerColor = NexusBackground,
            topBar = {
                NexusHUDHeader(
                    currentEnergy = preferences.energyLevel,
                    productivityIndex = productivityMetrics.productivityIndex,
                    onEnergySelected = { viewModel.setEnergyLevel(it) },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                NexusBottomNav(
                    currentDestination = currentDestination,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    NexusDestination.HOME -> {
                        HomeScreen(
                            userName = preferences.userName,
                            productivityMetrics = productivityMetrics,
                            prioritizedTasks = allPrioritizedTasks,
                            planningResult = planningResult,
                            habits = habits,
                            financeSummary = financeSummary,
                            currencyPrefix = preferences.currencyPrefix,
                            onNavigate = { viewModel.navigateTo(it) },
                            onSelectTaskForBreakdown = { viewModel.selectTaskForBreakdown(it) },
                            onToggleTask = { taskId, curr -> viewModel.toggleTask(taskId, curr) },
                            onToggleHabit = { habitId, curr, target -> viewModel.toggleHabit(habitId, curr, target) },
                            onQuickAddTask = { showCreateTaskDialog = true },
                            onQuickAddTransaction = { showCreateFinanceDialog = true }
                        )
                    }

                    NexusDestination.TASKS -> {
                        TasksScreen(
                            tasks = filteredTasks,
                            projects = projects,
                            currentTab = taskViewTab,
                            searchQuery = searchQuery,
                            onTabSelected = { viewModel.setTaskViewTab(it) },
                            onSearchQueryChanged = { viewModel.setTaskSearchQuery(it) },
                            onSelectTaskForBreakdown = { viewModel.selectTaskForBreakdown(it) },
                            onToggleTask = { taskId, curr -> viewModel.toggleTask(taskId, curr) },
                            onDeleteTask = { taskId -> viewModel.deleteTask(taskId) },
                            onDeleteProject = { proj -> viewModel.deleteProject(proj) },
                            onOpenCreateTask = { showCreateTaskDialog = true },
                            onOpenCreateProject = { showCreateProjectDialog = true }
                        )
                    }

                    NexusDestination.FINANCE -> {
                        FinanceScreen(
                            summary = financeSummary,
                            currencyPrefix = preferences.currencyPrefix,
                            onCurrencyToggled = { viewModel.setCurrencyPrefix(it) },
                            onDeleteTransaction = { txId -> viewModel.deleteTransaction(txId) },
                            onDeleteBudget = { bId -> viewModel.deleteBudget(bId) },
                            onOpenCreateTransaction = { showCreateFinanceDialog = true },
                            onOpenCreateBudget = { showCreateBudgetDialog = true }
                        )
                    }

                    NexusDestination.INSIGHTS -> {
                        InsightsScreen(
                            metrics = productivityMetrics,
                            financeSummary = financeSummary,
                            currencyPrefix = preferences.currencyPrefix
                        )
                    }

                    NexusDestination.SETTINGS -> {
                        SettingsScreen(
                            userPreferences = preferences,
                            onUpdateName = { viewModel.setUserName(it) },
                            onUpdateCurrency = { viewModel.setCurrencyPrefix(it) },
                            onUpdateDailyCapacityMinutes = { viewModel.setDailyAvailableMinutes(it) },
                            onUpdateWorkHours = { s, e -> viewModel.setWorkHours(s, e) },
                            onUpdateReminders = { viewModel.setRemindersEnabled(it) },
                            onGenerateExportJson = { viewModel.exportDataSummary() }
                        )
                    }
                }
            }
        }

        // Modals & Dialogs
        selectedTaskForBreakdown?.let { task ->
            PriorityBreakdownDialog(
                prioritizedTask = task,
                onDismiss = { viewModel.selectTaskForBreakdown(null) }
            )
        }

        if (showCreateTaskDialog) {
            CreateTaskDialog(
                projects = projects,
                onDismiss = { showCreateTaskDialog = false },
                onConfirm = { title, desc, projId, prio, urg, imp, mins, energy, due ->
                    viewModel.createTask(title, desc, projId, prio, urg, imp, mins, energy, due)
                    showCreateTaskDialog = false
                }
            )
        }

        if (showCreateProjectDialog) {
            CreateProjectDialog(
                onDismiss = { showCreateProjectDialog = false },
                onConfirm = { name, desc, color, cat ->
                    viewModel.createProject(name, desc, color, cat)
                    showCreateProjectDialog = false
                }
            )
        }

        if (showCreateHabitDialog) {
            CreateHabitDialog(
                onDismiss = { showCreateHabitDialog = false },
                onConfirm = { title, desc, target, unit, color ->
                    viewModel.createHabit(title, desc, target, unit, color)
                    showCreateHabitDialog = false
                }
            )
        }

        if (showCreateFinanceDialog) {
            CreateFinanceDialog(
                currencyPrefix = preferences.currencyPrefix,
                onDismiss = { showCreateFinanceDialog = false },
                onConfirm = { type, amount, cat, desc, acc ->
                    viewModel.createTransaction(type, amount, cat, desc, acc)
                    showCreateFinanceDialog = false
                }
            )
        }

        if (showCreateBudgetDialog) {
            CreateBudgetDialog(
                currencyPrefix = preferences.currencyPrefix,
                onDismiss = { showCreateBudgetDialog = false },
                onConfirm = { cat, limit ->
                    viewModel.createBudget(cat, limit)
                    showCreateBudgetDialog = false
                }
            )
        }
    }
}
