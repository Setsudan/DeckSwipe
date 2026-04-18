package one.launay.deckswipe.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import one.launay.deckswipe.ui.LocalStrings
import one.launay.deckswipe.ui.theme.LargeCardCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListSwipeableDeckRow(
    row: DeckListRow,
    onOpenDeck: () -> Unit,
    onStudyDeck: () -> Unit,
    onEditDeck: () -> Unit,
    onDeleteDeck: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            when (newValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onStudyDeck()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> false
                SwipeToDismissBoxValue.Settled -> true
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            SwipeBackground(
                swipeValue = dismissState.targetValue,
                studyLabel = strings.deckListSwipeStudy,
                editLabel = strings.deckListSwipeEdit,
                deleteA11y = strings.deckListSwipeDeleteA11y,
                onEditClick = {
                    onEditDeck()
                    scope.launch { dismissState.reset() }
                },
                onDeleteClick = {
                    onDeleteDeck()
                    scope.launch { dismissState.reset() }
                }
            )
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDeck() },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = LargeCardCornerShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                DeckCardRow(
                    deck = row.deck,
                    totalCards = row.totalCards,
                    dueNowCount = row.dueNowCount,
                    masteryProgress = row.masteryProgress,
                    showTags = true,
                    onToggleFavorite = onToggleFavorite,
                    onEdit = onEditDeck
                )
            }
        }
    )
}

@Composable
private fun RowScope.SwipeBackground(
    swipeValue: SwipeToDismissBoxValue,
    studyLabel: String,
    editLabel: String,
    deleteA11y: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    when (swipeValue) {
        SwipeToDismissBoxValue.StartToEnd -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = studyLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
        SwipeToDismissBoxValue.EndToStart -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = editLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clickable(onClick = onEditClick)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = deleteA11y,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        SwipeToDismissBoxValue.Settled -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            )
        }
    }
}
