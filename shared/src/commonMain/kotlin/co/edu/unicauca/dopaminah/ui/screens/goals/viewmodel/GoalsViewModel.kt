package co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel

import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
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

object GoalType {
    const val TOTAL_DAILY = "TOTAL_DAILY"
    const val APP_LIMIT = "APP_LIMIT"
    const val UNLOCK_LIMIT = "UNLOCK_LIMIT"
}

data class GoalDisplayModel(
    val id: Int,
    val goalType: String,
    val appPackageName: String? = null,
    val title: String,
    val subtitle: String,
    val progressLabel: String,
    val progressPercent: String,
    val progressFraction: Float,
    val isExceeded: Boolean,
    val currentLimitMinutes: Int = 0
)

data class GoalsState(
    val goals: List<GoalDisplayModel> = emptyList(),
    val installedApps: List<String> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = false
)

class GoalsViewModel(
    private val goalsRepository: GoalsRepository? = null,
    private val deviceUsageRepository: DeviceUsageRepository? = null,
    private val installedApps: Map<String, String> = emptyMap()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(
        GoalsState(
            installedApps = installedApps.keys.toList(),
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
                val displayModels = buildDisplayModels(rawGoals)
                _state.update { it.copy(goals = displayModels, isLoading = false) }
            }
        }
    }

    private suspend fun buildDisplayModels(goals: List<AppLimitGoal>): List<GoalDisplayModel> {
        if (goals.isEmpty()) return emptyList()

        val todayUsage = try {
            deviceUsageRepository?.getDailyUsageStats() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val totalScreenMillis = todayUsage.sumOf { it.totalTimeForegroundMillis }
        val deviceUnlocks = try {
            deviceUsageRepository?.getDailyDeviceUnlocks() ?: 0
        } catch (_: Exception) {
            0
        }
        val usageByPackage = todayUsage.associateBy { it.packageName }

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
                    GoalDisplayModel(
                        id = goal.id,
                        goalType = goal.goalType,
                        appPackageName = goal.packageName.ifBlank { null },
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

    fun submitNewGoal(typeLabel: String, appName: String?, limitMinutes: Int) {
        scope.launch(Dispatchers.Main) {
            val goalType = when (typeLabel) {
                "Tiempo Total Diario" -> GoalType.TOTAL_DAILY
                "Limite de Aplicacion" -> GoalType.APP_LIMIT
                "Limite Desbloqueos" -> GoalType.UNLOCK_LIMIT
                else -> GoalType.TOTAL_DAILY
            }

            var packageName = ""
            if (goalType == GoalType.APP_LIMIT && appName != null) {
                packageName = installedApps[appName] ?: appName
            }

            val goal = AppLimitGoal(
                goalType = goalType,
                packageName = packageName,
                appDisplayName = appName ?: "",
                maxTimeMillis = if (goalType != GoalType.UNLOCK_LIMIT) limitMinutes * 60_000L else 0L,
                maxUnlocks = if (goalType == GoalType.UNLOCK_LIMIT) limitMinutes else 0
            )
            goalsRepository?.saveGoal(goal)
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
