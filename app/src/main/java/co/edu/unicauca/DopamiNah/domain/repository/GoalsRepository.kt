package co.edu.unicauca.DopamiNah.domain.repository

import co.edu.unicauca.DopamiNah.domain.model.AppLimitGoal
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    fun getAllGoals(): Flow<List<AppLimitGoal>>
    fun getGoalForApp(packageName: String): Flow<AppLimitGoal?>
    suspend fun saveGoal(goal: AppLimitGoal)
    suspend fun deleteGoal(packageName: String)
}
