package one.launay.deckswipe.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import one.launay.deckswipe.ui.LocalStrings

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsState()
    val strings = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = strings.settingsTitle)
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = strings.languageLabel)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    LanguageOptionRow(
                        label = strings.languageEnglish,
                        selected = state.language == AppLanguage.EN,
                        onClick = { viewModel.setLanguage(AppLanguage.EN) }
                    )
                    LanguageOptionRow(
                        label = strings.languageFrench,
                        selected = state.language == AppLanguage.FR,
                        onClick = { viewModel.setLanguage(AppLanguage.FR) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = strings.themeLabel)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    ThemeOptionRow(
                        label = strings.themeLight,
                        selected = state.colorScheme == AppColorScheme.LIGHT,
                        onClick = { viewModel.setColorScheme(AppColorScheme.LIGHT) }
                    )
                    ThemeOptionRow(
                        label = strings.themeDark,
                        selected = state.colorScheme == AppColorScheme.DARK,
                        onClick = { viewModel.setColorScheme(AppColorScheme.DARK) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.height(0.dp))
        Text(text = label)
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.height(0.dp))
        Text(text = label)
    }
}

