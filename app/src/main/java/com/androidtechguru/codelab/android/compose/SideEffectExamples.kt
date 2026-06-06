package com.androidtechguru.codelab.android.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * COMPOSE SIDE EFFECTS — Interview Prep
 *
 * Side effects = operations that escape the scope of a composable function
 * (network calls, logging, navigation, snackbar, etc.)
 *
 * Key concepts:
 * 1. LaunchedEffect — run suspend function, restarts when key changes
 * 2. DisposableEffect — setup + cleanup (listeners, callbacks)
 * 3. SideEffect — runs on every successful recomposition
 * 4. rememberCoroutineScope — scope for event-driven coroutines
 * 5. rememberUpdatedState — capture latest value in long-lived effects
 */

// ── LaunchedEffect ──
// Runs a suspend function when entering composition, restarts when key changes
// Cancelled when leaving composition or key changes
@Composable
fun LaunchedEffectExample() {
    var userId by remember { mutableStateOf("user_1") }
    var userData by remember { mutableStateOf("Loading...") }

    // KEY = userId → restarts the effect whenever userId changes
    // The previous coroutine is CANCELLED before the new one starts
    LaunchedEffect(userId) {
        userData = "Loading $userId..."
        delay(1000) // simulate API call
        userData = "Data for $userId loaded!"
    }

    // LaunchedEffect(Unit) — runs ONCE, never restarts (like init)
    LaunchedEffect(Unit) {
        // One-time setup: analytics screen view, initial fetch, etc.
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(userData)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            userId = if (userId == "user_1") "user_2" else "user_1"
        }) {
            Text("Switch User")
        }
    }
}

// ── DisposableEffect ──
// For setup + cleanup (like onStart/onStop or addListener/removeListener)
@Composable
fun DisposableEffectExample() {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isVisible by remember { mutableStateOf(true) }

    // DisposableEffect — setup runs when entering, onDispose runs when leaving
    DisposableEffect(lifecycleOwner) {
        // SETUP: register listener, observer, callback
        // val observer = LifecycleEventObserver { _, event -> ... }
        // lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            // CLEANUP: unregister, release resources
            // lifecycleOwner.lifecycle.removeObserver(observer)
            // This is called when:
            // 1. The composable leaves the composition
            // 2. The key (lifecycleOwner) changes
        }
    }

    // INTERVIEW TIP: Use DisposableEffect for anything that needs cleanup:
    // - Lifecycle observers
    // - Sensor listeners
    // - Broadcast receivers
    // - Map/camera listeners
    // - Analytics start/stop tracking
}

// ── SideEffect ──
// Runs on EVERY successful recomposition (non-suspend)
// Use for: syncing Compose state with non-Compose code
@Composable
fun SideEffectExample(
    onScreenViewed: (String) -> Unit = {}
) {
    var screenName by remember { mutableStateOf("Home") }

    // Runs after every successful recomposition
    // Use for: updating external state, analytics, logging
    SideEffect {
        // Sync Compose state → external system
        onScreenViewed(screenName)
    }

    // INTERVIEW TIP: SideEffect vs LaunchedEffect:
    // - SideEffect: non-suspend, runs every recomposition
    // - LaunchedEffect: suspend, runs when key changes
}

// ── rememberCoroutineScope ──
// Creates a scope tied to the composition — for launching coroutines from callbacks
@Composable
fun RememberCoroutineScopeExample() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Button(onClick = {
                // Use scope for event-driven coroutines (button clicks, etc.)
                // DON'T use LaunchedEffect for click handlers — it's for composition lifecycle
                scope.launch {
                    snackbarHostState.showSnackbar("Button clicked!")
                }
            }) {
                Text("Show Snackbar")
            }
        }
    }

    // INTERVIEW TIP: rememberCoroutineScope vs LaunchedEffect:
    // - LaunchedEffect: for effects that should run on composition/key change
    // - rememberCoroutineScope: for effects triggered by USER EVENTS (clicks, gestures)
}

// ── rememberUpdatedState ──
// Captures the latest value of a changing parameter inside a long-lived effect
@Composable
fun RememberUpdatedStateExample(
    onTimeout: () -> Unit  // this callback might change between recompositions
) {
    // Problem: if onTimeout changes after LaunchedEffect started,
    // the effect still has the OLD reference (closure captures it once)
    //
    // Solution: rememberUpdatedState always holds the LATEST value
    val currentOnTimeout by rememberUpdatedState(onTimeout)

    LaunchedEffect(Unit) {
        delay(5000) // long delay
        currentOnTimeout() // calls the LATEST version, not the one captured at launch
    }

    // INTERVIEW TIP: Use rememberUpdatedState when:
    // - A LaunchedEffect runs for a long time
    // - It references a parameter that might change
    // - You want it to use the latest version when it finally executes
}
