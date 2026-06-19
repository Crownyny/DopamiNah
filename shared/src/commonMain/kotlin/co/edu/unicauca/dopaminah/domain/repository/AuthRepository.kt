package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/** Repository interface for authentication operations: sign-in, sign-out, and observing the current user. */
interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): AuthUser?
}
