package co.edu.unicauca.dopaminah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import co.edu.unicauca.dopaminah.ui.navigation.DopamiNahApp
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme
import co.edu.unicauca.dopaminah.ui.theme.LocalThemeController
import co.edu.unicauca.dopaminah.ui.theme.ThemeController
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point of the application.
 * * Key responsibilities:
 * - @AndroidEntryPoint: Enables Hilt dependency injection for this activity.
 * - installSplashScreen(): Handles the brand splash screen transition (Android 12+).
 * - enableEdgeToEdge(): Configures the UI to be drawn behind system bars.
 * - themeController: Manages the dynamic switching between light and dark themes 
 * based on user preferences or system settings.
 * - CompositionLocalProvider: Provides the ThemeController to the entire Compose 
 * hierarchy via LocalThemeController.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val themeController = ThemeController(applicationContext)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            val userChoice by themeController.isDarkTheme.collectAsState(initial = null)
            val darkTheme = userChoice ?: isSystemInDarkTheme()

            CompositionLocalProvider(LocalThemeController provides themeController) {
                DopamiNahTheme(darkTheme = darkTheme) {
                    DopamiNahApp()
                }
            }
        }
    }
}
