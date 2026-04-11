## DeckSwipe Development Notes

### Architecture Overview

- **UI layer** (`one.launay.deckswipe.ui`): Jetpack Compose screens, navigation, and ViewModels.
- **Domain layer** (`one.launay.deckswipe.domain`): Plain Kotlin models, repository interfaces, and spaced repetition engine.
- **Data layer** (`one.launay.deckswipe.data`): Room entities/DAO/database, repository implementation, and clipboard JSON parsing.

The app is offline-only. There are no network calls and no internet permission in the manifest.

### Key Entry Points

- `MainActivity` – hosts Compose and calls `DeckSwipeApp()`.
- `DeckSwipeApplication` – creates singleton `DeckSwipeDatabase`, `DeckRepositoryImpl`, and `ClipboardImporter` in `onCreate()`.
- `DeckSwipeApp` – provides repository and importer via `CompositionLocal`, applies theme, bottom navigation, and hosts `DeckSwipeNavHost`.
- `DeckSwipeNavHost` – defines navigation between home, deck list, deck details (`deck/{deckId}`), create/import, study, and settings.

### Persistence

- `DeckEntity` and `CardEntity` define the Room schema.
- Database version **2** adds `is_favorite`, `description`, and `cover_uri` on `decks` via `MIGRATION_1_2` in `DeckSwipeMigrations.kt`.
- `DeckSwipeDao` provides queries, including `getDueCardsForDeck`, `getDeckById`, and `countCardsForDeck`.
- `DeckRepositoryImpl` maps between Room entities and domain models.
- Room exports JSON schemas under `app/schemas/` (see `room.schemaLocation` in `app/build.gradle.kts`) for migration review.

### Spaced Repetition

- Implemented in `SpacedRepetitionEngine`.
- Takes a `Card` and a `ReviewResult` and returns an updated card with new interval, ease, and due date.

### Clipboard Import

- JSON schema is documented in `README.md`.
- `ClipboardImporter` parses clipboard JSON into domain `Deck` and `Card` objects.
- `ImportViewModel` uses `ClipboardAccessor` (see `ClipboardAccessor.kt`) so tests can fake clipboard outcomes without Android framework classes.

### Testing

- Unit tests for spaced repetition, clipboard import, and ViewModels live under `app/src/test/java/one/launay/deckswipe`.
- Android instrumented tests: `DeckSwipeDatabaseInstrumentedTest`, `DeckSwipeMigrationTest` (1 to 2 migration).
- Run unit tests with `./gradlew test`.
