package co.edu.unicauca.dopaminah.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Material 3 color schemes ────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = DopaminahPurpleLight,
    onPrimary = Color(0xFF1C1B1F),
    primaryContainer = DopaminahPurpleDark,
    onPrimaryContainer = DopaminahPurpleLight,
    secondary = DopaminahOrangeLight,
    onSecondary = Color.White,
    secondaryContainer = DopaminahOrangeDark,
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF3D3D40),
)

private val LightColorScheme = lightColorScheme(
    primary = DopaminahPurple,
    onPrimary = Color.White,
    primaryContainer = DopaminahPurpleDark,
    onPrimaryContainer = DopaminahPurpleLight,
    secondary = DopaminahOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB3),
    onSecondaryContainer = DopaminahOrangeDark,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFF3F4F6),
)

// ── Theme composable ────────────────────────────────────────────────

@Composable
fun DopamiNahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// ── Extension property for convenient access ────────────────────────

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
