package co.edu.unicauca.dopaminah.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Brand purple — used as primary color, accent bars, and interactive elements. */
val DopaminahPurple = Color(0xFF8B5CF6)
val DopaminahPurpleDark = Color(0xFF6D28D9)
val DopaminahPurpleLight = Color(0xFFDDD6FE)

/** Brand orange — used for highlights, tips, and secondary accents. */
val DopaminahOrange = Color(0xFFFA832B)
val DopaminahOrangeLight = Color(0xFFFA832B)
val DopaminahOrangeDark = Color(0xFFFA832B)

val DopaminahRedDark = Color(0xFF451A1A)
val DopaminahRedText = Color(0xFFEF4444)

/** Semantic colors for state indication. */
val SuccessGreen = Color(0xFF22C55E)
val SuccessGreenLight = Color(0xFF4ADE80)
val SuccessGreenDark = Color(0xFF2D4739)
val WarningYellow = Color(0xFFEAB308)
val WarningYellowDark = Color(0xFF2E2E2E)
val WarningYellowAccent = Color(0xFF1E1E1E)
val DangerRed = Color(0xFFEF4444)

/** Light theme background/surface/text colors. */
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceCard = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)

/** Dark theme background/surface/text colors. */
val BackgroundDark = Color(0xFF1C1B1F)
val SurfaceDark = Color(0xFF2B2930)
val TextPrimaryDark = Color(0xFFE6E1E5)
val TextSecondaryDark = Color(0xFFCAC4D0)

val StatCardDark = Color(0xFF28252C)
val StatCardPink = Color(0xFFE91E63)

/**
 * Extended brand colors provided via CompositionLocal.
 * Accessible as [MaterialTheme.extendedColors] inside [DopamiNahTheme].
 */
@Immutable
data class ExtendedColors(
    val aboutSurface: Color,
    val aboutBorder: Color,
    val successGreen: Color,
    val warningYellow: Color,
    val dangerRed: Color,
    val brandPurple: Color,
    val brandOrange: Color,
)

val LightExtendedColors = ExtendedColors(
    aboutSurface = Color(0xFFFAF5FF),
    aboutBorder = Color(0xFFE9D5FF),
    successGreen = SuccessGreen,
    warningYellow = WarningYellow,
    dangerRed = DangerRed,
    brandPurple = DopaminahPurple,
    brandOrange = DopaminahOrange,
)

val DarkExtendedColors = ExtendedColors(
    aboutSurface = Color(0xFF2A1A4A),
    aboutBorder = Color(0xFF4C1D95),
    successGreen = SuccessGreen,
    warningYellow = WarningYellow,
    dangerRed = DangerRed,
    brandPurple = DopaminahPurpleLight,
    brandOrange = DopaminahOrangeLight,
)

/** CompositionLocal key for accessing [ExtendedColors] throughout the composable tree. */
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/** Gradient color pairs used for chart lines/bars in stats screens. */
val AppChartColorsList = listOf(
    listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
    listOf(Color(0xFFF43F5E), Color(0xFFFB7185)),
    listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)),
    listOf(Color(0xFF10B981), Color(0xFF6EE7B7)),
    listOf(Color(0xFFF59E0B), Color(0xFFFCD34D)),
    listOf(Color(0xFF6366F1), Color(0xFFA5B4FC)),
    listOf(Color(0xFFEC4899), Color(0xFFF9A8D4)),
    listOf(Color(0xFF14B8A6), Color(0xFF5EEAD4))
)
