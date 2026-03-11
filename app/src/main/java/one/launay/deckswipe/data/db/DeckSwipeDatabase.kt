package one.launay.deckswipe.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import one.launay.deckswipe.data.entity.CardEntity
import one.launay.deckswipe.data.entity.DeckEntity

@Database(
    entities = [
        DeckEntity::class,
        CardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DeckSwipeDatabase : RoomDatabase() {

    abstract fun deckSwipeDao(): DeckSwipeDao

    companion object {
        @Volatile
        private var instance: DeckSwipeDatabase? = null

        fun getInstance(context: Context): DeckSwipeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DeckSwipeDatabase::class.java,
                    "deckswipe.db"
                ).build().also { db ->
                    instance = db
                }
            }
        }
    }
}

