package co.edu.unicauca.dopaminah.ui.theme

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

/**
 * Manages the user's theme preference (dark/light mode) backed by DataStore
 * so the choice persists across app restarts.
 */
class ThemeController(private val context: Context) {

    private object Keys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    /** Emits `null` when the user hasn't chosen yet (follow system), or a [Boolean]. */
    val isDarkTheme: Flow<Boolean?> = context.themeDataStore.data.map { prefs ->
        prefs[Keys.IS_DARK_MODE]
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[Keys.IS_DARK_MODE] = enabled
        }
    }
}

/** CompositionLocal providing access to the [ThemeController] instance. */
val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    error("No ThemeController provided — wrap your root composable with CompositionLocalProvider")
}
