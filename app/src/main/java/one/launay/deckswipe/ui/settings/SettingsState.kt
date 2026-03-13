package one.launay.deckswipe.ui.settings

enum class AppLanguage {
    EN,
    FR
}

enum class AppColorScheme {
    LIGHT,
    DARK
}

data class SettingsState(
    val language: AppLanguage = AppLanguage.EN,
    val colorScheme: AppColorScheme = AppColorScheme.LIGHT
)

