package one.launay.deckswipe

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import one.launay.deckswipe.data.db.DeckSwipeDatabase
import one.launay.deckswipe.data.db.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeckSwipeMigrationTest {

    private val dbName = "migration-test-db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        DeckSwipeDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2_addsDeckColumns() {
        helper.createDatabase(dbName, 1).apply {
            execSQL(
                "CREATE TABLE IF NOT EXISTS `decks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT NOT NULL, `topic_tags` TEXT NOT NULL, `created_at` INTEGER NOT NULL, " +
                    "`updated_at` INTEGER NOT NULL)"
            )
            execSQL(
                "CREATE TABLE IF NOT EXISTS `cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`deck_id` INTEGER NOT NULL, `front` TEXT NOT NULL, `back` TEXT NOT NULL, `hint` TEXT, " +
                    "`ease` REAL NOT NULL, `interval_days` INTEGER NOT NULL, `due_at` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`deck_id`) REFERENCES `decks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
            execSQL("CREATE INDEX IF NOT EXISTS `index_cards_deck_id` ON `cards` (`deck_id`)")
            execSQL("CREATE INDEX IF NOT EXISTS `index_cards_due_at` ON `cards` (`due_at`)")
            execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
            execSQL(
                "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, " +
                    "'f1672159098dba3575c7097ed4bd3438')"
            )
            close()
        }

        val db = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            DeckSwipeDatabase::class.java,
            dbName
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        runBlocking {
            val decks = db.deckSwipeDao().getDecks()
            assertEquals(0, decks.size)
        }
        db.close()
    }
}
