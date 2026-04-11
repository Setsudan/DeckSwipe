## DeckSwipe Manual QA Checklist

### Bottom navigation and create flow

- From each tab (Home, Cards, Decks, Settings), confirm the correct screen appears and the selected tab highlights.
- Tap the center FAB and confirm the create deck screen opens (AI import vs manual deck).
- From Home, tap the browse link and confirm the deck list opens.

### Deck list cards and deck details

- On the Decks tab, confirm each deck appears as a card with min height, title top-left, favorite icon top-right, circular progress with card count, and tag pills at the bottom.
- Tap the favorite icon on a deck card and confirm the state toggles without opening details.
- Tap a deck card (outside the favorite icon) and confirm the deck details screen opens.
- On deck details, verify cover placeholder or image, title, description, tags, Learn, and Edit cards actions.
- Use change cover, pick an image, leave details, and reopen the app; confirm the cover still shows (same device session).
- Edit title, description, and tags from deck details; confirm changes persist after navigating away and back.
- From deck details, open Learn and Edit cards and confirm navigation works.

### Importing a Deck

- Open the app and verify the deck list is empty on first launch (Decks tab).
- Copy a valid JSON payload matching the README schema to the clipboard.
- Use the FAB, choose AI flow, then `Import from clipboard`.
- Verify that a success state occurs and the app navigates to the study view for the new deck.
- Return to the deck list and confirm the new deck appears with the correct name and tags on the card.

### Database upgrade (existing installs)

- Install a build with database version 1 (if you keep an old APK), add at least one deck, then upgrade to the current build.
- Confirm the app opens without crash and deck list still shows decks; open deck details and set favorite, description, and cover without errors.

### Studying With Swipes

- From the deck list, open a deck with due cards.
- Confirm the study top bar shows the deck name and a back control.
- Verify the first card question appears centered on screen.
- Tap `Flip card` and confirm the card back (answer) is shown.
- Verify that any provided hint appears only when the card is showing the question side.
- Swipe right on a card and confirm it disappears and the next card appears.
- Swipe left on a card and confirm it is re-queued (will appear again during the session).
- When no cards remain, confirm the completion state and that `Back` returns to the previous screen.

### Spaced Repetition Behavior (Basic Sanity)

- Study several cards, mixing remembered and forgot outcomes.
- Close and reopen the app.
- Verify that only cards due for review appear when entering study for the same deck.

### Error Handling on Import

- Copy an empty string to the clipboard and attempt an import; verify a clear error message appears.
- Copy invalid JSON to the clipboard and attempt an import; verify a clear error message appears.
- Copy JSON missing required fields (for example, empty `deck_name` or empty `cards` array) and confirm an error is shown.

### Localization

- In Settings, switch language between English and French.
- Spot-check Home, Decks, Study, Import, and deck editor labels for both languages.

### Offline and Privacy Expectations

- Put the device in airplane mode and launch the app.
- Verify the app runs normally and import/study flows work with existing data.
- Inspect Android permissions and confirm there is no internet permission granted.

### Visual / Theming

- Toggle app light/dark scheme in Settings and verify DeckSwipe updates.
- Confirm card surfaces, backgrounds, and text remain legible in both light and dark modes.
