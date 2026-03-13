DeckSwipe UI Redesign – High-Level Design

Goal

- Rework the entire app UX/UI to match the provided pastel, card-based design with a gradient background and modern bottom navigation, while keeping existing functional flows (decks, study, import, settings).

Global Visual Language

- Background: Use a vertical pastel gradient (light pink to light purple/blue) behind the main content for the light theme. Dark theme keeps existing dark background but with softer surface colors.
- Surfaces: Primary surfaces are rounded rectangles with large corner radius (24–28 dp) and soft elevation. Cards have subtle shadows and light pastel fills (pink for AI deck, blue for manual deck, neutral for other content).
- Typography: Use Material 3 typography but favor larger display/body sizes for headings (e.g. headlineMedium for screen titles, titleMedium for card titles).
- Buttons: Use filled buttons with pill-shaped corners (large corner radius) and pastel colors matching their context; secondary actions use tonal/outlined buttons.

Navigation Structure

- Bottom navigation bar:
  - Tabs: Decks, Cards, Browse, Analytics (labels only for now; icons can be basic Material icons or left empty as in current code).
  - Center action: An oversized circular FAB-like plus button merged into the navigation bar background to match the reference.
  - Behavior:
    - Decks: opens the existing deck list screen (renamed visually to “Decks”).
    - Cards: for now, routes to the same deck list or a simple “All cards (coming soon)” placeholder screen; this keeps room for future expansion.
    - Browse: simple placeholder screen describing potential future browsing/sharing.
    - Analytics: simple placeholder describing upcoming stats; this replicates the reference tab labels without requiring new backend.
- Start destination:
  - A new `HomeScreen` that implements the “Create a deck” layout with stacked cards and uses the bottom navigation bar for global navigation.

Home Screen UX

- Layout:
  - Top section with the title “Create a deck”.
  - Vertical column of large cards:
    - Card 1 (AI generated):
      - Pink pastel background.
      - Title “AI generated”.
      - Short description text similar to the reference (“Enter a prompt and an amount of cards and see the magic happen”).
      - Full-width pill-shaped filled button: “Create an AI deck”.
    - Card 2 (Create own cards):
      - Blue pastel background.
      - Title “Create own cards”.
      - Description (“Make your own flashcards manually”).
      - Full-width pill-shaped button: “Create cards”.
    - Optionally, a third neutral card for Import if needed:
      - Neutral surface.
      - Title “Import deck”.
      - Description (“Paste cards from clipboard or external source”).
      - Button: “Import cards”.
- Actions:
  - Tapping “Create an AI deck” routes to the existing Import flow (ImportScreen) since that is the closest implementation to AI-based creation.
  - Tapping “Create cards” opens the manual deck editor (DeckEditorScreen).
  - Tapping “Import cards” (if present) also goes to ImportScreen.

Deck List Screen UX

- Access: From bottom nav “Decks” tab or from Home after creating/importing.
- Layout changes:
  - Remove the internal Scaffold and reuse the gradient background from the top-level scaffold.
  - Add a simple header “Your decks” with slightly larger typography.
  - Replace the horizontal New deck / Import buttons with a single subtle text link row because primary creation entry is now on Home:
    - Small row: “Create a deck” text button that navigates to Home or opens a small bottom sheet; initial implementation can just route to Home.
  - Deck cards:
    - Use rounded Card with larger radius and slightly pastel surface colors that stand out from the background.
    - Inside each card, show deck name, tags (as is), and optionally a small “Study” label to hint it is tappable.

Deck Editor Screen UX

- Keep existing fields and behavior but restyle:
  - Wrap content in a rounded white card over the gradient background.
  - Increase spacing between sections.
  - Change “New deck” heading to larger typography.
  - Use pill-shaped Save/Cancel buttons aligned to the bottom, with primary action emphasized.

Study Screen UX

- Keep the swipeable card mechanics unchanged.
- Surround the card stack with more generous padding and ensure cards have rounded corners and subtle shadow, matching the rest of the app.
- Use a pastel-tinted background for the Scaffold that still fits the gradient theme.

Settings Screen UX

- Keep options and logic but present them inside a rounded card surface with clear section titles and spacing.
- Use the same gradient background as other screens.

Theming Changes

- Add new light theme colors for gradient and card surfaces:
  - GradientStart (soft pink), GradientEnd (soft purple/blue).
  - CardPink, CardBlue, CardNeutral (for AI/manual/import cards).
- Extend `DeckSwipeTheme` to expose these colors via Material color roles (primary, secondary, surface) and possibly a small `LocalExtendedColors` for non-standard roles (gradient start/end).

Navigation Changes (Technical)

- Add new route constants:
  - `HOME`, `CARDS`, `BROWSE`, `ANALYTICS`.
- Update `DeckSwipeNavHost`:
  - Start destination changes from `DECK_LIST` to `HOME`.
  - Add composable for `HomeScreen`.
  - Add placeholder composables for `CardsScreen`, `BrowseScreen`, `AnalyticsScreen` for now.
- Update `DeckSwipeApp` bottom navigation:
  - Replace existing two-item nav bar with four tabs and a central large plus action button.
  - The plus button opens `HOME` or directly the “Create a deck” screen; for now it navigates to `HOME`.

Strings

- Add new string entries for navigation labels (“Decks”, “Cards”, “Browse”, “Analytics”), home titles (“Create a deck”, card titles and descriptions, button labels).
- Provide English and French values consistent with the existing pattern.

Out of Scope (for now)

- Deep analytics functionality or browsing remote decks; the new tabs will be styled placeholders to keep UX consistent with the reference while avoiding new backend work.
- Advanced iconography or custom fonts; we rely on default Material icons and font with updated sizes and colors.

