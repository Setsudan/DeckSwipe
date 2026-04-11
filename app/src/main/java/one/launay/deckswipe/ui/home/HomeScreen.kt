package one.launay.deckswipe.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import one.launay.deckswipe.ui.LocalStrings
import one.launay.deckswipe.ui.theme.LargeCardCornerShape

@Composable
fun CreateDeckScreen(
    onAiDeck: () -> Unit,
    onManualDeck: () -> Unit
) {
    val strings = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = strings.homeCreateDeckTitle,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        HomeOptionCard(
            title = strings.homeAiGeneratedTitle,
            description = strings.homeAiGeneratedDescription,
            buttonText = strings.homeAiGeneratedButton,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = onAiDeck
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeOptionCard(
            title = strings.homeManualTitle,
            description = strings.homeManualDescription,
            buttonText = strings.homeManualButton,
            containerColor = MaterialTheme.colorScheme.surface,
            onClick = onManualDeck
        )
    }
}

@Composable
private fun HomeOptionCard(
    title: String,
    description: String,
    buttonText: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LargeCardCornerShape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = buttonText)
            }
        }
    }
}

