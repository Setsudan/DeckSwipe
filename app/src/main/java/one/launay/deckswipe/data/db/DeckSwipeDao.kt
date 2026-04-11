package one.launay.deckswipe.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import one.launay.deckswipe.data.entity.CardEntity
import one.launay.deckswipe.data.entity.DeckEntity

@Dao
interface DeckSwipeDao {

    @Query("SELECT * FROM decks ORDER BY updated_at DESC")
    suspend fun getDecks(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE id = :deckId LIMIT 1")
    suspend fun getDeckById(deckId: Long): DeckEntity?

    @Query("SELECT COUNT(*) FROM cards WHERE deck_id = :deckId")
    suspend fun countCardsForDeck(deckId: Long): Int

    @Query(
        "SELECT * FROM cards " +
            "WHERE deck_id = :deckId AND due_at <= :nowMillis " +
            "ORDER BY due_at ASC"
    )
    suspend fun getDueCardsForDeck(deckId: Long, nowMillis: Long): List<CardEntity>

    @Query("SELECT * FROM cards WHERE deck_id = :deckId ORDER BY id ASC")
    suspend fun getCardsForDeck(deckId: Long): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeck(deck: DeckEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCards(cards: List<CardEntity>): List<Long>

    @Transaction
    suspend fun insertDeckWithCards(
        deck: DeckEntity,
        cards: List<CardEntity>
    ): Long {
        val deckId = upsertDeck(deck)
        if (cards.isNotEmpty()) {
            val withDeckId = cards.map { card ->
                card.copy(deckId = deckId)
            }
            upsertCards(withDeckId)
        }
        return deckId
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCard(card: CardEntity): Long

    @Query("DELETE FROM cards WHERE id = :cardId")
    suspend fun deleteCardById(cardId: Long)

    @Query("DELETE FROM decks WHERE id = :deckId")
    suspend fun deleteDeckById(deckId: Long)
}
