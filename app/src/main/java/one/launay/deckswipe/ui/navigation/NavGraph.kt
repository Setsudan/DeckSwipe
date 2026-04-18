package one.launay.deckswipe.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import one.launay.deckswipe.ui.decks.DeckDetailsScreen
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
    const val DECK_DETAILS = "deck/{deckId}"
    const val EDIT_DECK = "edit_deck/{deckId}"
    const val SETTINGS = "settings"
    const val CARDS = "cards"
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
            Box(Modifier.padding(contentPadding)) {
                HomeDashboardScreen(
                    onBrowseDecks = { navController.navigate(Routes.CARDS) },
                    onOpenDeckDetails = { deckId ->
                        navController.navigate("deck/$deckId")
                    },
                    onEditDeck = { deckId ->
                        navController.navigate("edit_deck/$deckId")
                    }
                )
            }
        }
        composable(Routes.CREATE) {
            Box(Modifier.padding(contentPadding)) {
                CreateDeckScreen(
                    onAiDeck = { navController.navigate(Routes.IMPORT) },
                    onManualDeck = { navController.navigate(Routes.NEW_DECK) }
                )
            }
        }
        composable(Routes.DECK_LIST) {
            Box(Modifier.padding(contentPadding)) {
                DeckListScreen(
                    onNewDeck = { navController.navigate(Routes.NEW_DECK) },
                    onImportClick = { navController.navigate(Routes.IMPORT) },
                    onOpenDeck = { deckId ->
                        navController.navigate("deck/$deckId")
                    },
                    onStudyDeck = { deckId ->
                        navController.navigate("study/$deckId")
                    },
                    onEditDeck = { deckId ->
                        navController.navigate("edit_deck/$deckId")
                    }
                )
            }
        }
        composable(Routes.NEW_DECK) {
            DeckEditorScreen(
                contentPadding = contentPadding,
                existingDeckId = null,
                onSaved = { deckId ->
                    navController.popBackStack()
                    navController.navigate("deck/$deckId")
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.EDIT_DECK,
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            DeckEditorScreen(
                contentPadding = contentPadding,
                existingDeckId = deckId,
                onSaved = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.IMPORT) {
            ImportScreen(
                contentPadding = contentPadding,
                onImportFinished = { newDeckId ->
                    navController.popBackStack()
                    navController.navigate("deck/$newDeckId")
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.DECK_DETAILS,
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            DeckDetailsScreen(
                deckId = deckId,
                contentPadding = contentPadding,
                onBack = { navController.popBackStack() },
                onLearnDeck = { id ->
                    navController.navigate("study/$id")
                },
                onEditCards = { id ->
                    navController.navigate("edit_deck/$id")
                }
            )
        }
        composable(
            route = Routes.STUDY
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: return@composable
            StudyScreen(
                deckId = deckId,
                contentPadding = contentPadding,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CARDS) {
            Box(Modifier.padding(contentPadding)) {
                DeckListScreen(
                    onNewDeck = { navController.navigate(Routes.NEW_DECK) },
                    onImportClick = { navController.navigate(Routes.IMPORT) },
                    onOpenDeck = { deckId ->
                        navController.navigate("deck/$deckId")
                    },
                    onStudyDeck = { deckId ->
                        navController.navigate("study/$deckId")
                    },
                    onEditDeck = { deckId ->
                        navController.navigate("edit_deck/$deckId")
                    }
                )
            }
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                paddingValues = contentPadding,
                viewModel = settingsViewModel
            )
        }
    }
}

