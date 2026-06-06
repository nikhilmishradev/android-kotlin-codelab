package com.androidtechguru.codelab.kotlin.collections

fun main() {
    println("╔══════════════════════════════════════════════╗")
    println("║  KOTLIN COLLECTIONS INTERVIEW PUZZLES        ║")
    println("║  Predict the output before running!          ║")
    println("╚══════════════════════════════════════════════╝\n")

    puzzle1_listOfVsMutableListOf()
    puzzle2_mapVsFlatMap()
    puzzle3_filterVsPartition()
    puzzle4_reduceVsFold()
    puzzle5_sequenceVsList()
    puzzle6_groupByAssociate()
    puzzle7_sortedByStability()
    puzzle8_listEqualityTrap()
    puzzle9_toMutableListTrap()
    puzzle10_firstVsSingle()
    puzzle11_mapMerge()
    puzzle12_flattenVsFlatMap()
    puzzle13_zipUnequal()
    puzzle14_anyAllNoneEmpty()
    puzzle15_sequenceShortCircuit()
    puzzle16_distinctByTrap()
    puzzle17_chunkedWindowed()
    puzzle18_mutableIteratorRemove()
    puzzle19_mapKeysCollision()
    puzzle20_buildListPattern()
}

// ═════════════════════════════════════════════
// PUZZLE 1: listOf returns immutable... or does it?
// ═════════════════════════════════════════════
fun puzzle1_listOfVsMutableListOf() {
    println("═══ PUZZLE 1: listOf — truly immutable? ═══")
    println("""
    CODE:
    val list = listOf(1, 2, 3)
    // list.add(4)  ← Does this compile?
    println(list is MutableList)
    println(list.javaClass.simpleName)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val list = listOf(1, 2, 3)
    println(list is MutableList)
    println(list.javaClass.simpleName)

    println("""
    EXPLANATION:
    list.add(4) → COMPILE ERROR. List interface has no add().
    But 'list is MutableList' might be TRUE!

    listOf() returns a Java ArrayList under the hood in many cases.
    The Kotlin type system sees it as List (read-only), but at runtime
    it IS a MutableList. You could cast it: (list as MutableList).add(4)
    and it would work... but DON'T. It's undefined behavior.

    KEY: Kotlin's List is a READ-ONLY VIEW, not truly immutable.
    For true immutability, use kotlinx.collections.immutable library.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 2: map vs flatMap
// ═════════════════════════════════════════════
fun puzzle2_mapVsFlatMap() {
    println("═══ PUZZLE 2: map vs flatMap ═══")
    println("""
    CODE:
    val words = listOf("Hello World", "Kotlin Fun")
    val mapped = words.map { it.split(" ") }
    val flatMapped = words.flatMap { it.split(" ") }
    println(mapped)
    println(flatMapped)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val words = listOf("Hello World", "Kotlin Fun")
    val mapped = words.map { it.split(" ") }
    val flatMapped = words.flatMap { it.split(" ") }
    println(mapped)
    println(flatMapped)

    println("""
    EXPLANATION:
    map: transforms each element → returns List<List<String>>
      [[Hello, World], [Kotlin, Fun]]

    flatMap: transforms THEN flattens → returns List<String>
      [Hello, World, Kotlin, Fun]

    flatMap = map + flatten in one step.

    RULE: Use map when each element maps to ONE result.
    Use flatMap when each element maps to a COLLECTION of results.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 3: filter vs partition
// ═════════════════════════════════════════════
fun puzzle3_filterVsPartition() {
    println("═══ PUZZLE 3: filter loses rejects, partition keeps both ═══")
    println("""
    CODE:
    val nums = listOf(1, 2, 3, 4, 5, 6)
    val filtered = nums.filter { it % 2 == 0 }
    val (evens, odds) = nums.partition { it % 2 == 0 }
    println("filtered: ${'$'}filtered")
    println("evens: ${'$'}evens, odds: ${'$'}odds")
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val nums = listOf(1, 2, 3, 4, 5, 6)
    val filtered = nums.filter { it % 2 == 0 }
    val (evens, odds) = nums.partition { it % 2 == 0 }
    println("filtered: $filtered")
    println("evens: $evens, odds: $odds")

    println("""
    EXPLANATION:
    filter: returns only matching elements. Non-matching are lost.
    partition: splits into TWO lists — matching and non-matching.
    Returns Pair<List<T>, List<T>> — perfect for destructuring.

    USE partition when you need BOTH groups. It's one pass, not two filters.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 4: reduce vs fold — empty list trap
// ═════════════════════════════════════════════
fun puzzle4_reduceVsFold() {
    println("═══ PUZZLE 4: reduce crashes on empty list ═══")
    println("""
    CODE:
    val nums = listOf(1, 2, 3)
    println(nums.reduce { acc, n -> acc + n })
    println(nums.fold(10) { acc, n -> acc + n })

    val empty = emptyList<Int>()
    println(empty.fold(0) { acc, n -> acc + n })
    // println(empty.reduce { acc, n -> acc + n })  ← What happens?
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val nums = listOf(1, 2, 3)
    println(nums.reduce { acc, n -> acc + n })
    println(nums.fold(10) { acc, n -> acc + n })

    val empty = emptyList<Int>()
    println(empty.fold(0) { acc, n -> acc + n })
    try {
        empty.reduce { acc, n -> acc + n }
    } catch (e: Exception) {
        println("reduce on empty: ${e.javaClass.simpleName}: ${e.message}")
    }

    println("""
    EXPLANATION:
    reduce: starts from FIRST element, no initial value. CRASHES on empty list!
    fold: starts from INITIAL VALUE. Safe on empty list — returns initial.

    nums.reduce = 1+2+3 = 6
    nums.fold(10) = 10+1+2+3 = 16
    empty.fold(0) = 0 (just returns initial)
    empty.reduce = UnsupportedOperationException!

    RULE: Always prefer fold() when the list might be empty.
    Or use reduceOrNull() for a safe alternative.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 5: Sequence lazy evaluation
// ═════════════════════════════════════════════
fun puzzle5_sequenceVsList() {
    println("═══ PUZZLE 5: Sequence processes element-by-element ═══")
    println("""
    CODE:
    listOf(1, 2, 3, 4, 5)
        .asSequence()
        .map { print("M${'$'}it "); it * 2 }
        .filter { print("F${'$'}it "); it > 4 }
        .first()
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val result = listOf(1, 2, 3, 4, 5)
        .asSequence()
        .map { print("M$it "); it * 2 }
        .filter { print("F$it "); it > 4 }
        .first()
    println("\nResult: $result")

    println("""
    EXPLANATION:
    Sequence processes element-by-element (vertical), not step-by-step (horizontal).

    Element 1: M1 → produces 2 → F2 → rejected (2 ≤ 4)
    Element 2: M2 → produces 4 → F4 → rejected (4 ≤ 4)
    Element 3: M3 → produces 6 → F6 → accepted! (6 > 4) → first() returns 6

    Elements 4 and 5 are NEVER processed! (short-circuit)

    Without sequence (List): ALL elements go through map first,
    THEN all go through filter. No short-circuit. More work.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 6: groupBy vs associate
// ═════════════════════════════════════════════
fun puzzle6_groupByAssociate() {
    println("═══ PUZZLE 6: groupBy vs associateBy — duplicates ═══")
    println("""
    CODE:
    data class User(val dept: String, val name: String)
    val users = listOf(
        User("Dev", "Alice"), User("Dev", "Bob"), User("QA", "Charlie")
    )
    val grouped = users.groupBy { it.dept }
    val associated = users.associateBy { it.dept }
    println(grouped)
    println(associated)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    data class User(val dept: String, val name: String)
    val users = listOf(
        User("Dev", "Alice"), User("Dev", "Bob"), User("QA", "Charlie")
    )
    val grouped = users.groupBy { it.dept }
    val associated = users.associateBy { it.dept }
    println("groupBy: $grouped")
    println("associateBy: $associated")

    println("""
    EXPLANATION:
    groupBy: Map<K, List<V>> — keeps ALL values per key.
      Dev → [Alice, Bob], QA → [Charlie]

    associateBy: Map<K, V> — keeps LAST value per key. Duplicates are LOST!
      Dev → Bob (Alice is overwritten!), QA → Charlie

    TRAP: associateBy silently drops duplicates. Use groupBy when keys repeat.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 7: sortedBy is STABLE
// ═════════════════════════════════════════════
fun puzzle7_sortedByStability() {
    println("═══ PUZZLE 7: Stable sort preserves order of equal elements ═══")
    println("""
    CODE:
    data class Item(val name: String, val priority: Int)
    val items = listOf(
        Item("C", 1), Item("A", 2), Item("B", 1), Item("D", 2)
    )
    println(items.sortedBy { it.priority })
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    data class Item(val name: String, val priority: Int)
    val items = listOf(Item("C", 1), Item("A", 2), Item("B", 1), Item("D", 2))
    println(items.sortedBy { it.priority })

    println("""
    EXPLANATION:
    Kotlin's sort is STABLE — equal elements maintain their original order.
    Priority 1: C comes before B (original order preserved)
    Priority 2: A comes before D (original order preserved)
    Output: [C(1), B(1), A(2), D(2)]

    This matters for multi-level sorting:
    items.sortedBy { it.name }.sortedBy { it.priority }
    First sorts by name, then by priority — within same priority, name order is kept.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 8: List equality is structural
// ═════════════════════════════════════════════
fun puzzle8_listEqualityTrap() {
    println("═══ PUZZLE 8: List equality ═══")
    println("""
    CODE:
    val a = listOf(1, 2, 3)
    val b = mutableListOf(1, 2, 3)
    val c = arrayListOf(1, 2, 3)
    println(a == b)
    println(b == c)
    println(a == c)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val a = listOf(1, 2, 3)
    val b = mutableListOf(1, 2, 3)
    val c = arrayListOf(1, 2, 3)
    println(a == b)
    println(b == c)
    println(a == c)

    println("""
    EXPLANATION:
    ALL true! List equality compares CONTENTS (structural), not type or reference.
    listOf, mutableListOf, and arrayListOf all return List implementations
    whose equals() checks element-by-element.

    Even List == MutableList is true if contents match.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 9: toMutableList creates a COPY
// ═════════════════════════════════════════════
fun puzzle9_toMutableListTrap() {
    println("═══ PUZZLE 9: toMutableList creates a copy ═══")
    println("""
    CODE:
    val original = mutableListOf(1, 2, 3)
    val copy = original.toMutableList()
    copy.add(4)
    println(original)
    println(copy)
    println(original === copy)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val original = mutableListOf(1, 2, 3)
    val copy = original.toMutableList()
    copy.add(4)
    println(original)
    println(copy)
    println(original === copy)

    println("""
    EXPLANATION:
    toMutableList() creates a NEW list (shallow copy). Modifying copy doesn't affect original.
    original = [1, 2, 3], copy = [1, 2, 3, 4]. Different references.

    BUT: if the list contains objects, it's a SHALLOW copy — objects are shared.
    val users = mutableListOf(User("Alice"))
    val copy = users.toMutableList()
    // copy[0] and users[0] are the SAME User object!
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 10: first vs single vs find
// ═════════════════════════════════════════════
fun puzzle10_firstVsSingle() {
    println("═══ PUZZLE 10: first vs single — multiple matches ═══")
    println("""
    CODE:
    val nums = listOf(1, 2, 3, 2, 1)
    println(nums.first { it == 2 })
    println(nums.find { it == 2 })
    // println(nums.single { it == 2 })  ← What happens?
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val nums = listOf(1, 2, 3, 2, 1)
    println(nums.first { it == 2 })
    println(nums.find { it == 2 })
    try {
        nums.single { it == 2 }
    } catch (e: Exception) {
        println("single: ${e.javaClass.simpleName}")
    }

    println("""
    EXPLANATION:
    first { } — returns first match. Throws if NO match.
    find { } — same as firstOrNull { }. Returns null if no match.
    single { } — returns the ONLY match. Throws if 0 or 2+ matches!

    single { it == 2 } throws because there are TWO 2s in the list.

    USE: single when you EXPECT exactly one match (e.g., find by unique ID).
    It catches bugs where duplicates exist unexpectedly.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 11: Map merge conflict
// ═════════════════════════════════════════════
fun puzzle11_mapMerge() {
    println("═══ PUZZLE 11: Map + (plus) vs putAll ═══")
    println("""
    CODE:
    val a = mapOf("x" to 1, "y" to 2)
    val b = mapOf("y" to 99, "z" to 3)
    val merged = a + b
    println(merged)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val a = mapOf("x" to 1, "y" to 2)
    val b = mapOf("y" to 99, "z" to 3)
    val merged = a + b
    println(merged)

    println("""
    EXPLANATION:
    + operator: right side WINS on key conflicts.
    "y" exists in both → b's value (99) overwrites a's value (2).
    Result: {x=1, y=99, z=3}

    This is intuitive but can silently lose data if not expected.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 12: flatten vs flatMap
// ═════════════════════════════════════════════
fun puzzle12_flattenVsFlatMap() {
    println("═══ PUZZLE 12: flatten works on List<List<T>> ═══")
    println("""
    CODE:
    val nested = listOf(listOf(1, 2), listOf(3), listOf(4, 5, 6))
    println(nested.flatten())
    println(nested.flatMap { it.filter { n -> n % 2 == 0 } })
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val nested = listOf(listOf(1, 2), listOf(3), listOf(4, 5, 6))
    println(nested.flatten())
    println(nested.flatMap { it.filter { n -> n % 2 == 0 } })

    println("""
    EXPLANATION:
    flatten: just merges nested lists → [1, 2, 3, 4, 5, 6]
    flatMap: transform each inner list + flatten → [2, 4, 6] (only evens)

    flatten = flatMap { it } (identity transform)
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 13: zip with unequal sizes
// ═════════════════════════════════════════════
fun puzzle13_zipUnequal() {
    println("═══ PUZZLE 13: zip truncates to shorter list ═══")
    println("""
    CODE:
    val a = listOf(1, 2, 3, 4, 5)
    val b = listOf("a", "b", "c")
    println(a.zip(b))
    println(a zip b)  // infix syntax
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val a = listOf(1, 2, 3, 4, 5)
    val b = listOf("a", "b", "c")
    println(a.zip(b))
    println(a zip b)

    println("""
    EXPLANATION:
    zip pairs elements by position. Stops at the SHORTER list.
    Elements 4 and 5 from list 'a' are DROPPED silently.
    Result: [(1,a), (2,b), (3,c)]

    No error, no warning — just truncation. Be careful with unequal sizes.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 14: any/all/none on empty list
// ═════════════════════════════════════════════
fun puzzle14_anyAllNoneEmpty() {
    println("═══ PUZZLE 14: any/all/none on empty list ═══")
    println("""
    CODE:
    val empty = emptyList<Int>()
    println(empty.any { it > 0 })
    println(empty.all { it > 0 })
    println(empty.none { it > 0 })
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val empty = emptyList<Int>()
    println("any: ${empty.any { it > 0 }}")
    println("all: ${empty.all { it > 0 }}")
    println("none: ${empty.none { it > 0 }}")

    println("""
    EXPLANATION:
    any on empty → false (no element satisfies the predicate)
    all on empty → TRUE! (vacuous truth — "all zero elements satisfy it")
    none on empty → true (no element violates the predicate)

    TRAP: empty.all { it > 1000 } is TRUE.
    This follows mathematical logic but surprises many developers.
    Always check isEmpty() first if this matters for your logic.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 15: Sequence short-circuits terminal ops
// ═════════════════════════════════════════════
fun puzzle15_sequenceShortCircuit() {
    println("═══ PUZZLE 15: Sequence does nothing without terminal op ═══")
    println("""
    CODE:
    val result = listOf(1, 2, 3, 4, 5)
        .asSequence()
        .map { println("Mapping ${'$'}it"); it * 2 }
        .filter { println("Filtering ${'$'}it"); it > 4 }
    println("Type: ${'$'}{result::class.simpleName}")
    println("---")
    println(result.toList())
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val result = listOf(1, 2, 3, 4, 5)
        .asSequence()
        .map { println("Mapping $it"); it * 2 }
        .filter { println("Filtering $it"); it > 4 }
    println("Type: ${result::class.simpleName}")
    println("---")
    println(result.toList())

    println("""
    EXPLANATION:
    Before "---": NOTHING is mapped or filtered!
    Sequences are LAZY — intermediate operations return a new Sequence,
    they don't execute until a TERMINAL operation (toList, first, count, forEach).

    After toList(): all mapping and filtering happens.

    TRAP: If you forget the terminal operation, your sequence does nothing.
    Common mistake: sequence.map { ... }.filter { ... } // no toList()!
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 16: distinctBy keeps FIRST occurrence
// ═════════════════════════════════════════════
fun puzzle16_distinctByTrap() {
    println("═══ PUZZLE 16: distinctBy keeps first, drops rest ═══")
    println("""
    CODE:
    data class User(val name: String, val score: Int)
    val users = listOf(
        User("Alice", 50), User("Bob", 90), User("Alice", 100)
    )
    val distinct = users.distinctBy { it.name }
    println(distinct)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    data class User(val name: String, val score: Int)
    val users = listOf(User("Alice", 50), User("Bob", 90), User("Alice", 100))
    println(users.distinctBy { it.name })

    println("""
    EXPLANATION:
    distinctBy keeps the FIRST element for each key.
    Alice(50) kept, Alice(100) dropped!

    If you wanted the HIGHEST score per name:
    users.groupBy { it.name }.mapValues { (_, v) -> v.maxBy { it.score } }
    Or: users.sortedByDescending { it.score }.distinctBy { it.name }
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 17: chunked vs windowed
// ═════════════════════════════════════════════
fun puzzle17_chunkedWindowed() {
    println("═══ PUZZLE 17: chunked vs windowed ═══")
    println("""
    CODE:
    val nums = listOf(1, 2, 3, 4, 5)
    println(nums.chunked(2))
    println(nums.windowed(3))
    println(nums.windowed(3, step = 2))
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val nums = listOf(1, 2, 3, 4, 5)
    println("chunked(2): ${nums.chunked(2)}")
    println("windowed(3): ${nums.windowed(3)}")
    println("windowed(3, step=2): ${nums.windowed(3, step = 2)}")

    println("""
    EXPLANATION:
    chunked(2): splits into non-overlapping groups of 2
      [[1,2], [3,4], [5]] — last chunk can be smaller

    windowed(3): sliding window of size 3, step 1 (overlapping!)
      [[1,2,3], [2,3,4], [3,4,5]]

    windowed(3, step=2): sliding window, but move 2 at a time
      [[1,2,3], [3,4,5]]

    USE: chunked for batching. windowed for moving averages, pair comparisons.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 18: ConcurrentModificationException
// ═════════════════════════════════════════════
fun puzzle18_mutableIteratorRemove() {
    println("═══ PUZZLE 18: Modifying list during iteration ═══")
    println("""
    CODE:
    val list = mutableListOf(1, 2, 3, 4, 5)
    // WRONG:
    // for (item in list) { if (item == 3) list.remove(item) }

    // CORRECT approaches:
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    // Approach 1: removeAll
    val list1 = mutableListOf(1, 2, 3, 4, 5)
    list1.removeAll { it == 3 }
    println("removeAll: $list1")

    // Approach 2: iterator.remove()
    val list2 = mutableListOf(1, 2, 3, 4, 5)
    val iter = list2.iterator()
    while (iter.hasNext()) {
        if (iter.next() == 3) iter.remove()
    }
    println("iterator: $list2")

    // Approach 3: filter to new list (functional, preferred)
    val list3 = listOf(1, 2, 3, 4, 5).filter { it != 3 }
    println("filter: $list3")

    println("""
    EXPLANATION:
    Modifying a list during for-each iteration throws ConcurrentModificationException.
    The iterator detects structural changes and fails fast.

    FIXES:
    1. removeAll { predicate } — built-in, safe
    2. iterator.remove() — manual, safe
    3. filter to new list — functional, immutable, PREFERRED

    INTERVIEW TIP: In Android, this often happens with RecyclerView lists
    or LiveData observers modifying the observed list.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 19: associateBy key collision
// ═════════════════════════════════════════════
fun puzzle19_mapKeysCollision() {
    println("═══ PUZZLE 19: mapKeys collision ═══")
    println("""
    CODE:
    val map = mapOf(1 to "one", 2 to "two", 3 to "three")
    val result = map.mapKeys { (k, _) -> k % 2 }
    println(result)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val map = mapOf(1 to "one", 2 to "two", 3 to "three")
    val result = map.mapKeys { (k, _) -> k % 2 }
    println(result)

    println("""
    EXPLANATION:
    1 % 2 = 1, 2 % 2 = 0, 3 % 2 = 1

    Key 1 maps to "one", then key 3 ALSO maps to 1 → "three" overwrites "one"!
    Result: {1=three, 0=two}

    mapKeys silently drops collisions (last write wins).
    Same trap as associateBy. Use groupBy when collisions are expected.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 20: buildList — conditional building
// ═════════════════════════════════════════════
fun puzzle20_buildListPattern() {
    println("═══ PUZZLE 20: buildList for conditional construction ═══")
    println("""
    CODE:
    val isAdmin = true
    val showBeta = false

    val menuItems = buildList {
        add("Home")
        add("Profile")
        if (isAdmin) add("Admin Panel")
        if (showBeta) add("Beta Features")
        addAll(listOf("Settings", "Logout"))
    }
    println(menuItems)
    println(menuItems is MutableList)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    val isAdmin = true
    val showBeta = false

    val menuItems = buildList {
        add("Home")
        add("Profile")
        if (isAdmin) add("Admin Panel")
        if (showBeta) add("Beta Features")
        addAll(listOf("Settings", "Logout"))
    }
    println(menuItems)
    println(menuItems is MutableList)

    println("""
    EXPLANATION:
    buildList: builds a MutableList inside the lambda, returns read-only List.
    Conditions determine which items are added — clean alternative to:
    val list = mutableListOf("Home").also { if (admin) it.add("Admin") }

    "Beta Features" is NOT in the list (showBeta = false).
    The returned list claims MutableList = true (it's an ArrayList under the hood),
    but the compiler type is List — you can't call add() on it.

    PATTERN: Use buildList/buildMap/buildSet for conditional collection construction.
    Cleaner than mutable + if/else chains.
    """.trimIndent())
    println()

    println("✅ Collections Interview Puzzles Complete!")
}
