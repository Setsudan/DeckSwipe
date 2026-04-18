@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package one.launay.deckswipe

import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.ui.import.ClipboardAccessor
import one.launay.deckswipe.ui.import.ClipboardReadOutcome
import one.launay.deckswipe.ui.import.ImportFailure
import one.launay.deckswipe.ui.import.ImportUiState
import one.launay.deckswipe.ui.import.ImportViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ImportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val json = Json { ignoreUnknownKeys = true }
    private val fixedNow = 2_000_000_000_000L

    @Test
    fun emptyClipboard_emitsClipboardEmpty() = runTest {
        val vm = ImportViewModel(
            ClipboardImporter(json) { fixedNow },
            FakeDeckRepository(),
            object : ClipboardAccessor {
                override fun read() = ClipboardReadOutcome.EmptyClipboard
                override fun writePlainText(label: String, text: String) = Unit
            }
        )
        vm.importFromClipboard()
        advanceUntilIdle()
        val s = vm.state.value
        assertTrue(s is ImportUiState.Error && s.failure == ImportFailure.ClipboardEmpty)
    }

    @Test
    fun invalidJson_emitsInvalidJson() = runTest {
        val vm = ImportViewModel(
            ClipboardImporter(json) { fixedNow },
            FakeDeckRepository(),
            object : ClipboardAccessor {
                override fun read() = ClipboardReadOutcome.Ok("{ not json")
                override fun writePlainText(label: String, text: String) = Unit
            }
        )
        vm.importFromClipboard()
        advanceUntilIdle()
        val s = vm.state.value
        assertTrue(s is ImportUiState.Error && s.failure == ImportFailure.InvalidJson)
    }

    @Test
    fun validJson_insertsDeck() = runTest {
        val raw = """
            {
              "deck_name": "Test",
              "topic_tags": ["a"],
              "cards": [
                { "front": "Q", "back": "A" }
              ]
            }
        """.trimIndent()
        val repo = FakeDeckRepository().apply { nextInsertId = 55L }
        val vm = ImportViewModel(
            ClipboardImporter(json) { fixedNow },
            repo,
            object : ClipboardAccessor {
                override fun read() = ClipboardReadOutcome.Ok(raw)
                override fun writePlainText(label: String, text: String) = Unit
            }
        )
        vm.importFromClipboard()
        advanceUntilIdle()
        assertEquals(ImportUiState.Success(55L), vm.state.value)
    }
}
