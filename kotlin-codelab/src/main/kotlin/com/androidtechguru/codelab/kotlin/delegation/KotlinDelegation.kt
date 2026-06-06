package com.androidtechguru.codelab.kotlin.delegation

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║   KOTLIN DELEGATION — Codelab            ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. CLASS DELEGATION (by keyword)
    // ─────────────────────────────────────────
    println("=== 1. Class Delegation ===")

    // Problem: You want to reuse behavior without inheritance
    // Solution: Delegate interface implementation to another object

    val printer = ConsolePrinter()
    val logger = PrefixLogger(printer, prefix = "[APP]")

    logger.print("Application started")
    logger.print("User logged in")

    // INTERVIEW TIP: Class delegation avoids:
    // 1. Deep inheritance hierarchies
    // 2. The fragile base class problem
    // 3. Tight coupling to parent implementation
    // Prefer delegation over inheritance (Effective Java Item 18)

    println("\n--- Delegation with override ---")
    val countingList = CountingArrayList<String>()
    countingList.add("A")
    countingList.add("B")
    countingList.addAll(listOf("C", "D"))
    println("  Items: $countingList")
    println("  Add count: ${countingList.addCount}")  // 4 (not 2!)

    // ─────────────────────────────────────────
    // 2. PROPERTY DELEGATION (by keyword)
    // ─────────────────────────────────────────
    println("\n=== 2. Property Delegation ===")

    // The 'by' keyword delegates getter/setter to another object
    // Pattern: val/var <property> by <delegate>

    // ─────────────────────────────────────────
    // 3. LAZY DELEGATION
    // ─────────────────────────────────────────
    println("\n=== 3. Lazy Delegation ===")

    // by lazy — value computed on first access, then cached
    println("--- Before accessing lazyValue ---")
    val lazyValue: String by lazy {
        println("  Computing lazy value...")
        "Hello from lazy!"
    }
    println("--- First access ---")
    println("  $lazyValue")
    println("--- Second access (cached) ---")
    println("  $lazyValue")

    // Thread safety modes
    println("""

    LazyThreadSafetyMode:
      SYNCHRONIZED (default) — thread-safe, uses lock. Best for most cases.
      PUBLICATION             — multiple threads can compute, first wins.
      NONE                    — no thread safety. Fastest. Use in single-threaded contexts.
    """.trimIndent())

    // INTERVIEW TIP: by lazy is perfect for:
    // - Expensive computations you might not need
    // - ViewBinding in Fragments: val binding by lazy { FragmentXBinding.bind(view) }
    // - ViewModel: val viewModel by lazy { ViewModelProvider(this)[MyVM::class.java] }

    // ─────────────────────────────────────────
    // 4. OBSERVABLE DELEGATION
    // ─────────────────────────────────────────
    println("\n=== 4. Observable Delegation ===")

    // Delegates.observable — callback on every change
    println("--- observable ---")
    var observedName: String by Delegates.observable("initial") { prop, old, new ->
        println("  ${prop.name} changed: '$old' → '$new'")
    }
    observedName = "Alice"
    observedName = "Bob"

    // Delegates.vetoable — callback can REJECT the change
    println("\n--- vetoable (rejects negative) ---")
    var positiveNumber: Int by Delegates.vetoable(0) { _, _, new ->
        new >= 0  // return false to reject
    }
    positiveNumber = 42
    println("  Set to 42: $positiveNumber")
    positiveNumber = -1  // rejected!
    println("  Tried -1: $positiveNumber (still 42)")
    positiveNumber = 100
    println("  Set to 100: $positiveNumber")

    // ─────────────────────────────────────────
    // 5. MAP DELEGATION
    // ─────────────────────────────────────────
    println("\n=== 5. Map Delegation ===")

    // Properties backed by a Map — great for JSON-like data
    val json = mapOf(
        "name" to "Alice",
        "age" to 25,
        "email" to "alice@test.com"
    )
    val userFromMap = UserFromMap(json)
    println("  Name: ${userFromMap.name}")
    println("  Age: ${userFromMap.age}")
    println("  Email: ${userFromMap.email}")

    // Mutable map delegation
    val mutableJson = mutableMapOf(
        "name" to "Bob" as Any,
        "score" to 0 as Any
    )
    val mutableUser = MutableMapUser(mutableJson)
    println("\n  Before: name=${mutableUser.name}, score=${mutableUser.score}")
    mutableUser.score = 100
    println("  After: name=${mutableUser.name}, score=${mutableUser.score}")
    println("  Map: $mutableJson")  // map is updated!

    // INTERVIEW TIP: Map delegation is useful for parsing JSON without
    // creating data classes — but data classes are usually better.

    // ─────────────────────────────────────────
    // 6. CUSTOM DELEGATES
    // ─────────────────────────────────────────
    println("\n=== 6. Custom Delegates ===")

    // Custom delegate implementing ReadWriteProperty
    println("--- Trimmed string delegate ---")
    var trimmedName: String by TrimmedString()
    trimmedName = "  Hello World  "
    println("  Assigned '  Hello World  ', got: '$trimmedName'")

    // Validated delegate
    println("\n--- Validated range delegate ---")
    var percentage: Int by RangeValidated(0, 100)
    percentage = 75
    println("  Set 75: $percentage")
    try {
        percentage = 150  // throws!
    } catch (e: IllegalArgumentException) {
        println("  Set 150: ${e.message}")
    }
    println("  Still: $percentage")

    // SharedPreferences-like delegate (simulated)
    println("\n--- Preference-like delegate ---")
    var theme: String by PreferenceDelegate("theme", "light")
    println("  Default theme: $theme")
    theme = "dark"
    println("  Updated theme: $theme")

    // ─────────────────────────────────────────
    // 7. DELEGATION vs INHERITANCE
    // ─────────────────────────────────────────
    println("\n=== 7. Delegation vs Inheritance ===")
    println("""
    ┌─────────────────────────────────────────────────────────────┐
    │ When to use Delegation vs Inheritance                       │
    ├─────────────────────────────────────────────────────────────┤
    │ USE DELEGATION when:                                        │
    │   • You want to reuse behavior without IS-A relationship    │
    │   • You need to compose multiple behaviors                  │
    │   • The parent class might change (fragile base class)      │
    │   • You want to decorate/proxy an interface                 │
    │   • Android: ViewModel delegates, preference delegates      │
    │                                                             │
    │ USE INHERITANCE when:                                       │
    │   • True IS-A relationship (Dog IS-A Animal)                │
    │   • You need to override specific abstract methods          │
    │   • Framework requires it (Activity, Fragment, ViewModel)   │
    │                                                             │
    │ KOTLIN DEFAULT: Classes are final. This pushes you toward   │
    │ composition/delegation by design. You must explicitly       │
    │ 'open' a class to allow inheritance.                        │
    └─────────────────────────────────────────────────────────────┘
    """.trimIndent())

    println("\n✅ Kotlin Delegation Codelab Complete!")
}

