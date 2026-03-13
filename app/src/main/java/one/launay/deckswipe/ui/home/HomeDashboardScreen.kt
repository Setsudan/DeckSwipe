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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings

data class HomeDashboardState(
    val isLoading: Boolean = true,
    val recentDeck: Deck? = null,
    val totalDecks: Int = 0,
    val totalDueCards: Int = 0,
    val decksToReview: List<Deck> = emptyList(),
    val errorMessage: String? = null
)

@Composable
fun HomeDashboardScreen(
    onBrowseDecks: () -> Unit,
    onStudyDeck: (Long) -> Unit
) {
    val repository = LocalDeckRepository.current
    val strings = LocalStrings.current

    var state by remember { mutableStateOf(HomeDashboardState()) }

    LaunchedEffect(Unit) {
        try {
            val decks = repository.getDecks()
            val now = System.currentTimeMillis()

            var totalDue = 0
            val decksToReview = mutableListOf<Deck>()

            for (deck in decks) {
                val dueCards = repository.getDueCardsForDeck(deck.id, now)
                if (dueCards.isNotEmpty()) {
                    decksToReview.add(deck)
                    totalDue += dueCards.size
                }
            }

            val recentDeck = decks.firstOrNull()

            state = HomeDashboardState(
                isLoading = false,
                recentDeck = recentDeck,
                totalDecks = decks.size,
                totalDueCards = totalDue,
                decksToReview = decksToReview
            )
        } catch (_: Throwable) {
            state = HomeDashboardState(
                isLoading = false,
                errorMessage = "Failed to load home data."
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

        // Recent deck
        Text(
            text = strings.homeRecentDeckTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        val recentDeck = state.recentDeck
        if (recentDeck != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStudyDeck(recentDeck.id) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = recentDeck.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = strings.homeTotalDueCardsLabel + ": " + state.totalDueCards,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            Text(
                text = strings.homeNoRecentDeck,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Statistics
        Text(
            text = strings.homeStatsTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
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

        // Suggestions
        Text(
            text = strings.homeSuggestionsTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (state.decksToReview.isEmpty()) {
            Text(
                text = strings.homeNoSuggestions,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.decksToReview.forEach { deck ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStudyDeck(deck.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = deck.name,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.clickable { onBrowseDecks() },
            text = strings.navBrowse,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

