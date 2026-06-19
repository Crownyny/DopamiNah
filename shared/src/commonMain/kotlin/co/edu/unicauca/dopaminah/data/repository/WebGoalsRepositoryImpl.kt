package co.edu.unicauca.dopaminah.data.repository

import co.edu.unicauca.dopaminah.data.db.DopamiNahDb
import co.edu.unicauca.dopaminah.domain.repository.WebGoalsRepository
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoal

class WebGoalsRepositoryImpl(
    private val db: DopamiNahDb
) : WebGoalsRepository {

    private val queries get() = db.dopamiNahDbQueries

    override fun loadGoals(): List<WebGoal> {
        return queries.getAllWebGoals().executeAsList().map { row ->
            WebGoal(
                id = row.id,
                domain = row.domain,
                displayUrl = row.display_url,
                dailyTimeLimitMinutes = row.daily_time_limit_minutes.toInt(),
                isActive = row.is_active != 0L
            )
        }
    }

    override fun saveGoals(goals: List<WebGoal>) {
        queries.transaction {
            queries.deleteAllWebGoals()
            goals.forEach { goal ->
                queries.insertWebGoal(
                    id = goal.id,
                    domain = goal.domain,
                    display_url = goal.displayUrl,
                    daily_time_limit_minutes = goal.dailyTimeLimitMinutes.toLong(),
                    is_active = if (goal.isActive) 1L else 0L
                )
            }
        }
    }

    override fun loadAccumulatedMinutes(): Map<String, Int> {
        return queries.getAllAccumulatedMinutes().executeAsList()
            .associate { row -> row.domain to row.minutes.toInt() }
    }

    override fun saveAccumulatedMinutes(minutes: Map<String, Int>) {
        queries.transaction {
            queries.deleteAllAccumulatedMinutes()
            minutes.entries
                .filter { it.value > 0 }
                .forEach { (domain, mins) ->
                    queries.insertAccumulatedMinute(
                        domain = domain,
                        minutes = mins.toLong()
                    )
                }
        }
    }
}
