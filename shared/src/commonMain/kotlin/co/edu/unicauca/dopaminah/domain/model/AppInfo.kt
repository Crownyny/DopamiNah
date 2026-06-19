package co.edu.unicauca.dopaminah.domain.model

data class AppInfo(
    val displayName: String,
    val packageName: String,
    val iconBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AppInfo
        return displayName == other.displayName &&
                packageName == other.packageName &&
                iconBytes.contentEquals(other.iconBytes)
    }

    override fun hashCode(): Int {
        var result = displayName.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + (iconBytes?.contentHashCode() ?: 0)
        return result
    }
}
