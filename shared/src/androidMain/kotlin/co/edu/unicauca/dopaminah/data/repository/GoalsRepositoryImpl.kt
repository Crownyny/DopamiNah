package co.edu.unicauca.dopaminah.data.repository

import android.content.Context
import android.content.SharedPreferences
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalsRepositoryImpl(context: Context) : GoalsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("goals_prefs", Context.MODE_PRIVATE)

    private val _goals = MutableStateFlow(loadGoals())

    override fun getAllGoals(): Flow<List<AppLimitGoal>> = _goals.asStateFlow()

    override suspend fun saveGoal(goal: AppLimitGoal) {
        val current = _goals.value.toMutableList()
        if (goal.id != 0) {
            val idx = current.indexOfFirst { it.id == goal.id }
            if (idx >= 0) current[idx] = goal else current.add(goal)
        } else {
            val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
            current.add(goal.copy(id = newId))
        }
        persist(current)
        _goals.value = current
    }

    override suspend fun deleteGoal(id: Int) {
        val current = _goals.value.filter { it.id != id }
        persist(current)
        _goals.value = current
    }

    private fun loadGoals(): List<AppLimitGoal> {
        val count = prefs.getInt("goal_count", 0)
        if (count == 0) return emptyList()
        val list = mutableListOf<AppLimitGoal>()
        for (i in 0 until count) {
            val prefix = "goal_$i"
            val id = prefs.getInt("${prefix}_id", 0)
            if (id == 0) continue
            list.add(
                AppLimitGoal(
                    id = id,
                    goalType = prefs.getString("${prefix}_goalType", "") ?: "",
                    packageName = prefs.getString("${prefix}_packageName", "") ?: "",
                    appDisplayName = prefs.getString("${prefix}_appDisplayName", "") ?: "",
                    maxTimeMillis = prefs.getLong("${prefix}_maxTimeMillis", 0L),
                    maxUnlocks = prefs.getInt("${prefix}_maxUnlocks", 0),
                    currentStreak = prefs.getInt("${prefix}_currentStreak", 0)
                )
            )
        }
        return list
    }

    private fun persist(goals: List<AppLimitGoal>) {
        prefs.edit().clear().apply()
        prefs.edit().putInt("goal_count", goals.size).apply()
        goals.forEachIndexed { i, goal ->
            val prefix = "goal_$i"
            prefs.edit()
                .putInt("${prefix}_id", goal.id)
                .putString("${prefix}_goalType", goal.goalType)
                .putString("${prefix}_packageName", goal.packageName)
                .putString("${prefix}_appDisplayName", goal.appDisplayName)
                .putLong("${prefix}_maxTimeMillis", goal.maxTimeMillis)
                .putInt("${prefix}_maxUnlocks", goal.maxUnlocks)
                .putInt("${prefix}_currentStreak", goal.currentStreak)
                .apply()
        }
    }
}
