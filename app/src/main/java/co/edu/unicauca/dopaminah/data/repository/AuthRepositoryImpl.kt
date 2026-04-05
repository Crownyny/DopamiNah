package co.edu.unicauca.dopaminah.data.repository

import android.util.Log
import co.edu.unicauca.dopaminah.domain.model.AuthUser
import co.edu.unicauca.dopaminah.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepositoryImpl"

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: Flow<AuthUser?> = _currentUser.asStateFlow()

    init {
        Log.d(TAG, "AuthRepositoryImpl initialized")
        _currentUser.value = firebaseAuth.currentUser?.let { user ->
            Log.d(TAG, "Existing user found: ${user.email}")
            AuthUser(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        Log.d(TAG, "signInWithGoogle called with idToken length: ${idToken.length}")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d(TAG, "GoogleAuthProvider credential created")
            
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            Log.d(TAG, "Firebase signInWithCredential completed")
            
            val user = authResult.user ?: throw Exception("User is null after authentication")
            Log.d(TAG, "User authenticated: ${user.email}, uid: ${user.uid}")
            
            val authUser = AuthUser(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
            _currentUser.value = authUser
            Log.d(TAG, "AuthUser created and stored in StateFlow")
            Result.success(authUser)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithGoogle failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        Log.d(TAG, "signOut called")
        return try {
            firebaseAuth.signOut()
            _currentUser.value = null
            Log.d(TAG, "User signed out successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "signOut failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): AuthUser? {
        val user = _currentUser.value
        Log.d(TAG, "getCurrentUser called, returning: ${user?.email ?: "null"}")
        return user
    }
}
