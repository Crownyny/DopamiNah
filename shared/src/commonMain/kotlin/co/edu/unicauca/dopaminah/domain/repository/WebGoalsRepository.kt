package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoal

/** Repository for persisting web goals and their accumulated domain time. */
interface WebGoalsRepository {
    fun loadGoals(): List<WebGoal>
    fun saveGoals(goals: List<WebGoal>)
    fun loadAccumulatedMinutes(): Map<String, Int>
    fun saveAccumulatedMinutes(minutes: Map<String, Int>)
}
