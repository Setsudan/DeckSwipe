package one.launay.deckswipe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import one.launay.deckswipe.DeckSwipeApplication
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.domain.repository.DeckRepository
import one.launay.deckswipe.ui.navigation.DeckSwipeNavHost
import one.launay.deckswipe.ui.navigation.Routes
import one.launay.deckswipe.ui.settings.AppLanguage
import one.launay.deckswipe.ui.settings.SettingsViewModel
import one.launay.deckswipe.ui.strings.Strings
import one.launay.deckswipe.ui.strings.StringsEn
import one.launay.deckswipe.ui.strings.StringsFr
import one.launay.deckswipe.ui.theme.DeckSwipeTheme
import one.launay.deckswipe.ui.theme.PillCornerShape

val LocalDeckRepository = staticCompositionLocalOf<DeckRepository> {
    error("DeckRepository not provided")
}

val LocalClipboardImporter = staticCompositionLocalOf<ClipboardImporter> {
    error("ClipboardImporter not provided")
}

val LocalStrings = staticCompositionLocalOf<Strings> {
    StringsEn
}

private val NavPillFabSpacing = 12.dp
private val FloatingChromeEdgePadding = 16.dp
private val ContentBottomReserve = 88.dp

@Composable
fun DeckSwipeApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as DeckSwipeApplication
    val repository = app.deckRepository
    val importer = app.clipboardImporter

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
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                val onNavigate: (String) -> Unit = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .statusBarsPadding()
                            .displayCutoutPadding()
                            .navigationBarsPadding()
                            .padding(bottom = ContentBottomReserve)
                    ) {
                        DeckSwipeNavHost(
                            navController = navController,
                            contentPadding = PaddingValues(),
                            settingsViewModel = settingsViewModel
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(top = 8.dp, end = FloatingChromeEdgePadding),
                        shape = PillCornerShape,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp,
                        shadowElevation = 2.dp
                    ) {
                        IconButton(
                            onClick = { onNavigate(Routes.SETTINGS) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = strings.floatingSettingsA11y
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(
                                start = FloatingChromeEdgePadding,
                                end = FloatingChromeEdgePadding,
                                bottom = 16.dp
                            ),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = PillCornerShape,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                NavPillIcon(
                                    selected = currentRoute == Routes.HOME,
                                    onClick = { onNavigate(Routes.HOME) },
                                    imageVector = Icons.Filled.Home,
                                    contentDescription = strings.navHome
                                )
                                NavPillIcon(
                                    selected = currentRoute == Routes.CARDS,
                                    onClick = { onNavigate(Routes.CARDS) },
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = strings.navCards
                                )
                                NavPillIcon(
                                    selected = currentRoute == Routes.DECK_LIST,
                                    onClick = { onNavigate(Routes.DECK_LIST) },
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = strings.navDecks
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(NavPillFabSpacing))
                        FloatingActionButton(
                            onClick = { onNavigate(Routes.CREATE) },
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 3.dp
                            ),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = strings.fabCreateDeck
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavPillIcon(
    selected: Boolean,
    onClick: () -> Unit,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    val colors = IconButtonDefaults.iconButtonColors(
        containerColor = if (selected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            Color.Transparent
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    )
    IconButton(
        onClick = onClick,
        colors = colors,
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}
