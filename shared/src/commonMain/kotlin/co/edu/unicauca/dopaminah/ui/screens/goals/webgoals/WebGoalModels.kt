package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals

data class WebGoal(
    val id: String,
    val domain: String,
    val displayUrl: String,
    val dailyTimeLimitMinutes: Int,
    val isActive: Boolean = true
)

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

data class WebGoalsState(
    val webGoals: List<WebGoalUiModel> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = true,
    val activeBlocks: Int = 0,
    val totalGoals: Int = 0
)
