@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package one.launay.deckswipe

import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import one.launay.deckswipe.ui.decks.DeckEditorValidationError
import one.launay.deckswipe.ui.decks.DeckEditorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class DeckEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun save_emptyName_setsValidationError() = runTest {
        val vm = DeckEditorViewModel(FakeDeckRepository(), existingDeckId = null)
        vm.save()
        assertEquals(DeckEditorValidationError.EmptyDeckName, vm.state.value.validationError)
    }

    @Test
    fun save_noValidCards_setsValidationError() = runTest {
        val vm = DeckEditorViewModel(FakeDeckRepository(), existingDeckId = null)
        vm.updateName("My deck")
        vm.save()
        assertEquals(DeckEditorValidationError.NoValidCards, vm.state.value.validationError)
    }

    @Test
    fun save_valid_setsSavedDeckId() = runTest {
        val repo = FakeDeckRepository().apply { nextInsertId = 7L }
        val vm = DeckEditorViewModel(repo, existingDeckId = null)
        vm.updateName("My deck")
        vm.updateCard(0, front = "Q", back = "A")
        vm.save()
        advanceUntilIdle()
        assertNull(vm.state.value.validationError)
        assertEquals(7L, vm.state.value.savedDeckId)
    }
}
