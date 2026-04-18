package one.launay.deckswipe

import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

class FakeDeckRepository(
    private var decks: List<Deck> = emptyList(),
    private var dueByDeckId: Map<Long, List<Card>> = emptyMap(),
    private var cardsByDeckId: Map<Long, List<Card>> = emptyMap(),
    var nextInsertId: Long = 100L
) : DeckRepository {

    val upsertedCards = mutableListOf<Card>()
    val updatedDecks = mutableListOf<Deck>()

    fun setDecks(value: List<Deck>) {
        decks = value
    }

    fun setDue(deckId: Long, cards: List<Card>) {
        dueByDeckId = dueByDeckId + (deckId to cards)
    }

    override suspend fun getDecks(): List<Deck> = decks

    override suspend fun getDeck(deckId: Long): Deck? =
        decks.find { it.id == deckId }

    override suspend fun getCardCountForDeck(deckId: Long): Int =
        cardsByDeckId[deckId]?.size ?: 0

    override suspend fun getDueCardsForDeck(deckId: Long, nowMillis: Long): List<Card> =
        dueByDeckId[deckId] ?: emptyList()

    override suspend fun getCardsForDeck(deckId: Long): List<Card> =
        cardsByDeckId[deckId] ?: emptyList()

    override suspend fun insertDeckWithCards(deck: Deck, cards: List<Card>): Long {
        return nextInsertId
    }

    override suspend fun updateDeck(deck: Deck) {
        updatedDecks.add(deck)
    }

    override suspend fun upsertCard(card: Card): Long {
        upsertedCards.add(card)
        return card.id
    }

    override suspend fun deleteCard(cardId: Long) = Unit

    override suspend fun deleteDeck(deckId: Long) {
        decks = decks.filter { it.id != deckId }
        dueByDeckId = dueByDeckId - deckId
        cardsByDeckId = cardsByDeckId - deckId
    }
}
