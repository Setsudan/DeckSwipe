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

sealed class DeckEditorValidationError {
    object EmptyDeckName : DeckEditorValidationError()
    object NoValidCards : DeckEditorValidationError()
}

data class DeckEditorState(
    val deckId: Long? = null,
    val originalCardIds: Set<Long> = emptySet(),
    val name: String = "",
    val tagsText: String = "",
    val cards: List<CardDraft> = emptyList(),
    val isSaving: Boolean = false,
    val validationError: DeckEditorValidationError? = null,
    val savedDeckId: Long? = null,
    val loadFailed: Boolean = false,
    val isLoadingDeck: Boolean = false
)

class DeckEditorViewModel(
    private val repository: DeckRepository,
    private val existingDeckId: Long?
) : ViewModel() {

    private val _state = MutableStateFlow(
        DeckEditorState(
            cards = listOf(CardDraft(id = 0L, front = "", back = "", hint = null)),
            isLoadingDeck = existingDeckId != null
        )
    )
    val state: StateFlow<DeckEditorState> = _state

    init {
        if (existingDeckId != null) {
            loadDeck(existingDeckId)
        }
    }

    private fun loadDeck(deckId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingDeck = true, loadFailed = false)
            try {
                val deck = repository.getDeck(deckId) ?: run {
                    _state.value = _state.value.copy(isLoadingDeck = false, loadFailed = true)
                    return@launch
                }
                val cards = repository.getCardsForDeck(deckId)
                val drafts = if (cards.isEmpty()) {
                    listOf(CardDraft(id = 0L, front = "", back = "", hint = null))
                } else {
                    cards.map { c ->
                        CardDraft(id = c.id, front = c.front, back = c.back, hint = c.hint)
                    }
                }
                _state.value = DeckEditorState(
                    deckId = deckId,
                    originalCardIds = cards.map { it.id }.toSet(),
                    name = deck.name,
                    tagsText = deck.topicTags.joinToString(", "),
                    cards = drafts,
                    isLoadingDeck = false
                )
            } catch (_: Throwable) {
                _state.value = _state.value.copy(isLoadingDeck = false, loadFailed = true)
            }
        }
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name, validationError = null)
    }

    fun updateTags(tags: String) {
        _state.value = _state.value.copy(tagsText = tags, validationError = null)
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
        _state.value = _state.value.copy(cards = current, validationError = null)
    }

    fun addCard() {
        _state.value = _state.value.copy(
            cards = _state.value.cards + CardDraft(
                id = 0L,
                front = "",
                back = "",
                hint = null
            ),
            validationError = null
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

    fun clearValidationError() {
        _state.value = _state.value.copy(validationError = null)
    }

    fun save() {
        val current = _state.value
        val name = current.name.trim()
        if (name.isEmpty()) {
            _state.value = current.copy(validationError = DeckEditorValidationError.EmptyDeckName)
            return
        }
        val validCards = current.cards
            .map { it.copy(front = it.front.trim(), back = it.back.trim()) }
            .filter { it.front.isNotEmpty() && it.back.isNotEmpty() }
        if (validCards.isEmpty()) {
            _state.value = current.copy(validationError = DeckEditorValidationError.NoValidCards)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, validationError = null)
            val tags = current.tagsText.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val now = System.currentTimeMillis()

            val existingId = current.deckId
            if (existingId != null) {
                val existingDeck = repository.getDeck(existingId) ?: run {
                    _state.value = _state.value.copy(isSaving = false, loadFailed = true)
                    return@launch
                }
                val updatedDeck = existingDeck.copy(
                    name = name,
                    topicTags = tags,
                    updatedAtMillis = now
                )
                repository.updateDeck(updatedDeck)

                val previousCards = repository.getCardsForDeck(existingId).associateBy { it.id }
                val newIds = validCards.map { it.id }.filter { it > 0L }.toSet()
                for (oid in current.originalCardIds) {
                    if (oid !in newIds) {
                        repository.deleteCard(oid)
                    }
                }
                for (draft in validCards) {
                    if (draft.id > 0L) {
                        val old = previousCards[draft.id]
                        if (old != null) {
                            repository.upsertCard(
                                old.copy(
                                    front = draft.front,
                                    back = draft.back,
                                    hint = draft.hint?.takeIf { it.isNotBlank() }
                                )
                            )
                        }
                    } else {
                        repository.upsertCard(
                            Card(
                                id = 0L,
                                deckId = existingId,
                                front = draft.front,
                                back = draft.back,
                                hint = draft.hint?.takeIf { it.isNotBlank() },
                                ease = 2.0f,
                                intervalDays = 1,
                                dueAtMillis = now
                            )
                        )
                    }
                }
                _state.value = _state.value.copy(
                    isSaving = false,
                    savedDeckId = existingId
                )
            } else {
                val deck = Deck(
                    id = 0L,
                    name = name,
                    topicTags = tags,
                    createdAtMillis = 0L,
                    updatedAtMillis = 0L,
                    isFavorite = false,
                    description = "",
                    coverUri = null
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
                        dueAtMillis = now
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
}
