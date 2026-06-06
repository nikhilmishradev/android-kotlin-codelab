# Kotlin Interview Questions

Common interview questions organized by topic. Practice answering these.

---

## Basics & Null Safety

1. What is the difference between `val` and `var`?
2. What is `const val` and where can it be used?
3. Explain `lateinit` vs `lazy`. When would you use each?
4. What are the null safety operators (`?.`, `?:`, `!!`) and when do you use them?
5. What is smart casting? How does Kotlin perform it?
6. What is the difference between `==` and `===` in Kotlin?
7. What are destructuring declarations? Give an example.
8. What is the difference between `Unit`, `Nothing`, and `Any`?

## OOP

9. What are data classes? What methods are auto-generated?
10. What is the `copy()` function in data classes and why is it useful?
11. Explain sealed classes vs enum classes. When to use which?
12. What is the difference between `object` declaration and `companion object`?
13. What are value/inline classes? What problem do they solve?
14. What is the difference between `abstract class` and `interface` in Kotlin?
15. Explain visibility modifiers: `public`, `private`, `protected`, `internal`.
16. What are nested classes vs inner classes?

## Functions

17. What are higher-order functions? Give an example.
18. Explain the 5 scope functions (`let`, `run`, `with`, `apply`, `also`) with use cases.
19. What are extension functions? Can they access private members?
20. What is the difference between `inline`, `noinline`, and `crossinline`?
21. What are infix functions? Give an example.
22. What is a lambda with receiver (`T.() -> Unit`)? Where is it used?
23. What is operator overloading? Give an example.
24. What are local functions and when are they useful?

## Collections

25. What is the difference between `List` and `MutableList`?
26. Explain `map`, `flatMap`, `filter`, `reduce`, `fold` with examples.
27. What is the difference between `Sequence` and `Iterable`? When to use which?
28. What is `groupBy` and `associate`? Give examples.
29. What is `partition` and how is it different from `filter`?

## Coroutines

30. What is a coroutine? How is it different from a thread?
31. Explain `launch` vs `async`. When to use each?
32. What are Dispatchers? Explain `Main`, `IO`, `Default`, `Unconfined`.
33. What is structured concurrency? Why does it matter?
34. Explain `Job` vs `SupervisorJob`. When to use `SupervisorJob`?
35. How does exception handling work in coroutines? (`try-catch` vs `CoroutineExceptionHandler`)
36. What is `supervisorScope` vs `coroutineScope`?
37. What is Flow? How is it different from LiveData?
38. Explain `StateFlow` vs `SharedFlow`. When to use which?
39. What are cold streams vs hot streams?
40. What is `collectLatest` and when would you use it?
41. Explain coroutine cancellation. What is cooperative cancellation?
42. What is `withContext` and how does it differ from `launch`?

## Generics

43. What is variance? Explain `in`, `out`, and invariance.
44. What is a reified type parameter? Why does it require `inline`?
45. What is star projection (`<*>`)?
46. What is type erasure? How does Kotlin handle it?

## Delegation

47. What is property delegation? Explain `by lazy`.
48. What is class delegation using `by`? How is it different from inheritance?
49. What is `Delegates.observable` and `Delegates.vetoable`?

---

> Write your answers below each question as you prepare. Focus on explaining with examples.
