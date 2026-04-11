package one.launay.deckswipe

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import one.launay.deckswipe.data.db.DeckSwipeDatabase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeckSwipeDatabaseInstrumentedTest {

    @Test
    fun inMemoryDatabase_deckDao_returnsEmptyList() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(
            context,
            DeckSwipeDatabase::class.java
        ).build()
        assertEquals(0, db.deckSwipeDao().getDecks().size)
        db.close()
    }
}
