package one.launay.deckswipe.domain.repository

import one.launay.deckswipe.domain.model.Card
import one.launay.deckswipe.domain.model.Deck

interface DeckRepository {
    suspend fun getDecks(): List<Deck>
    suspend fun getDeck(deckId: Long): Deck?
    suspend fun getCardCountForDeck(deckId: Long): Int
    suspend fun getDueCardsForDeck(deckId: Long, nowMillis: Long): List<Card>
    suspend fun getCardsForDeck(deckId: Long): List<Card>
    suspend fun insertDeckWithCards(deck: Deck, cards: List<Card>): Long
    suspend fun updateDeck(deck: Deck)
    suspend fun upsertCard(card: Card): Long
    suspend fun deleteCard(cardId: Long)
    suspend fun deleteDeck(deckId: Long)
}
