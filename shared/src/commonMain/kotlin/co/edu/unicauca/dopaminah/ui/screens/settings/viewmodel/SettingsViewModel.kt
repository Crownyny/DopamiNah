package co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel {
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _notifications = MutableStateFlow(true)
    val notifications: StateFlow<Boolean> = _notifications.asStateFlow()

    fun toggleDarkMode() {
        _darkMode.value = !_darkMode.value
    }

    fun toggleNotifications() {
        _notifications.value = !_notifications.value
    }
}
