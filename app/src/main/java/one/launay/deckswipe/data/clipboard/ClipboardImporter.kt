package one.launay.deckswipe.data.clipboard

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.model.Deck

sealed class ImportResult {
    data class Success(
        val deck: Deck,
        val cards: List<Card>
    ) : ImportResult()

    sealed class Error : ImportResult() {
        object EmptyInput : Error()
        object InvalidJson : Error()
        object MissingRequiredFields : Error()
    }
}

class ClipboardImporter(
    private val json: Json,
    private val nowProvider: () -> Long
) {

    fun importFromJson(raw: String?): ImportResult {
        val trimmed = raw?.trim()
        if (trimmed.isNullOrEmpty()) {
            return ImportResult.Error.EmptyInput
        }

        val dto = try {
            json.decodeFromString<DeckImportDto>(trimmed)
        } catch (_: SerializationException) {
            return ImportResult.Error.InvalidJson
        }

        if (dto.deckName.isBlank() || dto.cards.isEmpty()) {
            return ImportResult.Error.MissingRequiredFields
        }

        val now = nowProvider()
        val deck = Deck(
            id = 0L,
            name = dto.deckName.trim(),
            topicTags = dto.topicTags.map { it.trim() }.filter { it.isNotEmpty() },
            createdAtMillis = now,
            updatedAtMillis = now,
            isFavorite = false,
            description = "",
            coverUri = null
        )

        val cards = dto.cards.mapIndexed { index, cardDto ->
            Card(
                id = 0L,
                deckId = 0L,
                front = cardDto.front,
                back = cardDto.back,
                hint = cardDto.hint,
                ease = DEFAULT_EASE,
                intervalDays = DEFAULT_INTERVAL_DAYS,
                dueAtMillis = now + index
            )
        }

        return ImportResult.Success(deck = deck, cards = cards)
    }

    companion object {
        private const val DEFAULT_INTERVAL_DAYS = 1
        private const val DEFAULT_EASE = 2.0f
    }
}

