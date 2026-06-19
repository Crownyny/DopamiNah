package co.edu.unicauca.dopaminah.data.repository

import co.edu.unicauca.dopaminah.DevicePreferences
import co.edu.unicauca.dopaminah.domain.repository.WebGoalsRepository
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoal

class DevicePreferencesWebGoalsRepository(
    private val prefs: DevicePreferences
) : WebGoalsRepository {

    override fun loadGoals(): List<WebGoal> {
        val raw = prefs.getString(GOALS_KEY, "")
        if (raw.isEmpty()) return emptyList()
        return raw.split(GOAL_SEPARATOR).mapNotNull { entry ->
            val parts = entry.split(FIELD_SEPARATOR)
            if (parts.size < 4) return@mapNotNull null
            WebGoal(
                id = parts[0],
                domain = parts[1],
                displayUrl = parts[2],
                dailyTimeLimitMinutes = parts[3].toIntOrNull() ?: 0,
                isActive = parts.getOrNull(4)?.toBooleanStrictOrNull() ?: true
            )
        }
    }

    override fun saveGoals(goals: List<WebGoal>) {
        val raw = goals.joinToString(GOAL_SEPARATOR) { goal ->
            listOf(
                goal.id,
                goal.domain,
                goal.displayUrl,
                goal.dailyTimeLimitMinutes.toString(),
                goal.isActive.toString()
            ).joinToString(FIELD_SEPARATOR)
        }
        prefs.putString(GOALS_KEY, raw)
    }

    override fun loadAccumulatedMinutes(): Map<String, Int> {
        val raw = prefs.getString(MINUTES_KEY, "")
        if (raw.isEmpty()) return emptyMap()
        return raw.split(GOAL_SEPARATOR).mapNotNull { entry ->
            val parts = entry.split(FIELD_SEPARATOR)
            if (parts.size < 2) return@mapNotNull null
            parts[0] to (parts[1].toIntOrNull() ?: 0)
        }.toMap()
    }

    override fun saveAccumulatedMinutes(minutes: Map<String, Int>) {
        val raw = minutes.entries
            .filter { it.value > 0 }
            .joinToString(GOAL_SEPARATOR) { (domain, mins) ->
                "$domain$FIELD_SEPARATOR$mins"
            }
        prefs.putString(MINUTES_KEY, raw)
    }

    private companion object {
        private const val GOALS_KEY = "web_goals"
        private const val MINUTES_KEY = "web_accumulated_minutes"
        private const val GOAL_SEPARATOR = "\u001e"
        private const val FIELD_SEPARATOR = "\u001f"
    }
}
