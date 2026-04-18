package one.launay.deckswipe.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    onNewDeck: () -> Unit,
    onImportClick: () -> Unit,
    onOpenDeck: (Long) -> Unit,
    onStudyDeck: (Long) -> Unit,
    onEditDeck: (Long) -> Unit
) {
    val repository = LocalDeckRepository.current
    val strings = LocalStrings.current
    val vm: DeckListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                DeckListViewModel(repository = repository)
            }
        }
    )
    val state by vm.state.collectAsState()

    var deleteDeckId by remember { mutableStateOf<Long?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    deleteDeckId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteDeckId = null },
            title = { Text(text = strings.deckListDeleteConfirmTitle) },
            text = { Text(text = strings.deckListDeleteConfirmMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteDeck(id)
                        deleteDeckId = null
                    }
                ) {
                    Text(text = strings.commonOk)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDeckId = null }) {
                    Text(text = strings.commonCancel)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = strings.deckListTitle,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNewDeck,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = strings.deckListCreateDeck)
            }
            OutlinedButton(
                onClick = onImportClick,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = strings.deckListImportDeck)
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))

        when (val s = state) {
            is DeckListUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            is DeckListUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = strings.deckListLoadError)
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Button(
                        onClick = { vm.refresh() },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = strings.deckListRetry)
                    }
                }
            }
            is DeckListUiState.Loaded -> {
                if (s.rows.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = strings.deckListEmptyTitle)
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(text = strings.deckListEmptySubtitle)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.rows, key = { it.deck.id }) { row ->
                            DeckListSwipeableDeckRow(
                                row = row,
                                onOpenDeck = { onOpenDeck(row.deck.id) },
                                onStudyDeck = { onStudyDeck(row.deck.id) },
                                onEditDeck = { onEditDeck(row.deck.id) },
                                onDeleteDeck = { deleteDeckId = row.deck.id },
                                onToggleFavorite = { vm.toggleFavorite(row.deck.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
