package com.androidtechguru.codelab.android.lifecycle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LIFECYCLE — ViewModel Interview Prep
 *
 * Key concepts demonstrated:
 * 1. @HiltViewModel with @Inject constructor
 * 2. MutableStateFlow exposed as read-only StateFlow (backing property pattern)
 * 3. SavedStateHandle for surviving process death
 * 4. Sealed interface for UI state (Loading/Success/Error)
 * 5. viewModelScope for coroutine lifecycle management
 * 6. Unidirectional data flow (UDF): UI sends actions → ViewModel updates state
 */
@HiltViewModel
class LifecycleViewModel @Inject constructor(
    // SavedStateHandle survives PROCESS DEATH (not just config changes)
    // ViewModel alone only survives config changes (rotation, locale, etc.)
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ── UI State ──
    // Pattern: private MutableStateFlow + public StateFlow
    // This prevents the UI from directly modifying state (unidirectional data flow)
    private val _uiState = MutableStateFlow<LifecycleUiState>(LifecycleUiState.Loading)
    val uiState: StateFlow<LifecycleUiState> = _uiState.asStateFlow()

    // SavedStateHandle — persists across process death
    // Use for: search queries, scroll position, selected tab, form input
    // Do NOT use for: large data, network responses (use Repository/cache instead)
    private val searchQuery = savedStateHandle.getStateFlow("search_query", "")

    // Counter example — survives process death via SavedStateHandle
    val counter: StateFlow<Int> = savedStateHandle.getStateFlow("counter", 0)

    init {
        // viewModelScope — automatically cancelled when ViewModel is cleared
        // This prevents memory leaks from long-running coroutines
        loadData()
    }

    // ── Actions (events from UI) ──
    fun onAction(action: LifecycleAction) {
        when (action) {
            is LifecycleAction.Refresh -> loadData()
            is LifecycleAction.IncrementCounter -> {
                // SavedStateHandle survives process death
                val current = savedStateHandle.get<Int>("counter") ?: 0
                savedStateHandle["counter"] = current + 1
            }
            is LifecycleAction.UpdateSearch -> {
                savedStateHandle["search_query"] = action.query
            }
        }
    }

    private fun loadData() {
        // viewModelScope.launch: coroutine is cancelled when ViewModel is cleared
        // No need to manually cancel — structured concurrency handles it
        viewModelScope.launch {
            _uiState.value = LifecycleUiState.Loading

            try {
                delay(1000) // Simulate network call
                val items = listOf("Item 1", "Item 2", "Item 3")

                // update {} is thread-safe atomic update for StateFlow
                _uiState.update { LifecycleUiState.Success(items) }
            } catch (e: Exception) {
                _uiState.value = LifecycleUiState.Error(
                    message = e.message ?: "Unknown error"
                )
            }
        }
    }

    // INTERVIEW TIP: ViewModel.onCleared() is called when:
    // - Activity is finishing (back press, finish())
    // - Fragment is detached permanently
    // NOT called on config change (rotation) — ViewModel survives that!
    override fun onCleared() {
        super.onCleared()
        // Clean up resources that aren't managed by viewModelScope
        // viewModelScope coroutines are auto-cancelled here
    }
}

// ── UI State — Sealed Interface ──
// Sealed = exhaustive when expressions, compiler enforces all states are handled
sealed interface LifecycleUiState {
    data object Loading : LifecycleUiState
    data class Success(val items: List<String>) : LifecycleUiState
    data class Error(val message: String) : LifecycleUiState
}

// ── UI Actions — Sealed Interface ──
// All possible user interactions for this screen
sealed interface LifecycleAction {
    data object Refresh : LifecycleAction
    data object IncrementCounter : LifecycleAction
    data class UpdateSearch(val query: String) : LifecycleAction
}
