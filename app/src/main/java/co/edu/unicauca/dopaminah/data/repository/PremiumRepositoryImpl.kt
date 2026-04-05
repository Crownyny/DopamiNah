package co.edu.unicauca.dopaminah.data.repository

import co.edu.unicauca.dopaminah.domain.model.UserPremiumStatus
import co.edu.unicauca.dopaminah.domain.repository.PremiumRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : PremiumRepository {

    override fun getPremiumStatus(userId: String): Flow<UserPremiumStatus> = callbackFlow {
        val reference = database.getReference("premiumUsers/$userId")
        
        val listener = reference.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                try {
                    val data = snapshot.getValue<Map<String, Any?>>()
                    val status = UserPremiumStatus(
                        userId = userId,
                        isPremium = data?.get("isPremium") as? Boolean ?: false,
                        activationDate = (data?.get("activationDate") as? Number)?.toLong(),
                        expiryDate = (data?.get("expiryDate") as? Number)?.toLong()
                    )
                    trySend(status)
                } catch (e: Exception) {
                    trySend(UserPremiumStatus(userId = userId, isPremium = false))
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose {
            reference.removeEventListener(listener)
        }
    }

    override suspend fun setPremiumStatus(userId: String, isPremium: Boolean): Result<Unit> {
        return try {
            val reference = database.getReference("premiumUsers/$userId")
            val data = mapOf(
                "isPremium" to isPremium,
                "activationDate" to if (isPremium) System.currentTimeMillis() else null,
                "expiryDate" to null
            )
            reference.setValue(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isPremiumUser(userId: String): Boolean {
        return try {
            val reference = database.getReference("premiumUsers/$userId/isPremium")
            val snapshot = reference.get().await()
            snapshot.getValue<Boolean>() ?: false
        } catch (e: Exception) {
            false
        }
    }
}
