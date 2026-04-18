package one.launay.deckswipe.ui.decks

import one.launay.deckswipe.domain.model.Deck
import one.launay.deckswipe.domain.repository.DeckRepository

suspend fun deckRowForStats(
    repository: DeckRepository,
    deck: Deck,
    nowMillis: Long
): DeckListRow {
    val total = repository.getCardCountForDeck(deck.id)
    val due = repository.getDueCardsForDeck(deck.id, nowMillis).size
    val mastered = (total - due).coerceAtLeast(0)
    val mastery = if (total == 0) {
        0f
    } else {
        mastered.toFloat() / total.toFloat()
    }
    return DeckListRow(
        deck = deck,
        totalCards = total,
        dueNowCount = due,
        masteryProgress = mastery
    )
}
