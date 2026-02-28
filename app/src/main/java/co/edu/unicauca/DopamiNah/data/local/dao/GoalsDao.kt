package co.edu.unicauca.DopamiNah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.unicauca.DopamiNah.domain.model.AppLimitGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalsDao {
    @Query("SELECT * FROM app_limit_goals")
    fun getAllGoals(): Flow<List<AppLimitGoal>>

    @Query("SELECT * FROM app_limit_goals WHERE packageName = :packageName LIMIT 1")
    fun getGoalForApp(packageName: String): Flow<AppLimitGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: AppLimitGoal)

    @Query("DELETE FROM app_limit_goals WHERE packageName = :packageName")
    suspend fun deleteGoal(packageName: String)
}
