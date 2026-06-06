package com.androidtechguru.codelab.kotlin.functions

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║    KOTLIN FUNCTIONS — Codelab            ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. FUNCTION BASICS
    // ─────────────────────────────────────────
    println("=== 1. Function Basics ===")

    // Named arguments — great for readability
    println(formatUser(name = "Alice", age = 25, role = "Dev"))

    // Default parameters — no overloading needed
    println(formatUser(name = "Bob"))  // age=0, role="User"

    // Vararg
    println("Sum: ${sum(1, 2, 3, 4, 5)}")

    // Spread operator * — pass array as vararg
    val numbers = intArrayOf(10, 20, 30)
    println("Sum with spread: ${sum(*numbers)}")

    // Single-expression functions
    fun square(x: Int): Int = x * x
    println("square(7) = ${square(7)}")

    // ─────────────────────────────────────────
    // 2. HIGHER-ORDER FUNCTIONS
    // ─────────────────────────────────────────
    println("\n=== 2. Higher-Order Functions ===")

    // Function that takes a function as parameter
    fun operate(a: Int, b: Int, operation: (Int, Int) -> Int): Int = operation(a, b)

    println("operate(10, 3, +) = ${operate(10, 3) { x, y -> x + y }}")
    println("operate(10, 3, *) = ${operate(10, 3) { x, y -> x * y }}")

    // Function that returns a function
    fun multiplier(factor: Int): (Int) -> Int = { it * factor }
    val triple = multiplier(3)
    println("triple(7) = ${triple(7)}")

    // Function reference ::
    fun isEven(n: Int): Boolean = n % 2 == 0
    val evens = listOf(1, 2, 3, 4, 5, 6).filter(::isEven)
    println("Evens (function ref): $evens")

    // ─────────────────────────────────────────
    // 3. LAMBDAS
    // ─────────────────────────────────────────
    println("\n=== 3. Lambdas ===")

    // Full syntax
    val add: (Int, Int) -> Int = { a: Int, b: Int -> a + b }
    println("Lambda add(3,4) = ${add(3, 4)}")

    // 'it' — implicit name for single parameter
    val doubled = listOf(1, 2, 3).map { it * 2 }
    println("Doubled: $doubled")

    // Trailing lambda — if last param is function, move outside ()
    val filtered = listOf(1, 2, 3, 4, 5).filter { it > 3 }
    println("Filtered > 3: $filtered")

    // SAM (Single Abstract Method) conversion — Java interface with one method
    // val runnable = Runnable { println("Running!") }
    // Equivalent to: val runnable = object : Runnable { override fun run() { ... } }

    // Lambda with destructuring
    val map = mapOf("a" to 1, "b" to 2)
    map.forEach { (key, value) -> println("  $key -> $value") }

    // ─────────────────────────────────────────
    // 4. EXTENSION FUNCTIONS & PROPERTIES
    // ─────────────────────────────────────────
    println("\n=== 4. Extension Functions & Properties ===")

    // Extension function — adds function to existing class without modifying it
    println("\"hello world\".capitalizeWords() = ${"hello world".capitalizeWords()}")
    println("\"kotlin\".removeFirstAndLast() = ${"kotlin".removeFirstAndLast()}")

    // Extension on nullable type
    println("null.isNullOrBlankCustom() = ${null.isNullOrBlankCustom()}")
    println("\"\".isNullOrBlankCustom() = ${"".isNullOrBlankCustom()}")
    println("\"hi\".isNullOrBlankCustom() = ${"hi".isNullOrBlankCustom()}")

    // Extension property
    println("\"Hello\".lastChar = ${"Hello".lastChar}")

    // INTERVIEW TIP: Extensions are resolved STATICALLY (compile time), not at runtime.
    // They don't actually modify the class — they're syntactic sugar for static methods.

    // ─────────────────────────────────────────
    // 5. INFIX FUNCTIONS
    // ─────────────────────────────────────────
    println("\n=== 5. Infix Functions ===")

    // infix — can be called without dot and parentheses
    println("5 pow 3 = ${5 pow 3}")

    // Built-in infix functions
    val pair = "key" to "value"  // to is infix
    println("Pair: $pair")

    val range = 1 until 5  // until is infix
    println("Range: ${range.toList()}")

    // ─────────────────────────────────────────
    // 6. INLINE FUNCTIONS
    // ─────────────────────────────────────────
    println("\n=== 6. Inline Functions ===")

    // inline — compiler copies the function body at the call site
    // Avoids creating lambda objects (performance for HOFs)
    measureTime("test operation") {
        Thread.sleep(10)
    }

    // noinline — prevents specific lambda from being inlined
    // crossinline — lambda can't use non-local return

    // INTERVIEW TIP: Use inline when:
    // 1. Function takes lambda parameters (avoids object allocation)
    // 2. You need reified type parameters
    // Don't use for large function bodies — increases bytecode size

    // reified — access generic type at runtime (requires inline)
    println("isType<String>(\"hello\"): ${isType<String>("hello")}")
    println("isType<Int>(\"hello\"): ${isType<Int>("hello")}")

    // ─────────────────────────────────────────
    // 7. SCOPE FUNCTIONS
    // ─────────────────────────────────────────
    println("\n=== 7. Scope Functions ===")

    data class Person(var name: String, var age: Int, var email: String = "")

    // let — context: it, returns: lambda result
    // USE: null checks, transformations
    println("--- let ---")
    val nameLength: Int? = "Kotlin"?.let {
        println("  Inside let: $it")
        it.length  // returns this
    }
    println("  nameLength: $nameLength")

    // run — context: this, returns: lambda result
    // USE: object configuration + computing a result
    println("\n--- run ---")
    val greeting = Person("Alice", 25).run {
        email = "alice@test.com"  // 'this' is implicit
        "Hello, $name! ($email)"  // returns this
    }
    println("  run result: $greeting")

    // with — context: this, returns: lambda result (non-extension)
    // USE: calling multiple methods on an object
    println("\n--- with ---")
    val info = with(Person("Bob", 30)) {
        email = "bob@test.com"
        "Name: $name, Age: $age, Email: $email"
    }
    println("  with result: $info")

    // apply — context: this, returns: context object
    // USE: object configuration (returns the object itself)
    println("\n--- apply ---")
    val person = Person("Charlie", 28).apply {
        email = "charlie@test.com"
        age = 29
    }
    println("  apply result: $person")

    // also — context: it, returns: context object
    // USE: side effects (logging, validation) without modifying chain
    println("\n--- also ---")
    val numbers2 = mutableListOf(1, 2, 3)
        .also { println("  Before add: $it") }
        .also { it.add(4) }
        .also { println("  After add: $it") }
    println("  also result: $numbers2")

    println("""

    ┌─────────────┬──────────────┬──────────────┐
    │  Function   │ Context Obj  │   Returns    │
    ├─────────────┼──────────────┼──────────────┤
    │  let        │     it       │ Lambda result│
    │  run        │     this     │ Lambda result│
    │  with       │     this     │ Lambda result│
    │  apply      │     this     │ Context obj  │
    │  also       │     it       │ Context obj  │
    └─────────────┴──────────────┴──────────────┘
    """.trimIndent())

    // ─────────────────────────────────────────
    // 8. OPERATOR OVERLOADING
    // ─────────────────────────────────────────
    println("\n=== 8. Operator Overloading ===")

    val v1 = Vector(1.0, 2.0)
    val v2 = Vector(3.0, 4.0)
    println("v1 + v2 = ${v1 + v2}")
    println("v1 * 3 = ${v1 * 3.0}")
    println("v1 == v2: ${v1 == v2}")
    println("-v1 = ${-v1}")
    println("v1[0] = ${v1[0]}, v1[1] = ${v1[1]}")

    // invoke operator
    val greeter = Greeter("Hello")
    println(greeter("World"))  // calls invoke

    // ─────────────────────────────────────────
    // 9. TAILREC & LOCAL FUNCTIONS
    // ─────────────────────────────────────────
    println("\n=== 9. Tailrec & Local Functions ===")

    // tailrec — compiler optimizes tail-recursive calls to loops (no stack overflow)
    println("factorial(10) = ${factorial(10)}")
    println("fibonacci(10) = ${fibonacci(10, 0, 1)}")

    // Local functions — function inside a function
    fun validateUser(name: String, email: String): Boolean {
        // Local function — can access outer function's parameters
        fun validateField(field: String, fieldName: String): Boolean {
            if (field.isBlank()) {
                println("  $fieldName is blank!")
                return false
            }
            return true
        }
        return validateField(name, "Name") && validateField(email, "Email")
    }
    println("Valid: ${validateUser("Alice", "alice@test.com")}")
    println("Valid: ${validateUser("", "bob@test.com")}")

    println("\n✅ Kotlin Functions Codelab Complete!")
}

