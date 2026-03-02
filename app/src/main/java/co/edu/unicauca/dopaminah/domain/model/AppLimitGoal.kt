package co.edu.unicauca.dopaminah.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_limit_goals")
data class AppLimitGoal(
    @PrimaryKey
    val packageName: String,
    val maxTimeMillis: Long,          // Time limit in milliseconds
    val maxUnlocks: Int,              // Unlock limit
    val currentStreak: Int = 0        // Days user met the goal
)
