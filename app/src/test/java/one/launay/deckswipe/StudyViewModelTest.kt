@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package one.launay.deckswipe

import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.spacedrepetition.SpacedRepetitionEngine
import one.launay.deckswipe.ui.study.StudyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class StudyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixedNow = 1_000_000_000_000L

    private fun sampleCard(
        id: Long = 1L,
        deckId: Long = 1L,
        front: String = "q",
        back: String = "a"
    ): Card {
        return Card(
            id = id,
            deckId = deckId,
            front = front,
            back = back,
            hint = null,
            ease = 2.0f,
            intervalDays = 1,
            dueAtMillis = fixedNow
        )
    }

    @Test
    fun load_showsFirstDueCardAndDeckTitle() = runTest {
        val deck = Deck(
            id = 1L,
            name = "Biology",
            topicTags = emptyList(),
            createdAtMillis = 0L,
            updatedAtMillis = 0L
        )
        val c1 = sampleCard(id = 10L, front = "Q1")
        val c2 = sampleCard(id = 11L, front = "Q2")
        val repo = FakeDeckRepository()
        repo.setDecks(listOf(deck))
        repo.setDue(1L, listOf(c1, c2))
        val vm = StudyViewModel(
            deckId = 1L,
            repository = repo,
            engine = SpacedRepetitionEngine { fixedNow }
        )
        advanceUntilIdle()
        assertEquals("Biology", vm.state.value.deckTitle)
        assertEquals(10L, vm.state.value.currentCard?.id)
        assertEquals(2, vm.state.value.remainingCount)
        assertEquals(0, vm.state.value.sessionReviewsCompleted)
    }

    @Test
    fun knewIt_advancesToNextCard() = runTest {
        val deck = Deck(
            id = 1L,
            name = "D",
            topicTags = emptyList(),
            createdAtMillis = 0L,
            updatedAtMillis = 0L
        )
        val c1 = sampleCard(id = 10L)
        val c2 = sampleCard(id = 11L)
        val repo = FakeDeckRepository()
        repo.setDecks(listOf(deck))
        repo.setDue(1L, listOf(c1, c2))
        val vm = StudyViewModel(
            deckId = 1L,
            repository = repo,
            engine = SpacedRepetitionEngine { fixedNow }
        )
        advanceUntilIdle()
        vm.onKnewIt()
        advanceUntilIdle()
        assertEquals(11L, vm.state.value.currentCard?.id)
        assertEquals(1, vm.state.value.remainingCount)
        assertEquals(1, vm.state.value.sessionReviewsCompleted)
        assertEquals(1, repo.upsertedCards.size)
    }

    @Test
    fun forgot_requeuesCardAndContinues() = runTest {
        val deck = Deck(
            id = 1L,
            name = "D",
            topicTags = emptyList(),
            createdAtMillis = 0L,
            updatedAtMillis = 0L
        )
        val c1 = sampleCard(id = 10L)
        val c2 = sampleCard(id = 11L)
        val repo = FakeDeckRepository()
        repo.setDecks(listOf(deck))
        repo.setDue(1L, listOf(c1, c2))
        val vm = StudyViewModel(
            deckId = 1L,
            repository = repo,
            engine = SpacedRepetitionEngine { fixedNow }
        )
        advanceUntilIdle()
        vm.onForgot()
        advanceUntilIdle()
        assertEquals(2, vm.state.value.remainingCount)
        assertEquals(11L, vm.state.value.currentCard?.id)
        assertEquals(1, vm.state.value.sessionReviewsCompleted)
    }

    @Test
    fun lastCardKnewIt_emptiesQueue() = runTest {
        val deck = Deck(
            id = 1L,
            name = "D",
            topicTags = emptyList(),
            createdAtMillis = 0L,
            updatedAtMillis = 0L
        )
        val c1 = sampleCard(id = 10L)
        val repo = FakeDeckRepository()
        repo.setDecks(listOf(deck))
        repo.setDue(1L, listOf(c1))
        val vm = StudyViewModel(
            deckId = 1L,
            repository = repo,
            engine = SpacedRepetitionEngine { fixedNow }
        )
        advanceUntilIdle()
        vm.onKnewIt()
        advanceUntilIdle()
        assertNull(vm.state.value.currentCard)
        assertEquals(0, vm.state.value.remainingCount)
        assertEquals(1, vm.state.value.sessionReviewsCompleted)
    }
}
