## DeckSwipe Manual QA Checklist

### Importing a Deck

- Open the app and verify the deck list is empty on first launch.
- Copy a valid JSON payload matching the README schema to the clipboard.
- From the deck list, tap `Import deck` and then `Import from clipboard`.
- Verify that a success state occurs and the app navigates to the study view for the new deck.
- Return to the deck list and confirm the new deck appears with the correct name and tags.

### Studying With Swipes

- From the deck list, open a deck with due cards.
- Verify the first card question appears centered on screen.
- Tap `Flip card` and confirm the card back (answer) is shown.
- Verify that any provided hint appears only when the card is showing the question side.
- Swipe right on a card and confirm it disappears and the next card appears.
- Swipe left on a card and confirm it is re-queued (will appear again during the session).
- Use the `Forgot` and `Knew it` buttons and confirm they behave the same as swipes.

### Spaced Repetition Behavior (Basic Sanity)

- Study several cards, mixing `Knew it` and `Forgot` outcomes.
- Close and reopen the app.
- Verify that only cards due for review appear when entering study for the same deck.

### Error Handling on Import

- Copy an empty string to the clipboard and attempt an import; verify a clear error message appears.
- Copy invalid JSON to the clipboard and attempt an import; verify a clear error message appears.
- Copy JSON missing required fields (for example, empty `deck_name` or empty `cards` array) and confirm an error is shown.

### Offline and Privacy Expectations

- Put the device in airplane mode and launch the app.
- Verify the app runs normally and import/study flows work with existing data.
- Inspect Android permissions and confirm there is no internet permission granted.

### Visual / Theming

- Toggle system dark mode and verify DeckSwipe follows the system theme.
- Confirm card surfaces, backgrounds, and text remain legible in both light and dark modes.

