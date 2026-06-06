package com.androidtechguru.codelab.kotlin.oops

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║    KOTLIN OOP — Codelab                  ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. CLASSES & CONSTRUCTORS
    // ─────────────────────────────────────────
    println("=== 1. Classes & Constructors ===")

    // Primary constructor — concise, in class header
    class Person(val name: String, var age: Int) {
        // init block runs after primary constructor
        init {
            println("  init block: Person created — $name, $age")
        }

        // Secondary constructor — must delegate to primary
        constructor(name: String) : this(name, 0) {
            println("  secondary constructor: age defaulted to 0")
        }
    }

    val p1 = Person("Alice", 25)
    val p2 = Person("Baby")
    println("p1: ${p1.name}, ${p1.age}")
    println("p2: ${p2.name}, ${p2.age}")

    // ─────────────────────────────────────────
    // 2. PROPERTIES — Getters, Setters, Backing Field
    // ─────────────────────────────────────────
    println("\n=== 2. Properties ===")

    class Temperature(celsius: Double) {
        var celsius: Double = celsius
            set(value) {
                require(value >= -273.15) { "Below absolute zero!" }
                field = value  // 'field' is the backing field
            }

        // Computed property (no backing field, calculated on access)
        val fahrenheit: Double
            get() = celsius * 9 / 5 + 32

        // Backing property pattern (common in ViewModel: _mutable exposed as immutable)
        private var _history = mutableListOf(celsius)
        val history: List<Double>
            get() = _history  // expose as read-only List
    }

    val temp = Temperature(100.0)
    println("${temp.celsius}°C = ${temp.fahrenheit}°F")
    temp.celsius = 0.0
    println("${temp.celsius}°C = ${temp.fahrenheit}°F")
    println("History: ${temp.history}")

    // ─────────────────────────────────────────
    // 3. DATA CLASSES
    // ─────────────────────────────────────────
    println("\n=== 3. Data Classes ===")

    // Compiler generates: equals(), hashCode(), toString(), copy(), componentN()
    data class User(val id: Int, val name: String, val email: String)

    val user1 = User(1, "Alice", "alice@test.com")
    val user2 = User(1, "Alice", "alice@test.com")
    val user3 = user1.copy(name = "Bob")  // copy with modification

    println("user1: $user1")                        // auto toString()
    println("user1 == user2: ${user1 == user2}")    // true — structural equality via equals()
    println("user1 === user2: ${user1 === user2}")  // false — different objects
    println("copy: $user3")

    // Destructuring
    val (id, name, email) = user1
    println("Destructured: id=$id, name=$name, email=$email")

    // INTERVIEW TIP: Properties in body are NOT included in equals/hashCode/toString
    data class UserWithExtra(val id: Int, val name: String) {
        var loginCount: Int = 0  // NOT in equals/hashCode!
    }
    val u1 = UserWithExtra(1, "Alice").apply { loginCount = 5 }
    val u2 = UserWithExtra(1, "Alice").apply { loginCount = 10 }
    println("loginCount differs but equals: ${u1 == u2}")  // true!

    // ─────────────────────────────────────────
    // 4. SEALED CLASSES & INTERFACES
    // ─────────────────────────────────────────
    println("\n=== 4. Sealed Classes & Interfaces ===")

    // All subclasses must be in the same file (compile-time exhaustive)
    // Perfect for representing restricted hierarchies like UI state
    fun handleResult(result: NetworkResult<String>): String = when (result) {
        is NetworkResult.Success -> "Data: ${result.data}"
        is NetworkResult.Error -> "Error ${result.code}: ${result.message}"
        is NetworkResult.Loading -> "Loading..."
        // No 'else' needed — compiler knows all cases!
    }

    println(handleResult(NetworkResult.Success("Users loaded")))
    println(handleResult(NetworkResult.Error(404, "Not found")))
    println(handleResult(NetworkResult.Loading))

    // Sealed interface — allows implementing multiple sealed hierarchies
    // (sealed class can only extend one class)

    // ─────────────────────────────────────────
    // 5. ENUM CLASSES
    // ─────────────────────────────────────────
    println("\n=== 5. Enum Classes ===")

    println("All directions: ${Direction.entries}")  // entries (Kotlin 1.9+, replaces values())
    println("NORTH opposite: ${Direction.NORTH.opposite()}")
    println("valueOf: ${Direction.valueOf("SOUTH")}")

    // Enum with properties
    println("HTTP 200: ${HttpStatus.OK.code} — ${HttpStatus.OK.description}")
    println("HTTP 404: ${HttpStatus.NOT_FOUND.code} — ${HttpStatus.NOT_FOUND.description}")

    // ─────────────────────────────────────────
    // 6. OBJECT DECLARATIONS
    // ─────────────────────────────────────────
    println("\n=== 6. Object Declarations ===")

    // Singleton
    println("Singleton — Logger.log:")
    Logger.log("App started")

    // Companion object — like static methods/properties in Java
    println("\nCompanion object — factory pattern:")
    val jsonUser = User.fromJson("""{"id": 1, "name": "Alice"}""")
    println("Created via companion: $jsonUser")
    println("Max name length: ${User.MAX_NAME_LENGTH}")

    // Object expression (anonymous object) — like anonymous inner class
    println("\nObject expression:")
    val comparator = object : Comparator<String> {
        override fun compare(a: String, b: String): Int = a.length - b.length
    }
    val sorted = listOf("banana", "fig", "apple").sortedWith(comparator)
    println("Sorted by length: $sorted")

    // ─────────────────────────────────────────
    // 7. INHERITANCE
    // ─────────────────────────────────────────
    println("\n=== 7. Inheritance ===")

    // Classes are final by default — must use 'open' to allow inheritance
    val dog = Dog("Buddy")
    println("${dog.name} says: ${dog.sound()}")
    println("${dog.name} type: ${dog.type}")
    dog.describe()

    val cat = Cat("Whiskers")
    println("${cat.name} says: ${cat.sound()}")
    cat.describe()

    // Abstract classes
    val circle = Circle(5.0)
    println("Circle area: ${circle.area()}, perimeter: ${circle.perimeter()}")

    // ─────────────────────────────────────────
    // 8. INTERFACES
    // ─────────────────────────────────────────
    println("\n=== 8. Interfaces ===")

    // Interfaces can have default implementations and property declarations
    val repo = UserRepository()
    println("findById: ${repo.findById(1)}")
    println("isValid: ${repo.isValid(1)}")  // uses default implementation
    println("source: ${repo.source}")

    // Multiple interface implementation
    val smartDevice = SmartPhone()
    smartDevice.call()
    smartDevice.browse()

    // ─────────────────────────────────────────
    // 9. VISIBILITY MODIFIERS
    // ─────────────────────────────────────────
    println("\n=== 9. Visibility Modifiers ===")
    println("""
        public    — visible everywhere (DEFAULT in Kotlin, unlike Java's package-private)
        private   — visible inside the class/file only
        protected — visible in class + subclasses (NOT same package!)
        internal  — visible in the same module (Gradle module)

        INTERVIEW TIP: Kotlin's 'internal' maps to 'public' in Java bytecode
        with name mangling to prevent accidental access from Java.
    """.trimIndent())

    // ─────────────────────────────────────────
    // 10. NESTED vs INNER CLASSES
    // ─────────────────────────────────────────
    println("\n=== 10. Nested vs Inner Classes ===")

    // Nested class (default) — does NOT hold reference to outer class
    val nested = Outer.Nested()
    println("Nested: ${nested.describe()}")

    // Inner class — holds reference to outer, can access outer members
    val outer = Outer("OuterData")
    val inner = outer.Inner()
    println("Inner: ${inner.describe()}")

    // INTERVIEW TIP: Prefer nested (no 'inner') to avoid memory leaks!
    // Inner classes hold a reference to the outer instance.

    // ─────────────────────────────────────────
    // 11. VALUE CLASSES (Inline Classes)
    // ─────────────────────────────────────────
    println("\n=== 11. Value Classes ===")

    // Wraps a single value — no runtime overhead (inlined by compiler)
    // Provides type safety without allocation cost
    val email1 = Email("user@test.com")
    val userId = UserId(42)

    println("Email: ${email1.value}")
    println("UserId: ${userId.value}")
    // sendEmail(userId) // COMPILE ERROR — type safety without runtime cost!
    sendEmail(email1)

    // ─────────────────────────────────────────
    // 12. TYPE HIERARCHY
    // ─────────────────────────────────────────
    println("\n=== 12. Type Hierarchy ===")
    println("""
        Any     — root of all types (like Object in Java). Has equals(), hashCode(), toString()
        Unit    — equivalent to void, but is an actual type/object. Functions return Unit by default.
        Nothing — subtype of ALL types. Used for functions that never return (throw, infinite loop).

        Any? is the TRUE root (nullable root) — everything is a subtype of Any?
        Nothing is at the bottom — subtype of everything
    """.trimIndent())

    // Unit example
    val unitResult: Unit = println("This returns Unit")
    println("Unit value: $unitResult")

    // Nothing example — function that never returns
    // fun fail(msg: String): Nothing = throw IllegalArgumentException(msg)
    // val result: String = nullableStr ?: fail("was null")  // Nothing lets this compile!

    println("\n✅ Kotlin OOP Codelab Complete!")
}

