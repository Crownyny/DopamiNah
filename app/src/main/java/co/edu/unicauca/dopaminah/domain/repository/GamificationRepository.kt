package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    fun getGamificationStats(): Flow<UserGamificationStats>
    
    suspend fun incrementStreakAndPoints()
}
