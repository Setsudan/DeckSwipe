package one.launay.deckswipe.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

sealed class DeckListUiState {
    object Loading : DeckListUiState()
    data class Loaded(val decks: List<Deck>) : DeckListUiState()
    data class Error(val message: String) : DeckListUiState()
}

class DeckListViewModel(
    private val repository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DeckListUiState>(DeckListUiState.Loading)
    val state: StateFlow<DeckListUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = DeckListUiState.Loading
            try {
                val decks = repository.getDecks()
                _state.value = DeckListUiState.Loaded(decks)
            } catch (_: Throwable) {
                _state.value = DeckListUiState.Error("Failed to load decks.")
            }
        }
    }
}

