package one.launay.deckswipe.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import one.launay.deckswipe.ui.decks.DeckListScreen
import one.launay.deckswipe.ui.decks.DeckEditorScreen
import one.launay.deckswipe.ui.home.CreateDeckScreen
import one.launay.deckswipe.ui.home.HomeDashboardScreen
import one.launay.deckswipe.ui.import.ImportScreen
import one.launay.deckswipe.ui.settings.SettingsScreen
import one.launay.deckswipe.ui.settings.SettingsViewModel
import one.launay.deckswipe.ui.study.StudyScreen

object Routes {
    const val HOME = "home"
    const val DECK_LIST = "deck_list"
    const val NEW_DECK = "new_deck"
    const val CREATE = "create"
    const val IMPORT = "import"
    const val STUDY = "study/{deckId}"
    const val SETTINGS = "settings"
    const val CARDS = "cards"
    const val BROWSE = "browse"
    const val ANALYTICS = "analytics"
}

@Composable
fun DeckSwipeNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeDashboardScreen(
                onBrowseDecks = { navController.navigate(Routes.BROWSE) },
                onStudyDeck = { deckId ->
                    navController.navigate("study/$deckId")
                }
            )
        }
        composable(Routes.CREATE) {
            CreateDeckScreen(
                onAiDeck = { navController.navigate(Routes.IMPORT) },
                onManualDeck = { navController.navigate(Routes.NEW_DECK) }
            )
        }
        composable(Routes.DECK_LIST) {
            DeckListScreen(
                onNewDeck = { navController.navigate(Routes.NEW_DECK) },
                onImportClick = { navController.navigate(Routes.IMPORT) },
                onStudyDeck = { deckId ->
                    navController.navigate("study/$deckId")
                }
            )
        }
        composable(Routes.NEW_DECK) {
            DeckEditorScreen(
                onSaved = { deckId ->
                    navController.popBackStack()
                    navController.navigate("study/$deckId")
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.IMPORT) {
            ImportScreen(
                onImportFinished = { newDeckId ->
                    navController.popBackStack()
                    navController.navigate("study/$newDeckId")
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.STUDY
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: return@composable
            StudyScreen(
                deckId = deckId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CARDS) {
            SimplePlaceholder(text = "All cards (coming soon)")
        }
        composable(Routes.BROWSE) {
            DeckListScreen(
                onNewDeck = { navController.navigate(Routes.NEW_DECK) },
                onImportClick = { navController.navigate(Routes.IMPORT) },
                onStudyDeck = { deckId ->
                    navController.navigate("study/$deckId")
                }
            )
        }
        composable(Routes.ANALYTICS) {
            SimplePlaceholder(text = "Analytics (coming soon)")
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                paddingValues = contentPadding,
                viewModel = settingsViewModel
            )
        }
    }
}

@Composable
private fun SimplePlaceholder(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

