package co.edu.unicauca.dopaminah.data.repository

import co.edu.unicauca.dopaminah.data.local.dao.GoalsDao
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalsRepositoryImpl @Inject constructor(
    private val goalsDao: GoalsDao
) : GoalsRepository {

    override fun getAllGoals(): Flow<List<AppLimitGoal>> {
        return goalsDao.getAllGoals()
    }

    override fun getGoalForApp(packageName: String): Flow<AppLimitGoal?> {
        return goalsDao.getGoalForApp(packageName)
    }

    override suspend fun saveGoal(goal: AppLimitGoal) {
        goalsDao.insertGoal(goal)
    }

    override suspend fun deleteGoal(packageName: String) {
        goalsDao.deleteGoal(packageName)
    }
}
