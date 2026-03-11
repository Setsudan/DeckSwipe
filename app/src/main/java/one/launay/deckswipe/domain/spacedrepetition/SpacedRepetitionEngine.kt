package one.launay.deckswipe.domain.spacedrepetition

import one.launay.deckswipe.domain.model.Card
import kotlin.math.max
import kotlin.math.min

enum class ReviewResult {
    KNEW_IT,
    FORGOT
}

data class SpacedRepetitionUpdate(
    val updatedCard: Card
)

class SpacedRepetitionEngine(
    private val nowProvider: () -> Long
) {

    fun review(card: Card, result: ReviewResult): SpacedRepetitionUpdate {
        val now = nowProvider()
        return when (result) {
            ReviewResult.KNEW_IT -> handleKnewIt(card, now)
            ReviewResult.FORGOT -> handleForgot(card, now)
        }
    }

    private fun handleKnewIt(card: Card, now: Long): SpacedRepetitionUpdate {
        val currentInterval = if (card.intervalDays <= 0) {
            1
        } else {
            card.intervalDays
        }
        val newInterval = min(currentInterval * 2, 60)
        val newEase = min(card.ease + 0.15f, 2.5f)
        val nextDue = now + newInterval * MILLIS_PER_DAY
        val updated = card.copy(
            ease = newEase,
            intervalDays = newInterval,
            dueAtMillis = nextDue
        )
        return SpacedRepetitionUpdate(updatedCard = updated)
    }

    private fun handleForgot(card: Card, now: Long): SpacedRepetitionUpdate {
        val newInterval = 1
        val newEase = max(card.ease - 0.2f, 1.3f)
        val nextDue = now + SESSION_REVIEW_OFFSET_MILLIS
        val updated = card.copy(
            ease = newEase,
            intervalDays = newInterval,
            dueAtMillis = nextDue
        )
        return SpacedRepetitionUpdate(updatedCard = updated)
    }

    companion object {
        private const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L
        private const val SESSION_REVIEW_OFFSET_MILLIS: Long = 15L * 60L * 1000L
    }
}

