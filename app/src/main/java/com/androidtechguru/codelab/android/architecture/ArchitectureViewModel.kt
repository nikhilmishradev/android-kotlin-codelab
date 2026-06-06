package com.androidtechguru.codelab.android.architecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ARCHITECTURE — Full MVVM + Clean Architecture ViewModel
 *
 * Demonstrates:
 * 1. Unidirectional Data Flow (UDF): events UP, state DOWN
 * 2. Sealed interface for actions (all possible user interactions)
 * 3. StateFlow for UI state
 * 4. SharedFlow for one-time events (navigation, snackbar)
 * 5. Using Use Cases (Interactors) for business logic
 */
@HiltViewModel
class ArchitectureViewModel @Inject constructor(
    private val getArticles: GetArticlesUseCase,
    private val refreshArticles: RefreshArticlesUseCase,
    private val createArticle: CreateArticleUseCase
) : ViewModel() {

    // ── UI State ──
    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()

    // ── One-time events (navigation, snackbar) ──
    // SharedFlow with replay=0 — events are NOT replayed to new collectors
    // This prevents re-showing a snackbar after rotation
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        observeArticles()
    }

    // ── Handle UI Actions (single entry point) ──
    fun onAction(action: ArticleAction) {
        when (action) {
            is ArticleAction.Refresh -> refresh()
            is ArticleAction.ArticleClicked -> navigateToDetail(action.id)
            is ArticleAction.CreateArticle -> create(action.title, action.content, action.author)
        }
    }

    private fun observeArticles() {
        // Use Case returns Flow — collect it in viewModelScope
        getArticles()
            .onStart { _uiState.value = UiState.Loading }
            .catch { e -> _uiState.value = UiState.Error(e.message ?: "Unknown error") }
            .onEach { articles -> _uiState.value = UiState.Success(articles) }
            .launchIn(viewModelScope)  // launchIn = launch { flow.collect() }
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshArticles()
                .onSuccess {
                    _events.emit(UiEvent.ShowSnackbar("Refreshed!"))
                }
                .onFailure { error ->
                    val message = when (error) {
                        is AppError.Network -> "Network error: ${error.message}"
                        is AppError.Database -> "Database error: ${error.message}"
                        is AppError.Unknown -> "Unexpected error"
                    }
                    _events.emit(UiEvent.ShowSnackbar(message))
                }
        }
    }

    private fun navigateToDetail(articleId: String) {
        viewModelScope.launch {
            _events.emit(UiEvent.Navigate("article/$articleId"))
        }
    }

    private fun create(title: String, content: String, author: String) {
        viewModelScope.launch {
            createArticle(title, content, author)
                .onSuccess {
                    _events.emit(UiEvent.ShowSnackbar("Article created!"))
                }
                .onFailure { error ->
                    _events.emit(UiEvent.ShowSnackbar("Failed: ${(error as? AppError.Unknown)?.throwable?.message}"))
                }
        }
    }
}

// ── UI Actions — all possible user interactions ──
sealed interface ArticleAction {
    data object Refresh : ArticleAction
    data class ArticleClicked(val id: String) : ArticleAction
    data class CreateArticle(val title: String, val content: String, val author: String) : ArticleAction
}

// INTERVIEW TIP — UDF (Unidirectional Data Flow):
//
//   UI ──(Action)──→ ViewModel ──(calls)──→ UseCase ──→ Repository
//   UI ←──(State)─── ViewModel ←──(Flow)─── UseCase ←── Repository
//
// State flows DOWN (ViewModel → UI via StateFlow)
// Events flow UP (UI → ViewModel via onAction())
// One-time events use SharedFlow (navigation, snackbar, toast)
