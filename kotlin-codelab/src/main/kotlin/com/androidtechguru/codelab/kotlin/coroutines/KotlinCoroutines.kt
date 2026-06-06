package com.androidtechguru.codelab.kotlin.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun main() = runBlocking {
    println("╔══════════════════════════════════════════╗")
    println("║   KOTLIN COROUTINES — Codelab            ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. BASICS — suspend, coroutineScope, runBlocking
    // ─────────────────────────────────────────
    println("=== 1. Coroutine Basics ===")

    // suspend function — can only be called from coroutine or another suspend function
    suspend fun fetchUser(): String {
        delay(100)  // non-blocking sleep (unlike Thread.sleep)
        return "Alice"
    }

    // runBlocking — bridges blocking world to coroutine world
    // (we're inside runBlocking right now — this is main())
    val user = fetchUser()
    println("Fetched user: $user")

    // coroutineScope — creates a scope, waits for all children to complete
    coroutineScope {
        launch { delay(50); println("  coroutineScope child 1 done") }
        launch { delay(100); println("  coroutineScope child 2 done") }
    }
    println("  coroutineScope completed (all children finished)")

    // ─────────────────────────────────────────
    // 2. BUILDERS — launch, async, withContext
    // ─────────────────────────────────────────
    println("\n=== 2. Coroutine Builders ===")

    // launch — fire-and-forget, returns Job (no result)
    println("--- launch ---")
    val job = launch {
        delay(50)
        println("  launch completed")
    }
    job.join()  // wait for completion

    // async — returns Deferred<T>, call .await() for result
    println("\n--- async ---")
    val deferred1 = async { delay(100); 10 }
    val deferred2 = async { delay(100); 20 }
    // Both run concurrently! Total time ~100ms, not 200ms
    println("  async results: ${deferred1.await()} + ${deferred2.await()} = ${deferred1.await() + deferred2.await()}")

    // withContext — switches context, suspends until done, returns result
    println("\n--- withContext ---")
    val result = withContext(Dispatchers.Default) {
        // This runs on Default dispatcher
        (1..100).sum()
    }
    println("  withContext result: $result")

    // INTERVIEW TIP:
    // launch  → when you don't need the result (fire events, update UI state)
    // async   → when you need the result AND want parallel execution
    // withContext → when you need to switch dispatcher (e.g., IO for network)

    // ─────────────────────────────────────────
    // 3. DISPATCHERS
    // ─────────────────────────────────────────
    println("\n=== 3. Dispatchers ===")
    println("""
        Dispatchers.Main      — UI thread (Android main thread). Use for UI updates.
        Dispatchers.IO        — Optimized for I/O (network, disk). Shared thread pool, up to 64 threads.
        Dispatchers.Default   — CPU-intensive work. Pool size = number of CPU cores.
        Dispatchers.Unconfined — Starts in caller thread, resumes in whatever thread. Rarely used.

        INTERVIEW TIP: viewModelScope uses Dispatchers.Main.immediate by default.
        Always wrap IO operations with withContext(Dispatchers.IO) for main safety.
    """.trimIndent())

    // ─────────────────────────────────────────
    // 4. STRUCTURED CONCURRENCY
    // ─────────────────────────────────────────
    println("\n=== 4. Structured Concurrency ===")

    // Parent-child relationship: if parent is cancelled, ALL children are cancelled
    println("--- Parent cancels children ---")
    val parentJob = launch {
        val child1 = launch {
            delay(500)
            println("  Child 1 completed")  // won't print — parent cancelled first
        }
        val child2 = launch {
            delay(500)
            println("  Child 2 completed")  // won't print
        }
        delay(100)
        println("  Parent cancelling...")
    }
    parentJob.join()
    delay(100)  // give children time to realize they're cancelled
    println("  Parent done (children were cancelled with it)")

    // coroutineScope — if ANY child fails, ALL siblings are cancelled
    println("\n--- coroutineScope: one fails, all cancel ---")
    try {
        coroutineScope {
            launch {
                delay(200)
                println("  This won't print — sibling failed")
            }
            launch {
                delay(50)
                throw RuntimeException("Child failed!")
            }
        }
    } catch (e: RuntimeException) {
        println("  Caught: ${e.message}")
    }

    // ─────────────────────────────────────────
    // 5. JOB & SUPERVISOR JOB
    // ─────────────────────────────────────────
    println("\n=== 5. Job & SupervisorJob ===")

    // SupervisorJob — child failure does NOT cancel siblings
    println("--- supervisorScope: one fails, others continue ---")
    supervisorScope {
        val child1 = launch {
            delay(100)
            println("  SupervisorScope child 1 completed ✓")
        }
        val child2 = launch {
            delay(50)
            throw RuntimeException("Child 2 failed!")
        }
        child2.invokeOnCompletion { ex ->
            if (ex != null) println("  Child 2 failed: ${ex.message}")
        }
        child1.join()
    }

    // INTERVIEW TIP: Use SupervisorJob in ViewModel's viewModelScope.
    // You don't want one failed API call to cancel all other coroutines.

    // ─────────────────────────────────────────
    // 6. EXCEPTION HANDLING
    // ─────────────────────────────────────────
    println("\n=== 6. Exception Handling ===")

    // try-catch inside coroutine
    println("--- try-catch ---")
    val safeResult = runCatching {
        delay(10)
        throw IllegalStateException("Something broke")
    }
    println("  runCatching: isFailure=${safeResult.isFailure}, message=${safeResult.exceptionOrNull()?.message}")

    // CoroutineExceptionHandler — last resort for uncaught exceptions
    println("\n--- CoroutineExceptionHandler ---")
    val handler = CoroutineExceptionHandler { _, exception ->
        println("  CEH caught: ${exception.message}")
    }
    val handledJob = CoroutineScope(handler).launch {
        throw RuntimeException("Unhandled!")
    }
    handledJob.join()

    // INTERVIEW TIP: CEH only works with launch (not async — async stores exception in Deferred)
    // CEH must be in the root coroutine or scope, not in child

    // ─────────────────────────────────────────
    // 7. FLOW BASICS
    // ─────────────────────────────────────────
    println("\n=== 7. Flow Basics ===")

    // Flow is a COLD stream — doesn't produce values until collected
    println("--- flow builder ---")
    val numbersFlow = flow {
        for (i in 1..3) {
            delay(50)
            emit(i)  // emit values downstream
            println("  Emitted $i")
        }
    }

    // Collecting triggers the flow
    print("  Collected: ")
    numbersFlow.collect { print("$it ") }
    println()

    // flowOf — create flow from fixed values
    println("\n--- flowOf ---")
    flowOf("A", "B", "C").collect { print("$it ") }
    println()

    // asFlow — convert collection to flow
    println("\n--- asFlow ---")
    (1..5).asFlow().collect { print("$it ") }
    println()

    // ─────────────────────────────────────────
    // 8. FLOW OPERATORS
    // ─────────────────────────────────────────
    println("\n=== 8. Flow Operators ===")

    val source = (1..10).asFlow()

    // map
    println("--- map ---")
    source.map { it * 2 }.collect { print("$it ") }
    println()

    // filter
    println("\n--- filter ---")
    source.filter { it % 2 == 0 }.collect { print("$it ") }
    println()

    // transform — more flexible than map (can emit 0 or more values per input)
    println("\n--- transform ---")
    source.take(3).transform { value ->
        emit("Processing $value")
        emit("Done $value")
    }.collect { println("  $it") }

    // take — limits number of values
    println("\n--- take ---")
    source.take(3).collect { print("$it ") }
    println()

    // zip — combines two flows pair-wise
    println("\n--- zip ---")
    val letters = flowOf("a", "b", "c")
    val nums = flowOf(1, 2, 3)
    letters.zip(nums) { l, n -> "$l$n" }.collect { print("$it ") }
    println()

    // combine — combines latest values from both flows
    println("\n--- combine ---")
    val flow1 = flow { emit(1); delay(100); emit(2) }
    val flow2 = flow { emit("A"); delay(150); emit("B") }
    flow1.combine(flow2) { n, l -> "$n$l" }.collect { print("$it ") }
    println()

    // ─────────────────────────────────────────
    // 9. STATEFLOW & SHAREDFLOW
    // ─────────────────────────────────────────
    println("\n=== 9. StateFlow & SharedFlow ===")

    // StateFlow — hot stream, always has a value, replays latest to new collectors
    println("--- StateFlow ---")
    val stateFlow = MutableStateFlow(0)
    val stateJob = launch {
        stateFlow.collect { println("  StateFlow value: $it") }
    }
    stateFlow.value = 1
    delay(50)
    stateFlow.value = 2
    delay(50)
    stateFlow.value = 2  // same value — NOT emitted (StateFlow deduplicates via equals)
    stateFlow.value = 3
    delay(50)
    stateJob.cancel()

    // SharedFlow — hot stream, configurable replay, no initial value
    println("\n--- SharedFlow ---")
    val sharedFlow = MutableSharedFlow<String>(replay = 1)
    sharedFlow.emit("Event 1")  // buffered in replay cache
    val sharedJob = launch {
        sharedFlow.collect { println("  SharedFlow: $it") }
    }
    delay(50)
    sharedFlow.emit("Event 2")
    delay(50)
    sharedJob.cancel()

    println("""

    ┌─────────────────────────────────────────────────────┐
    │ StateFlow vs SharedFlow                              │
    ├─────────────────────────────────────────────────────┤
    │ StateFlow  — has value, replays 1, deduplicates     │
    │              Use for: UI state in ViewModel          │
    │ SharedFlow — no initial value, configurable replay   │
    │              Use for: one-time events (nav, snackbar)│
    └─────────────────────────────────────────────────────┘
    """.trimIndent())

    // ─────────────────────────────────────────
    // 10. CHANNELS
    // ─────────────────────────────────────────
    println("\n=== 10. Channels ===")

    // Channel — hot stream for communication between coroutines
    println("--- produce (ReceiveChannel) ---")
    val channel = produce {
        for (i in 1..5) {
            delay(50)
            send(i)
        }
    }
    for (value in channel) {
        print("$value ")
    }
    println()

    // INTERVIEW TIP: Channel types:
    // RENDEZVOUS (0 buffer) — sender suspends until receiver is ready
    // BUFFERED (n buffer) — sender suspends when buffer is full
    // CONFLATED — keeps only latest, drops old
    // UNLIMITED — never suspends sender (can OOM)

    // ─────────────────────────────────────────
    // 11. CANCELLATION
    // ─────────────────────────────────────────
    println("\n=== 11. Cancellation ===")

    // Cooperative cancellation — coroutine must check for cancellation
    println("--- isActive check ---")
    val cancelJob = launch {
        var i = 0
        while (isActive) {  // check if cancelled
            i++
            if (i > 3) break
            println("  Working... $i")
            delay(50)  // delay is a cancellation point
        }
        println("  Finished (isActive: $isActive)")
    }
    cancelJob.join()

    // ensureActive — throws CancellationException if not active
    // NonCancellable — run cleanup code that shouldn't be cancelled
    println("\n--- NonCancellable for cleanup ---")
    val cleanupJob = launch {
        try {
            delay(500)
        } catch (e: CancellationException) {
            println("  Cancelled! Running cleanup...")
            withContext(NonCancellable) {
                delay(100)  // cleanup delay works because NonCancellable
                println("  Cleanup done")
            }
        }
    }
    delay(50)
    cleanupJob.cancel()
    cleanupJob.join()

    // ─────────────────────────────────────────
    // 12. MUTEX
    // ─────────────────────────────────────────
    println("\n=== 12. Mutex (Thread Safety) ===")

    // Mutex — coroutine-safe mutual exclusion (like synchronized but non-blocking)
    val mutex = Mutex()
    var counter = 0

    coroutineScope {
        repeat(100) {
            launch {
                mutex.withLock {
                    counter++
                }
            }
        }
    }
    println("  Counter with Mutex: $counter (should be 100)")

    // INTERVIEW TIP: Don't use synchronized in coroutines — it blocks the thread.
    // Use Mutex.withLock instead — it suspends, not blocks.

    println("\n✅ Kotlin Coroutines Codelab Complete!")
}
