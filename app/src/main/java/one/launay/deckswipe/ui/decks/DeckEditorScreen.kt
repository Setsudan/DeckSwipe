package one.launay.deckswipe.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings

@Composable
fun DeckEditorScreen(
    contentPadding: PaddingValues,
    existingDeckId: Long? = null,
    onSaved: (Long) -> Unit,
    onCancel: () -> Unit
) {
    val repository = LocalDeckRepository.current
    val strings = LocalStrings.current
    val vm: DeckEditorViewModel = viewModel(
        key = "deck_editor_${existingDeckId ?: "new"}",
        factory = viewModelFactory {
            initializer {
                DeckEditorViewModel(
                    repository = repository,
                    existingDeckId = existingDeckId
                )
            }
        }
    )
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.validationError) {
        when (val err = state.validationError) {
            DeckEditorValidationError.EmptyDeckName -> {
                snackbarHostState.showSnackbar(strings.deckEditorErrorEmptyName)
                vm.clearValidationError()
            }
            DeckEditorValidationError.NoValidCards -> {
                snackbarHostState.showSnackbar(strings.deckEditorErrorNoCards)
                vm.clearValidationError()
            }
            null -> Unit
        }
    }

    LaunchedEffect(state.savedDeckId) {
        val id = state.savedDeckId
        if (id != null) {
            onSaved(id)
        }
    }

    val screenTitle = if (existingDeckId != null) {
        strings.deckEditorTitleEdit
    } else {
        strings.deckEditorTitle
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoadingDeck -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(contentPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            state.loadFailed -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(contentPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.deckDetailsLoadError,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onCancel,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = strings.deckEditorCancel)
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(contentPadding)
                        .padding(16.dp)
                ) {
                    Text(text = screenTitle)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.name,
                        onValueChange = vm::updateName,
                        label = { Text(text = strings.deckEditorDeckName) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.tagsText,
                        onValueChange = vm::updateTags,
                        label = { Text(text = strings.deckEditorTags) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = strings.deckEditorCardsSection)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(state.cards) { index, card ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = card.front,
                                    onValueChange = { vm.updateCard(index, front = it) },
                                    label = { Text(text = strings.deckEditorFront) }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = card.back,
                                    onValueChange = { vm.updateCard(index, back = it) },
                                    label = { Text(text = strings.deckEditorBack) }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = card.hint.orEmpty(),
                                    onValueChange = { vm.updateCard(index, hint = it) },
                                    label = { Text(text = strings.deckEditorHint) }
                                )
                                if (state.cards.size > 1) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = { vm.removeCard(index) },
                                            shape = MaterialTheme.shapes.medium
                                        ) {
                                            Text(text = strings.deckEditorRemoveCard)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { vm.addCard() },
                            enabled = !state.isSaving,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = strings.deckEditorAddCard)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onCancel,
                                enabled = !state.isSaving,
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = strings.deckEditorCancel)
                            }
                            Button(
                                onClick = { vm.save() },
                                enabled = !state.isSaving,
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = strings.deckEditorSave)
                            }
                        }
                    }
                }
            }
        }
    }
}
