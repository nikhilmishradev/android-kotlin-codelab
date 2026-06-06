package com.androidtechguru.codelab.kotlin.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * COROUTINE INTERVIEW PUZZLES — Predict the Output
 *
 * These are the exact kind of questions asked in Android interviews.
 * For each puzzle: read the code, predict the output, then run to verify.
 *
 * Run this file → see answers printed with explanations.
 */
fun main() = runBlocking {

    println("╔══════════════════════════════════════════════╗")
    println("║  COROUTINE INTERVIEW PUZZLES                 ║")
    println("║  Predict the output before running!          ║")
    println("╚══════════════════════════════════════════════╝\n")

    puzzle1_asyncOrdering()
    puzzle2_launchVsAsync()
    puzzle3_cancelledParent()
    puzzle4_exceptionInLaunch()
    puzzle5_exceptionInAsync()
    puzzle6_supervisorScope()
    puzzle7_withContextVsLaunch()
    puzzle8_flowColdStream()
    puzzle9_sharedFlowVsStateFlow()
    puzzle10_cancellationCooperation()
    puzzle11_nestedCoroutineScope()
    puzzle12_dispatcherSwitch()
    puzzle13_launchOrder()
    puzzle14_joinVsAwait()
    puzzle15_raceCondition()
    puzzle16_collectLatestTrap()
    puzzle17_supervisorJobMisuse()
    puzzle18_coroutineScopeVsSupervisorScope()
    puzzle19_nonCancellable()
    puzzle20_channelFanOut()
}

