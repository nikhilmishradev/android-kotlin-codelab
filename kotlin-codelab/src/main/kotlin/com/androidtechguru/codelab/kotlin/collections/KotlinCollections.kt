package com.androidtechguru.codelab.kotlin.collections

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║   KOTLIN COLLECTIONS — Codelab           ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. LIST
    // ─────────────────────────────────────────
    println("=== 1. List ===")

    val immutableList = listOf(1, 2, 3, 4, 5)           // read-only
    val mutableList = mutableListOf("a", "b", "c")       // mutable
    val emptyList = emptyList<String>()                   // typed empty
    val arrayList = arrayListOf(1, 2, 3)                  // Java ArrayList

    mutableList.add("d")
    mutableList += "e"  // operator overload for add
    mutableList -= "a"  // operator overload for remove

    println("Immutable: $immutableList")
    println("Mutable after ops: $mutableList")
    println("Empty: $emptyList, isEmpty: ${emptyList.isEmpty()}")

    // buildList — idiomatic way to build a list conditionally
    val built = buildList {
        add("always")
        if (true) add("conditional")
        addAll(listOf("x", "y"))
    }
    println("buildList: $built")

    // ─────────────────────────────────────────
    // 2. SET
    // ─────────────────────────────────────────
    println("\n=== 2. Set ===")

    val set = setOf(1, 2, 3, 2, 1)          // duplicates removed, preserves insertion order (LinkedHashSet)
    val hashSet = hashSetOf(3, 1, 2)          // no guaranteed order
    val mutableSet = mutableSetOf("a", "b")

    mutableSet.add("c")
    mutableSet.add("a")  // no effect — already exists

    println("Set (deduped): $set")
    println("HashSet: $hashSet")
    println("MutableSet: $mutableSet")

    // ─────────────────────────────────────────
    // 3. MAP
    // ─────────────────────────────────────────
    println("\n=== 3. Map ===")

    val map = mapOf("name" to "Alice", "age" to "25")
    val mutableMap = mutableMapOf("a" to 1, "b" to 2)

    mutableMap["c"] = 3
    mutableMap.putIfAbsent("a", 99)  // doesn't overwrite existing
    mutableMap.getOrPut("d") { 4 }   // creates if absent

    println("Map: $map")
    println("MutableMap: $mutableMap")
    println("map[\"name\"]: ${map["name"]}")
    println("map.getOrDefault(\"x\", \"N/A\"): ${map.getOrDefault("x", "N/A")}")

    // Destructuring
    for ((key, value) in map) print("  $key=$value ")
    println()

    // ─────────────────────────────────────────
    // 4. TRANSFORMATIONS
    // ─────────────────────────────────────────
    println("\n=== 4. Transformations ===")

    data class User(val name: String, val age: Int, val tags: List<String>)
    val users = listOf(
        User("Alice", 25, listOf("dev", "kotlin")),
        User("Bob", 30, listOf("dev", "java")),
        User("Charlie", 22, listOf("design", "figma"))
    )

    // map — transform each element
    val names = users.map { it.name }
    println("map (names): $names")

    // flatMap — map + flatten
    val allTags = users.flatMap { it.tags }
    println("flatMap (all tags): $allTags")

    // flatten
    val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    println("flatten: ${nested.flatten()}")

    // zip — combine two lists into pairs
    val letters = listOf("a", "b", "c")
    val nums = listOf(1, 2, 3)
    println("zip: ${letters.zip(nums)}")
    println("zip with transform: ${letters.zip(nums) { l, n -> "$l$n" }}")

    // associate — create map from list
    val userById = users.associateBy { it.name }
    println("associateBy name: ${userById.keys}")

    val nameToAge = users.associate { it.name to it.age }
    println("associate: $nameToAge")

    // groupBy — group elements by key
    val byFirstLetter = names.groupBy { it.first() }
    println("groupBy first letter: $byFirstLetter")

    val devsByTag = users.flatMap { user -> user.tags.map { tag -> tag to user.name } }
        .groupBy({ it.first }, { it.second })
    println("groupBy tag: $devsByTag")

    // ─────────────────────────────────────────
    // 5. FILTERING
    // ─────────────────────────────────────────
    println("\n=== 5. Filtering ===")

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    println("filter (even): ${numbers.filter { it % 2 == 0 }}")
    println("filterNot (even): ${numbers.filterNot { it % 2 == 0 }}")

    // filterIsInstance — filter by type (great for mixed lists)
    val mixed: List<Any> = listOf(1, "hello", 2, "world", 3.0)
    val strings: List<String> = mixed.filterIsInstance<String>()
    println("filterIsInstance<String>: $strings")

    // partition — split into two lists based on predicate
    val (evens, odds) = numbers.partition { it % 2 == 0 }
    println("partition — evens: $evens, odds: $odds")

    // ─────────────────────────────────────────
    // 6. AGGREGATION
    // ─────────────────────────────────────────
    println("\n=== 6. Aggregation ===")

    println("sum: ${numbers.sum()}")
    println("count(>5): ${numbers.count { it > 5 }}")
    println("min: ${numbers.min()}")
    println("max: ${numbers.max()}")
    println("average: ${numbers.average()}")

    // reduce — accumulates starting from first element
    val product = numbers.reduce { acc, n -> acc * n }
    println("reduce (product): $product")

    // fold — like reduce but with initial value
    val sumPlusTen = numbers.fold(10) { acc, n -> acc + n }
    println("fold (sum + 10): $sumPlusTen")

    // INTERVIEW TIP: reduce crashes on empty list, fold doesn't (has initial value)

    // sumOf — concise sum with transform
    val totalAge = users.sumOf { it.age }
    println("sumOf ages: $totalAge")

    // ─────────────────────────────────────────
    // 7. ORDERING
    // ─────────────────────────────────────────
    println("\n=== 7. Ordering ===")

    val unsorted = listOf(5, 3, 8, 1, 9, 2)
    println("sorted: ${unsorted.sorted()}")
    println("sortedDescending: ${unsorted.sortedDescending()}")
    println("reversed: ${unsorted.reversed()}")

    // sortedBy — sort by property
    val byAge = users.sortedBy { it.age }
    println("sortedBy age: ${byAge.map { "${it.name}(${it.age})" }}")

    val byAgeDesc = users.sortedByDescending { it.age }
    println("sortedByDescending: ${byAgeDesc.map { "${it.name}(${it.age})" }}")

    // sortedWith — custom Comparator
    val byNameLength = users.sortedWith(compareBy<User> { it.name.length }.thenBy { it.age })
    println("sortedWith (name length, then age): ${byNameLength.map { it.name }}")

    // ─────────────────────────────────────────
    // 8. ELEMENT ACCESS
    // ─────────────────────────────────────────
    println("\n=== 8. Element Access ===")

    val items = listOf(10, 20, 30, 40, 50)

    println("first: ${items.first()}")
    println("first { >25 }: ${items.first { it > 25 }}")
    println("firstOrNull { >100 }: ${items.firstOrNull { it > 100 }}")
    println("last: ${items.last()}")
    println("find { >25 }: ${items.find { it > 25 }}")  // alias for firstOrNull
    println("single (list of 1): ${listOf(42).single()}")
    // single throws if list has 0 or 2+ elements
    println("singleOrNull { >45 }: ${items.singleOrNull { it > 45 }}")
    println("elementAt(2): ${items.elementAt(2)}")
    println("elementAtOrNull(99): ${items.elementAtOrNull(99)}")

    // ─────────────────────────────────────────
    // 9. CHECKING
    // ─────────────────────────────────────────
    println("\n=== 9. Checking ===")

    println("any { >40 }: ${items.any { it > 40 }}")     // at least one matches
    println("all { >0 }: ${items.all { it > 0 }}")       // all match
    println("none { <0 }: ${items.none { it < 0 }}")     // none match
    println("contains(30): ${items.contains(30)}")
    println("30 in items: ${30 in items}")                 // operator overload

    // ─────────────────────────────────────────
    // 10. SEQUENCES (Lazy Evaluation)
    // ─────────────────────────────────────────
    println("\n=== 10. Sequences ===")

    // EAGER (List) — each operation processes ALL elements before next step
    println("--- Eager (List) ---")
    val eagerResult = listOf(1, 2, 3, 4, 5)
        .map {
            print("  map($it) ")
            it * 2
        }
        .filter {
            print("  filter($it) ")
            it > 4
        }
    println("\n  Eager result: $eagerResult")

    // LAZY (Sequence) — processes one element through ALL steps before next element
    println("\n--- Lazy (Sequence) ---")
    val lazyResult = listOf(1, 2, 3, 4, 5)
        .asSequence()
        .map {
            print("  map($it) ")
            it * 2
        }
        .filter {
            print("  filter($it) ")
            it > 4
        }
        .toList()  // terminal operation triggers execution
    println("\n  Lazy result: $lazyResult")

    // INTERVIEW TIP: Sequences are better for large collections or chained operations
    // because they avoid creating intermediate lists and can short-circuit.

    // Sequence with first() — short circuits!
    println("\n--- Sequence short-circuit ---")
    val firstBig = (1..1_000_000).asSequence()
        .map { it * 2 }
        .first { it > 100 }
    println("  First > 100 in 1..1M: $firstBig (processed only ~51 elements!)")

    // generateSequence — infinite lazy sequence
    val powersOfTwo = generateSequence(1) { it * 2 }
    println("  First 10 powers of 2: ${powersOfTwo.take(10).toList()}")

    // ─────────────────────────────────────────
    // 11. ARRAY vs LIST
    // ─────────────────────────────────────────
    println("\n=== 11. Array vs List ===")

    // IntArray — primitive array (no boxing, better performance)
    val intArray = IntArray(5) { it * 10 }  // [0, 10, 20, 30, 40]
    println("IntArray: ${intArray.toList()}")

    // Array<Int> — boxed Integer array (object overhead)
    val boxedArray = Array(5) { it * 10 }
    println("Array<Int>: ${boxedArray.toList()}")

    println("""

    ┌────────────────────────────────────────────────────────┐
    │ IntArray vs Array<Int> vs List<Int>                     │
    ├────────────────────────────────────────────────────────┤
    │ IntArray     — primitive[], best performance            │
    │ Array<Int>   — Integer[], boxed, has auto-boxing cost   │
    │ List<Int>    — immutable interface, most idiomatic      │
    │ MutableList  — when you need add/remove                 │
    │                                                        │
    │ RULE: Use List for APIs, IntArray for performance-      │
    │ critical paths (rare in Android app code).              │
    └────────────────────────────────────────────────────────┘
    """.trimIndent())

    println("\n✅ Kotlin Collections Codelab Complete!")
}
