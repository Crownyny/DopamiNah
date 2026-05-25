package co.edu.unicauca.dopaminah.domain.model

data class AppLimitGoal(
    val id: Int = 0,
    val goalType: String,
    val packageName: String = "",
    val appDisplayName: String = "",
    val maxTimeMillis: Long = 0L,
    val maxUnlocks: Int = 0,
    val currentStreak: Int = 0
)
