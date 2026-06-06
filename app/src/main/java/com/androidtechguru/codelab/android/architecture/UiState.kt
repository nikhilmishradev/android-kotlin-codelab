package com.androidtechguru.codelab.android.architecture

/**
 * ARCHITECTURE — UI State & Events Pattern
 *
 * Sealed interface for exhaustive state handling.
 * The UI observes this as StateFlow<UiState> and renders accordingly.
 */

// ── Generic UI State — reusable across screens ──
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val cause: Throwable? = null) : UiState<Nothing>
}

// Extension to check state easily
val <T> UiState<T>.isLoading get() = this is UiState.Loading
val <T> UiState<T>.dataOrNull get() = (this as? UiState.Success)?.data

// ── One-time events (navigation, snackbar, toast) ──
// Use SharedFlow(replay=0) in ViewModel to emit these
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data object NavigateBack : UiEvent
}

// ── Result wrapper for repository/domain layer ──
// Separates success/failure without throwing exceptions through layers
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Failure(val error: AppError) : Result<Nothing>
}

sealed interface AppError {
    data class Network(val message: String, val code: Int? = null) : AppError
    data class Database(val message: String) : AppError
    data class Unknown(val throwable: Throwable) : AppError
}

// Extensions for clean error handling
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onFailure(action: (AppError) -> Unit): Result<T> {
    if (this is Result.Failure) action(error)
    return this
}

fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}

// INTERVIEW TIP: Why sealed interface over sealed class?
// - Sealed interface allows implementing multiple sealed hierarchies
// - Sealed class allows constructors with shared state
// - Prefer sealed interface when subclasses don't share common state
