package com.androidtechguru.codelab.kotlin.basics

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║       KOTLIN BASICS — Codelab            ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. VARIABLES
    // ─────────────────────────────────────────
    println("=== 1. Variables ===")

    val immutable = "I cannot be reassigned"   // val = read-only reference (like Java final)
    var mutable = "I can be reassigned"         // var = reassignable
    mutable = "See? Changed!"

    println("val: $immutable")
    println("var: $mutable")

    // const val — compile-time constant, must be top-level or in companion object
    println("const val: $COMPILE_TIME_CONST")

    // lateinit — for non-null vars initialized later (commonly used with DI)
    lateinit var name: String
    // println(name) // would crash: UninitializedPropertyAccessException
    name = "Nikhil"
    println("lateinit: $name, isInitialized can be checked via ::name.isInitialized")

    // ─────────────────────────────────────────
    // 2. DATA TYPES
    // ─────────────────────────────────────────
    println("\n=== 2. Data Types ===")

    val byte: Byte = 127               // 8-bit  (-128 to 127)
    val short: Short = 32767           // 16-bit
    val int: Int = 2_147_483_647       // 32-bit (underscores for readability)
    val long: Long = 9_223_372_036_854_775_807L  // 64-bit
    val float: Float = 3.14f           // 32-bit floating point
    val double: Double = 3.14159265    // 64-bit floating point
    val boolean: Boolean = true
    val char: Char = 'K'

    println("Byte: $byte, Short: $short, Int: $int")
    println("Long: $long")
    println("Float: $float, Double: $double")
    println("Boolean: $boolean, Char: $char")

    // Type inference — Kotlin figures out the type
    val inferred = 42          // Int
    val inferredStr = "Hello"  // String
    println("Inferred types: $inferred (Int), $inferredStr (String)")

    // ─────────────────────────────────────────
    // 3. NULL SAFETY
    // ─────────────────────────────────────────
    println("\n=== 3. Null Safety ===")

    var nullable: String? = "Hello"  // ? makes it nullable
    nullable = null                   // allowed because of ?

    // Safe call operator ?.
    println("--- Safe Call ?. ---")
    println("nullable?.length = ${nullable?.length}")  // null (no crash)
    nullable = "Kotlin"
    println("nullable?.length = ${nullable?.length}")  // 6

    // Elvis operator ?:
    println("\n--- Elvis Operator ?: ---")
    val nullStr: String? = null
    val length = nullStr?.length ?: -1  // if null, use default value
    println("nullStr?.length ?: -1 = $length")

    // Elvis with throw (common pattern in Android)
    // val id = intent.getStringExtra("id") ?: throw IllegalArgumentException("id required")

    // Non-null assertion !!
    println("\n--- Non-Null Assertion !! ---")
    val notNull: String? = "I'm not null"
    println("notNull!!.length = ${notNull!!.length}")  // 11 — will crash if null!
    // INTERVIEW TIP: Avoid !! — use ?. or ?: instead. !! defeats null safety.

    // Safe call with let
    println("\n--- let for Null Check ---")
    val email: String? = "user@example.com"
    email?.let { e ->
        println("Email is not null: $e")
        println("Uppercase: ${e.uppercase()}")
    }
    val nullEmail: String? = null
    nullEmail?.let { println("This won't print") }
    println("nullEmail?.let block was skipped (null)")

    // ─────────────────────────────────────────
    // 4. TYPE CASTING
    // ─────────────────────────────────────────
    println("\n=== 4. Type Casting ===")

    // is — type check (like instanceof)
    val obj: Any = "Hello Kotlin"
    if (obj is String) {
        // Smart cast: obj is automatically cast to String inside this block
        println("Smart cast: obj.length = ${obj.length}")
    }

    // as — unsafe cast (throws ClassCastException if wrong)
    val str: String = obj as String
    println("Unsafe cast: $str")

    // as? — safe cast (returns null if wrong)
    val num: Any = 42
    val safeStr: String? = num as? String
    println("Safe cast (Int as? String): $safeStr")  // null

    // Smart cast with when
    println("\n--- Smart Cast with when ---")
    fun describe(obj: Any): String = when (obj) {
        is Int -> "Integer: ${obj + 1}"       // smart cast to Int
        is String -> "String of length ${obj.length}"  // smart cast to String
        is Boolean -> "Boolean: ${!obj}"       // smart cast to Boolean
        else -> "Unknown"
    }
    println(describe(42))
    println(describe("Hello"))
    println(describe(true))

    // ─────────────────────────────────────────
    // 5. STRING TEMPLATES
    // ─────────────────────────────────────────
    println("\n=== 5. String Templates ===")

    val userName = "Nikhil"
    val age = 28

    // Simple variable reference
    println("Name: $userName, Age: $age")

    // Expression in template
    println("In 5 years: ${age + 5}")
    println("Name uppercase: ${userName.uppercase()}")

    // Multi-line strings (trimIndent removes leading whitespace)
    val json = """
        {
            "name": "$userName",
            "age": $age
        }
    """.trimIndent()
    println("Multi-line:\n$json")

    // ─────────────────────────────────────────
    // 6. CONTROL FLOW
    // ─────────────────────────────────────────
    println("\n=== 6. Control Flow ===")

    // if is an EXPRESSION (returns a value) — no ternary operator needed
    println("--- if Expression ---")
    val a = 10; val b = 20
    val max = if (a > b) a else b
    println("max($a, $b) = $max")

    // when — Kotlin's powerful switch replacement
    println("\n--- when Expression ---")
    fun gradeScore(score: Int): String = when {
        score >= 90 -> "A"
        score >= 80 -> "B"
        score >= 70 -> "C"
        score >= 60 -> "D"
        else -> "F"
    }
    println("Score 95 -> ${gradeScore(95)}")
    println("Score 72 -> ${gradeScore(72)}")

    // when with argument
    fun dayType(day: String): String = when (day) {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday"
        "Saturday", "Sunday" -> "Weekend"
        else -> "Unknown"
    }
    println("Monday is a ${dayType("Monday")}")

    // when with ranges and is
    fun classify(value: Any): String = when (value) {
        in 1..10 -> "Between 1 and 10"
        is String -> "A string: $value"
        !in 100..200 -> "Not between 100-200"
        else -> "Something else"
    }
    println("classify(5) = ${classify(5)}")
    println("classify(\"hi\") = ${classify("hi")}")

    // ─────────────────────────────────────────
    // 7. LOOPS
    // ─────────────────────────────────────────
    println("\n=== 7. Loops ===")

    // for with ranges
    print("Range 1..5: ")
    for (i in 1..5) print("$i ")  // inclusive both ends
    println()

    print("until (exclusive end): ")
    for (i in 1 until 5) print("$i ")  // 1,2,3,4
    println()

    print("downTo: ")
    for (i in 5 downTo 1) print("$i ")
    println()

    print("step: ")
    for (i in 0..20 step 5) print("$i ")
    println()

    // for with index
    val fruits = listOf("Apple", "Banana", "Cherry")
    println("withIndex:")
    for ((index, fruit) in fruits.withIndex()) {
        println("  [$index] $fruit")
    }

    // repeat
    print("repeat(3): ")
    repeat(3) { print("${it + 1} ") }
    println()

    // ─────────────────────────────────────────
    // 8. EQUALITY
    // ─────────────────────────────────────────
    println("\n=== 8. Equality ===")

    // == structural equality (calls equals())
    // === referential equality (same object in memory)
    val s1 = "Hello"
    val s2 = "Hello"
    val s3 = String("Hello".toCharArray())

    println("s1 == s2 (structural): ${s1 == s2}")   // true
    println("s1 === s2 (referential): ${s1 === s2}") // true (string pool)
    println("s1 == s3 (structural): ${s1 == s3}")    // true
    println("s1 === s3 (referential): ${s1 === s3}") // false (different object)

    // INTERVIEW TIP: In Kotlin, == is null-safe! null == null is true, null == "x" is false (no NPE)
    println("null == null: ${null == null}")
    println("null == \"x\": ${null == "x"}")

    // ─────────────────────────────────────────
    // 9. DESTRUCTURING DECLARATIONS
    // ─────────────────────────────────────────
    println("\n=== 9. Destructuring Declarations ===")

    // Data class destructuring
    data class User(val name: String, val age: Int, val email: String)
    val user = User("Nikhil", 28, "nikhil@example.com")
    val (dName, dAge, dEmail) = user  // calls component1(), component2(), component3()
    println("Destructured: name=$dName, age=$dAge, email=$dEmail")

    // Skip with _
    val (justName, _, justEmail) = user
    println("Skipped age: name=$justName, email=$justEmail")

    // Map destructuring
    val map = mapOf("a" to 1, "b" to 2, "c" to 3)
    println("Map destructuring:")
    for ((key, value) in map) {
        println("  $key -> $value")
    }

    // Pair and Triple
    val pair = Pair("Kotlin", 2.1)
    val (lang, version) = pair
    println("Pair: $lang $version")

    // ─────────────────────────────────────────
    // 10. TYPE ALIASES
    // ─────────────────────────────────────────
    println("\n=== 10. Type Aliases ===")

    // typealias creates an alternative name (doesn't create new type)
    // Defined at top-level (see below main)
    val handler: ClickHandler = { println("  Button clicked!") }
    val users: UserList = listOf(
        User("Alice", 25, "alice@test.com"),
        User("Bob", 30, "bob@test.com")
    )
    println("ClickHandler invoked:")
    handler()
    println("UserList size: ${users.size}")

    println("\n✅ Kotlin Basics Codelab Complete!")
}

// Compile-time constant — must be top-level or in companion object
const val COMPILE_TIME_CONST = "I'm a compile-time constant"

// Type aliases — must be top-level
typealias ClickHandler = () -> Unit
typealias UserList = List<Any>  // Using Any since User is defined inside main
typealias StringMap = Map<String, String>
typealias Predicate<T> = (T) -> Boolean
