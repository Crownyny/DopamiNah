package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals

/** Raw domain goal: URL, daily time limit, and active state. */
data class WebGoal(
    val id: String,
    val domain: String,
    val displayUrl: String,
    val dailyTimeLimitMinutes: Int,
    val isActive: Boolean = true
)

/** Display-ready web goal with computed spent time, progress, and block state. */
data class WebGoalUiModel(
    val id: String,
    val domain: String,
    val displayUrl: String,
    val dailyTimeLimitMinutes: Int,
    val todaySpentMinutes: Int,
    val progressPercent: Float,
    val isBlocked: Boolean,
    val remainingMinutes: Int,
    val isActive: Boolean
) {
    val progressFraction: Float get() = progressPercent.coerceIn(0f, 1f)
}

/** UI state for the web goals screen. */
data class WebGoalsState(
    val webGoals: List<WebGoalUiModel> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = true,
    val activeBlocks: Int = 0,
    val totalGoals: Int = 0
)
