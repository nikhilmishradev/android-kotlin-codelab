package com.androidtechguru.codelab.android.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * COMPOSE STATE — Interview Prep
 *
 * Key concepts:
 * 1. remember — survives recomposition, lost on config change
 * 2. rememberSaveable — survives recomposition AND config change
 * 3. mutableStateOf — triggers recomposition when value changes
 * 4. State hoisting — separating state from UI for testability
 * 5. derivedStateOf — compute only when dependencies change
 */

// ── STATE HOISTING PATTERN ──
// Stateful wrapper — owns the state
@Composable
fun CounterScreen() {
    // rememberSaveable — survives configuration changes (rotation, theme)
    // Uses Bundle serialization under the hood
    var count by rememberSaveable { mutableStateOf(0) }

    // Hoist state DOWN to stateless composable
    StatelessCounter(
        count = count,                         // state flows DOWN
        onIncrement = { count++ },             // events flow UP
        onDecrement = { if (count > 0) count-- },
        onReset = { count = 0 }
    )
}

// Stateless composable — receives state as params, emits events via callbacks
// Benefits: reusable, testable, previewable
@Composable
fun StatelessCounter(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Count: $count", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onIncrement) { Text("+") }
            Button(onClick = onDecrement) { Text("-") }
            OutlinedButton(onClick = onReset) { Text("Reset") }
        }
    }
}

// ── REMEMBER vs REMEMBER_SAVEABLE ──
@Composable
fun RememberDemo() {
    // remember — survives recomposition only
    // Lost on: configuration change, process death, navigation
    var rememberCount by remember { mutableStateOf(0) }

    // rememberSaveable — survives recomposition + config change
    // Lost on: process death (unless using SavedStateHandle in ViewModel)
    var saveableCount by rememberSaveable { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("remember (lost on rotation): $rememberCount")
        Text("rememberSaveable (survives rotation): $saveableCount")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { rememberCount++; saveableCount++ }) {
            Text("Increment Both")
        }
    }
}

// ── DERIVED STATE ──
@Composable
fun DerivedStateExample() {
    var text by rememberSaveable { mutableStateOf("") }

    // derivedStateOf — recomputes ONLY when 'text' actually changes
    // Without it, the validation would recalculate on every recomposition
    // even if text didn't change
    val isValid by remember {
        derivedStateOf { text.length >= 3 && text.contains("@") }
    }

    // Another example: derived from multiple states
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    val fullName by remember {
        derivedStateOf { "$firstName $lastName".trim() }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Email") },
            isError = text.isNotEmpty() && !isValid,
            supportingText = {
                if (text.isNotEmpty() && !isValid) {
                    Text("Must be 3+ chars with @")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last") },
                modifier = Modifier.weight(1f)
            )
        }
        if (fullName.isNotEmpty()) {
            Text("Full name: $fullName")
        }
    }
}

// INTERVIEW TIPS:
// - remember {} runs ONCE per composition, result cached across recompositions
// - mutableStateOf creates observable state that triggers recomposition on change
// - State hoisting: lift state UP to the lowest common ancestor that needs it
// - derivedStateOf: use when a value depends on other state and is read often
//   but the source state changes less frequently than recomposition occurs
