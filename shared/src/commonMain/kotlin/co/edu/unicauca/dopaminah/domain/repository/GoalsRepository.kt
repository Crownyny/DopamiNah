package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    fun getAllGoals(): Flow<List<AppLimitGoal>>
    suspend fun saveGoal(goal: AppLimitGoal)
    suspend fun deleteGoal(id: Int)
}
