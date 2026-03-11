package one.launay.deckswipe

import kotlinx.serialization.json.Json
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.data.clipboard.ImportResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardImporterTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val fixedNow = 2_000_000_000_000L

    @Test
    fun emptyInputReturnsError() {
        val importer = ClipboardImporter(json) { fixedNow }

        val result = importer.importFromJson("  ")

        assertTrue(result is ImportResult.Error.EmptyInput)
    }

    @Test
    fun invalidJsonReturnsError() {
        val importer = ClipboardImporter(json) { fixedNow }

        val result = importer.importFromJson("{ not json }")

        assertTrue(result is ImportResult.Error.InvalidJson)
    }

    @Test
    fun missingFieldsReturnsError() {
        val importer = ClipboardImporter(json) { fixedNow }
        val raw = """
            {
              "deck_name": "",
              "topic_tags": [],
              "cards": []
            }
        """.trimIndent()

        val result = importer.importFromJson(raw)

        assertTrue(result is ImportResult.Error.MissingRequiredFields)
    }

    @Test
    fun validJsonProducesDeckAndCards() {
        val importer = ClipboardImporter(json) { fixedNow }
        val raw = """
            {
              "deck_name": "Intro to Biology",
              "topic_tags": ["biology", "cells"],
              "cards": [
                {
                  "front": "What is the powerhouse of the cell?",
                  "back": "The mitochondrion",
                  "hint": "Organelle"
                },
                {
                  "front": "What carries genetic information?",
                  "back": "DNA"
                }
              ]
            }
        """.trimIndent()

        val result = importer.importFromJson(raw)

        require(result is ImportResult.Success)
        val deck = result.deck
        val cards = result.cards

        assertEquals("Intro to Biology", deck.name)
        assertEquals(listOf("biology", "cells"), deck.topicTags)
        assertEquals(2, cards.size)
        assertEquals("What is the powerhouse of the cell?", cards[0].front)
        assertEquals("The mitochondrion", cards[0].back)
        assertEquals("Organelle", cards[0].hint)
    }
}