// ═══════════════════════════════════════════
// Supporting types
// ═══════════════════════════════════════════

// --- Class Delegation ---
interface Printer {
    fun print(message: String)
}

class ConsolePrinter : Printer {
    override fun print(message: String) = println("  $message")
}

// Delegates Printer to 'printer' param, overrides print to add prefix
class PrefixLogger(printer: Printer, private val prefix: String) : Printer by printer {
    override fun print(message: String) {
        // Delegates to printer, but we can override specific methods
        println("  $prefix $message")
    }
}

// Delegation with override — counting list
class CountingArrayList<T>(
    private val inner: MutableList<T> = mutableListOf()
) : MutableList<T> by inner {
    var addCount = 0
        private set

    override fun add(element: T): Boolean {
        addCount++
        return inner.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        addCount += elements.size
        return inner.addAll(elements)
    }
}

// --- Map Delegation ---
class UserFromMap(map: Map<String, Any>) {
    val name: String by map
    val age: Int by map
    val email: String by map
}

class MutableMapUser(map: MutableMap<String, Any>) {
    var name: String by map
    var score: Int by map
}

// --- Custom Delegates ---
class TrimmedString : ReadWriteProperty<Any?, String> {
    private var value: String = ""

    override fun getValue(thisRef: Any?, property: KProperty<*>): String = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value.trim()
    }
}

class RangeValidated(
    private val min: Int,
    private val max: Int
) : ReadWriteProperty<Any?, Int> {
    private var value: Int = min

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        require(value in min..max) { "${property.name} must be in $min..$max, got $value" }
        this.value = value
    }
}

// Simulated SharedPreferences-style delegate
class PreferenceDelegate(
    private val key: String,
    private val defaultValue: String
) : ReadWriteProperty<Any?, String> {
    private val store = mutableMapOf<String, String>()  // simulated prefs

    override fun getValue(thisRef: Any?, property: KProperty<*>): String =
        store.getOrDefault(key, defaultValue)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        store[key] = value
        println("  [Pref] Saved '$key' = '$value'")
    }
}
