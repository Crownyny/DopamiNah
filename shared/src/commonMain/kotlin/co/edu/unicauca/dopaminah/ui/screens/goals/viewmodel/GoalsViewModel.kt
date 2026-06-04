package co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel

import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal

class GoalsViewModel {
    val goals = listOf(
        AppLimitGoal(id = 1, goalType = "TOTAL_DAILY", maxTimeMillis = 14400000L, currentStreak = 3),
        AppLimitGoal(id = 2, goalType = "APP_LIMIT", packageName = "com.instagram.android", appDisplayName = "Instagram", maxTimeMillis = 3600000L, currentStreak = 5),
        AppLimitGoal(id = 3, goalType = "UNLOCK_LIMIT", maxUnlocks = 50, currentStreak = 2),
    )
}
