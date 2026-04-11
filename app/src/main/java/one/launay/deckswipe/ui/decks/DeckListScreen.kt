package one.launay.deckswipe.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings
import one.launay.deckswipe.ui.theme.LargeCardCornerShape
import one.launay.deckswipe.ui.theme.PillCornerShape

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeckListScreen(
    onNewDeck: () -> Unit,
    onImportClick: () -> Unit,
    onOpenDeck: (Long) -> Unit
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
        OutlinedButton(
            onClick = onNewDeck,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = strings.deckListCreateDeck)
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenDeck(row.deck.id) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                shape = LargeCardCornerShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 132.dp)
                                ) {
                                    if (!row.deck.coverUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = row.deck.coverUri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clip(LargeCardCornerShape)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f)
                                                    )
                                                )
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Black.copy(alpha = 0.22f),
                                                        Color.Black.copy(alpha = 0.62f)
                                                    )
                                                )
                                            )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = row.deck.name.ifBlank { strings.studyUntitledDeck },
                                                style = MaterialTheme.typography.titleLarge,
                                                color = Color.White,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.size(52.dp)
                                                ) {
                                                    CircularProgressIndicator(
                                                        progress = { row.masteryProgress },
                                                        modifier = Modifier.fillMaxSize(),
                                                        strokeWidth = 4.dp,
                                                        color = Color.White,
                                                        trackColor = Color.White.copy(alpha = 0.25f)
                                                    )
                                                    Text(
                                                        text = row.totalCards.toString(),
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = Color.White
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { vm.toggleFavorite(row.deck.id) }
                                                ) {
                                                    Icon(
                                                        imageVector = if (row.deck.isFavorite) {
                                                            Icons.Filled.Favorite
                                                        } else {
                                                            Icons.Filled.FavoriteBorder
                                                        },
                                                        tint = Color.White,
                                                        contentDescription = strings.deckFavoriteA11y
                                                    )
                                                }
                                            }
                                        }
                                        if (row.deck.topicTags.isNotEmpty()) {
                                            Spacer(modifier = Modifier.padding(top = 12.dp))
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                row.deck.topicTags.forEach { tag ->
                                                    Surface(
                                                        shape = PillCornerShape,
                                                        color = Color.White.copy(alpha = 0.22f)
                                                    ) {
                                                        Text(
                                                            text = tag,
                                                            color = Color.White,
                                                            modifier = Modifier.padding(
                                                                horizontal = 12.dp,
                                                                vertical = 6.dp
                                                            ),
                                                            style = MaterialTheme.typography.labelMedium
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
        }
    }
}
