package co.edu.unicauca.dopaminah.ui.screens.focusbrowser

data class BlockRule(
    val id: String,
    val domain: String,
    val label: String,
    val isActive: Boolean = true,
    val isWildcard: Boolean = false
) {
    fun matches(url: String): Boolean {
        if (!isActive) return false
        val raw = url.trim()
        val host = raw
            .removePrefix("https://").removePrefix("http://")
            .removePrefix("ftp://")
            .split("/").firstOrNull() ?: raw
        val cleanHost = host.split(":").firstOrNull() ?: host
        return if (isWildcard) {
            cleanHost.endsWith(domain.removePrefix("*."))
        } else {
            cleanHost == domain || cleanHost.endsWith(".$domain")
        }
    }
}
