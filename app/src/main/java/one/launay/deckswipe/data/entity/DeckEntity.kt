package one.launay.deckswipe.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "topic_tags")
    val topicTags: String,
    @ColumnInfo(name = "created_at")
    val createdAtMillis: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAtMillis: Long,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "cover_uri")
    val coverUri: String? = null
)
