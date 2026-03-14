package co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// For now, representing the goal visually. Later this will map to a Room Entity.
data class GoalUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTintColor: Color,
    val progressLabel: String,
    val progressPercent: String,
    val progressFraction: Float,
    val isExceeded: Boolean
)

data class GoalsState(
    val goals: List<GoalUiModel> = emptyList(),
    val installedApps: List<String> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(GoalsState())
    val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        loadMockGoals()
        loadInstalledApps()
    }

    private fun loadMockGoals() {
        _state.update { it.copy(isLoading = true) }
        
        // We will populate this from Compose later where we have access to Theme Colors and Icons
        // For now, we'll keep the list empty and let the screen render the mock data directly until
        // we fully connect it, or we can pass the Theme colors from the UI later. 
        // A better approach is to keep UI concerns out of ViewModel, so the ViewModel should hold
        // raw data (type, limit, current progress) and the UI maps it to colors/icons.
        
        _state.update { it.copy(isLoading = false) }
    }

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

    fun showCreateGoalDialog() {
        _state.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateGoalDialog() {
        _state.update { it.copy(showCreateDialog = false) }
    }

    fun submitNewGoal(type: String, appName: String?, limitMinutes: Int) {
        // TODO: Save to Room DB. For now, just close dialog.
        // We will receive something like type="Límite de Aplicación", appName="Instagram", limitMinutes=30
        hideCreateGoalDialog()
    }
}
