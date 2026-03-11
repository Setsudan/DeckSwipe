package one.launay.deckswipe.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import one.launay.deckswipe.data.db.DeckSwipeDao
import one.launay.deckswipe.data.entity.CardEntity
import one.launay.deckswipe.data.entity.DeckEntity
import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

class DeckRepositoryImpl(
    private val dao: DeckSwipeDao,
    private val clock: () -> Long
): DeckRepository {

    override suspend fun getDecks(): List<Deck> = withContext(Dispatchers.IO) {
        dao.getDecks().map { it.toDomain() }
    }

    override suspend fun getDueCardsForDeck(deckId: Long, nowMillis: Long): List<Card> =
        withContext(Dispatchers.IO) {
            dao.getDueCardsForDeck(deckId, nowMillis).map { it.toDomain() }
        }

    override suspend fun getCardsForDeck(deckId: Long): List<Card> =
        withContext(Dispatchers.IO) {
            dao.getCardsForDeck(deckId).map { it.toDomain() }
        }

    override suspend fun insertDeckWithCards(deck: Deck, cards: List<Card>): Long =
        withContext(Dispatchers.IO) {
            val now = clock()
            val deckEntity = deck.toEntity(now)
            val cardEntities = cards.map { it.toEntity(deckId = deck.id) }
            dao.insertDeckWithCards(deckEntity, cardEntities)
        }

    override suspend fun upsertCard(card: Card): Long = withContext(Dispatchers.IO) {
        dao.upsertCard(card.toEntity(deckId = card.deckId))
    }

    override suspend fun deleteCard(cardId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCardById(cardId)
    }

    override suspend fun deleteDeck(deckId: Long) = withContext(Dispatchers.IO) {
        dao.deleteDeckById(deckId)
    }

    private fun DeckEntity.toDomain(): Deck {
        val tags = if (topicTags.isBlank()) {
            emptyList()
        } else {
            topicTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
        return Deck(
            id = id,
            name = name,
            topicTags = tags,
            createdAtMillis = createdAtMillis,
            updatedAtMillis = updatedAtMillis
        )
    }

    private fun CardEntity.toDomain(): Card {
        return Card(
            id = id,
            deckId = deckId,
            front = front,
            back = back,
            hint = hint,
            ease = ease,
            intervalDays = intervalDays,
            dueAtMillis = dueAtMillis
        )
    }

    private fun Deck.toEntity(nowMillis: Long): DeckEntity {
        val tags = if (topicTags.isEmpty()) {
            ""
        } else {
            topicTags.joinToString(separator = ",")
        }
        val created = if (createdAtMillis == 0L) nowMillis else createdAtMillis
        return DeckEntity(
            id = if (id == 0L) 0L else id,
            name = name,
            topicTags = tags,
            createdAtMillis = created,
            updatedAtMillis = nowMillis
        )
    }

    private fun Card.toEntity(deckId: Long): CardEntity {
        return CardEntity(
            id = if (id == 0L) 0L else id,
            deckId = deckId,
            front = front,
            back = back,
            hint = hint,
            ease = ease,
            intervalDays = intervalDays,
            dueAtMillis = dueAtMillis
        )
    }
}

