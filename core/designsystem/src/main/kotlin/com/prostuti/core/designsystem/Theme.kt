package com.prostuti.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Prostuti Signature Brand Colors
val ProstutiCrimson = Color(0xFF9E1B32)
val ProstutiCrimsonDark = Color(0xFF6B0014)
val ProstutiCrimsonLight = Color(0xFFFFF1F2)
val ProstutiCrimsonContainer = Color(0xFFFFE4E6)
val ProstutiGold = Color(0xFFD97706)
val ProstutiGoldLight = Color(0xFFFEF3C7)
val ProstutiTeal = Color(0xFF0D9488)
val ProstutiTealLight = Color(0xFFCCFBF1)
val ProstutiNavy = Color(0xFF1E293B)

val ProstutiGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF9E1B32), Color(0xFFC41E3A), Color(0xFFE11D48))
)

val ProstutiCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFF8F8))
)

val ProstutiHeroDarkGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF881337), Color(0xFF4C0519), Color(0xFF1E0108))
)

private val LightColorScheme = lightColorScheme(
    primary = ProstutiCrimson,
    onPrimary = Color.White,
    primaryContainer = ProstutiCrimsonContainer,
    onPrimaryContainer = ProstutiCrimsonDark,
    secondary = ProstutiTeal,
    onSecondary = Color.White,
    secondaryContainer = ProstutiTealLight,
    onSecondaryContainer = Color(0xFF003731),
    tertiary = ProstutiGold,
    onTertiary = Color.White,
    tertiaryContainer = ProstutiGoldLight,
    onTertiaryContainer = Color(0xFF78350F),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFDA4AF),
    onPrimary = Color(0xFF4C0519),
    primaryContainer = Color(0xFF881337),
    onPrimaryContainer = Color(0xFFFFE4E6),
    secondary = Color(0xFF5EEAD4),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = Color(0xFFFCD34D),
    onTertiary = Color(0xFF78350F),
    background = Color(0xFF0B0F17),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF141B26),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
)

@Composable
fun ProstutiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
