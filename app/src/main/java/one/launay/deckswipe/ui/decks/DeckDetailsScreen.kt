package one.launay.deckswipe.ui.decks

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import coil.request.ImageRequest
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings
import one.launay.deckswipe.ui.theme.PillCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DeckDetailsScreen(
    deckId: Long,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onLearnDeck: (Long) -> Unit,
    onEditCards: (Long) -> Unit
) {
    val repository = LocalDeckRepository.current
    val strings = LocalStrings.current
    val vm: DeckDetailsViewModel = viewModel(
        key = "deck_details_$deckId",
        factory = viewModelFactory {
            initializer {
                DeckDetailsViewModel(
                    deckId = deckId,
                    repository = repository,
                    clock = { System.currentTimeMillis() }
                )
            }
        }
    )
    val state by vm.state.collectAsState()

    var titleDialog by remember { mutableStateOf(false) }
    var titleDraft by remember { mutableStateOf("") }
    var descDialog by remember { mutableStateOf(false) }
    var descDraft by remember { mutableStateOf("") }
    var tagsDialog by remember { mutableStateOf(false) }
    var tagsDraft by remember { mutableStateOf("") }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { vm.updateCoverUri(it.toString()) }
    }

    val deck = state.deck
    val title = deck?.name?.ifBlank { strings.studyUntitledDeck } ?: ""

    if (titleDialog && deck != null) {
        AlertDialog(
            onDismissRequest = { titleDialog = false },
            title = { Text(text = strings.deckDetailsEditTitle) },
            text = {
                OutlinedTextField(
                    value = titleDraft,
                    onValueChange = { titleDraft = it },
                    label = { Text(text = strings.deckDetailsTitleHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.updateTitle(titleDraft)
                        titleDialog = false
                    }
                ) {
                    Text(text = strings.commonOk)
                }
            },
            dismissButton = {
                TextButton(onClick = { titleDialog = false }) {
                    Text(text = strings.commonCancel)
                }
            }
        )
    }

    if (descDialog && deck != null) {
        AlertDialog(
            onDismissRequest = { descDialog = false },
            title = { Text(text = strings.deckDetailsDescription) },
            text = {
                OutlinedTextField(
                    value = descDraft,
                    onValueChange = { descDraft = it },
                    label = { Text(text = strings.deckDetailsDescHint) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.updateDescription(descDraft)
                        descDialog = false
                    }
                ) {
                    Text(text = strings.deckDetailsSave)
                }
            },
            dismissButton = {
                TextButton(onClick = { descDialog = false }) {
                    Text(text = strings.commonCancel)
                }
            }
        )
    }

    if (tagsDialog && deck != null) {
        AlertDialog(
            onDismissRequest = { tagsDialog = false },
            title = { Text(text = strings.deckDetailsTags) },
            text = {
                OutlinedTextField(
                    value = tagsDraft,
                    onValueChange = { tagsDraft = it },
                    label = { Text(text = strings.deckDetailsTagsHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.updateTagsFromCommaText(tagsDraft)
                        tagsDialog = false
                    }
                ) {
                    Text(text = strings.deckDetailsSave)
                }
            },
            dismissButton = {
                TextButton(onClick = { tagsDialog = false }) {
                    Text(text = strings.commonCancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = strings.studyDoneBack)
                    }
                },
                actions = {
                    if (deck != null) {
                        IconButton(onClick = { vm.toggleFavorite() }) {
                            Icon(
                                imageVector = if (deck.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = strings.deckFavoriteA11y
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(contentPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.loadError || deck == null -> {
                    Text(
                        text = strings.deckDetailsLoadError,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val context = LocalContext.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            if (deck.coverUri != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(Uri.parse(deck.coverUri))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {}
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { coverPicker.launch("image/*") },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = strings.deckDetailsChangeCover)
                            }
                            OutlinedButton(
                                onClick = { vm.updateCoverUri(null) },
                                enabled = deck.coverUri != null,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = strings.deckDetailsRemoveCover)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = deck.name.ifBlank { strings.studyUntitledDeck },
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = {
                                    titleDraft = deck.name
                                    titleDialog = true
                                }
                            ) {
                                Text(text = strings.deckDetailsEditTitle)
                            }
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = strings.deckDetailsDescription,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(
                                    onClick = {
                                        descDraft = deck.description
                                        descDialog = true
                                    }
                                ) {
                                    Text(text = strings.deckDetailsEditTitle)
                                }
                            }
                            Text(
                                text = deck.description.ifBlank { strings.deckDetailsNoDescription },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = strings.deckDetailsTags,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(
                                    onClick = {
                                        tagsDraft = deck.topicTags.joinToString(", ")
                                        tagsDialog = true
                                    }
                                ) {
                                    Text(text = strings.deckDetailsEditTitle)
                                }
                            }
                            if (deck.topicTags.isEmpty()) {
                                Text(
                                    text = strings.deckDetailsTagsHint,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    deck.topicTags.forEach { tag ->
                                        Surface(
                                            shape = PillCornerShape,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = tag,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(72.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { state.masteryProgress },
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidth = 6.dp
                                )
                                Text(
                                    text = state.totalCards.toString(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Column {
                                Text(
                                    text = strings.deckDetailsMastery + ": " + (state.masteryProgress * 100).toInt() + "%",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = strings.deckDetailsCardsCount + ": " + state.totalCards,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = strings.deckDetailsDueCount + ": " + state.dueNowCount,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { onLearnDeck(deckId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = strings.deckDetailsLearn)
                        }
                        OutlinedButton(
                            onClick = { onEditCards(deckId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = strings.deckDetailsEditDeck)
                        }
                    }
                }
            }
        }
    }
}
