package co.edu.unicauca.dopaminah.domain.model

/** Premium subscription status for a user, including activation and expiry timestamps. */
data class UserPremiumStatus(
    val userId: String,
    val isPremium: Boolean,
    val activationDate: Long? = null,
    val expiryDate: Long? = null
)

/** Authenticated user data returned by the auth provider. */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
