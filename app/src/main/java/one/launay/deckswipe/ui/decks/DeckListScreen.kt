package one.launay.deckswipe.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.theme.GradientEnd
import one.launay.deckswipe.ui.theme.GradientStart

@Composable
fun DeckListScreen(
    onNewDeck: () -> Unit,
    onImportClick: () -> Unit,
    onStudyDeck: (Long) -> Unit
) {
    val repository = LocalDeckRepository.current
    val vm: DeckListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                DeckListViewModel(repository = repository)
            }
        }
    )
    val state by vm.state.collectAsState()

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

    val gradient = Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd))

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Your decks",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            OutlinedButton(
                onClick = onNewDeck
            ) {
                Text(text = "Create deck")
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
                        Text(text = s.message)
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Button(onClick = { vm.refresh() }) {
                            Text(text = "Retry")
                        }
                    }
                }
                is DeckListUiState.Loaded -> {
                    if (s.decks.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "No decks yet.")
                            Spacer(modifier = Modifier.padding(top = 8.dp))
                            Text(text = "Create a new deck manually or import one from your favorite AI.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(s.decks) { deck ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onStudyDeck(deck.id) },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(text = deck.name)
                                        if (deck.topicTags.isNotEmpty()) {
                                            Spacer(modifier = Modifier.padding(top = 4.dp))
                                            Text(
                                                text = deck.topicTags.joinToString(separator = " • ")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

