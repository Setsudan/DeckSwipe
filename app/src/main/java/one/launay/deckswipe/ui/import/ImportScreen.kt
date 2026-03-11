package one.launay.deckswipe.ui.import

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalClipboardImporter
import one.launay.deckswipe.ui.LocalDeckRepository

@Composable
fun ImportScreen(
    onImportFinished: (Long) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val importer = LocalClipboardImporter.current
    val repository = LocalDeckRepository.current
    val vm: ImportViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ImportViewModel(
                    context = context,
                    clipboardImporter = importer,
                    repository = repository
                )
            }
        }
    )
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        when (val s = state) {
            is ImportUiState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                vm.reset()
            }
            is ImportUiState.Success -> {
                onImportFinished(s.deckId)
                vm.reset()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You can generate decks with your favorite AI or paste JSON you prepared yourself. Your data never leaves this device.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val clipboard = android.content.ClipboardManager::class
                    val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val prompt = """
Use this JSON schema to generate a DeckSwipe flashcard deck. Respond ONLY with minified JSON that matches exactly this structure, no explanations:

{
  "deck_name": "String",
  "topic_tags": ["String"],
  "cards": [
    {
      "front": "String",
      "back": "String",
      "hint": "String (optional)"
    }
  ]
}

Topic: <describe topic here>
Level: <difficulty/level here>
Number of cards: <e.g. 20>
                    """.trimIndent()
                    val clip = android.content.ClipData.newPlainText("DeckSwipe AI prompt", prompt)
                    cm.setPrimaryClip(clip)
                    // snackbar handled via effect on state; here we reuse host just for UX
                    // immediate feedback is not necessary beyond system toast
                }
            ) {
                Text(text = "Copy AI prompt to clipboard")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { vm.importFromClipboard() },
                enabled = state !is ImportUiState.Importing
            ) {
                if (state is ImportUiState.Importing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(24.dp)
                    )
                } else {
                    Text(text = "Import from clipboard")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCancel,
                enabled = state !is ImportUiState.Importing
            ) {
                Text(text = "Cancel")
            }
        }
    }
}

