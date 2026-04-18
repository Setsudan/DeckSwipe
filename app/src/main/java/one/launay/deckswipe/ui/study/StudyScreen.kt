package one.launay.deckswipe.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.ui.LocalStrings
import one.launay.deckswipe.domain.spacedrepetition.SpacedRepetitionEngine
import one.launay.deckswipe.ui.study.components.SwipeableCardStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    deckId: Long,
    contentPadding: PaddingValues,
    onBack: () -> Unit
) {
    val repository = LocalDeckRepository.current
    val strings = LocalStrings.current
    val vm: StudyViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                StudyViewModel(
                    deckId = deckId,
                    repository = repository,
                    engine = SpacedRepetitionEngine { System.currentTimeMillis() }
                )
            }
        }
    )
    val state by vm.state.collectAsState()
    var showBack by remember { mutableStateOf(false) }

    val title = state.deckTitle.ifBlank { strings.studyUntitledDeck }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = strings.studyDoneBack)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(contentPadding)
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = strings.studyLoading)
                }
            } else if (state.currentCard == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.studyDoneTitle,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.studyDoneSubtitle,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = strings.studyDoneBack)
                    }
                }
            } else {
                val card = state.currentCard ?: return@Box
                val denom = state.sessionReviewsCompleted + state.remainingCount
                val progress = if (denom > 0) {
                    state.sessionReviewsCompleted.toFloat() / denom.toFloat()
                } else {
                    0f
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.studyRemaining + ": " + state.remainingCount,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = strings.studySessionReviewed + ": " + state.sessionReviewsCompleted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    SwipeableCardStack(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onSwipedLeft = {
                            showBack = false
                            vm.onForgot()
                        },
                        onSwipedRight = {
                            showBack = false
                            vm.onKnewIt()
                        },
                        onCardTap = { showBack = !showBack }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (showBack) card.back else card.front,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (!showBack && card.hint != null) {
                                Text(
                                    text = strings.studyHintLabel + ": " + card.hint,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = { showBack = !showBack },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        text = if (showBack) {
                                            strings.studyShowQuestion
                                        } else {
                                            strings.studyFlipCard
                                        }
                                    )
                                }
                                Text(
                                    text = strings.studyRemaining + ": " + state.remainingCount,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = strings.studySwipeHint,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
