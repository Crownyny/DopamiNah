package co.edu.unicauca.dopaminah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.unicauca.dopaminah.data.local.entity.AppLimitGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalsDao {
    @Query("SELECT * FROM app_limit_goals")
    fun getAllGoals(): Flow<List<AppLimitGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: AppLimitGoalEntity)

    @Query("DELETE FROM app_limit_goals WHERE id = :id")
    suspend fun deleteGoal(id: Int)
}
