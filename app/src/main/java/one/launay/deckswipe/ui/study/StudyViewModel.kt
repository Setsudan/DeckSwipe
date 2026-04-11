package one.launay.deckswipe.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.repository.DeckRepository
import one.launay.deckswipe.domain.spacedrepetition.ReviewResult
import one.launay.deckswipe.domain.spacedrepetition.SpacedRepetitionEngine

data class StudyUiState(
    val isLoading: Boolean = true,
    val deckTitle: String = "",
    val currentCard: Card? = null,
    val remainingCount: Int = 0,
    val sessionReviewsCompleted: Int = 0
)

class StudyViewModel(
    private val deckId: Long,
    private val repository: DeckRepository,
    private val engine: SpacedRepetitionEngine
) : ViewModel() {

    private val _state = MutableStateFlow(StudyUiState(isLoading = true))
    val state: StateFlow<StudyUiState> = _state

    private var queue: MutableList<Card> = mutableListOf()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val now = System.currentTimeMillis()
            val decks = repository.getDecks()
            val title = decks.find { it.id == deckId }?.name?.trim().orEmpty()
            val cards = repository.getDueCardsForDeck(deckId, now)
            queue = cards.toMutableList()
            _state.value = StudyUiState(
                isLoading = false,
                deckTitle = title,
                currentCard = queue.firstOrNull(),
                remainingCount = queue.size,
                sessionReviewsCompleted = 0
            )
        }
    }

    fun onKnewIt() {
        handleReview(ReviewResult.KNEW_IT)
    }

    fun onForgot() {
        handleReview(ReviewResult.FORGOT)
    }

    private fun handleReview(result: ReviewResult) {
        val card = _state.value.currentCard ?: return
        viewModelScope.launch {
            val update = engine.review(card, result)
            repository.upsertCard(update.updatedCard)
            if (result == ReviewResult.FORGOT) {
                queue.add(update.updatedCard)
            }
            if (queue.isNotEmpty()) {
                queue.removeAt(0)
            }
            val nextCard = queue.firstOrNull()
            val prev = _state.value
            _state.value = prev.copy(
                currentCard = nextCard,
                remainingCount = queue.size,
                sessionReviewsCompleted = prev.sessionReviewsCompleted + 1
            )
        }
    }
}
