# DeckSwipe UI Redesign Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Rework DeckSwipe’s UX and UI to match the provided pastel card-based design with a gradient background and new bottom navigation, while preserving existing functionality.

**Architecture:** Use Jetpack Compose with the existing `DeckSwipeTheme` as the base, extend theme colors for gradients and cards, introduce a new `HomeScreen` composable as the start destination, and refactor the bottom navigation and existing screens to adopt the new visual style without changing data or domain layers.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, existing DeckSwipe data/domain modules.

---

### Task 1: Extend theme colors for gradients and cards

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/theme/Color.kt`
- Modify: `app/src/main/java/one/launay/deckswipe/ui/theme/Theme.kt`

**Steps:**
1. Add new `Color` values for gradient start/end and card backgrounds in `Color.kt` (e.g. `GradientStart`, `GradientEnd`, `CardPink`, `CardBlue`, `CardNeutral`).
2. Update `LightColors` and `DarkColors` in `Theme.kt` to better match the reference (e.g. softer surface/background colors).
3. Add an optional `LocalExtendedColors` or simple helper composable if needed for non-standard roles, but keep it minimal.
4. Build the app to ensure no compilation errors.

### Task 2: Introduce HomeScreen with “Create a deck” cards

**Files:**
- Add: `app/src/main/java/one/launay/deckswipe/ui/home/HomeScreen.kt`
- Modify: `app/src/main/java/one/launay/deckswipe/ui/strings/Strings.kt`

**Steps:**
1. Create `HomeScreen` composable that:
   - Uses a gradient background.
   - Shows “Create a deck” title.
   - Contains two large rounded cards: AI generated (pink) and Create own cards (blue), each with title, description, and button.
2. Wire callbacks for the buttons: `onAiDeck` and `onManualDeck`.
3. Add any strings required for labels and descriptions into `Strings.kt` for both English and French.
4. Build and fix any compilation issues.

### Task 3: Update navigation routes and NavHost for Home and new tabs

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/navigation/NavGraph.kt`

**Steps:**
1. Add new route constants: `HOME`, `CARDS`, `BROWSE`, `ANALYTICS`.
2. Change the NavHost `startDestination` from `DECK_LIST` to `HOME`.
3. Add a composable for `HOME` using `HomeScreen`, wiring:
   - `onAiDeck` to navigate to `Routes.IMPORT`.
   - `onManualDeck` to navigate to `Routes.NEW_DECK`.
4. Add placeholder composables for `CARDS`, `BROWSE`, and `ANALYTICS` (simple centered text).
5. Keep existing routes (DECK_LIST, NEW_DECK, IMPORT, STUDY, SETTINGS) intact.

### Task 4: Redesign bottom navigation with four tabs and center plus

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/DeckSwipeApp.kt`

**Steps:**
1. Replace the two-item `NavigationBar` with four items mapped to `HOME` (or `DECK_LIST` as Decks), `CARDS`, `BROWSE`, `ANALYTICS`.
2. Add a central large circular plus button (using `FloatingActionButton` or custom `Box`) visually integrated with the nav bar.
3. Make the plus button navigate to `Routes.HOME` (or scroll to top if already there).
4. Use strings from `Strings` for tab labels (`navDecks`, `navCards`, `navBrowse`, `navAnalytics`).
5. Verify that back navigation and tab selection states behave correctly.

### Task 5: Apply new styling to DeckListScreen

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/decks/DeckListScreen.kt`

**Steps:**
1. Remove the inner `Scaffold` and rely on the padding from the parent Scaffold.
2. Wrap the content in a column using the gradient background (or rely on parent background) and adjust padding.
3. Replace “New deck” and “Import from AI” buttons row with a smaller link-like row or simple text suggesting creation via Home; keep at least one manual entry to `onNewDeck` and `onImportClick` for power users.
4. Update deck cards to use larger corner radius and slightly adjusted elevation; optionally apply a surface color tint.
5. Ensure loading and error states still look acceptable over the new background.

### Task 6: Apply new styling to DeckEditorScreen

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/decks/DeckEditorScreen.kt`

**Steps:**
1. Wrap the main content column in a rounded surface card with padding (e.g. `Card` with large corner radius).
2. Adjust spacings so that heading, fields, and buttons feel airy like the reference.
3. Make the bottom row buttons pill-shaped and ensure `Save deck` remains visually primary.
4. Verify snackbar still appears correctly above the content.

### Task 7: Apply new styling to StudyScreen

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/study/StudyScreen.kt`

**Steps:**
1. Ensure the outer `Scaffold` uses background consistent with the gradient theme or a darkened variant.
2. Confirm that `SwipeableCardStack` cards have rounded corners and appropriate padding; if needed, wrap with a surface providing card-like styling.
3. Tweak text styles (sizes) to fit the new aesthetic but keep readability.

### Task 8: Apply new styling to SettingsScreen

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/settings/SettingsScreen.kt`

**Steps:**
1. Remove the inner `Scaffold` if redundant and rely on parent padding.
2. Place settings content inside a rounded surface card with padding and spacing between sections.
3. Ensure radio rows align nicely and labels use consistent typography and spacing.

### Task 9: Update Strings for navigation and Home copy

**Files:**
- Modify: `app/src/main/java/one/launay/deckswipe/ui/strings/Strings.kt`

**Steps:**
1. Add fields for new navigation labels (`navDecks`, `navCards`, `navBrowse`, `navAnalytics`) and home texts (titles, card labels, descriptions, button texts).
2. Provide corresponding English and French values.
3. Update all usages in `DeckSwipeApp.kt`, `HomeScreen.kt`, and any other files to reference the new string fields.

### Task 10: Run lint and basic manual verification

**Files/Tools:**
- Run lints on modified files using the linter integration.
- Run the Android app manually if possible.

**Steps:**
1. Use the linter on the modified Kotlin files to catch obvious issues.
2. Build and run the app; manually verify:
   - Gradient background and card styling appear on Home, Decks, Study, and Settings.
   - Bottom navigation tabs and center plus navigate as expected.
   - Buttons on Home correctly open AI/import and manual deck editor flows.
3. Fix any issues discovered.

