package one.launay.deckswipe.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.data.db.DeckSwipeDatabase
import one.launay.deckswipe.data.repository.DeckRepositoryImpl
import one.launay.deckswipe.domain.repository.DeckRepository
import one.launay.deckswipe.ui.navigation.DeckSwipeNavHost
import one.launay.deckswipe.ui.navigation.Routes
import one.launay.deckswipe.ui.settings.AppColorScheme
import one.launay.deckswipe.ui.settings.AppLanguage
import one.launay.deckswipe.ui.settings.SettingsViewModel
import one.launay.deckswipe.ui.strings.Strings
import one.launay.deckswipe.ui.strings.StringsEn
import one.launay.deckswipe.ui.strings.StringsFr
import one.launay.deckswipe.ui.theme.DeckSwipeTheme
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.json.Json
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import one.launay.deckswipe.ui.theme.GradientEnd
import one.launay.deckswipe.ui.theme.GradientStart

val LocalDeckRepository = staticCompositionLocalOf<DeckRepository> {
    error("DeckRepository not provided")
}

val LocalClipboardImporter = staticCompositionLocalOf<ClipboardImporter> {
    error("ClipboardImporter not provided")
}

val LocalStrings = staticCompositionLocalOf<Strings> {
    StringsEn
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

    val settingsViewModel: SettingsViewModel = viewModel()
    val settingsState by settingsViewModel.state.collectAsState()
    val strings = when (settingsState.language) {
        AppLanguage.EN -> StringsEn
        AppLanguage.FR -> StringsFr
    }

    DeckSwipeTheme(colorScheme = settingsState.colorScheme) {
        CompositionLocalProvider(
            LocalDeckRepository provides repository,
            LocalClipboardImporter provides importer,
            LocalStrings provides strings
        ) {
            Surface {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        DeckSwipeBottomBar(
                            currentRoute = currentRoute,
                            strings = strings,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(Routes.HOME) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    val modifier = when (settingsState.colorScheme) {
                        AppColorScheme.LIGHT -> {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                            Modifier.background(gradient)
                        }
                        AppColorScheme.DARK -> {
                            Modifier.background(MaterialTheme.colorScheme.background)
                        }
                    }

                    Box(modifier = modifier) {
                        DeckSwipeNavHost(
                            navController = navController,
                            contentPadding = innerPadding,
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckSwipeBottomBar(
    currentRoute: String?,
    strings: Strings,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            NavigationBarItem(
                selected = currentRoute == Routes.HOME,
                onClick = { onNavigate(Routes.HOME) },
                label = { Text(text = strings.navHome) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = strings.navHome
                    )
                }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.CARDS,
                onClick = { onNavigate(Routes.CARDS) },
                label = { Text(text = strings.navCards) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = strings.navCards
                    )
                }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.BROWSE,
                onClick = { onNavigate(Routes.BROWSE) },
                label = { Text(text = strings.navBrowse) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = strings.navBrowse
                    )
                }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.SETTINGS,
                onClick = { onNavigate(Routes.SETTINGS) },
                label = { Text(text = strings.navSettings) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = strings.navSettings
                    )
                }
            )
        }

        FloatingActionButton(
            onClick = { onNavigate(Routes.CREATE) },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(text = "+")
        }
    }
}

