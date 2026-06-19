package co.edu.unicauca.dopaminah.domain.model

data class TrustedContact(
    val name: String = "",
    val phone: String = ""
) {
    val isConfigured: Boolean get() = name.isNotBlank() && phone.isNotBlank()
}
