package com.androidtechguru.codelab.android.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * COMPOSE LAZY LISTS — Interview Prep
 *
 * Key concepts:
 * 1. LazyColumn/LazyRow — only composes visible items (like RecyclerView)
 * 2. key — stable identity for efficient recomposition and animation
 * 3. contentType — helps Compose reuse compositions across item types
 * 4. stickyHeader — pinned section headers
 * 5. rememberLazyListState — scroll state observation
 */

data class ListItem(
    val id: String,
    val title: String,
    val category: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyListExamplesScreen(
    items: List<ListItem> = sampleItems()
) {
    val listState = rememberLazyListState()

    // Observe scroll position — useful for "scroll to top" FAB
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 5 }
    }

    // snapshotFlow — convert Compose state to Flow for complex observation
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                // Analytics: track scroll depth
                // Log: "User scrolled to item $index"
            }
    }

    val grouped = remember(items) { items.groupBy { it.category } }

    Scaffold(
        floatingActionButton = {
            if (showScrollToTop) {
                val scope = rememberCoroutineScope()
                FloatingActionButton(onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }) {
                    Text("↑")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            grouped.forEach { (category, categoryItems) ->
                // stickyHeader — stays pinned at top while scrolling through section
                stickyHeader(key = "header_$category") {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 8.dp)
                    )
                }

                items(
                    items = categoryItems,

                    // KEY — stable identity for each item
                    // Without key: Compose uses position (index), causing:
                    //   - Incorrect recomposition on insert/delete/reorder
                    //   - Lost state (text fields, checkboxes) on reorder
                    //   - Poor animation behavior
                    // ALWAYS provide a unique, stable key!
                    key = { it.id },

                    // CONTENT TYPE — tells Compose which items are "same kind"
                    // Compose can REUSE compositions between items of same type
                    // Huge performance win for lists with mixed item types
                    contentType = { "list_item" }
                ) { item ->
                    ListItemCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun ListItemCard(
    item: ListItem,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// INTERVIEW TIPS — LazyColumn Performance:
// 1. ALWAYS use key {} — enables correct diffing and state preservation
// 2. Use contentType for mixed-type lists (headers vs items)
// 3. Avoid heavy computation in item composables — move to ViewModel
// 4. Use derivedStateOf for computed scroll state (avoid unnecessary recomps)
// 5. Keep item composables stable (@Stable data, immutable params)
// 6. Don't nest LazyColumn inside LazyColumn (use nested items/sections instead)

private fun sampleItems(): List<ListItem> = buildList {
    val categories = listOf("Kotlin", "Android", "Compose", "Testing")
    categories.forEachIndexed { ci, category ->
        repeat(5) { i ->
            add(ListItem(
                id = "${ci}_$i",
                title = "$category Topic ${i + 1}",
                category = category
            ))
        }
    }
}
