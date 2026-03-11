package one.launay.deckswipe.ui.import

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.data.clipboard.ImportResult
import one.launay.deckswipe.domain.repository.DeckRepository

sealed class ImportUiState {
    object Idle : ImportUiState()
    object Importing : ImportUiState()
    data class Success(val deckId: Long) : ImportUiState()
    data class Error(val message: String) : ImportUiState()
}

class ImportViewModel(
    private val context: Context,
    private val clipboardImporter: ClipboardImporter,
    private val repository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val state: StateFlow<ImportUiState> = _state

    fun importFromClipboard() {
        viewModelScope.launch {
            _state.value = ImportUiState.Importing
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip

            if (clip == null || !clipboard.hasPrimaryClip()) {
                _state.value = ImportUiState.Error("Clipboard is empty.")
                return@launch
            }

            val description: ClipDescription = clip.description
            if (!description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) &&
                !description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
            ) {
                _state.value = ImportUiState.Error("Clipboard does not contain text.")
                return@launch
            }

            val item = clip.getItemAt(0)
            val text = item.text?.toString()

            when (val result = clipboardImporter.importFromJson(text)) {
                is ImportResult.Error.EmptyInput -> {
                    _state.value = ImportUiState.Error("Clipboard text is empty.")
                }
                is ImportResult.Error.InvalidJson -> {
                    _state.value = ImportUiState.Error("Clipboard text is not valid JSON.")
                }
                is ImportResult.Error.MissingRequiredFields -> {
                    _state.value = ImportUiState.Error("JSON is missing required fields.")
                }
                is ImportResult.Success -> {
                    val deckId = repository.insertDeckWithCards(result.deck, result.cards)
                    _state.value = ImportUiState.Success(deckId = deckId)
                }
            }
        }
    }

    fun reset() {
        _state.value = ImportUiState.Idle
    }
}

