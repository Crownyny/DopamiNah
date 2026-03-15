package co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Goal types
object GoalType {
    const val TOTAL_DAILY = "TOTAL_DAILY"
    const val APP_LIMIT = "APP_LIMIT"
    const val UNLOCK_LIMIT = "UNLOCK_LIMIT"
}

/**
 * Represents a single goal as understood by the UI.
 * Keeps UI concerns (display strings) separate from raw Room entity.
 */
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
    val currentLimitMinutes: Int = 0   // Pre-filled value for the edit dialog
)

data class GoalsState(
    val goals: List<GoalDisplayModel> = emptyList(),
    val installedApps: List<String> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val goalsRepository: GoalsRepository,
    private val usageRepository: DeviceUsageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GoalsState(isLoading = true))
    val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        loadInstalledApps()
        observeGoals()
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Goals loading — reacts to Room DB changes via Flow
    // ──────────────────────────────────────────────────────────────────────────

    private fun observeGoals() {
        viewModelScope.launch {
            goalsRepository.getAllGoals().collect { rawGoals ->
                val displayModels = buildDisplayModels(rawGoals)
                _state.update { it.copy(goals = displayModels, isLoading = false) }
            }
        }
    }

    /**
     * Cross-references each stored AppLimitGoal with today's real usage data from
     * DeviceUsageRepository to compute progress fractions and exceeded flags.
     */
    private suspend fun buildDisplayModels(goals: List<AppLimitGoal>): List<GoalDisplayModel> {
        if (goals.isEmpty()) return emptyList()

        // Fetch today's usage once and build a lookup map by packageName
        val todayUsage = try {
            usageRepository.getDailyUsageStats()
        } catch (e: Exception) {
            emptyList()
        }

        val totalScreenMillis = todayUsage.sumOf { it.totalTimeForegroundMillis }
        val deviceUnlocks = try { usageRepository.getDailyDeviceUnlocks() } catch (e: Exception) { 0 }
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
                        subtitle = "Máximo: ${formatMillis(limitMillis)}",
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
                        title = "Límite de Aplicación",
                        subtitle = "$displayName — máx ${formatMillis(limitMillis)}",
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
                        title = "Límite de Desbloqueos",
                        subtitle = "Máximo: $limitUnlocks desbloqueos",
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

    // ──────────────────────────────────────────────────────────────────────────
    //  Create / Delete
    // ──────────────────────────────────────────────────────────────────────────

    fun showCreateGoalDialog() {
        _state.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateGoalDialog() {
        _state.update { it.copy(showCreateDialog = false) }
    }

    /**
     * Saves a new goal to the Room DB from the dialog selection.
     * @param typeLabel  The UI label chosen by the user (matches createGoalDialog options)
     * @param appName    Display name of the app when typeLabel == "Límite de Aplicación"
     * @param limitMinutes  The numeric limit (minutes for time-based, count for unlocks)
     */
    fun submitNewGoal(typeLabel: String, appName: String?, limitMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val goalType = when (typeLabel) {
                "Tiempo Total Diario" -> GoalType.TOTAL_DAILY
                "Límite de Aplicación" -> GoalType.APP_LIMIT
                "Límite Desbloqueos" -> GoalType.UNLOCK_LIMIT
                else -> GoalType.TOTAL_DAILY
            }

            // Resolve packageName from display name if it's an APP_LIMIT
            var packageName = ""
            if (goalType == GoalType.APP_LIMIT && appName != null) {
                packageName = resolvePackageName(appName)
            }

            val goal = AppLimitGoal(
                goalType = goalType,
                packageName = packageName,
                appDisplayName = appName ?: "",
                maxTimeMillis = if (goalType != GoalType.UNLOCK_LIMIT) limitMinutes * 60_000L else 0L,
                maxUnlocks = if (goalType == GoalType.UNLOCK_LIMIT) limitMinutes else 0
            )
            goalsRepository.saveGoal(goal)
        }
        hideCreateGoalDialog()
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            goalsRepository.deleteGoal(id)
        }
    }

    /**
     * Updates the time/unlock limit of an existing goal.
     * The new value is stored immediately but the UI makes clear it takes effect "next day".
     */
    fun editGoal(id: Int, newLimitMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = goalsRepository.getAllGoals().first()
                .firstOrNull { it.id == id } ?: return@launch
            val updated = when (current.goalType) {
                GoalType.UNLOCK_LIMIT -> current.copy(maxUnlocks = newLimitMinutes)
                else -> current.copy(maxTimeMillis = newLimitMinutes * 60_000L)
            }
            goalsRepository.saveGoal(updated)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Installed apps
    // ──────────────────────────────────────────────────────────────────────────

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            val appNames = resolvedInfos.map { it.loadLabel(pm).toString() }
                .distinct()
                .sortedWith(String.CASE_INSENSITIVE_ORDER)

            _state.update { it.copy(installedApps = appNames) }
        }
    }

    /**
     * Tries to resolve the package name for a given app display label.
     * Falls back to the label itself so saves never silently fail.
     */
    private fun resolvePackageName(displayName: String): String {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolved = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        return resolved.firstOrNull { it.loadLabel(pm).toString() == displayName }
            ?.activityInfo?.packageName ?: displayName
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private fun formatMillis(millis: Long): String {
        val minutes = millis / 60_000
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
}
