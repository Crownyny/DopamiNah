package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import kotlinx.coroutines.flow.Flow

/** Repository interface for reading and updating user gamification data (level, points, streaks). */
interface GamificationRepository {
    fun getGamificationStats(): Flow<UserGamificationStats>
    suspend fun checkAndIncrementStreak()
    suspend fun getStreak(): Int
    suspend fun getBestStreak(): Int
    suspend fun getTotalPoints(): Int
}
