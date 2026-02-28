package co.edu.unicauca.DopamiNah.domain.repository

import co.edu.unicauca.DopamiNah.domain.model.UserGamificationStats
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    fun getGamificationStats(): Flow<UserGamificationStats>
    
    suspend fun incrementStreakAndPoints()
}
