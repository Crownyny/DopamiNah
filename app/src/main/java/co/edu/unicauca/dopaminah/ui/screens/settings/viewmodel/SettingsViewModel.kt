package co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.dopaminah.domain.model.AuthUser
import co.edu.unicauca.dopaminah.domain.repository.AuthRepository
import co.edu.unicauca.dopaminah.domain.repository.PremiumRepository
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

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeController: ThemeController,
    private val notificationHelper: NotificationHelper,
    private val authRepository: AuthRepository,
    private val premiumRepository: PremiumRepository
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

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _currentUser.value = user
                if (user != null) {
                    checkPremiumStatus(user.uid)
                } else {
                    _isPremium.value = false
                }
            }
        }
    }

    private fun checkPremiumStatus(userId: String) {
        viewModelScope.launch {
            premiumRepository.getPremiumStatus(userId).collect { status ->
                _isPremium.value = status.isPremium
            }
        }
    }

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

    fun signInWithGoogle(idToken: String) {
        Log.d(TAG, "signInWithGoogle called with idToken length: ${idToken.length}")
        viewModelScope.launch {
            _isSigningIn.value = true
            _errorMessage.value = null
            Log.d(TAG, "isSigningIn set to true")
            
            val result = authRepository.signInWithGoogle(idToken)
            Log.d(TAG, "authRepository.signInWithGoogle result: ${result.isSuccess}")
            
            result.onSuccess { user ->
                Log.d(TAG, "Sign-in successful - user: ${user.email}, uid: ${user.uid}")
                activatePremium(user.uid)
            }.onFailure { error ->
                Log.e(TAG, "Sign-in failed: ${error.message}", error)
                _errorMessage.value = "Error al iniciar sesión: ${error.message}"
                _isSigningIn.value = false
            }
        }
    }

    fun signOut() {
        Log.d(TAG, "signOut called")
        viewModelScope.launch {
            authRepository.signOut()
            _isPremium.value = false
            Log.d(TAG, "User signed out")
        }
    }

    private fun activatePremium(userId: String) {
        Log.d(TAG, "activatePremium called for userId: $userId")
        viewModelScope.launch {
            val result = premiumRepository.setPremiumStatus(userId, true)
            Log.d(TAG, "setPremiumStatus result: ${result.isSuccess}")
            
            result.onSuccess {
                Log.d(TAG, "Premium status set successfully")
                _isPremium.value = true
                _isSigningIn.value = false
                notificationHelper.showNotification(
                    id = 1000,
                    title = "¡Premium Activado!",
                    message = "Disfruta de todas las características premium"
                )
            }.onFailure { error ->
                Log.e(TAG, "Failed to set premium status: ${error.message}", error)
                _errorMessage.value = "Error al activar premium: ${error.message}"
                _isSigningIn.value = false
            }
        }
    }

    fun setError(error: String) {
        Log.d(TAG, "setError called: $error")
        _errorMessage.value = error
        _isSigningIn.value = false
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

    fun clearError() {
        _errorMessage.value = null
    }
}
