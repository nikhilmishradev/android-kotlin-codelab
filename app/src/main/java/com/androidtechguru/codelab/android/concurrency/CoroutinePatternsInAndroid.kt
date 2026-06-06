package com.androidtechguru.codelab.android.concurrency

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * CONCURRENCY — Coroutine Patterns in Android
 *
 * Reference file demonstrating key patterns.
 * Each method documents a specific pattern with inline comments.
 */
class CoroutinePatternsReference {

    // ═══════════════════════════════════════════
    // 1. viewModelScope — auto-cancelled on ViewModel clear
    // ═══════════════════════════════════════════

    // In ViewModel:
    // viewModelScope.launch {
    //     val result = repository.getUsers()  // suspend call
    //     _uiState.value = UiState.Success(result)
    // }
    //
    // viewModelScope uses Dispatchers.Main.immediate
    // All coroutines auto-cancel when ViewModel.onCleared() is called
    // No manual Job tracking needed!

    // ═══════════════════════════════════════════
    // 2. lifecycleScope + repeatOnLifecycle — lifecycle-aware collection
    // ═══════════════════════════════════════════

    // In Activity/Fragment:
    // lifecycleScope.launch {
    //     repeatOnLifecycle(Lifecycle.State.STARTED) {
    //         // This block runs when lifecycle is at least STARTED
    //         // Stops when lifecycle goes below STARTED (app backgrounded)
    //         // Restarts when lifecycle reaches STARTED again (app foregrounded)
    //         viewModel.uiState.collect { state ->
    //             updateUI(state)
    //         }
    //     }
    // }
    //
    // In Compose, collectAsStateWithLifecycle() does this automatically:
    // val state by viewModel.uiState.collectAsStateWithLifecycle()

    // ═══════════════════════════════════════════
    // 3. Main Safety — withContext(Dispatchers.IO)
    // ═══════════════════════════════════════════

    // A function is "main-safe" if it can be called from the main thread
    // without blocking it. Repositories should always be main-safe.
    suspend fun mainSafeRepositoryCall(): List<String> {
        // withContext switches to IO dispatcher for blocking work
        // then resumes on the original dispatcher when done
        return withContext(Dispatchers.IO) {
            // Heavy I/O work here (network, disk)
            listOf("result")
        }
    }

    // INTERVIEW TIP: Room and Retrofit suspend functions are already main-safe
    // (they switch to a background thread internally).
    // You DON'T need withContext(IO) when calling room.getUsers() or api.getUsers().
    // You DO need it for: file I/O, JSON parsing, image processing.

    // ═══════════════════════════════════════════
    // 4. Parallel Decomposition — async + awaitAll
    // ═══════════════════════════════════════════

    suspend fun loadDashboard(): DashboardData = coroutineScope {
        // Launch multiple API calls in parallel
        val userDeferred = async { fetchUser() }
        val postsDeferred = async { fetchPosts() }
        val settingsDeferred = async { fetchSettings() }

        // Wait for all to complete (total time = max of individual times)
        DashboardData(
            user = userDeferred.await(),
            posts = postsDeferred.await(),
            settings = settingsDeferred.await()
        )
        // If any fails, coroutineScope cancels all siblings
    }

    // ═══════════════════════════════════════════
    // 5. Mutex — Thread safety in coroutines
    // ═══════════════════════════════════════════

    private val mutex = Mutex()
    private var counter = 0

    // DON'T use synchronized in coroutines — it blocks the thread
    // USE Mutex — it suspends (non-blocking)
    suspend fun incrementSafely() {
        mutex.withLock {
            counter++
        }
    }

    // ═══════════════════════════════════════════
    // 6. Cancellation — cooperative patterns
    // ═══════════════════════════════════════════

    suspend fun cooperativeCancellation() = coroutineScope {
        val job = launch {
            repeat(1000) { i ->
                // Check 1: isActive (manual check)
                if (!isActive) return@launch

                // Check 2: ensureActive() (throws CancellationException)
                ensureActive()

                // Check 3: yield() (suspends, checks cancellation, gives other coroutines a chance)
                yield()

                // delay() is also a cancellation point
                delay(100)
            }
        }

        delay(500)
        job.cancel()  // cooperative — coroutine must check for cancellation
        job.join()     // wait for cancellation to complete

        // cancelAndJoin() = cancel() + join() in one call
    }

    // ═══════════════════════════════════════════
    // 7. Flow patterns in Android
    // ═══════════════════════════════════════════

    // Combine multiple flows
    fun combinedState(
        userFlow: Flow<String>,
        settingsFlow: Flow<String>
    ): Flow<String> = combine(userFlow, settingsFlow) { user, settings ->
        "User: $user, Settings: $settings"
    }

    // Debounce for search
    fun searchWithDebounce(queryFlow: Flow<String>): Flow<List<String>> =
        queryFlow
            .debounce(300)  // wait 300ms after last emission
            .distinctUntilChanged()  // skip if same as previous
            .filter { it.length >= 2 }  // minimum query length
            .flatMapLatest { query ->
                // flatMapLatest cancels previous flow when new query arrives
                flow { emit(search(query)) }
            }

    // ── Helpers ──
    private suspend fun fetchUser(): String { delay(100); return "User" }
    private suspend fun fetchPosts(): List<String> { delay(200); return listOf("Post") }
    private suspend fun fetchSettings(): String { delay(150); return "Settings" }
    private suspend fun search(query: String): List<String> = listOf("Result for $query")
}

data class DashboardData(val user: String, val posts: List<String>, val settings: String)