// ═══════════════════════════════════════════
// Supporting types (must be outside main)
// ═══════════════════════════════════════════

// --- Sealed class example ---
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

// --- Enum examples ---
enum class Direction {
    NORTH, SOUTH, EAST, WEST;

    fun opposite(): Direction = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
    }
}

enum class HttpStatus(val code: Int, val description: String) {
    OK(200, "Success"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");
}

// --- Singleton ---
object Logger {
    private val logs = mutableListOf<String>()

    fun log(message: String) {
        logs.add(message)
        println("  [LOG] $message")
    }
}

// --- Companion object with factory ---
private data class User(val id: Int, val name: String, val email: String = "") {
    companion object {
        const val MAX_NAME_LENGTH = 50

        fun fromJson(json: String): User {
            // Simplified parsing for demo
            return User(1, "Alice", "alice@test.com")
        }
    }
}

// --- Inheritance ---
open class Animal(val name: String) {
    open val type: String = "Animal"
    open fun sound(): String = "..."
    open fun describe() {
        println("  I'm a $type named $name")
    }
}

class Dog(name: String) : Animal(name) {
    override val type = "Dog"
    override fun sound() = "Woof!"
}

class Cat(name: String) : Animal(name) {
    override val type = "Cat"
    override fun sound() = "Meow!"
}

// --- Abstract class ---
abstract class Shape {
    abstract fun area(): Double
    abstract fun perimeter(): Double

