package one.launay.deckswipe.domain.model

data class Card(
    val id: Long,
    val deckId: Long,
    val front: String,
    val back: String,
    val hint: String?,
    val ease: Float,
    val intervalDays: Int,
    val dueAtMillis: Long
)

