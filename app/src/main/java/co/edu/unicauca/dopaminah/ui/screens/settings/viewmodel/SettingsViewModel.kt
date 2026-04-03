package co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.dopaminah.ui.theme.ThemeController
import co.edu.unicauca.dopaminah.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeController: ThemeController,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean?> = themeController.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _pajaroVerdeMode = MutableStateFlow(false)
    val pajaroVerdeMode: StateFlow<Boolean> = _pajaroVerdeMode.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themeController.setDarkMode(enabled)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun togglePajaroVerdeMode(enabled: Boolean) {
        _pajaroVerdeMode.value = enabled
    }

    fun setPremium(enabled: Boolean) {
        _isPremium.value = enabled
    }

    fun sendTestNotification() {
        notificationHelper.showNotification(
            id = 999,
            title = "Prueba de DopamiNah",
            message = "¡Las notificaciones están funcionando correctamente!"
        )
    }
}
