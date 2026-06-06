package com.androidtechguru.codelab.kotlin.generics

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║    KOTLIN GENERICS — Codelab             ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. GENERIC CLASSES & FUNCTIONS
    // ─────────────────────────────────────────
    println("=== 1. Generic Classes & Functions ===")

    // Generic class — type parameter <T>
    val intBox = Box(42)
    val strBox = Box("Hello")
    println("IntBox: ${intBox.value}, StringBox: ${strBox.value}")

    // Generic function
    fun <T> singletonList(item: T): List<T> = listOf(item)
    println("singletonList(42): ${singletonList(42)}")
    println("singletonList(\"hi\"): ${singletonList("hi")}")

    // Multiple type parameters
    val pair = GenericPair("name", 42)
    println("GenericPair: first=${pair.first}, second=${pair.second}")

    // ─────────────────────────────────────────
    // 2. TYPE CONSTRAINTS
    // ─────────────────────────────────────────
    println("\n=== 2. Type Constraints ===")

    // Upper bound — T must be Comparable
    fun <T : Comparable<T>> maxOf(a: T, b: T): T = if (a > b) a else b
    println("maxOf(3, 7) = ${maxOf(3, 7)}")
    println("maxOf(\"apple\", \"banana\") = ${maxOf("apple", "banana")}")

    // Multiple bounds with 'where' clause
    // fun <T> doSomething(item: T) where T : Serializable, T : Comparable<T>
    println("  Multiple bounds use 'where T : A, T : B' syntax")

    // ─────────────────────────────────────────
    // 3. VARIANCE — The Core Interview Topic
    // ─────────────────────────────────────────
    println("\n=== 3. Variance ===")

    // INVARIANT — default. Box<Dog> is NOT a subtype of Box<Animal>
    println("--- Invariant (default) ---")
    // val animalBox: Box<Animal> = Box(Dog("Buddy"))  // COMPILE ERROR!
    // Even though Dog is a subtype of Animal, Box<Dog> is NOT a subtype of Box<Animal>
    println("  Box<Dog> is NOT Box<Animal> — they are invariant")

    // COVARIANT (out) — Producer<Dog> IS a subtype of Producer<Animal>
    // 'out' means "this type is only produced (returned), never consumed (accepted as parameter)"
    println("\n--- Covariant (out) — Producer ---")
    val dogProducer: Producer<Dog> = Producer(Dog("Buddy"))
    val animalProducer: Producer<Animal> = dogProducer  // OK! Producer<Dog> → Producer<Animal>
    println("  Producer<Dog> IS Producer<Animal>: ${animalProducer.produce().name}")
    // REAL WORLD: List<out E> — you can assign List<Dog> to List<Animal>
    val dogs: List<Dog> = listOf(Dog("Rex"), Dog("Max"))
    val animals: List<Animal> = dogs  // Works because List is covariant (out)
    println("  List<Dog> as List<Animal>: ${animals.map { it.name }}")

    // CONTRAVARIANT (in) — Consumer<Animal> IS a subtype of Consumer<Dog>
    // 'in' means "this type is only consumed (accepted), never produced (returned)"
    println("\n--- Contravariant (in) — Consumer ---")
    val animalConsumer = Consumer<Animal> { println("    Feeding ${it.name}") }
    val dogConsumer: Consumer<Dog> = animalConsumer  // OK! Consumer<Animal> → Consumer<Dog>
    dogConsumer.consume(Dog("Buddy"))
    // REAL WORLD: Comparable<in T> — Comparable<Animal> can compare Dogs

    println("""

    ┌─────────────────────────────────────────────────────────────┐
    │ Variance Cheat Sheet                                        │
    ├─────────────────────────────────────────────────────────────┤
    │ INVARIANT (default)  — Box<Dog> ≠ Box<Animal>               │
    │   Use when type is both read and written                    │
    │                                                             │
    │ COVARIANT (out T)    — Producer<Dog> → Producer<Animal>     │
    │   T only in OUT position (return types)                     │
    │   Example: List<out E>, Flow<out T>                         │
    │                                                             │
    │ CONTRAVARIANT (in T) — Consumer<Animal> → Consumer<Dog>     │
    │   T only in IN position (parameter types)                   │
    │   Example: Comparable<in T>, Comparator<in T>               │
    │                                                             │
    │ Remember: Producer=out, Consumer=in (POCI mnemonic)         │
    └─────────────────────────────────────────────────────────────┘
    """.trimIndent())

    // ─────────────────────────────────────────
    // 4. DECLARATION-SITE vs USE-SITE VARIANCE
    // ─────────────────────────────────────────
    println("\n=== 4. Declaration-Site vs Use-Site Variance ===")

    // Declaration-site — variance declared on class (Kotlin way)
    // class Producer<out T>(val value: T)  ← out is on the CLASS

    // Use-site — variance declared where type is used (Java way / type projection)
    // fun copy(from: Array<out Any>, to: Array<Any>) ← out is at the USE SITE
    fun printAll(items: Array<out Any>) {  // use-site 'out' — can only read
        for (item in items) print("$item ")
    }
    printAll(arrayOf("a", "b", "c"))
    println()

    fun fill(dest: Array<in String>, value: String) {  // use-site 'in' — can only write
        for (i in dest.indices) dest[i] = value
    }
    val arr = arrayOfNulls<Any>(3)
    fill(arr, "filled")
    println("  Filled: ${arr.toList()}")

    // ─────────────────────────────────────────
    // 5. STAR PROJECTION
    // ─────────────────────────────────────────
    println("\n=== 5. Star Projection ===")

    // * means "I don't know or care about the type parameter"
    // Box<*> is like Box<out Any?> — you can read (as Any?) but can't write
    fun printBoxContent(box: Box<*>) {
        println("  Box contains: ${box.value}")  // value is Any?
    }
    printBoxContent(Box(42))
    printBoxContent(Box("Hello"))

    // INTERVIEW TIP: Use * when you only need to read, don't know the type,
    // and don't need type safety for the parameter.

    // ─────────────────────────────────────────
    // 6. REIFIED TYPE PARAMETERS
    // ─────────────────────────────────────────
    println("\n=== 6. Reified Type Parameters ===")

    // Problem: generics are erased at runtime (type erasure)
    // fun <T> isType(value: Any): Boolean = value is T  // COMPILE ERROR!

    // Solution: inline + reified — type is available at runtime
    println("  isOfType<String>(\"hello\"): ${isOfType<String>("hello")}")
    println("  isOfType<Int>(\"hello\"): ${isOfType<Int>("hello")}")
    println("  isOfType<Int>(42): ${isOfType<Int>(42)}")

    // Real-world: filterIsInstance
    val mixed: List<Any> = listOf(1, "two", 3, "four", 5.0)
    val strings = mixed.filterByType<String>()
    println("  filterByType<String>: $strings")

    val ints = mixed.filterByType<Int>()
    println("  filterByType<Int>: $ints")

    // INTERVIEW TIP: reified requires inline because the compiler needs to
    // substitute the actual type at each call site (can't work with erased type)

    // ─────────────────────────────────────────
    // 7. TYPE ERASURE
    // ─────────────────────────────────────────
    println("\n=== 7. Type Erasure ===")
    println("""
        At runtime, List<String> and List<Int> are both just List (JVM limitation).

        You CANNOT do:
          if (list is List<String>)  // ERROR — type argument erased

        You CAN do:
          if (list is List<*>)       // OK — just check it's a List
          inline fun <reified T> check(list: List<*>) = list.all { it is T }

        Workaround: Pass Class<T> as parameter or use reified.
    """.trimIndent())

    // ─────────────────────────────────────────
    // 8. GENERIC EXTENSIONS
    // ─────────────────────────────────────────
    println("\n=== 8. Generic Extensions ===")

    // Extension function with generic type
    println("  listOf(3,1,4,1,5).secondOrNull() = ${listOf(3, 1, 4, 1, 5).secondOrNull()}")
    println("  listOf(1).secondOrNull() = ${listOf(1).secondOrNull()}")
    println("  emptyList<Int>().secondOrNull() = ${emptyList<Int>().secondOrNull()}")

    // Generic extension with constraint
    println("  listOf(3,1,4,1,5).sortedAndDistinct() = ${listOf(3, 1, 4, 1, 5).sortedAndDistinct()}")

    println("\n✅ Kotlin Generics Codelab Complete!")
}

// ═══════════════════════════════════════════
// Supporting types
// ═══════════════════════════════════════════

class Box<T>(val value: T)

data class GenericPair<A, B>(val first: A, val second: B)

open class Animal(val name: String)
class Dog(name: String) : Animal(name)
class Cat(name: String) : Animal(name)

// Covariant — only produces T (out position)
class Producer<out T>(private val value: T) {
    fun produce(): T = value
    // fun consume(item: T) {}  // COMPILE ERROR — can't use T in 'in' position
}

// Contravariant — only consumes T (in position)
class Consumer<in T>(private val action: (T) -> Unit) {
    fun consume(item: T) = action(item)
    // fun produce(): T  // COMPILE ERROR — can't use T in 'out' position
}

// Reified
inline fun <reified T> isOfType(value: Any): Boolean = value is T

inline fun <reified T> List<Any>.filterByType(): List<T> =
    filterIsInstance<T>()

// Generic extensions
fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

fun <T : Comparable<T>> List<T>.sortedAndDistinct(): List<T> =
    sorted().distinct()
