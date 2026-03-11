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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import one.launay.deckswipe.ui.LocalDeckRepository
import one.launay.deckswipe.domain.spacedrepetition.SpacedRepetitionEngine
import one.launay.deckswipe.ui.study.components.SwipeableCardStack

@Composable
fun StudyScreen(
    deckId: Long,
    onBack: () -> Unit
) {
    val repository = LocalDeckRepository.current
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

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Loading cards...")
                }
            } else if (state.currentCard == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "You are done for now.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "There are no cards due in this deck.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val card = state.currentCard ?: return@Scaffold
                SwipeableCardStack(
                    modifier = Modifier.fillMaxSize(),
                    onSwipedLeft = {
                        showBack = false
                        vm.onForgot()
                    },
                    onSwipedRight = {
                        showBack = false
                        vm.onKnewIt()
                    }
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
                                text = "Hint: ${card.hint}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = { showBack = !showBack }) {
                                Text(text = if (showBack) "Show question" else "Flip card")
                            }
                            Text(
                                text = "Remaining: ${state.remainingCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = { vm.onForgot(); showBack = false }) {
                                Text(text = "Forgot (swipe left)")
                            }
                            Button(onClick = { vm.onKnewIt(); showBack = false }) {
                                Text(text = "Knew it (swipe right)")
                            }
                        }
                    }
                }
            }
        }
    }
}