// ═══════════════════════════════════════════
// Supporting declarations
// ═══════════════════════════════════════════

fun formatUser(name: String, age: Int = 0, role: String = "User"): String =
    "User(name=$name, age=$age, role=$role)"

fun sum(vararg numbers: Int): Int = numbers.sum()

// Extension functions
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

fun String.removeFirstAndLast(): String =
    if (length <= 2) "" else substring(1, length - 1)

fun String?.isNullOrBlankCustom(): Boolean = this == null || this.isBlank()

// Extension property
val String.lastChar: Char
    get() = this[length - 1]

// Infix function
infix fun Int.pow(exponent: Int): Int {
    var result = 1
    repeat(exponent) { result *= this }
    return result
}

// Inline function with lambda
inline fun measureTime(label: String, block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val duration = System.currentTimeMillis() - start
    println("  [$label] took ${duration}ms")
}

// Reified type parameter
inline fun <reified T> isType(value: Any): Boolean = value is T

// Operator overloading
data class Vector(val x: Double, val y: Double) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)
    operator fun unaryMinus() = Vector(-x, -y)
    operator fun get(index: Int): Double = when (index) {
        0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException()
    }
}

class Greeter(private val greeting: String) {
    operator fun invoke(name: String) = "$greeting, $name!"
}

// Tailrec
tailrec fun factorial(n: Long, acc: Long = 1): Long =
    if (n <= 1) acc else factorial(n - 1, n * acc)

tailrec fun fibonacci(n: Int, a: Long, b: Long): Long =
    if (n == 0) a else fibonacci(n - 1, b, a + b)
