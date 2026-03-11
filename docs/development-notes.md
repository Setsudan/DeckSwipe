## DeckSwipe Development Notes

### Architecture Overview

- **UI layer** (`one.launay.deckswipe.ui`): Jetpack Compose screens, navigation, and ViewModels.
- **Domain layer** (`one.launay.deckswipe.domain`): Plain Kotlin models, repository interfaces, and spaced repetition engine.
- **Data layer** (`one.launay.deckswipe.data`): Room entities/DAO/database, repository implementation, and clipboard JSON parsing.

The app is offline-only. There are no network calls and no internet permission in the manifest.

### Key Entry Points

- `MainActivity` – hosts Compose and calls `DeckSwipeApp()`.
- `DeckSwipeApp` – sets up Room, repository, clipboard importer, and navigation graph.
- `DeckSwipeNavHost` – defines navigation between deck list, import, and study screens.

### Persistence

- `DeckEntity` and `CardEntity` define the Room schema.
- `DeckSwipeDao` provides queries, including `getDueCardsForDeck`.
- `DeckRepositoryImpl` maps between Room entities and domain models.

### Spaced Repetition

- Implemented in `SpacedRepetitionEngine`.
- Takes a `Card` and a `ReviewResult` and returns an updated card with new interval, ease, and due date.

### Clipboard Import

- JSON schema is documented in `README.md`.
- `ClipboardImporter` parses clipboard JSON into domain `Deck` and `Card` objects.
- `ImportViewModel` wires `ClipboardManager` to `ClipboardImporter` and the repository.

### Testing

- Unit tests for spaced repetition and clipboard import live under `app/src/test/java/one/launay/deckswipe`.
- Run with `./gradlew test`.

