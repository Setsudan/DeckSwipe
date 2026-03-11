package one.launay.deckswipe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import one.launay.deckswipe.ui.decks.DeckListScreen
import one.launay.deckswipe.ui.decks.DeckEditorScreen
import one.launay.deckswipe.ui.import.ImportScreen
import one.launay.deckswipe.ui.study.StudyScreen

object Routes {
    const val DECK_LIST = "deck_list"
    const val NEW_DECK = "new_deck"
    const val IMPORT = "import"
    const val STUDY = "study/{deckId}"
}

@Composable
fun DeckSwipeNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DECK_LIST
    ) {
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
    }
}

