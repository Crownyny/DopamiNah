package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.UserPremiumStatus
import kotlinx.coroutines.flow.Flow

/** Repository interface for managing user premium subscription status. */
interface PremiumRepository {
    fun getPremiumStatus(userId: String): Flow<UserPremiumStatus>
    suspend fun setPremiumStatus(userId: String, isPremium: Boolean): Result<Unit>
    suspend fun isPremiumUser(userId: String): Boolean
}
