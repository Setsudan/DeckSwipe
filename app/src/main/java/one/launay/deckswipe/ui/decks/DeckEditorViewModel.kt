package one.launay.deckswipe.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

data class CardDraft(
    val id: Long,
    val front: String,
    val back: String,
    val hint: String?
)

data class DeckEditorState(
    val deckId: Long? = null,
    val name: String = "",
    val tagsText: String = "",
    val cards: List<CardDraft> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedDeckId: Long? = null
)

class DeckEditorViewModel(
    private val repository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        DeckEditorState(
            cards = listOf(CardDraft(id = 0L, front = "", back = "", hint = null))
        )
    )
    val state: StateFlow<DeckEditorState> = _state

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name, errorMessage = null)
    }

    fun updateTags(tags: String) {
        _state.value = _state.value.copy(tagsText = tags, errorMessage = null)
    }

    fun updateCard(index: Int, front: String? = null, back: String? = null, hint: String? = null) {
        val current = _state.value.cards.toMutableList()
        if (index !in current.indices) return
        val existing = current[index]
        current[index] = existing.copy(
            front = front ?: existing.front,
            back = back ?: existing.back,
            hint = hint ?: existing.hint
        )
        _state.value = _state.value.copy(cards = current, errorMessage = null)
    }

    fun addCard() {
        val nextId = (_state.value.cards.maxOfOrNull { it.id } ?: 0L) + 1L
        _state.value = _state.value.copy(
            cards = _state.value.cards + CardDraft(
                id = nextId,
                front = "",
                back = "",
                hint = null
            ),
            errorMessage = null
        )
    }

    fun removeCard(index: Int) {
        val current = _state.value.cards.toMutableList()
        if (index !in current.indices) return
        current.removeAt(index)
        _state.value = _state.value.copy(cards = if (current.isEmpty()) {
            listOf(CardDraft(id = 0L, front = "", back = "", hint = null))
        } else {
            current
        })
    }

    fun save() {
        val current = _state.value
        val name = current.name.trim()
        if (name.isEmpty()) {
            _state.value = current.copy(errorMessage = "Deck name cannot be empty.")
            return
        }
        val validCards = current.cards
            .map { it.copy(front = it.front.trim(), back = it.back.trim()) }
            .filter { it.front.isNotEmpty() && it.back.isNotEmpty() }
        if (validCards.isEmpty()) {
            _state.value = current.copy(errorMessage = "Add at least one card with front and back.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            val tags = current.tagsText.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val deck = Deck(
                id = 0L,
                name = name,
                topicTags = tags,
                createdAtMillis = 0L,
                updatedAtMillis = 0L
            )
            val cards = validCards.map {
                Card(
                    id = 0L,
                    deckId = 0L,
                    front = it.front,
                    back = it.back,
                    hint = it.hint,
                    ease = 2.0f,
                    intervalDays = 1,
                    dueAtMillis = System.currentTimeMillis()
                )
            }
            val deckId = repository.insertDeckWithCards(deck, cards)
            _state.value = _state.value.copy(
                isSaving = false,
                savedDeckId = deckId
            )
        }
    }
}