// ═════════════════════════════════════════════
// PUZZLE 1: async ordering (from your example)
// ═════════════════════════════════════════════
suspend fun puzzle1_asyncOrdering() {
    println("═══ PUZZLE 1: async ordering ═══")
    println("""
    CODE:
    val deferred1 = async {
        delay(100)
        println("First")
        10
    }
    val deferred2 = async {
        delay(50)
        println("Second")
        20
    }
    println(deferred1.await())
    println(deferred2.await())
    """.trimIndent())
    println("\n>>> YOUR PREDICTION: ?\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val deferred1 = async {
            delay(100)
            println("First")
            10
        }
        val deferred2 = async {
            delay(50)
            println("Second")
            20
        }
        println(deferred1.await())
        println(deferred2.await())
    }

    println("""
    EXPLANATION:
    Both async blocks start IMMEDIATELY (concurrently).
    deferred2 has shorter delay (50ms), so "Second" prints first.
    BUT deferred1.await() is called first — it SUSPENDS until deferred1 completes.
    By the time deferred1 finishes (100ms), deferred2 is already done (50ms).
    So output: Second, First, 10, 20
    KEY: await() doesn't START the coroutine — it just waits for the result.
    Both coroutines were already running since async{} was called.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 2: launch vs async — fire and forget
// ═════════════════════════════════════════════
suspend fun puzzle2_launchVsAsync() {
    println("═══ PUZZLE 2: launch vs async — lost result ═══")
    println("""
    CODE:
    val result = async {
        delay(100)
        "Hello"
    }
    launch {
        delay(50)
        println("World")
    }
    println(result.await())
    println("Done")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val result = async {
            delay(100)
            "Hello"
        }
        launch {
            delay(50)
            println("World")
        }
        println(result.await())
        println("Done")
    }

    println("""
    EXPLANATION:
    async and launch both start immediately.
    launch finishes at 50ms → prints "World"
    result.await() suspends until 100ms → prints "Hello"
    Then "Done" prints.
    Output: World, Hello, Done
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 3: What happens when parent is cancelled?
// ═════════════════════════════════════════════
suspend fun puzzle3_cancelledParent() {
    println("═══ PUZZLE 3: Parent cancellation ═══")
    println("""
    CODE:
    val job = launch {
        val child1 = launch {
            delay(1000)
            println("Child 1 done")
        }
        val child2 = launch {
            delay(1000)
            println("Child 2 done")
        }
        delay(100)
        println("Parent done")
    }
    delay(200)
    job.cancel()
    delay(1500)
    println("End")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val job = launch {
            launch {
                delay(1000)
                println("Child 1 done")
            }
            launch {
                delay(1000)
                println("Child 2 done")
            }
            delay(100)
            println("Parent done")
        }
        delay(200)
        job.cancel()
        delay(1500)
        println("End")
    }

    println("""
    EXPLANATION:
    Parent prints "Parent done" at 100ms.
    At 200ms, job.cancel() cancels parent AND all children.
    Children never complete → "Child 1 done" and "Child 2 done" never print.
    Only output: Parent done, End
    KEY: Cancelling a parent cancels ALL its children (structured concurrency).
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 4: Exception in launch — crashes parent
// ═════════════════════════════════════════════
suspend fun puzzle4_exceptionInLaunch() {
    println("═══ PUZZLE 4: Exception in launch ═══")
    println("""
    CODE:
    try {
        coroutineScope {
            launch {
                delay(100)
                throw RuntimeException("Boom!")
            }
            launch {
                delay(200)
                println("Second child")
            }
            println("Parent scope")
        }
    } catch (e: Exception) {
        println("Caught: ${'$'}{e.message}")
    }
    println("After scope")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    try {
        coroutineScope {
            launch {
                delay(100)
                throw RuntimeException("Boom!")
            }
            launch {
                delay(200)
                println("Second child")
            }
            println("Parent scope")
        }
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
    println("After scope")

    println("""
    EXPLANATION:
    "Parent scope" prints immediately (no delay).
    At 100ms, first child throws → coroutineScope CANCELS all siblings.
    Second child is cancelled (never prints "Second child").
    Exception propagates to the try-catch → "Caught: Boom!"
    Then "After scope" prints.
    KEY: In coroutineScope, ONE child failure cancels ALL siblings + rethrows.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 5: Exception in async — when does it throw?
// ═════════════════════════════════════════════
suspend fun puzzle5_exceptionInAsync() {
    println("═══ PUZZLE 5: Exception in async — deferred ═══")
    println("""
    CODE:
    val deferred = async {
        delay(100)
        throw RuntimeException("Async Boom!")
        42
    }
    delay(200)
    println("Before await")
    try {
        println(deferred.await())
    } catch (e: Exception) {
        println("Caught at await: ${'$'}{e.message}")
    }
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    // Need supervisorScope here to prevent exception from cancelling parent
    supervisorScope {
        val deferred = async {
            delay(100)
            throw RuntimeException("Async Boom!")
            @Suppress("UNREACHABLE_CODE")
            42
        }
        delay(200)
        println("Before await")
        try {
            println(deferred.await())
        } catch (e: Exception) {
            println("Caught at await: ${e.message}")
        }
    }

    println("""
    EXPLANATION:
    With async, the exception is STORED in the Deferred.
    It doesn't throw immediately — it throws when you call .await().
    "Before await" prints first, then await() throws, caught in try-catch.

    GOTCHA: In a regular coroutineScope (not supervisorScope), the exception
    from async ALSO propagates to the parent immediately — even before await()!
    In supervisorScope, it only throws at await().
    This is one of the TRICKIEST interview questions.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 6: supervisorScope — sibling survival
// ═════════════════════════════════════════════
suspend fun puzzle6_supervisorScope() {
    println("═══ PUZZLE 6: supervisorScope — siblings survive ═══")
    println("""
    CODE:
    supervisorScope {
        launch {
            delay(50)
            throw RuntimeException("Child 1 failed")
        }
        launch {
            delay(100)
            println("Child 2 survived!")
        }
        println("Supervisor parent")
    }
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    supervisorScope {
        launch {
            delay(50)
            throw RuntimeException("Child 1 failed")
        }
        launch {
            delay(100)
            println("Child 2 survived!")
        }
        println("Supervisor parent")
    }

    println("""
    EXPLANATION:
    "Supervisor parent" prints immediately.
    At 50ms, child 1 fails — but supervisorScope does NOT cancel siblings.
    At 100ms, child 2 completes and prints "Child 2 survived!"
    Output: Supervisor parent, Child 2 survived!
    KEY: supervisorScope = child failure is isolated. Use in ViewModel scope.
    vs coroutineScope = child failure cancels everything.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 7: withContext vs launch — sequential vs concurrent
// ═════════════════════════════════════════════
suspend fun puzzle7_withContextVsLaunch() {
    println("═══ PUZZLE 7: withContext is SEQUENTIAL ═══")
    println("""
    CODE:
    val time = measureTimeMillis {
        val r1 = withContext(Dispatchers.Default) { delay(100); 1 }
        val r2 = withContext(Dispatchers.Default) { delay(100); 2 }
        println("Result: ${'$'}{r1 + r2}")
    }
    println("Took ~${'$'}time ms")  // ~200ms, NOT 100ms!
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val time = kotlin.system.measureTimeMillis {
        val r1 = withContext(Dispatchers.Default) { delay(100); 1 }
        val r2 = withContext(Dispatchers.Default) { delay(100); 2 }
        println("Result: ${r1 + r2}")
    }
    println("Took ~${time}ms (sequential!)")

    println("""
    EXPLANATION:
    withContext SUSPENDS — it's sequential! r1 completes, THEN r2 starts.
    Total: ~200ms.

    For PARALLEL execution, use async:
      val d1 = async { delay(100); 1 }
      val d2 = async { delay(100); 2 }
      println(d1.await() + d2.await())  // ~100ms total

    INTERVIEW TRAP: Many candidates use withContext for "parallel" calls.
    withContext = switch context + WAIT. async = start concurrent + await later.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 8: Flow is COLD — each collector re-runs
// ═════════════════════════════════════════════
suspend fun puzzle8_flowColdStream() {
    println("═══ PUZZLE 8: Flow is COLD ═══")
    println("""
    CODE:
    val flow = flow {
        println("Flow started")
        emit(1)
        emit(2)
    }
    println("Before collect")
    flow.collect { println("Collector 1: ${'$'}it") }
    flow.collect { println("Collector 2: ${'$'}it") }
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val coldFlow = flow {
        println("Flow started")
        emit(1)
        emit(2)
    }
    println("Before collect")
    coldFlow.collect { println("Collector 1: $it") }
    coldFlow.collect { println("Collector 2: $it") }

    println("""
    EXPLANATION:
    Flow is COLD — the code inside flow{} doesn't run until collect() is called.
    "Before collect" prints first.
    Each collect() RESTARTS the flow from scratch.
    "Flow started" prints TWICE — once per collect() call.

    KEY: Flow = cold (restarts per collector). StateFlow/SharedFlow = hot (always active).
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 9: StateFlow deduplication
// ═════════════════════════════════════════════
suspend fun puzzle9_sharedFlowVsStateFlow() {
    println("═══ PUZZLE 9: StateFlow skips equal values ═══")
    println("""
    CODE:
    val stateFlow = MutableStateFlow(1)
    launch {
        stateFlow.collect { println("StateFlow: ${'$'}it") }
    }
    delay(50)
    stateFlow.value = 1  // same value!
    stateFlow.value = 2
    stateFlow.value = 2  // same again!
    stateFlow.value = 3
    delay(100)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val stateFlow = MutableStateFlow(1)
        val job = launch {
            stateFlow.collect { println("StateFlow: $it") }
        }
        delay(50)
        stateFlow.value = 1  // skipped — same as current
        stateFlow.value = 2
        stateFlow.value = 2  // skipped — same as current
        stateFlow.value = 3
        delay(100)
        job.cancel()
    }

    println("""
    EXPLANATION:
    StateFlow uses equals() to DEDUPLICATE. Setting same value = no emission.
    Emits: 1 (initial), 2, 3. The duplicate 1 and duplicate 2 are skipped.

    GOTCHA: If you emit a data class, then mutate and re-emit the SAME reference,
    StateFlow won't emit because equals() returns true!
    Solution: always create new objects with .copy()

    SharedFlow does NOT deduplicate — it emits every value.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 10: Cancellation is cooperative!
// ═════════════════════════════════════════════
suspend fun puzzle10_cancellationCooperation() {
    println("═══ PUZZLE 10: Non-cooperative cancellation ═══")
    println("""
    CODE:
    val job = launch {
        var i = 0
        while (i < 5) {  // BUG: no cancellation check!
            Thread.sleep(50)  // BUG: blocking, not suspend!
            println("Working ${'$'}i")
            i++
        }
    }
    delay(100)
    println("Cancelling...")
    job.cancel()
    job.join()
    println("Done")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val job = launch(Dispatchers.Default) {
            var i = 0
            while (i < 5) {
                Thread.sleep(50)  // blocking! not a cancellation point
                println("Working $i")
                i++
            }
        }
        delay(100)
        println("Cancelling...")
        job.cancel()
        job.join()
        println("Done")
    }

    println("""
    EXPLANATION:
    The loop uses Thread.sleep (BLOCKING) instead of delay (SUSPENDING).
    Thread.sleep is NOT a cancellation point — cancel() has no effect!
    ALL 5 iterations complete despite cancellation.

    FIX: Use delay() instead of Thread.sleep, OR check isActive:
      while (isActive && i < 5) { ... }
    Or use ensureActive() / yield() inside the loop.

    KEY: Cancellation is COOPERATIVE. The coroutine must check for it.
    Suspending functions (delay, emit, withContext) are cancellation points.
    CPU-bound work must manually check isActive.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 11: Nested coroutineScope behavior
// ═════════════════════════════════════════════
suspend fun puzzle11_nestedCoroutineScope() {
    println("═══ PUZZLE 11: coroutineScope is sequential ═══")
    println("""
    CODE:
    println("A")
    coroutineScope {
        launch { delay(100); println("B") }
        println("C")
    }
    println("D")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    println("A")
    coroutineScope {
        launch { delay(100); println("B") }
        println("C")
    }
    println("D")

    println("""
    EXPLANATION:
    "A" prints. coroutineScope starts.
    "C" prints immediately (it's not in a launch/async).
    coroutineScope WAITS for all children before returning.
    "B" prints at 100ms.
    Only THEN does coroutineScope return, and "D" prints.
    Output: A, C, B, D

    KEY: coroutineScope suspends the parent until ALL children complete.
    Code after coroutineScope{} only runs when everything inside is done.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 12: Dispatcher switching
// ═════════════════════════════════════════════
suspend fun puzzle12_dispatcherSwitch() {
    println("═══ PUZZLE 12: Thread switching with withContext ═══")
    println("""
    CODE:
    println("1: ${'$'}{Thread.currentThread().name}")
    withContext(Dispatchers.IO) {
        println("2: ${'$'}{Thread.currentThread().name}")
        withContext(Dispatchers.Default) {
            println("3: ${'$'}{Thread.currentThread().name}")
        }
        println("4: ${'$'}{Thread.currentThread().name}")
    }
    println("5: ${'$'}{Thread.currentThread().name}")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    println("1: ${Thread.currentThread().name}")
    withContext(Dispatchers.IO) {
        println("2: ${Thread.currentThread().name}")
        withContext(Dispatchers.Default) {
            println("3: ${Thread.currentThread().name}")
        }
        println("4: ${Thread.currentThread().name}")
    }
    println("5: ${Thread.currentThread().name}")

    println("""
    EXPLANATION:
    1: main thread (runBlocking)
    2: IO thread (switched by withContext)
    3: Default thread (switched again)
    4: BACK to IO thread (withContext restores previous context)
    5: BACK to main thread (outer withContext restores)

    KEY: withContext switches dispatcher AND switches BACK when done.
    Each withContext is like a context stack — push on enter, pop on exit.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 13: launch ordering — are they in order?
// ═════════════════════════════════════════════
suspend fun puzzle13_launchOrder() {
    println("═══ PUZZLE 13: launch execution order ═══")
    println("""
    CODE:
    launch { println("A") }
    launch { println("B") }
    launch { println("C") }
    println("D")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        launch { println("A") }
        launch { println("B") }
        launch { println("C") }
        println("D")
    }

    println("""
    EXPLANATION:
    "D" prints FIRST — launch{} schedules but doesn't execute immediately.
    Then A, B, C execute in order (same dispatcher, FIFO scheduling).
    Output: D, A, B, C

    KEY: launch{} is NOT immediate. The current coroutine continues first.
    The launched coroutines run when the current coroutine suspends or finishes.

    Exception: Dispatchers.Unconfined starts immediately in the caller's thread.
    And Dispatchers.Main.immediate runs immediately if already on Main thread.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 14: join() vs await()
// ═════════════════════════════════════════════
suspend fun puzzle14_joinVsAwait() {
    println("═══ PUZZLE 14: join() doesn't rethrow ═══")
    println("""
    CODE:
    val job = launch {
        throw RuntimeException("Failed!")
    }
    // Does job.join() throw?
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    supervisorScope {
        val job = launch {
            throw RuntimeException("Failed!")
        }
        try {
            job.join()  // does NOT throw!
            println("join() completed — no exception!")
        } catch (e: Exception) {
            println("join() threw: ${e.message}")
        }
    }

    println("""
    EXPLANATION:
    join() waits for completion but does NOT rethrow the exception.
    await() on a Deferred DOES rethrow.

    launch → Job → join() (no exception)
    async → Deferred → await() (rethrows exception)

    The exception from launch propagates to the PARENT scope instead.
    That's why supervisorScope is used here — to prevent parent cancellation.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 15: Race condition without synchronization
// ═════════════════════════════════════════════
suspend fun puzzle15_raceCondition() {
    println("═══ PUZZLE 15: Race condition ═══")
    println("""
    CODE:
    var counter = 0
    coroutineScope {
        repeat(1000) {
            launch(Dispatchers.Default) { counter++ }
        }
    }
    println("Counter: ${'$'}counter")  // Expected 1000, but...
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    var counter = 0
    coroutineScope {
        repeat(1000) {
            launch(Dispatchers.Default) { counter++ }
        }
    }
    println("Counter (unsafe): $counter (expected 1000, might be less!)")

    // Fix with Mutex
    var safeCounter = 0
    val mutex = Mutex()
    coroutineScope {
        repeat(1000) {
            launch(Dispatchers.Default) {
                mutex.withLock { safeCounter++ }
            }
        }
    }
    println("Counter (Mutex): $safeCounter (always 1000)")

    println("""
    EXPLANATION:
    counter++ is NOT atomic. Multiple coroutines read-modify-write concurrently.
    On Dispatchers.Default (multi-threaded), this causes lost updates.
    Result is often LESS than 1000.

    FIXES:
    1. Mutex — coroutine-safe lock (recommended)
    2. AtomicInteger — lock-free atomic operations
    3. Single-thread confinement — run on one dispatcher
    4. Channel — actor pattern for sequential access

    DON'T use synchronized{} — it blocks the thread, defeating coroutines.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 16: collectLatest trap
// ═════════════════════════════════════════════
suspend fun puzzle16_collectLatestTrap() {
    println("═══ PUZZLE 16: collectLatest cancels previous ═══")
    println("""
    CODE:
    flow {
        emit(1)
        delay(50)
        emit(2)
        delay(50)
        emit(3)
    }.collectLatest { value ->
        println("Start processing ${'$'}value")
        delay(100)  // takes longer than emission interval!
        println("Done processing ${'$'}value")
    }
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    flow {
        emit(1)
        delay(50)
        emit(2)
        delay(50)
        emit(3)
    }.collectLatest { value ->
        println("Start processing $value")
        delay(100)
        println("Done processing $value")
    }

    println("""
    EXPLANATION:
    collectLatest CANCELS the previous collection when a new value arrives.

    - emit(1) → "Start processing 1"
    - 50ms later, emit(2) → CANCELS processing of 1 → "Start processing 2"
    - 50ms later, emit(3) → CANCELS processing of 2 → "Start processing 3"
    - 100ms later → "Done processing 3" (only the last one completes!)

    Only value 3 fully processes. 1 and 2 are cancelled mid-processing.

    USE CASE: Search-as-you-type — cancel previous search when new input arrives.
    TRAP: If your collector does important work (DB save), use collect() not collectLatest().
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 17: SupervisorJob misuse — common bug
// ═════════════════════════════════════════════
suspend fun puzzle17_supervisorJobMisuse() {
    println("═══ PUZZLE 17: SupervisorJob WRONG usage ═══")
    println("""
    CODE (BUGGY):
    // Developer thinks this provides supervisor behavior...
    coroutineScope {
        launch(SupervisorJob()) {  // ← BUG!
            launch { throw RuntimeException("Fail") }
            launch { delay(100); println("Sibling") }
        }
    }
    """.trimIndent())
    println("\n>>> EXPLANATION (not running — would crash):")

    println("""
    THIS IS WRONG! SupervisorJob() creates a NEW root job that is NOT
    a child of the coroutineScope. This breaks structured concurrency:
    - The coroutineScope won't wait for this job to complete
    - Cancellation won't propagate properly
    - It's essentially a leaked coroutine

    CORRECT WAY:
    supervisorScope {
        launch { throw RuntimeException("Fail") }
        launch { delay(100); println("Sibling") }
    }

    Or in ViewModel: viewModelScope already uses SupervisorJob + Dispatchers.Main.immediate

    RULE: Never pass SupervisorJob() to launch/async. Use supervisorScope{} instead.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 18: coroutineScope vs supervisorScope
// ═════════════════════════════════════════════
suspend fun puzzle18_coroutineScopeVsSupervisorScope() {
    println("═══ PUZZLE 18: coroutineScope vs supervisorScope ═══")
    println("""
    QUESTION: What's the difference?

    coroutineScope {
        launch { throw Exception("A") }
        launch { delay(100); println("B") }
    }

    vs

    supervisorScope {
        launch { throw Exception("A") }
        launch { delay(100); println("B") }
    }
    """.trimIndent())

    println("""

    ANSWER:

    coroutineScope:
    - Child A fails → ALL siblings cancelled → "B" never prints
    - Exception propagates UP to caller
    - Use when: all children are related, one failing means all should stop

    supervisorScope:
    - Child A fails → siblings CONTINUE → "B" prints!
    - Exception does NOT propagate to siblings
    - Use when: children are independent (parallel API calls, ViewModel scope)

    ┌──────────────────┬──────────────────────┬──────────────────────┐
    │                  │ coroutineScope       │ supervisorScope      │
    ├──────────────────┼──────────────────────┼──────────────────────┤
    │ Child fails      │ Cancels ALL siblings │ Only that child dies │
    │ Exception flow   │ Propagates up        │ Handled per-child    │
    │ Use case         │ Related tasks        │ Independent tasks    │
    │ Android example  │ Parallel API calls   │ viewModelScope       │
    │                  │ that all must succeed│ (independent actions)│
    └──────────────────┴──────────────────────┴──────────────────────┘
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 19: NonCancellable — cleanup after cancel
// ═════════════════════════════════════════════
suspend fun puzzle19_nonCancellable() {
    println("═══ PUZZLE 19: NonCancellable for cleanup ═══")
    println("""
    CODE:
    val job = launch {
        try {
            println("Working...")
            delay(1000)
        } catch (e: CancellationException) {
            println("Cancelled!")
            delay(100)  // ← Will this work?
            println("Cleanup done")
        }
    }
    delay(50)
    job.cancelAndJoin()
    println("After cancel")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val job = launch {
            try {
                println("Working...")
                delay(1000)
            } catch (e: CancellationException) {
                println("Cancelled!")
                try {
                    delay(100)  // THIS THROWS AGAIN — we're cancelled!
                    println("Cleanup done (won't print)")
                } catch (e2: CancellationException) {
                    println("delay threw CancellationException again!")
                }
            }
        }
        delay(50)
        job.cancelAndJoin()
        println("After cancel")
    }

    println("""
    EXPLANATION:
    After cancellation, the coroutine is in cancelled state.
    Any suspend function (like delay) inside catch will throw CancellationException again!
    "Cleanup done" never prints.

    FIX — wrap cleanup in withContext(NonCancellable):
    catch (e: CancellationException) {
        withContext(NonCancellable) {
            delay(100)  // now works!
            println("Cleanup done")
        }
    }

    USE CASE: Saving state to DB, closing connections, flushing logs after cancellation.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 20: Channel fan-out
// ═════════════════════════════════════════════
suspend fun puzzle20_channelFanOut() {
    println("═══ PUZZLE 20: Channel — each value consumed once ═══")
    println("""
    CODE:
    val channel = Channel<Int>()
    launch { for (i in 1..5) { channel.send(i) }; channel.close() }
    launch { for (v in channel) println("A: ${'$'}v") }
    launch { for (v in channel) println("B: ${'$'}v") }
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    coroutineScope {
        val channel = kotlinx.coroutines.channels.Channel<Int>()
        launch { for (i in 1..5) { channel.send(i) }; channel.close() }
        launch { for (v in channel) println("A: $v") }
        launch { for (v in channel) println("B: $v") }
        delay(200)
    }

    println("""
    EXPLANATION:
    Channel is like a queue — each value is consumed by ONE receiver only!
    Values are distributed between A and B, NOT duplicated.
    Roughly: A gets some values, B gets the rest (depends on scheduling).

    Channel = one-to-one delivery (like a pipe)
    SharedFlow = one-to-many broadcast (every collector gets every value)

    KEY: Use Channel for work distribution (fan-out).
    Use SharedFlow for event broadcasting (fan-out with replay).
    """.trimIndent())
    println()

    println("═══════════════════════════════════════════════")
    println("        BONUS: Quick-Fire Interview Answers     ")
    println("═══════════════════════════════════════════════")
    println("""
    Q: What's the difference between a Thread and a Coroutine?
    A: Thread = OS-level, expensive (1MB stack), blocking.
       Coroutine = lightweight, suspendable, many on one thread.
       10,000 coroutines = fine. 10,000 threads = crash.

    Q: What is structured concurrency?
    A: Coroutines form parent-child trees. Parent waits for children.
       If parent cancels, children cancel. If child fails, parent knows.
       No leaked/orphaned coroutines.

    Q: When do you use launch vs async?
    A: launch = fire-and-forget (returns Job, no result)
       async = need the result (returns Deferred<T>, call await())

    Q: What makes a function "main-safe"?
    A: It can be called from the Main thread without blocking it.
       Use withContext(Dispatchers.IO) for I/O work inside the function.

    Q: Flow vs LiveData?
    A: Flow = Kotlin, richer operators, cold, not lifecycle-aware by default.
       LiveData = Android, simple, lifecycle-aware, limited operators.
       Modern approach: Flow + collectAsStateWithLifecycle() in Compose.

    Q: StateFlow vs SharedFlow?
    A: StateFlow = always has value, deduplicates, replay 1.
       SharedFlow = no initial value, configurable replay.
       StateFlow for UI state, SharedFlow for events.

    Q: What happens if you don't use Dispatchers.IO for network calls?
    A: Retrofit/Room handle it internally (already main-safe).
       But raw file I/O or JSON parsing on Main = ANR!
    """.trimIndent())

    println("\n✅ Coroutine Interview Puzzles Complete!")
}


/**
Puzzles cover:

┌─────┬───────────────────────────────────┬────────────────────────────────────────────────────────────────────┐
│  #  │               Topic               │                                Trap                                │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 1   │ async ordering                    │ await() doesn't start coroutine — both run concurrently            │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 2   │ launch vs async                   │ Fire-and-forget vs result                                          │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 3   │ Parent cancellation               │ Cancelling parent kills all children                               │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 4   │ Exception in launch               │ coroutineScope cancels ALL siblings                                │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 5   │ Exception in async                │ Throws at await(), but ALSO propagates to parent in coroutineScope │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 6   │ supervisorScope                   │ Siblings survive failure                                           │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 7   │ withContext is sequential         │ Common trap — NOT parallel like async                              │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 8   │ Flow is cold                      │ Restarts for each collector                                        │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 9   │ StateFlow deduplication           │ Same value = no emission                                           │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 10  │ Non-cooperative cancel            │ Thread.sleep ignores cancellation                                  │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 11  │ coroutineScope waits              │ Code after scope{} runs only when children done                    │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 12  │ Dispatcher switching              │ withContext pushes/pops context like a stack                       │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 13  │ launch order                      │ launch{} doesn't execute immediately — "D" first                   │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 14  │ join() vs await()                 │ join() doesn't rethrow exceptions                                  │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 15  │ Race condition                    │ counter++ is not atomic                                            │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 16  │ collectLatest trap                │ Cancels previous processing mid-way                                │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 17  │ SupervisorJob() misuse            │ Never pass to launch — breaks structured concurrency               │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 18  │ coroutineScope vs supervisorScope │ Full comparison table                                              │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 19  │ NonCancellable                    │ delay() in catch block throws again                                │
├─────┼───────────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ 20  │ Channel fan-out                   │ Each value consumed by ONE receiver only                           │
└─────┴───────────────────────────────────┴────────────────────────────────────────────────────────────────────┘
*/
