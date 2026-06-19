package co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel

import co.edu.unicauca.dopaminah.domain.model.AppInfo
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Constants identifying the supported goal types. */
object GoalType {
    const val TOTAL_DAILY = "TOTAL_DAILY"
    const val APP_LIMIT = "APP_LIMIT"
    const val UNLOCK_LIMIT = "UNLOCK_LIMIT"
}

/** Display-ready model for a single goal card. */
data class GoalDisplayModel(
    val id: Int,
    val goalType: String,
    val appPackageName: String? = null,
    val appIconBytes: ByteArray? = null,
    val title: String,
    val subtitle: String,
    val progressLabel: String,
    val progressPercent: String,
    val progressFraction: Float,
    val isExceeded: Boolean,
    val currentLimitMinutes: Int = 0
)

/** UI state for the goals screen. */
data class GoalsState(
    val goals: List<GoalDisplayModel> = emptyList(),
    val installedApps: List<AppInfo> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = false
)

/** ViewModel for the device usage goals screen, managing CRUD of daily-time, app-limit, and unlock-limit goals. */
class GoalsViewModel(
    private val goalsRepository: GoalsRepository? = null,
    private val deviceUsageRepository: DeviceUsageRepository? = null,
    private val installedApps: List<AppInfo> = emptyList()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(
        GoalsState(
            installedApps = installedApps,
            isLoading = goalsRepository != null
        )
    )
    val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        if (goalsRepository != null) {
            observeGoals()
        }
    }

    private fun observeGoals() {
        scope.launch {
            goalsRepository!!.getAllGoals().collect { rawGoals ->
                val todayUsage = try {
                    deviceUsageRepository?.getDailyUsageStats() ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

                val usageByPackage = todayUsage.associate { it.packageName to it.totalTimeForegroundMillis }
                val sortedApps = installedApps.sortedByDescending { usageByPackage[it.packageName] ?: 0L }

                val displayModels = buildDisplayModels(rawGoals, todayUsage)
                _state.update {
                    it.copy(
                        goals = displayModels,
                        installedApps = sortedApps,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun buildDisplayModels(
        goals: List<AppLimitGoal>,
        todayUsage: List<AppUsageSummary>
    ): List<GoalDisplayModel> {
        if (goals.isEmpty()) return emptyList()

        val totalScreenMillis = todayUsage.sumOf { it.totalTimeForegroundMillis }
        val deviceUnlocks = try {
            deviceUsageRepository?.getDailyDeviceUnlocks() ?: 0
        } catch (_: Exception) {
            0
        }
        val usageByPackage = todayUsage.associateBy { it.packageName }
        val appIconBytesMap = installedApps.associate { it.packageName to it.iconBytes }

        return goals.map { goal ->
            when (goal.goalType) {
                GoalType.TOTAL_DAILY -> {
                    val limitMillis = goal.maxTimeMillis.coerceAtLeast(1L)
                    val fraction = (totalScreenMillis.toFloat() / limitMillis).coerceIn(0f, 1f)
                    val percent = ((totalScreenMillis.toFloat() / limitMillis) * 100).toInt()
                    GoalDisplayModel(
                        id = goal.id,
                        goalType = goal.goalType,
                        title = "Tiempo Total Diario",
                        subtitle = "Maximo: ${formatMillis(limitMillis)}",
                        progressLabel = "Progreso Hoy",
                        progressPercent = "$percent%",
                        progressFraction = fraction,
                        isExceeded = totalScreenMillis > limitMillis,
                        currentLimitMinutes = (limitMillis / 60_000).toInt()
                    )
                }
                GoalType.APP_LIMIT -> {
                    val appUsage = usageByPackage[goal.packageName]
                    val usedMillis = appUsage?.totalTimeForegroundMillis ?: 0L
                    val limitMillis = goal.maxTimeMillis.coerceAtLeast(1L)
                    val fraction = (usedMillis.toFloat() / limitMillis).coerceIn(0f, 1f)
                    val percent = ((usedMillis.toFloat() / limitMillis) * 100).toInt()
                    val displayName = goal.appDisplayName.ifBlank { goal.packageName }
                    val iconBytes = appUsage?.iconBytes ?: appIconBytesMap[goal.packageName]
                    GoalDisplayModel(
                        id = goal.id,
                        goalType = goal.goalType,
                        appPackageName = goal.packageName.ifBlank { null },
                        appIconBytes = iconBytes,
                        title = "Limite de Aplicacion",
                        subtitle = "$displayName — max ${formatMillis(limitMillis)}",
                        progressLabel = "Uso hoy",
                        progressPercent = "$percent%",
                        progressFraction = fraction,
                        isExceeded = usedMillis > limitMillis,
                        currentLimitMinutes = (limitMillis / 60_000).toInt()
                    )
                }
                GoalType.UNLOCK_LIMIT -> {
                    val limitUnlocks = goal.maxUnlocks.coerceAtLeast(1)
                    val fraction = (deviceUnlocks.toFloat() / limitUnlocks).coerceIn(0f, 1f)
                    val percent = ((deviceUnlocks.toFloat() / limitUnlocks) * 100).toInt()
                    GoalDisplayModel(
                        id = goal.id,
                        goalType = goal.goalType,
                        title = "Limite de Desbloqueos",
                        subtitle = "Maximo: $limitUnlocks desbloqueos",
                        progressLabel = "Desbloqueos hoy",
                        progressPercent = "$percent%",
                        progressFraction = fraction,
                        isExceeded = deviceUnlocks > limitUnlocks,
                        currentLimitMinutes = limitUnlocks
                    )
                }
                else -> GoalDisplayModel(
                    id = goal.id,
                    goalType = goal.goalType,
                    title = "Meta desconocida",
                    subtitle = "",
                    progressLabel = "",
                    progressPercent = "0%",
                    progressFraction = 0f,
                    isExceeded = false
                )
            }
        }
    }

    fun showCreateGoalDialog() {
        _state.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateGoalDialog() {
        _state.update { it.copy(showCreateDialog = false) }
    }

    fun submitNewGoal(typeLabel: String, selectedApps: List<String>, limitMinutes: Int) {
        scope.launch(Dispatchers.Main) {
            val goalType = when (typeLabel) {
                "Tiempo Total Diario" -> GoalType.TOTAL_DAILY
                "Limite de Aplicacion" -> GoalType.APP_LIMIT
                "Limite Desbloqueos" -> GoalType.UNLOCK_LIMIT
                else -> GoalType.TOTAL_DAILY
            }

            if (goalType == GoalType.APP_LIMIT) {
                for (appDisplayName in selectedApps) {
                    val appInfo = installedApps.find { it.displayName == appDisplayName }
                    val packageName = appInfo?.packageName ?: appDisplayName
                    val goal = AppLimitGoal(
                        goalType = goalType,
                        packageName = packageName,
                        appDisplayName = appDisplayName,
                        maxTimeMillis = limitMinutes * 60_000L,
                        maxUnlocks = 0
                    )
                    goalsRepository?.saveGoal(goal)
                }
            } else {
                val goal = AppLimitGoal(
                    goalType = goalType,
                    packageName = "",
                    appDisplayName = "",
                    maxTimeMillis = if (goalType != GoalType.UNLOCK_LIMIT) limitMinutes * 60_000L else 0L,
                    maxUnlocks = if (goalType == GoalType.UNLOCK_LIMIT) limitMinutes else 0
                )
                goalsRepository?.saveGoal(goal)
            }
        }
        hideCreateGoalDialog()
    }

    fun deleteGoal(id: Int) {
        scope.launch {
            goalsRepository?.deleteGoal(id)
        }
    }

    fun editGoal(id: Int, newLimitMinutes: Int) {
        scope.launch {
            val current = goalsRepository?.getAllGoals()?.first()
                ?.firstOrNull { it.id == id } ?: return@launch
            val updated = when (current.goalType) {
                GoalType.UNLOCK_LIMIT -> current.copy(maxUnlocks = newLimitMinutes)
                else -> current.copy(maxTimeMillis = newLimitMinutes * 60_000L)
            }
            goalsRepository.saveGoal(updated)
        }
    }

    private fun formatMillis(millis: Long): String {
        val minutes = millis / 60_000
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
}
