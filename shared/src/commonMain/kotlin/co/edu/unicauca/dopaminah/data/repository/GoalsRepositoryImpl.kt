package co.edu.unicauca.dopaminah.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.edu.unicauca.dopaminah.data.db.DopamiNahDb
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GoalsRepositoryImpl(
    private val db: DopamiNahDb
) : GoalsRepository {

    private val queries get() = db.dopamiNahDbQueries

    override fun getAllGoals(): Flow<List<AppLimitGoal>> {
        return queries.getAllAppLimitGoals()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    AppLimitGoal(
                        id = row.id.toInt(),
                        goalType = row.goal_type,
                        packageName = row.package_name,
                        appDisplayName = row.app_display_name,
                        maxTimeMillis = row.max_time_millis,
                        maxUnlocks = row.max_unlocks.toInt(),
                        currentStreak = row.current_streak.toInt()
                    )
                }
            }
    }

    override suspend fun saveGoal(goal: AppLimitGoal) = withContext(Dispatchers.Default) {
        if (goal.id == 0) {
            val nextId = queries.getNextAppLimitGoalId().executeAsOne()
            queries.insertAppLimitGoal(
                id = nextId,
                goal_type = goal.goalType,
                package_name = goal.packageName,
                app_display_name = goal.appDisplayName,
                max_time_millis = goal.maxTimeMillis,
                max_unlocks = goal.maxUnlocks.toLong(),
                current_streak = goal.currentStreak.toLong()
            )
        } else {
            queries.insertAppLimitGoal(
                id = goal.id.toLong(),
                goal_type = goal.goalType,
                package_name = goal.packageName,
                app_display_name = goal.appDisplayName,
                max_time_millis = goal.maxTimeMillis,
                max_unlocks = goal.maxUnlocks.toLong(),
                current_streak = goal.currentStreak.toLong()
            )
        }
        Unit
    }

    override suspend fun deleteGoal(id: Int) = withContext(Dispatchers.Default) {
        queries.deleteAppLimitGoal(id.toLong())
        Unit
    }
}
