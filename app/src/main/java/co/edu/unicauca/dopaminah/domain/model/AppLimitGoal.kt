package co.edu.unicauca.dopaminah.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user-defined usage limit goal stored in the Room database.
 * - TOTAL_DAILY: limits total screen time per day (maxTimeMillis)
 * - APP_LIMIT: limits a specific app's usage (maxTimeMillis, packageName + appDisplayName)
 * - UNLOCK_LIMIT: limits number of device unlocks (maxUnlocks)
 */
@Entity(tableName = "app_limit_goals")
data class AppLimitGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalType: String,             // "TOTAL_DAILY" | "APP_LIMIT" | "UNLOCK_LIMIT"
    val packageName: String = "",     // Only used for APP_LIMIT
    val appDisplayName: String = "",  // Human-readable name of the target app
    val maxTimeMillis: Long = 0L,     // Time limit in milliseconds (TOTAL_DAILY / APP_LIMIT)
    val maxUnlocks: Int = 0,          // Unlock limit (UNLOCK_LIMIT)
    val currentStreak: Int = 0        // Days user met the goal
)

