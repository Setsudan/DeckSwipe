package one.launay.deckswipe.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalDeckRepository

@Composable
fun DeckEditorScreen(
    onSaved: (Long) -> Unit,
    onCancel: () -> Unit
) {
    val repository = LocalDeckRepository.current
    val vm: DeckEditorViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                DeckEditorViewModel(repository = repository)
            }
        }
    )
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        val msg = state.errorMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(state.savedDeckId) {
        val id = state.savedDeckId
        if (id != null) {
            onSaved(id)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "New deck")
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                onValueChange = vm::updateName,
                label = { Text(text = "Deck name") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.tagsText,
                onValueChange = vm::updateTags,
                label = { Text(text = "Tags (comma-separated, optional)") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Cards")
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
                            label = { Text(text = "Front") }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = card.back,
                            onValueChange = { vm.updateCard(index, back = it) },
                            label = { Text(text = "Back") }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = card.hint.orEmpty(),
                            onValueChange = { vm.updateCard(index, hint = it) },
                            label = { Text(text = "Hint (optional)") }
                        )
                        if (state.cards.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = { vm.removeCard(index) }) {
                                    Text(text = "Remove card")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { vm.addCard() }, enabled = !state.isSaving) {
                    Text(text = "Add card")
                }
                Row {
                    OutlinedButton(
                        onClick = onCancel,
                        enabled = !state.isSaving
                    ) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { vm.save() },
                        enabled = !state.isSaving
                    ) {
                        Text(text = "Save deck")
                    }
                }
            }
        }
    }
}

