package co.edu.unicauca.dopaminah.data.repository

import co.edu.unicauca.dopaminah.data.local.dao.GoalsDao
import co.edu.unicauca.dopaminah.data.mapper.toDomain
import co.edu.unicauca.dopaminah.data.mapper.toEntity
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalsRepositoryImpl @Inject constructor(
    private val goalsDao: GoalsDao
) : GoalsRepository {

    override fun getAllGoals(): Flow<List<AppLimitGoal>> {
        return goalsDao.getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveGoal(goal: AppLimitGoal) {
        goalsDao.insertGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(id: Int) {
        goalsDao.deleteGoal(id)
    }
}
