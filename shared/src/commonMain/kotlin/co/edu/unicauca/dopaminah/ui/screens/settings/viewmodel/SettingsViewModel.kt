package co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel

import co.edu.unicauca.dopaminah.domain.model.TrustedContact
import co.edu.unicauca.dopaminah.loadTrustedContact
import co.edu.unicauca.dopaminah.saveTrustedContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel {
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _pajaroVerdeMode = MutableStateFlow(false)
    val pajaroVerdeMode: StateFlow<Boolean> = _pajaroVerdeMode.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _trustedContact = MutableStateFlow(loadTrustedContact())
    val trustedContact: StateFlow<TrustedContact> = _trustedContact.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun togglePajaroVerdeMode(enabled: Boolean) {
        _pajaroVerdeMode.value = enabled
    }

    fun setPremium(enabled: Boolean) {
        _isPremium.value = enabled
    }

    fun updateTrustedContact(name: String, phone: String) {
        val contact = TrustedContact(name = name.trim(), phone = phone.trim())
        _trustedContact.value = contact
        saveTrustedContact(contact)
    }

    fun clearTrustedContact() {
        updateTrustedContact("", "")
    }
}
