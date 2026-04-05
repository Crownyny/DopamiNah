package co.edu.unicauca.dopaminah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_limit_goals")
data class AppLimitGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalType: String,
    val packageName: String = "",
    val appDisplayName: String = "",
    val maxTimeMillis: Long = 0L,
    val maxUnlocks: Int = 0,
    val currentStreak: Int = 0
)
