package com.androidtechguru.codelab.kotlin.oops

fun main() {
    println("╔══════════════════════════════════════════════╗")
    println("║  KOTLIN OOP INTERVIEW PUZZLES                ║")
    println("║  Predict the output before running!          ║")
    println("╚══════════════════════════════════════════════╝\n")

    puzzle1_dataClassEquality()
    puzzle2_dataClassCopyTrap()
    puzzle3_dataClassBodyProperty()
    puzzle4_sealedExhaustive()
    puzzle5_companionInit()
    puzzle6_objectVsClass()
    puzzle7_inheritanceOverride()
    puzzle8_initBlockOrder()
    puzzle9_interfaceDiamondProblem()
    puzzle10_smartCastMutable()
    puzzle11_enumOrdinal()
    puzzle12_nestedVsInner()
    puzzle13_valueClassEquality()
    puzzle14_nothingType()
    puzzle15_abstractVsInterface()
    puzzle16_extensionVsMember()
    puzzle17_companionInheritance()
    puzzle18_objectExpressionCapture()
    puzzle19_sealedWhenCoverage()
    puzzle20_equalsHashCodeContract()
}

// ═════════════════════════════════════════════
// PUZZLE 1: Data class equality — what's equal?
// ═════════════════════════════════════════════
fun puzzle1_dataClassEquality() {
    println("═══ PUZZLE 1: Data class equality ═══")
    println("""
    CODE:
    data class User(val name: String, val age: Int)
    val a = User("Alice", 25)
    val b = User("Alice", 25)
    val c = a
    println(a == b)
    println(a === b)
    println(a === c)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    data class User(val name: String, val age: Int)
    val a = User("Alice", 25)
    val b = User("Alice", 25)
    val c = a
    println(a == b)   // true
    println(a === b)  // false
    println(a === c)  // true

    println("""
    EXPLANATION:
    == calls equals() → data class generates equals() from constructor params → true
    === checks same object reference → a and b are different objects → false
    c = a → same reference → true
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 2: copy() is SHALLOW
// ═════════════════════════════════════════════
fun puzzle2_dataClassCopyTrap() {
    println("═══ PUZZLE 2: Data class copy() is SHALLOW ═══")
    println("""
    CODE:
    data class Config(val tags: MutableList<String>)
    val original = Config(mutableListOf("a", "b"))
    val copied = original.copy()
    copied.tags.add("c")
    println(original.tags)
    println(copied.tags)
    println(original == copied)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    data class Config(val tags: MutableList<String>)
    val original = Config(mutableListOf("a", "b"))
    val copied = original.copy()
    copied.tags.add("c")
    println(original.tags)
    println(copied.tags)
    println(original == copied)

    println("""
    EXPLANATION:
    copy() does SHALLOW copy — both original and copied share the SAME list!
    Adding "c" to copied.tags also modifies original.tags.
    Both print [a, b, c]. And they're equal because same list reference.

    FIX: Deep copy manually:
    val copied = original.copy(tags = original.tags.toMutableList())

    INTERVIEW TIP: This is why immutable data is preferred for state.
    Use List (not MutableList) in data classes to prevent this.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 3: Body properties NOT in equals/hashCode
// ═════════════════════════════════════════════
fun puzzle3_dataClassBodyProperty() {
    println("═══ PUZZLE 3: Body properties are EXCLUDED ═══")
    println("""
    CODE:
    data class Person(val name: String) {
        var age: Int = 0
    }
    val p1 = Person("Alice").apply { age = 25 }
    val p2 = Person("Alice").apply { age = 99 }
    println(p1 == p2)
    println(p1.hashCode() == p2.hashCode())
    println(p1)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    data class Person(val name: String) {
        var age: Int = 0
    }
    val p1 = Person("Alice").apply { age = 25 }
    val p2 = Person("Alice").apply { age = 99 }
    println(p1 == p2)
    println(p1.hashCode() == p2.hashCode())
    println(p1)

    println("""
    EXPLANATION:
    Only PRIMARY CONSTRUCTOR properties participate in equals/hashCode/toString/copy.
    'age' is in the body → excluded.
    p1 == p2 is true (both have name="Alice").
    hashCode is same. toString shows only "Person(name=Alice)" — no age!

    TRAP: If you use a data class as a Map key, body properties are invisible
    to the Map. Two "different" objects map to the same key.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 4: Sealed class when — missing branch
// ═════════════════════════════════════════════
fun puzzle4_sealedExhaustive() {
    println("═══ PUZZLE 4: Sealed class exhaustive when ═══")
    println("""
    CODE:
    sealed class Result {
        data class Success(val data: String) : Result()
        data class Error(val msg: String) : Result()
        data object Loading : Result()
    }

    fun handle(r: Result): String = when (r) {
        is Result.Success -> "OK: ${'$'}{r.data}"
        is Result.Error -> "Fail: ${'$'}{r.msg}"
        is Result.Loading -> "..."
        // No 'else' needed!
    }

    QUESTION: What happens if you add a new subclass and forget to update when?
    """.trimIndent())
    println("\n>>> ANSWER:")

    println("""
    COMPILE ERROR! The when expression becomes non-exhaustive.
    The compiler forces you to handle all cases. This is the #1 reason to use sealed classes.

    With 'else' branch: compiles but you silently miss the new case (dangerous!).
    Without 'else': compiler error tells you exactly what's missing (safe!).

    INTERVIEW TIP: Always avoid 'else' in when with sealed classes.
    Let the compiler catch missing branches at compile time.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 5: Companion object initialization
// ═════════════════════════════════════════════
fun puzzle5_companionInit() {
    println("═══ PUZZLE 5: Companion object init timing ═══")
    println("""
    CODE:
    class MyClass {
        init { println("Instance init") }
        companion object {
            init { println("Companion init") }
            val TAG = "MyClass".also { println("TAG created") }
        }
    }
    println("Before access")
    println(MyClass.TAG)
    println("Before instance")
    MyClass()
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    // Note: companion objects can't be in local classes, so this uses a top-level class.
    // See CompanionDemo defined outside this function.
    println("Before access")
    println(CompanionDemo.TAG)
    println("Before instance")
    CompanionDemo()

    println("""
    EXPLANATION:
    "Before access" first.
    Accessing MyClass.TAG triggers companion initialization:
      "Companion init" → "TAG created" → then TAG value "MyClass" is printed.
    "Before instance" prints.
    MyClass() creates instance → "Instance init".

    KEY: Companion object is initialized on first access (like static initializer).
    It's initialized ONCE, even if you create multiple instances.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 6: object declaration — singleton timing
// ═════════════════════════════════════════════
fun puzzle6_objectVsClass() {
    println("═══ PUZZLE 6: object is a singleton ═══")
    println("""
    CODE:
    object Counter {
        var count = 0
        fun increment() = ++count
    }
    println(Counter.increment())
    println(Counter.increment())
    println(Counter.increment())

    val a = Counter
    val b = Counter
    println(a === b)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    // Named objects can't be local — see CounterSingleton at bottom of file.
    println(CounterSingleton.increment())
    println(CounterSingleton.increment())
    println(CounterSingleton.increment())

    val a = CounterSingleton
    val b = CounterSingleton
    println(a === b)

    println("""
    EXPLANATION:
    object = singleton. Only ONE instance exists.
    increment() returns 1, 2, 3 — state persists across calls.
    a === b is true — same instance.

    INTERVIEW TIP: Kotlin object compiles to a Java class with a static INSTANCE field.
    Thread safety of initialization is guaranteed (like Java static initializer).
    But mutable state inside is NOT thread-safe — you need synchronization.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 7: Inheritance — which method is called?
// ═════════════════════════════════════════════
fun puzzle7_inheritanceOverride() {
    println("═══ PUZZLE 7: Property override in constructor ═══")
    println("""
    CODE:
    open class Base(open val value: String = "Base") {
        init { println("Base init: value = ${'$'}value") }
    }
    class Derived(override val value: String = "Derived") : Base() {
        init { println("Derived init: value = ${'$'}value") }
    }
    Derived()
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    open class Base(open val value: String = "Base") {
        init { println("Base init: value = $value") }
    }
    class Derived(override val value: String = "Derived") : Base() {
        init { println("Derived init: value = $value") }
    }
    Derived()

    println("""
    EXPLANATION:
    Base init runs FIRST (parent before child).
    But 'value' is overridden by Derived. At the time Base.init runs,
    Derived's backing field for 'value' hasn't been initialized yet!
    So Base.init sees... it depends on the compiler/runtime — could be null or "Derived".

    THIS IS A KNOWN KOTLIN GOTCHA. Accessing open properties in init blocks
    is dangerous because the derived class hasn't initialized yet.

    RULE: Never access open/overridable members in init blocks or constructors.
    The compiler warns: "Accessing non-final property in constructor."
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 8: init block execution order
// ═════════════════════════════════════════════
fun puzzle8_initBlockOrder() {
    println("═══ PUZZLE 8: init block order ═══")
    println("""
    CODE:
    class Example {
        val a = "A".also { println("Property a") }
        init { println("Init block 1") }
        val b = "B".also { println("Property b") }
        init { println("Init block 2") }
    }
    Example()
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    class Example {
        val a = "A".also { println("Property a") }
        init { println("Init block 1") }
        val b = "B".also { println("Property b") }
        init { println("Init block 2") }
    }
    Example()

    println("""
    EXPLANATION:
    Properties and init blocks execute in ORDER OF APPEARANCE in the file.
    Not "all properties first" or "all inits first" — TEXTUAL ORDER.
    Output: Property a → Init block 1 → Property b → Init block 2

    KEY: init blocks and property initializers are merged into the constructor
    in the order they appear in the source code.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 9: Interface diamond problem
// ═════════════════════════════════════════════
fun puzzle9_interfaceDiamondProblem() {
    println("═══ PUZZLE 9: Diamond problem — super<> syntax ═══")
    println("""
    CODE:
    interface A { fun greet() = println("A") }
    interface B { fun greet() = println("B") }
    class C : A, B {
        override fun greet() {
            super<A>.greet()
            super<B>.greet()
        }
    }
    C().greet()
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    interface A { fun greet() = println("A") }
    interface B { fun greet() = println("B") }
    class C : A, B {
        override fun greet() {
            super<A>.greet()
            super<B>.greet()
        }
    }
    C().greet()

    println("""
    EXPLANATION:
    When two interfaces have the same method with default implementations,
    the implementing class MUST override and resolve the conflict.
    Use super<InterfaceName>.method() to call a specific parent.
    Output: A, B

    Without the override, it's a COMPILE ERROR — Kotlin won't pick one for you.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 10: Smart cast fails on mutable
// ═════════════════════════════════════════════
fun puzzle10_smartCastMutable() {
    println("═══ PUZZLE 10: Smart cast fails on var ═══")
    println("""
    CODE:
    var x: Any = "Hello"
    if (x is String) {
        // println(x.length)  ← Does this compile?
    }
    """.trimIndent())
    println("\n>>> ANSWER:")

    // Can't demonstrate the error at runtime, so explain
    println("""
    It DEPENDS!

    With 'val x': COMPILES. Smart cast works because val can't be reassigned.
    With 'var x': May NOT compile if x could be modified by another thread
    between the check and the usage.

    In a local function scope: var smart cast usually works (compiler can prove safety).
    As a class property 'var': FAILS — another thread could change it.

    class Foo {
        var x: Any = "Hello"
        fun test() {
            if (x is String) {
                // println(x.length) ← COMPILE ERROR!
                // "Smart cast to String is impossible because x is a mutable property"
            }
        }
    }

    FIX: val local = x; if (local is String) { println(local.length) }
    Or use: (x as? String)?.length
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 11: Enum ordinal surprise
// ═════════════════════════════════════════════
fun puzzle11_enumOrdinal() {
    println("═══ PUZZLE 11: Enum ordinal and name ═══")
    println("""
    CODE:
    enum class Color { RED, GREEN, BLUE }
    println(Color.RED.ordinal)
    println(Color.GREEN.ordinal)
    println(Color.BLUE.name)
    println(Color.valueOf("RED"))
    println(Color.entries.size)
    println(Color.RED == Color.RED)
    println(Color.RED === Color.RED)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    // Enum classes can't be local — see PuzzleColor at bottom of file.
    println(PuzzleColor.RED.ordinal)
    println(PuzzleColor.GREEN.ordinal)
    println(PuzzleColor.BLUE.name)
    println(PuzzleColor.valueOf("RED"))
    println(PuzzleColor.entries.size)
    println(PuzzleColor.RED == PuzzleColor.RED)
    println(PuzzleColor.RED === PuzzleColor.RED)

    println("""
    EXPLANATION:
    ordinal = position (0-based): RED=0, GREEN=1
    name = string name: "BLUE"
    valueOf("RED") = enum constant from string
    entries.size = 3 (Kotlin 1.9+ replacement for values())
    == and === are BOTH true — enum constants are singletons!

    TRAP: Don't persist ordinal to database — adding a new enum value
    shifts all ordinals. Persist name instead.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 12: Nested vs Inner — reference leak
// ═════════════════════════════════════════════
fun puzzle12_nestedVsInner() {
    println("═══ PUZZLE 12: Nested vs Inner class ═══")
    println("""
    CODE:
    class Outer(val x: Int) {
        class Nested {
            // fun getX() = x  ← Does this compile?
        }
        inner class Inner {
            fun getX() = x  // Does this compile?
        }
    }
    // val n = Outer.Nested()       ← Does this work?
    // val i = Outer(42).Inner()    ← Does this work?
    """.trimIndent())
    println("\n>>> ANSWER:")

    class Outer(val x: Int) {
        class Nested
        inner class Inner {
            fun getX() = x
        }
    }
    val n = Outer.Nested()
    val i = Outer(42).Inner()
    println("Nested created without outer: $n")
    println("Inner.getX() = ${i.getX()}")

    println("""
    EXPLANATION:
    Nested class (default):
      - Does NOT hold reference to outer
      - Created without outer instance: Outer.Nested()
      - Cannot access outer's members (x is not accessible)
      - Like static inner class in Java

    Inner class (keyword 'inner'):
      - HOLDS reference to outer instance
      - Must be created from outer: Outer(42).Inner()
      - CAN access outer's members (x works)
      - Like non-static inner class in Java

    INTERVIEW TIP: Inner classes cause MEMORY LEAKS if they outlive the outer!
    Example: an inner class stored in a long-lived cache holds the entire Activity.
    RULE: Default to nested (no 'inner'). Only use 'inner' when you need outer access.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 13: Value class equality
// ═════════════════════════════════════════════
fun puzzle13_valueClassEquality() {
    println("═══ PUZZLE 13: Value class — inlined at runtime ═══")
    println("""
    CODE:
    @JvmInline value class Email(val value: String)
    @JvmInline value class Username(val value: String)

    val e = Email("test@test.com")
    val u = Username("test@test.com")
    println(e == e)
    // println(e == u)  ← Does this compile?
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    @JvmInline value class Email(val value: String)
    @JvmInline value class Username(val value: String)

    val e = Email("test@test.com")
    println(e == e)

    println("""
    e == u → COMPILE ERROR! Different types.
    Even though both wrap String with the same value, they're different types.
    This is the WHOLE POINT of value classes — type safety without runtime cost.

    At runtime, Email("x") is just the String "x" (no wrapper object allocated).
    But at compile time, Email and Username are distinct types.

    USE CASES: UserId, Email, Password, OrderId — prevent mixing up String params.
    fun sendEmail(to: Email, from: Email) — can't accidentally swap with Username.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 14: Nothing type
// ═════════════════════════════════════════════
fun puzzle14_nothingType() {
    println("═══ PUZZLE 14: Nothing type — function that never returns ═══")
    println("""
    CODE:
    fun fail(msg: String): Nothing = throw IllegalArgumentException(msg)

    fun getUser(name: String?): String {
        val n = name ?: fail("Name is required")
        return n.uppercase()
    }
    println(getUser("alice"))
    // println(getUser(null))  ← What happens?
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    fun fail(msg: String): Nothing = throw IllegalArgumentException(msg)
    fun getUser(name: String?): String {
        val n = name ?: fail("Name is required")
        return n.uppercase()
    }
    println(getUser("alice"))
    try {
        getUser(null)
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }

    println("""
    EXPLANATION:
    Nothing is the bottom type — subtype of EVERY type.
    A function returning Nothing NEVER returns normally (always throws or loops forever).

    Why it works with ?:
    val n = name ?: fail("...")
    The right side of ?: returns Nothing, which is a subtype of String,
    so the compiler infers n as String (not String?). Smart!

    Without Nothing return type, you'd need:
    val n = name ?: throw IllegalArgumentException("...")
    Nothing lets you extract the throw into a reusable function.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 15: Abstract class vs Interface — when to use which
// ═════════════════════════════════════════════
fun puzzle15_abstractVsInterface() {
    println("═══ PUZZLE 15: Abstract class vs Interface ═══")
    println("""
    QUESTION: Which can have constructors, state, and multiple inheritance?
    """.trimIndent())
    println("\n>>> ANSWER:")

    println("""
    ┌─────────────────────────┬──────────────────┬──────────────────┐
    │ Feature                 │ Abstract Class   │ Interface        │
    ├─────────────────────────┼──────────────────┼──────────────────┤
    │ Constructors            │ ✅ Yes            │ ❌ No             │
    │ State (backing fields)  │ ✅ Yes            │ ❌ No*            │
    │ Multiple inheritance    │ ❌ No (single)    │ ✅ Yes (multiple) │
    │ Default methods         │ ✅ Yes            │ ✅ Yes            │
    │ Abstract methods        │ ✅ Yes            │ ✅ Yes            │
    │ Visibility modifiers    │ All              │ All              │
    │ init blocks             │ ✅ Yes            │ ❌ No             │
    └─────────────────────────┴──────────────────┴──────────────────┘

    *Interface properties have no backing field — only getters.
     interface Foo { val x: Int get() = 42 }  // computed, no field

    RULE OF THUMB:
    - Use interface when: defining a contract / capability (Repository, Clickable)
    - Use abstract class when: sharing state + behavior among related classes
    - Default to interface. Use abstract class only when you NEED constructor/state.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 16: Extension function vs Member — who wins?
// ═════════════════════════════════════════════
fun puzzle16_extensionVsMember() {
    println("═══ PUZZLE 16: Extension vs Member function ═══")
    println("""
    CODE:
    class Dog {
        fun speak() = println("Woof (member)")
    }
    fun Dog.speak() = println("Bark (extension)")
    Dog().speak()
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    class Dog {
        fun speak() = println("Woof (member)")
    }
    fun Dog.speak() = println("Bark (extension)")
    Dog().speak()

    println("""
    EXPLANATION:
    Member function ALWAYS wins over extension function with same signature.
    Output: "Woof (member)"

    The extension is silently shadowed — no compile error, just a warning.

    KEY: Extensions are resolved STATICALLY at compile time.
    They don't actually modify the class. They're syntactic sugar for:
    fun speak(dog: Dog) = println("Bark")

    This also means extensions don't support polymorphism:
    val animal: Animal = Dog()
    animal.someExtension()  // calls Animal.someExtension, NOT Dog.someExtension
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 17: Companion object "inherits"
// ═════════════════════════════════════════════
fun puzzle17_companionInheritance() {
    println("═══ PUZZLE 17: Companion object can implement interface ═══")
    println("""
    CODE:
    interface Factory<T> {
        fun create(): T
    }
    class MyClass private constructor(val name: String) {
        companion object : Factory<MyClass> {
            override fun create() = MyClass("Default")
        }
    }
    val obj = MyClass.create()
    println(obj.name)

    fun <T> buildObject(factory: Factory<T>): T = factory.create()
    val obj2 = buildObject(MyClass)  // passing companion as Factory!
    println(obj2.name)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    interface Factory<T> {
        fun create(): T
    }
    class MyClass private constructor(val name: String) {
        companion object : Factory<MyClass> {
            override fun create() = MyClass("Default")
        }
    }
    val obj = MyClass.create()
    println(obj.name)

    fun <T> buildObject(factory: Factory<T>): T = factory.create()
    val obj2 = buildObject(MyClass)
    println(obj2.name)

    println("""
    EXPLANATION:
    Companion objects can implement interfaces!
    MyClass.Companion is a singleton that implements Factory<MyClass>.
    You can pass it anywhere a Factory is expected: buildObject(MyClass)

    This enables the Factory pattern without separate factory classes.
    Used in real world: Kotlin Serialization's companion serializer.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 18: Object expression captures mutable var
// ═════════════════════════════════════════════
fun puzzle18_objectExpressionCapture() {
    println("═══ PUZZLE 18: Object expression captures mutable var ═══")
    println("""
    CODE:
    var count = 0
    val runnable = object : Runnable {
        override fun run() {
            count++  // ← Can Kotlin capture mutable var?
        }
    }
    runnable.run()
    runnable.run()
    println(count)
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    var count = 0
    val runnable = object : Runnable {
        override fun run() {
            count++
        }
    }
    runnable.run()
    runnable.run()
    println(count)

    println("""
    EXPLANATION:
    Yes! Unlike Java (which requires effectively final variables),
    Kotlin CAN capture and MODIFY mutable vars in lambdas and object expressions.

    Under the hood, Kotlin wraps the var in an IntRef object (boxing).
    Both the outer function and the lambda/object share the same IntRef.

    Java equivalent: final int[] count = {0}; count[0]++;
    Kotlin does this automatically.
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 19: Sealed class — when as statement vs expression
// ═════════════════════════════════════════════
fun puzzle19_sealedWhenCoverage() {
    println("═══ PUZZLE 19: when STATEMENT is not exhaustive ═══")
    println("""
    CODE:
    sealed class State {
        data object Loading : State()
        data class Success(val data: String) : State()
        data class Error(val msg: String) : State()
    }

    // Statement (no assignment):
    fun handleStatement(s: State) {
        when (s) {
            is State.Loading -> println("Loading")
            is State.Success -> println(s.data)
            // Missing Error — COMPILES! No warning!
        }
    }

    // Expression (assigned to val):
    fun handleExpression(s: State): String = when (s) {
        is State.Loading -> "Loading"
        is State.Success -> s.data
        // Missing Error — COMPILE ERROR!
    }
    """.trimIndent())
    println("\n>>> ANSWER:")

    println("""
    when as STATEMENT (not returning a value): NOT exhaustive, no error.
    when as EXPRESSION (returning a value): MUST be exhaustive, compile error.

    TRAP: Using when as a statement with sealed class gives NO safety guarantee!
    Adding a new sealed subclass won't break compilation.

    FIX: Always use when as an expression to get exhaustiveness checking:
    val result = when (state) { ... }
    Or assign to Unit:
    val _: Unit = when (state) { ... }  // force exhaustive check
    """.trimIndent())
    println()
}

// ═════════════════════════════════════════════
// PUZZLE 20: equals/hashCode contract
// ═════════════════════════════════════════════
fun puzzle20_equalsHashCodeContract() {
    println("═══ PUZZLE 20: Broken equals/hashCode in HashMap ═══")
    println("""
    CODE:
    class Key(val value: Int) {
        override fun equals(other: Any?) = other is Key && other.value == value
        // hashCode NOT overridden!
    }
    val map = hashMapOf(Key(1) to "one", Key(2) to "two")
    println(map[Key(1)])
    println(Key(1) == Key(1))
    """.trimIndent())
    println("\n>>> ACTUAL OUTPUT:")

    class Key(val value: Int) {
        override fun equals(other: Any?) = other is Key && other.value == value
        // hashCode NOT overridden!
    }
    val map = hashMapOf(Key(1) to "one", Key(2) to "two")
    println(map[Key(1)])
    println(Key(1) == Key(1))

    println("""
    EXPLANATION:
    map[Key(1)] returns NULL even though Key(1) == Key(1) is true!

    Why? HashMap uses hashCode() first to find the bucket, THEN equals() to match.
    Default hashCode() (from Any) uses object identity — each Key(1) has different hash.
    So HashMap looks in the WRONG bucket and never finds the entry.

    THE CONTRACT:
    If a == b, then a.hashCode() MUST equal b.hashCode().
    If you override equals(), you MUST override hashCode().

    This is why data classes are recommended — they generate both correctly.
    """.trimIndent())
    println()

    println("✅ OOP Interview Puzzles Complete!")
}

// ═══════════════════════════════════════════
// Top-level declarations for puzzles
// (objects, enums, companion objects can't be local)
// ═══════════════════════════════════════════

// Puzzle 5 — companion object
class CompanionDemo {
    init { println("Instance init") }
    companion object {
        init { println("Companion init") }
        val TAG = "MyClass".also { println("TAG created") }
    }
}

// Puzzle 6 — named object singleton
object CounterSingleton {
    var count = 0
    fun increment() = ++count
}

// Puzzle 11 — enum class
enum class PuzzleColor { RED, GREEN, BLUE }
