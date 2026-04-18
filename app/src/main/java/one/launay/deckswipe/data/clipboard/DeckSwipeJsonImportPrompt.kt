package one.launay.deckswipe.data.clipboard

object DeckSwipeJsonImportPrompt {

    private val SCHEMA_PROMPT: String =
        """
Use this JSON schema to generate a DeckSwipe flashcard deck. Respond ONLY with minified JSON that matches exactly this structure, no explanations:

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

Topic: <describe topic here>
Level: <difficulty/level here>
Number of cards: <e.g. 20>
        """.trimIndent()

    private const val SOURCE_START = "\n\n---COURSE_SOURCE_START---\n\n"
    private const val TRUNCATION_FOOTER =
        "\n\n[DeckSwipe: source text was truncated for clipboard size.]"

    fun schemaPromptOnly(): String = SCHEMA_PROMPT

    fun buildClipboardWithSource(sourceBody: String, truncated: Boolean): String {
        val footer = if (truncated) TRUNCATION_FOOTER else ""
        return SCHEMA_PROMPT + SOURCE_START + sourceBody + footer
    }
}
