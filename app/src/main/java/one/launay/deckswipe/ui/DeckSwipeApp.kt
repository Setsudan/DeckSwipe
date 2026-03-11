package one.launay.deckswipe.ui

import android.app.Application
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.compose.rememberNavController
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.data.db.DeckSwipeDatabase
import one.launay.deckswipe.data.repository.DeckRepositoryImpl
import one.launay.deckswipe.domain.repository.DeckRepository
import one.launay.deckswipe.ui.navigation.DeckSwipeNavHost
import one.launay.deckswipe.ui.theme.DeckSwipeTheme
import kotlinx.serialization.json.Json

val LocalDeckRepository = staticCompositionLocalOf<DeckRepository> {
    error("DeckRepository not provided")
}

val LocalClipboardImporter = staticCompositionLocalOf<ClipboardImporter> {
    error("ClipboardImporter not provided")
}

@Composable
fun DeckSwipeApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val appContext = context.applicationContext as Application
    val db = DeckSwipeDatabase.getInstance(appContext)
    val repository: DeckRepository = DeckRepositoryImpl(
        dao = db.deckSwipeDao(),
        clock = { System.currentTimeMillis() }
    )
    val importer = ClipboardImporter(
        json = Json { ignoreUnknownKeys = true },
        nowProvider = { System.currentTimeMillis() }
    )

    DeckSwipeTheme {
        CompositionLocalProvider(
            LocalDeckRepository provides repository,
            LocalClipboardImporter provides importer
        ) {
            Surface {
                val navController = rememberNavController()
                DeckSwipeNavHost(navController = navController)
            }
        }
    }
}

