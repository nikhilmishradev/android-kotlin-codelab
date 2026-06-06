# Jetpack Compose Cheatsheet

Quick reference for Compose patterns and best practices.

---

## State Management

```kotlin
// remember — survives recomposition
var count by remember { mutableStateOf(0) }

// rememberSaveable — survives configuration changes
var text by rememberSaveable { mutableStateOf("") }

// State hoisting pattern
@Composable
fun StatefulCounter() {
    var count by remember { mutableStateOf(0) }
    StatelessCounter(count = count, onIncrement = { count++ })
}

@Composable
fun StatelessCounter(count: Int, onIncrement: () -> Unit) {
    Button(onClick = onIncrement) { Text("Count: $count") }
}

// Collecting Flow as State
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// Derived state — recompute only when dependencies change
val isValid by remember { derivedStateOf { text.length >= 3 } }
```

## Side Effects

```kotlin
// LaunchedEffect — run suspend function when key changes
LaunchedEffect(key1) {
    // coroutine scope — cancelled when key changes or composable leaves
}

// DisposableEffect — setup + cleanup (like listeners)
DisposableEffect(key1) {
    val listener = ...
    onDispose { /* cleanup */ }
}

// SideEffect — run on every successful recomposition (non-suspend)
SideEffect { analytics.log("screen_viewed") }

// rememberCoroutineScope — scope tied to composition
val scope = rememberCoroutineScope()
Button(onClick = { scope.launch { /* suspend work */ } })

// snapshotFlow — convert Compose state to Flow
LaunchedEffect(Unit) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .collect { index -> /* react to scroll */ }
}
```

## Layouts

```kotlin
Column(modifier, verticalArrangement, horizontalAlignment) { }
Row(modifier, horizontalArrangement, verticalAlignment) { }
Box(modifier, contentAlignment) { }
Scaffold(topBar, bottomBar, floatingActionButton, snackbarHost) { padding -> }
```

## Lazy Lists

```kotlin
LazyColumn {
    items(
        items = list,
        key = { it.id },           // stable keys for recomposition
        contentType = { "item" }   // helps Compose reuse compositions
    ) { item ->
        ItemCard(item)
    }
}
```

## Modifiers (Order Matters!)

```kotlin
Modifier
    .clickable { }        // clickable area = everything after this
    .padding(16.dp)       // padding outside the background
    .background(Color.Red)
    .padding(8.dp)        // padding inside the background
    .fillMaxWidth()
```

## Navigation

```kotlin
val navController = rememberNavController()

NavHost(navController = navController, startDestination = "home") {
    composable("home") { HomeScreen(onNavigate = { navController.navigate("detail/$id") }) }
    composable("detail/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")
        DetailScreen(id = id)
    }
}
```

## Animations

```kotlin
// Simple value animation
val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f)

// Visibility animation
AnimatedVisibility(visible = isVisible) { Text("Hello") }

// Content switching
Crossfade(targetState = currentScreen) { screen -> /* show screen */ }
```

## Stability & Performance

```kotlin
@Stable       // Compose can skip recomposition if instance hasn't changed
data class UiState(val items: List<Item>)

@Immutable    // Stronger contract — all properties will never change
data class Config(val theme: String)

// Key rules:
// - Prefer immutable data classes for state
// - Use key {} in lazy lists
// - Use derivedStateOf for computed values
// - Avoid allocations in composable body (lambdas, objects)
```

## CompositionLocal

```kotlin
val LocalAppColors = staticCompositionLocalOf { AppColors() }

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides customColors) {
        content()
    }
}

// Access anywhere in the tree
val colors = LocalAppColors.current
```

---

> Add your own patterns and snippets as you practice.
