package one.launay.deckswipe

import android.app.Application
import kotlinx.serialization.json.Json
import one.launay.deckswipe.data.clipboard.ClipboardImporter
import one.launay.deckswipe.data.db.DeckSwipeDatabase
import one.launay.deckswipe.data.repository.DeckRepositoryImpl
import one.launay.deckswipe.domain.repository.DeckRepository

class DeckSwipeApplication : Application() {

    lateinit var database: DeckSwipeDatabase
        private set

    lateinit var deckRepository: DeckRepository
        private set

    lateinit var clipboardImporter: ClipboardImporter
        private set

    override fun onCreate() {
        super.onCreate()
        database = DeckSwipeDatabase.getInstance(this)
        deckRepository = DeckRepositoryImpl(
            dao = database.deckSwipeDao(),
            clock = { System.currentTimeMillis() }
        )
        clipboardImporter = ClipboardImporter(
            json = Json { ignoreUnknownKeys = true },
            nowProvider = { System.currentTimeMillis() }
        )
    }
}
