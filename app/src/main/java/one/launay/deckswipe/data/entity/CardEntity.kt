package one.launay.deckswipe.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deck_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["deck_id"]),
        Index(value = ["due_at"])
    ]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "deck_id")
    val deckId: Long,
    @ColumnInfo(name = "front")
    val front: String,
    @ColumnInfo(name = "back")
    val back: String,
    @ColumnInfo(name = "hint")
    val hint: String?,
    @ColumnInfo(name = "ease")
    val ease: Float,
    @ColumnInfo(name = "interval_days")
    val intervalDays: Int,
    @ColumnInfo(name = "due_at")
    val dueAtMillis: Long
)

