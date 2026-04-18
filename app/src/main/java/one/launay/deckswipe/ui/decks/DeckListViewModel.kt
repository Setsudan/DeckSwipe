package one.launay.deckswipe.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

data class DeckListRow(
    val deck: Deck,
    val totalCards: Int,
    val dueNowCount: Int,
    val masteryProgress: Float
)

sealed class DeckListUiState {
    object Loading : DeckListUiState()
    data class Loaded(val rows: List<DeckListRow>) : DeckListUiState()
    object Error : DeckListUiState()
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
                val now = System.currentTimeMillis()
                val rows = decks.map { deck ->
                    deckRowForStats(repository, deck, now)
                }
                _state.value = DeckListUiState.Loaded(rows)
            } catch (_: Throwable) {
                _state.value = DeckListUiState.Error
            }
        }
    }

    fun toggleFavorite(deckId: Long) {
        val loaded = _state.value as? DeckListUiState.Loaded ?: return
        val row = loaded.rows.find { it.deck.id == deckId } ?: return
        val deck = row.deck
        viewModelScope.launch {
            repository.updateDeck(deck.copy(isFavorite = !deck.isFavorite))
            _state.value = DeckListUiState.Loaded(
                loaded.rows.map { r ->
                    if (r.deck.id == deckId) {
                        r.copy(deck = deck.copy(isFavorite = !deck.isFavorite))
                    } else {
                        r
                    }
                }
            )
        }
    }

    fun deleteDeck(deckId: Long) {
        val loaded = _state.value as? DeckListUiState.Loaded ?: return
        viewModelScope.launch {
            try {
                repository.deleteDeck(deckId)
                _state.value = DeckListUiState.Loaded(
                    loaded.rows.filter { it.deck.id != deckId }
                )
            } catch (_: Throwable) {
                _state.value = DeckListUiState.Error
            }
        }
    }
}
