package one.launay.deckswipe.data.clipboard

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeckImportDto(
    @SerialName("deck_name")
    val deckName: String,
    @SerialName("topic_tags")
    val topicTags: List<String> = emptyList(),
    @SerialName("cards")
    val cards: List<CardImportDto>
)

@Serializable
data class CardImportDto(
    @SerialName("front")
    val front: String,
    @SerialName("back")
    val back: String,
    @SerialName("hint")
    val hint: String? = null
)

