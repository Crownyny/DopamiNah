package co.edu.unicauca.dopaminah.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Brand colors (constant across themes) ──────────────────────────
val DopaminahPurple = Color(0xFF8B5CF6)
val DopaminahPurpleDark = Color(0xFF6D28D9)
val DopaminahPurpleLight = Color(0xFFDDD6FE)

val DopaminahOrange = Color(0xFFF97316)
val DopaminahOrangeLight = Color(0xFFFB923C)
val DopaminahOrangeDark = Color(0xFFC2410C)

val DopaminahRedDark = Color(0xFF451A1A)
val DopaminahRedText = Color(0xFFEF4444)

// ── Semantic status colors ──────────────────────────────────────────
val SuccessGreen = Color(0xFF22C55E)
val WarningYellow = Color(0xFFEAB308)
val DangerRed = Color(0xFFEF4444)

// ── Light scheme raw tokens ─────────────────────────────────────────
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceCard = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)

// ── Dark scheme raw tokens ──────────────────────────────────────────
val BackgroundDark = Color(0xFF1C1B1F)
val SurfaceDark = Color(0xFF2B2930)
val TextPrimaryDark = Color(0xFFE6E1E5)
val TextSecondaryDark = Color(0xFFCAC4D0)

// ── Extended colors (theme-aware, non-Material3 slots) ──────────────
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

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }