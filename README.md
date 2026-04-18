# DeckSwipe

A privacy-first, offline flashcard app that gamifies study sessions using familiar dating-app swipe mechanics. 

![Version](https://img.shields.io/badge/Version-0.1.0-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Prototype-green?style=flat-square)
![License](https://img.shields.io/badge/License-GPLv3-red?style=flat-square)
![Privacy](https://img.shields.io/badge/Tracking-0%25-success?style=flat-square)

## The Concept

Studying is a chore. Traditional flashcard apps (like Anki) are incredibly powerful but visually dated and clunky to use on mobile. **DeckSwipe** changes the UX paradigm by borrowing the most addictive mobile interaction: **the swipe.**

* **Tap:** Flip the card to reveal the answer.
* **Swipe Right:** "I knew this." (Increases the card's interval).
* **Swipe Left:** "I forgot." (Reshuffles the card back into the current deck).

## Key Features

### 🧠 Bring Your Own AI (BYOAI)
Harness the power of Large Language Models (LLMs) to generate study decks from your PDFs and course notes—**without the app ever connecting to the internet.**

Instead of managing API keys or sending your personal files to a server, DeckSwipe uses a secure **Clipboard Bridge**:
1. Tap **"Copy AI prompt to clipboard"** and attach your own notes, or tap **"Pick PDF or text notes for AI"** so the app copies the prompt plus locally extracted text (PDF or plain text) in one clip.
2. Paste that clipboard content into your favorite AI (ChatGPT, Claude, local LLM).
3. Copy the AI's response and tap **"Import from Clipboard"** in DeckSwipe.
4. The app parses the strict JSON output and instantly builds your deck locally.

### 🎴 Core Experience
* **Tinder-Style Gestures:** Fluid, physics-based card swiping built entirely in Jetpack Compose.
* **Spaced Repetition:** A lightweight, local algorithm that ensures you review difficult cards more frequently.
* **Hint System:** Optional hints available before flipping the card.

## Technical Stack

Built with **100% Kotlin** and **Jetpack Compose** using **Clean Architecture**.

| Layer | Technologies |
| :--- | :--- |
| **UI** | Material 3, Jetpack Compose Gestures (`pointerInput`, `Animatable`) |
| **Domain** | MVVM, Coroutines, Flow |
| **Data** | Room (Local Database), `kotlinx.serialization` (JSON Parsing) |
| **System** | Android ClipboardManager API, Storage Access Framework, local PDF and plain-text extraction (`pdfbox-android`, no network) |

**Requirements:** Android 15 (API 35) or newer; project targets API 36.

## Privacy & Manifesto

**DeckSwipe is part of the NoTrackNoTrace 2026 App Suite.**

1. **100% Local:** Look at our `AndroidManifest.xml`. There is no `<uses-permission android:name="android.permission.INTERNET" />`. 
2. **AI Without the Spy:** By utilizing the clipboard for AI generation, your personal study materials and notes never touch our servers (because we don't have any).
3. **Auditable:** Fully open source under GPLv3.

## The JSON Schema Contract
For developers or power users, DeckSwipe expects the following JSON structure when importing via clipboard:

```json
{
  "deck_name": "String",
  "topic_tags": ["String"],
  "cards": [
    {
      "front": "String",
      "back": "String",
      "hint": "String (optional)"
    }
  ]
}

```

## Current Implementation

The current prototype includes:

* Jetpack Compose swipeable card stack for studying decks, with tap-to-flip on the card (plus a flip control for accessibility).
* Room database schema for decks and cards, with a simple spaced repetition model.
* Clipboard-based JSON import matching the schema described above.
* BYOAI helpers: copy the schema prompt only, or pick a PDF / text file to copy prompt plus extracted source text for pasting into an external LLM (still fully offline).
* Dark and light themes using Material 3.

To run the app, open this project in Android Studio (Giraffe or newer) and run the `app` configuration on an emulator or device running Android 15 (API 35) or newer.

To run unit tests:

```bash
./gradlew test
```

## License

**GPLv3** - See [LICENSE](LICENSE).
Free to use, study, and modify.