    // Concrete method in abstract class
    fun describe(): String = "Area=${area()}, Perimeter=${perimeter()}"
}

class Circle(private val radius: Double) : Shape() {
    override fun area() = Math.PI * radius * radius
    override fun perimeter() = 2 * Math.PI * radius
}

// --- Interfaces ---
interface Repository<T> {
    val source: String  // abstract property

    fun findById(id: Int): T?

    // Default implementation
    fun isValid(id: Int): Boolean = id > 0
}

interface Callable {
    fun call()
}

interface Browsable {
    fun browse()
}

class UserRepository : Repository<String> {
    override val source = "Database"
    override fun findById(id: Int): String? = if (id > 0) "User#$id" else null
}

class SmartPhone : Callable, Browsable {
    override fun call() = println("  Calling...")
    override fun browse() = println("  Browsing...")
}

// --- Nested vs Inner ---
class Outer(private val data: String = "default") {
    class Nested {
        // Cannot access 'data' — no reference to outer
        fun describe() = "I'm nested (no access to outer)"
    }

    inner class Inner {
        // Can access outer's members
        fun describe() = "I'm inner, outer data: $data"
    }
}

// --- Value classes ---
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email" }
    }
}

@JvmInline
value class UserId(val value: Int)

fun sendEmail(email: Email) {
    println("Sending email to: ${email.value}")
}
