package co.edu.unicauca.dopaminah.domain.model

data class UserPremiumStatus(
    val userId: String,
    val isPremium: Boolean,
    val activationDate: Long? = null,
    val expiryDate: Long? = null
)

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
