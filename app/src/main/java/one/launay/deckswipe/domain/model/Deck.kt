package one.launay.deckswipe.domain.model

data class Deck(
    val id: Long,
    val name: String,
    val topicTags: List<String>,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

