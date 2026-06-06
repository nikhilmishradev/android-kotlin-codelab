# Kotlin Cheatsheet

Quick reference for Kotlin syntax and features.

---

## Variables & Types

```kotlin
val immutable: String = "can't reassign"      // val = read-only
var mutable: Int = 42                          // var = reassignable
const val COMPILE_TIME = "compile-time const"  // must be top-level or in companion
lateinit var lateInit: String                  // initialized later (non-null, var only)
```

## Null Safety

```kotlin
var nullable: String? = null        // ? allows null
nullable?.length                    // safe call — returns null if nullable is null
nullable ?: "default"               // elvis operator — fallback if null
nullable!!.length                   // non-null assertion — throws NPE if null
nullable?.let { println(it) }      // execute block only if non-null
```

## Control Flow

```kotlin
// if is an expression
val max = if (a > b) a else b

// when is an expression (exhaustive with sealed/enum)
val result = when (x) {
    1 -> "one"
    in 2..10 -> "2 to 10"
    is String -> "it's a string"
    else -> "other"
}
```

## Functions

```kotlin
fun greet(name: String, greeting: String = "Hello"): String = "$greeting, $name!"

// Extension function
fun String.addExclamation(): String = "$this!"

// Higher-order function
fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)

// Lambda
val sum = { a: Int, b: Int -> a + b }
```

## Scope Functions

| Function | Context Object | Return Value | Use Case |
|----------|---------------|--------------|----------|
| `let` | `it` | Lambda result | Null check + transform |
| `run` | `this` | Lambda result | Object config + compute |
| `with` | `this` | Lambda result | Grouping calls on object |
| `apply` | `this` | Context object | Object configuration |
| `also` | `it` | Context object | Side effects (logging) |

## Collections

```kotlin
val list = listOf(1, 2, 3)
val filtered = list.filter { it > 1 }         // [2, 3]
val mapped = list.map { it * 2 }              // [2, 4, 6]
val grouped = list.groupBy { it % 2 }         // {1=[1,3], 0=[2]}
val folded = list.fold(0) { acc, i -> acc + i } // 6
```

## Coroutines

```kotlin
// Builders
launch { }               // fire-and-forget, returns Job
async { }                // returns Deferred<T>, call .await()
withContext(Dispatchers.IO) { }  // switch context, suspend until done

// Flow
flow { emit(1); emit(2) }
    .map { it * 2 }
    .filter { it > 2 }
    .collect { println(it) }

// StateFlow vs SharedFlow
val state = MutableStateFlow(initialValue)  // always has value, replays latest
val shared = MutableSharedFlow<Event>()     // no initial value, configurable replay
```

## OOP

```kotlin
data class User(val name: String, val age: Int)            // equals, hashCode, copy, toString
sealed class Result<out T> {                                // exhaustive when
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}
enum class Direction { NORTH, SOUTH, EAST, WEST }
object Singleton { val x = 1 }                             // singleton
value class Email(val value: String)                        // inline/value class
```

## Generics

```kotlin
class Box<T>(val value: T)                    // invariant
class Producer<out T>(val value: T)           // covariant (producer)
class Consumer<in T> { fun consume(t: T) {} } // contravariant (consumer)
inline fun <reified T> isType(value: Any): Boolean = value is T
```

## Delegation

```kotlin
val lazyValue: String by lazy { "computed once" }
var observed: String by Delegates.observable("initial") { _, old, new -> println("$old -> $new") }
class Delegated(list: List<String>) : List<String> by list  // class delegation
```

---

> Add your own notes and examples as you practice each topic.
