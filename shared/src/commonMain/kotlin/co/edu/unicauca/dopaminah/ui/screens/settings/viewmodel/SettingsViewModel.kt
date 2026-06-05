package co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** ViewModel for the Settings screen, managing notification, Pajaro Verde mode, and premium toggles. */
class SettingsViewModel {
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _pajaroVerdeMode = MutableStateFlow(false)
    val pajaroVerdeMode: StateFlow<Boolean> = _pajaroVerdeMode.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun togglePajaroVerdeMode(enabled: Boolean) {
        _pajaroVerdeMode.value = enabled
    }

    fun setPremium(enabled: Boolean) {
        _isPremium.value = enabled
    }
}
