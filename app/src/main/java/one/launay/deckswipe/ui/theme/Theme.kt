package one.launay.deckswipe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import one.launay.deckswipe.ui.settings.AppColorScheme

private val DarkColors = darkColorScheme(
    primary = Clay,
    onPrimary = NearBlack,
    primaryContainer = ClayDark,
    onPrimaryContainer = White,
    secondary = ClayDark,
    onSecondary = White,
    secondaryContainer = ClaySurfaceDark,
    onSecondaryContainer = ClayLight,
    tertiary = ClayLight,
    onTertiary = NearBlack,
    background = NearBlack,
    onBackground = White,
    surface = ClaySurfaceDark,
    onSurface = White,
    surfaceVariant = ClayDark,
    onSurfaceVariant = ClayLight,
    outline = ClayDark,
    error = Color(0xFFB0B0B0),
    onError = NearBlack,
    errorContainer = ClayDark,
    onErrorContainer = White
)

private val LightColors = lightColorScheme(
    primary = NearBlack,
    onPrimary = White,
    primaryContainer = ClayLight,
    onPrimaryContainer = NearBlack,
    secondary = ClayDark,
    onSecondary = White,
    secondaryContainer = ClayLight,
    onSecondaryContainer = NearBlack,
    tertiary = Clay,
    onTertiary = NearBlack,
    background = ClaySurfaceLight,
    onBackground = NearBlack,
    surface = White,
    onSurface = NearBlack,
    surfaceVariant = ClayLight,
    onSurfaceVariant = NearBlack,
    outline = Clay,
    error = Color(0xFF5C5C5C),
    onError = White,
    errorContainer = ClayLight,
    onErrorContainer = NearBlack
)

private val AppShapes = Shapes(
    extraSmall = TextFieldCornerShape,
    small = TextFieldCornerShape,
    medium = PillCornerShape,
    large = LargeCardCornerShape,
    extraLarge = LargeCardCornerShape
)

@Composable
fun DeckSwipeTheme(
    colorScheme: AppColorScheme,
    content: @Composable () -> Unit
) {
    val scheme = when (colorScheme) {
        AppColorScheme.LIGHT -> LightColors
        AppColorScheme.DARK -> DarkColors
    }
    MaterialTheme(
        colorScheme = scheme,
        shapes = AppShapes,
        content = content
    )
}
