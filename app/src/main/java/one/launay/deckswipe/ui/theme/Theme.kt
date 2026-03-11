package one.launay.deckswipe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = GreenCorrect,
    onPrimary = Color.Black,
    secondary = RedForgot,
    onSecondary = Color.White,
    background = DeckBackgroundDark,
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColors = lightColorScheme(
    primary = GreenCorrect,
    onPrimary = Color.White,
    secondary = RedForgot,
    onSecondary = Color.White,
    background = DeckBackgroundLight,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun DeckSwipeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

