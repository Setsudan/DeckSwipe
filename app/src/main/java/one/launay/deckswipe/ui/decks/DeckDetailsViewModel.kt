package one.launay.deckswipe.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

data class DeckDetailsUiState(
    val isLoading: Boolean = true,
    val deck: Deck? = null,
    val totalCards: Int = 0,
    val dueNowCount: Int = 0,
    val masteryProgress: Float = 0f,
    val loadError: Boolean = false
)

class DeckDetailsViewModel(
    private val deckId: Long,
    private val repository: DeckRepository,
    private val clock: () -> Long
) : ViewModel() {

    private val _state = MutableStateFlow(DeckDetailsUiState())
    val state: StateFlow<DeckDetailsUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = DeckDetailsUiState(isLoading = true)
            try {
                val deck = repository.getDeck(deckId) ?: run {
                    _state.value = DeckDetailsUiState(isLoading = false, loadError = true)
                    return@launch
                }
                val now = clock()
                val total = repository.getCardCountForDeck(deckId)
                val due = repository.getDueCardsForDeck(deckId, now).size
                val mastered = (total - due).coerceAtLeast(0)
                val mastery = if (total == 0) {
                    0f
                } else {
                    mastered.toFloat() / total.toFloat()
                }
                _state.value = DeckDetailsUiState(
                    isLoading = false,
                    deck = deck,
                    totalCards = total,
                    dueNowCount = due,
                    masteryProgress = mastery
                )
            } catch (_: Throwable) {
                _state.value = DeckDetailsUiState(isLoading = false, loadError = true)
            }
        }
    }

    fun toggleFavorite() {
        val d = _state.value.deck ?: return
        viewModelScope.launch {
            repository.updateDeck(d.copy(isFavorite = !d.isFavorite))
            refresh()
        }
    }

    fun updateTitle(name: String) {
        val d = _state.value.deck ?: return
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.updateDeck(
                d.copy(name = trimmed, updatedAtMillis = clock())
            )
            refresh()
        }
    }

    fun updateDescription(description: String) {
        val d = _state.value.deck ?: return
        viewModelScope.launch {
            repository.updateDeck(
                d.copy(description = description, updatedAtMillis = clock())
            )
            refresh()
        }
    }

    fun updateTagsFromCommaText(tagsText: String) {
        val d = _state.value.deck ?: return
        val tags = tagsText.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        viewModelScope.launch {
            repository.updateDeck(
                d.copy(topicTags = tags, updatedAtMillis = clock())
            )
            refresh()
        }
    }

    fun updateCoverUri(uri: String?) {
        val d = _state.value.deck ?: return
        viewModelScope.launch {
            repository.updateDeck(
                d.copy(coverUri = uri, updatedAtMillis = clock())
            )
            refresh()
        }
    }
}
