package one.launay.deckswipe

import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.spacedrepetition.ReviewResult
import one.launay.deckswipe.domain.spacedrepetition.SpacedRepetitionEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class SpacedRepetitionEngineTest {

    private val fixedNow = 1_000_000_000_000L

    private fun baseCard(
        intervalDays: Int = 1,
        ease: Float = 2.0f,
        dueAtMillis: Long = fixedNow
    ): Card {
        return Card(
            id = 1L,
            deckId = 1L,
            front = "front",
            back = "back",
            hint = null,
            ease = ease,
            intervalDays = intervalDays,
            dueAtMillis = dueAtMillis
        )
    }

    @Test
    fun knewIt_doublesIntervalUpToCap() {
        val engine = SpacedRepetitionEngine { fixedNow }
        val card = baseCard(intervalDays = 5)

        val update = engine.review(card, ReviewResult.KNEW_IT)
        val updated = update.updatedCard

        assertEquals(10, updated.intervalDays)
        assertEquals(2.15f, updated.ease, 0.0001f)
        assertEquals(fixedNow + 10L * 24L * 60L * 60L * 1000L, updated.dueAtMillis)
    }

    @Test
    fun knewIt_respectsUpperIntervalCap() {
        val engine = SpacedRepetitionEngine { fixedNow }
        val card = baseCard(intervalDays = 40)

        val update = engine.review(card, ReviewResult.KNEW_IT)
        val updated = update.updatedCard

        assertEquals(60, updated.intervalDays)
    }

    @Test
    fun forgot_resetsIntervalAndSchedulesSoon() {
        val engine = SpacedRepetitionEngine { fixedNow }
        val card = baseCard(intervalDays = 10, ease = 2.0f)

        val update = engine.review(card, ReviewResult.FORGOT)
        val updated = update.updatedCard

        assertEquals(1, updated.intervalDays)
        assertEquals(1.8f, updated.ease, 0.0001f)
        assertEquals(fixedNow + 15L * 60L * 1000L, updated.dueAtMillis)
    }
}

