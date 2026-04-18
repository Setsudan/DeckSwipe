package one.launay.deckswipe.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import one.launay.deckswipe.ui.decks.DeckCardRow
import one.launay.deckswipe.ui.decks.DeckListRow
import one.launay.deckswipe.ui.decks.deckRowForStats
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings
import one.launay.deckswipe.ui.theme.LargeCardCornerShape

data class HomeDashboardState(
    val isLoading: Boolean = true,
    val recentDeckRow: DeckListRow? = null,
    val totalDecks: Int = 0,
    val totalDueCards: Int = 0,
    val suggestionsRows: List<DeckListRow> = emptyList(),
    val errorMessage: String? = null
)

@Composable
fun HomeDashboardScreen(
    onBrowseDecks: () -> Unit,
    onOpenDeckDetails: (Long) -> Unit,
    onEditDeck: (Long) -> Unit
) {
    val repository = LocalDeckRepository.current
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    var state by remember { mutableStateOf(HomeDashboardState()) }
    var refreshNonce by remember { mutableIntStateOf(0) }

    LaunchedEffect(refreshNonce) {
        try {
            val decks = repository.getDecks()
            val now = System.currentTimeMillis()

            var totalDue = 0
            val suggestionRows = mutableListOf<DeckListRow>()

            for (deck in decks) {
                val row = deckRowForStats(repository, deck, now)
                if (row.dueNowCount > 0) {
                    suggestionRows.add(row)
                    totalDue += row.dueNowCount
                }
            }

            val recentDeckRow = decks.firstOrNull()?.let { first ->
                deckRowForStats(repository, first, now)
            }

            state = HomeDashboardState(
                isLoading = false,
                recentDeckRow = recentDeckRow,
                totalDecks = decks.size,
                totalDueCards = totalDue,
                suggestionsRows = suggestionRows
            )
        } catch (_: Throwable) {
            state = HomeDashboardState(
                isLoading = false,
                errorMessage = strings.homeLoadError
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = strings.homeDashboardTitle,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            return@Column
        }

        Text(
            text = strings.homeRecentDeckTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        val recentDeckRow = state.recentDeckRow
        if (recentDeckRow != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDeckDetails(recentDeckRow.deck.id) },
                shape = LargeCardCornerShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                DeckCardRow(
                    deck = recentDeckRow.deck,
                    totalCards = recentDeckRow.totalCards,
                    dueNowCount = recentDeckRow.dueNowCount,
                    masteryProgress = recentDeckRow.masteryProgress,
                    showTags = false,
                    onToggleFavorite = {
                        scope.launch {
                            val d = recentDeckRow.deck
                            repository.updateDeck(d.copy(isFavorite = !d.isFavorite))
                            refreshNonce++
                        }
                    },
                    onEdit = { onEditDeck(recentDeckRow.deck.id) }
                )
            }
        } else {
            Text(
                text = strings.homeNoRecentDeck,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = strings.homeStatsTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = LargeCardCornerShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.homeTotalDecksLabel,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = state.totalDecks.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.homeTotalDueCardsLabel,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = state.totalDueCards.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = strings.homeSuggestionsTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (state.suggestionsRows.isEmpty()) {
            Text(
                text = strings.homeNoSuggestions,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.suggestionsRows.forEach { row ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDeckDetails(row.deck.id) },
                        shape = LargeCardCornerShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        DeckCardRow(
                            deck = row.deck,
                            totalCards = row.totalCards,
                            dueNowCount = row.dueNowCount,
                            masteryProgress = row.masteryProgress,
                            showTags = false,
                            onToggleFavorite = {
                                scope.launch {
                                    val d = row.deck
                                    repository.updateDeck(d.copy(isFavorite = !d.isFavorite))
                                    refreshNonce++
                                }
                            },
                            onEdit = { onEditDeck(row.deck.id) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.clickable { onBrowseDecks() },
            text = strings.homeBrowseDecksLink,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
