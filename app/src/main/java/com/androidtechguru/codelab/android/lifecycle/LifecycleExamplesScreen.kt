package com.androidtechguru.codelab.android.lifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * LIFECYCLE — Composable Screen Interview Prep
 *
 * Key concepts demonstrated:
 * 1. collectAsStateWithLifecycle — lifecycle-aware Flow collection
 * 2. hiltViewModel() — scoped ViewModel injection
 * 3. LaunchedEffect — side effects in Compose
 * 4. Handling all UI states (Loading/Success/Error)
 */
@Composable
fun LifecycleExamplesScreen(
    // hiltViewModel() — provides ViewModel scoped to this composable's NavBackStackEntry
    // Survives recomposition AND configuration changes
    viewModel: LifecycleViewModel = hiltViewModel()
) {
    // collectAsStateWithLifecycle — THE correct way to collect Flow in Compose
    // It stops collection when lifecycle goes below STARTED (e.g., app backgrounded)
    // This prevents unnecessary work and potential crashes from UI updates while invisible
    //
    // DON'T use: .collectAsState() — doesn't respect lifecycle, keeps collecting in background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val counter by viewModel.counter.collectAsStateWithLifecycle()

    // LaunchedEffect — runs a suspend function when the composition enters
    // Key = Unit means it runs ONCE (like init). If key changes, the effect restarts.
    LaunchedEffect(Unit) {
        // One-time setup: analytics, initial load, etc.
        // This coroutine is cancelled when the composable leaves the composition
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Counter — demonstrates SavedStateHandle (survives process death)
        Text(
            text = "Counter (survives process death): $counter",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.onAction(LifecycleAction.IncrementCounter) }) {
                Text("Increment")
            }
            OutlinedButton(onClick = { viewModel.onAction(LifecycleAction.Refresh) }) {
                Text("Refresh Data")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Handle all UI states — exhaustive when with sealed interface
        when (val state = uiState) {
            is LifecycleUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LifecycleUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.items,
                        key = { it } // stable key for efficient recomposition
                    ) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = item,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            is LifecycleUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.onAction(LifecycleAction.Refresh) }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
