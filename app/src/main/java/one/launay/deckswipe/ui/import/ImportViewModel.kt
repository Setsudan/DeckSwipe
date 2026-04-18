package one.launay.deckswipe.ui.import

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.data.clipboard.DeckSwipeJsonImportPrompt
import one.launay.deckswipe.data.clipboard.ImportResult
import one.launay.deckswipe.data.document.SourceDocumentFailure
import one.launay.deckswipe.data.document.SourceDocumentReader
import one.launay.deckswipe.data.document.SourceDocumentResult
import one.launay.deckswipe.domain.repository.DeckRepository

sealed class ImportFailure {
    object ClipboardEmpty : ImportFailure()
    object ClipboardNotText : ImportFailure()
    object TextEmpty : ImportFailure()
    object InvalidJson : ImportFailure()
    object MissingFields : ImportFailure()
}

sealed class ImportUiState {
    object Idle : ImportUiState()
    object Importing : ImportUiState()
    data class Success(val deckId: Long) : ImportUiState()
    data class Error(val failure: ImportFailure) : ImportUiState()
}

sealed class DocumentForAiEvent {
    object Idle : DocumentForAiEvent()
    data class Error(val failure: SourceDocumentFailure) : DocumentForAiEvent()
    data class Success(val truncated: Boolean) : DocumentForAiEvent()
}

class ImportViewModel(
    private val clipboardImporter: ClipboardImporter,
    private val repository: DeckRepository,
    private val clipboardAccessor: ClipboardAccessor
) : ViewModel() {

    private val _state = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val state: StateFlow<ImportUiState> = _state

    private val _documentEvent = MutableStateFlow<DocumentForAiEvent>(DocumentForAiEvent.Idle)
    val documentEvent: StateFlow<DocumentForAiEvent> = _documentEvent

    private val _documentBusy = MutableStateFlow(false)
    val documentBusy: StateFlow<Boolean> = _documentBusy

    fun copyPromptOnly() {
        clipboardAccessor.writePlainText(
            "DeckSwipe AI prompt",
            DeckSwipeJsonImportPrompt.schemaPromptOnly()
        )
    }

    fun prepareDocumentForAi(resolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _documentBusy.value = true
            try {
                when (val r = SourceDocumentReader.extractText(resolver, uri)) {
                    is SourceDocumentResult.Ok -> {
                        val clip = DeckSwipeJsonImportPrompt.buildClipboardWithSource(
                            sourceBody = r.text,
                            truncated = r.truncated
                        )
                        clipboardAccessor.writePlainText(
                            "DeckSwipe AI prompt + source",
                            clip
                        )
                        _documentEvent.value = DocumentForAiEvent.Success(truncated = r.truncated)
                    }
                    is SourceDocumentResult.Error -> {
                        _documentEvent.value = DocumentForAiEvent.Error(r.failure)
                    }
                }
            } finally {
                _documentBusy.value = false
            }
        }
    }

    fun resetDocumentEvent() {
        _documentEvent.value = DocumentForAiEvent.Idle
    }

    fun importFromClipboard() {
        viewModelScope.launch {
            _state.value = ImportUiState.Importing
            when (val outcome = clipboardAccessor.read()) {
                is ClipboardReadOutcome.EmptyClipboard -> {
                    _state.value = ImportUiState.Error(ImportFailure.ClipboardEmpty)
                    return@launch
                }
                is ClipboardReadOutcome.UnsupportedMime -> {
                    _state.value = ImportUiState.Error(ImportFailure.ClipboardNotText)
                    return@launch
                }
                is ClipboardReadOutcome.Ok -> {
                    when (val result = clipboardImporter.importFromJson(outcome.text)) {
                        is ImportResult.Error.EmptyInput -> {
                            _state.value = ImportUiState.Error(ImportFailure.TextEmpty)
                        }
                        is ImportResult.Error.InvalidJson -> {
                            _state.value = ImportUiState.Error(ImportFailure.InvalidJson)
                        }
                        is ImportResult.Error.MissingRequiredFields -> {
                            _state.value = ImportUiState.Error(ImportFailure.MissingFields)
                        }
                        is ImportResult.Success -> {
                            val deckId = repository.insertDeckWithCards(result.deck, result.cards)
                            _state.value = ImportUiState.Success(deckId = deckId)
                        }
                    }
                }
            }
        }
    }

    fun reset() {
        _state.value = ImportUiState.Idle
    }
}
