package one.launay.deckswipe.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    fun setLanguage(language: AppLanguage) {
        _state.value = _state.value.copy(language = language)
    }

    fun setColorScheme(scheme: AppColorScheme) {
        _state.value = _state.value.copy(colorScheme = scheme)
    }
}

