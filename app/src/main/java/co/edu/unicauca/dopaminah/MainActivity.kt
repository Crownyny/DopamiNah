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
import co.edu.unicauca.dopaminah.ui.navigation.DopamiNahApp
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme
import co.edu.unicauca.dopaminah.ui.theme.LocalThemeController
import co.edu.unicauca.dopaminah.ui.theme.ThemeController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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