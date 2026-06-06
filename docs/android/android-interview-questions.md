# Android Interview Questions

Common interview questions organized by topic. Practice answering these.

---

## Activity & Fragment Lifecycle

1. Explain the complete Activity lifecycle with all callbacks.
2. What happens to an Activity when the user rotates the device?
3. What is the difference between `onStop()` and `onDestroy()`?
4. Explain the Fragment lifecycle. How does it differ from Activity?
5. What is `onSaveInstanceState`? When is it called?
6. What is process death? How do you survive it?
7. What is `setRetainInstance(true)` and why is it deprecated?

## ViewModel & LiveData

8. What is ViewModel? How does it survive configuration changes?
9. What is the lifecycle of a ViewModel?
10. What is `SavedStateHandle`? When do you need it?
11. What is LiveData? How is it lifecycle-aware?
12. What is the difference between `LiveData` and `StateFlow`?
13. What is `MediatorLiveData`? Give a use case.
14. Can a ViewModel reference an Activity/Context? Why not?

## Architecture

15. Explain MVVM pattern. What is the role of each layer?
16. What is Clean Architecture? Explain the 3 layers.
17. What is the Repository pattern? Why is it useful?
18. What is Unidirectional Data Flow (UDF)?
19. Explain MVI pattern. How does it differ from MVVM?
20. What are Use Cases / Interactors? Why separate them from ViewModel?
21. What is the difference between DTOs and domain models?

## Dependency Injection (Hilt)

22. What is dependency injection? Why use it?
23. Explain Hilt components and their lifecycles.
24. What is the difference between `@Provides` and `@Binds`?
25. What are Hilt scopes? Explain `@Singleton`, `@ViewModelScoped`, `@ActivityScoped`.
26. What are qualifiers (`@Named`, custom)? When do you need them?
27. What is `@AssistedInject`? Give a use case.
28. How do you test with Hilt?

## Networking

29. What is Retrofit? How does it work under the hood?
30. What is the difference between application and network interceptors in OkHttp?
31. How do you handle token refresh / authentication?
32. How do you handle network errors gracefully?
33. What is certificate pinning? Why is it important?

## Storage

34. What is Room? What are its main components (`@Entity`, `@Dao`, `@Database`)?
35. How do Room migrations work? What is auto-migration?
36. What is the difference between `@Embedded` and `@Relation`?
37. What is DataStore? How is it better than SharedPreferences?
38. Explain scoped storage (Android 10+). What changed?

## Concurrency

39. What is `viewModelScope`? What happens when ViewModel is cleared?
40. What is `lifecycleScope`? What is `repeatOnLifecycle`?
41. What is main safety? How do you ensure it?
42. What is WorkManager? When should you use it vs Services?
43. What is the difference between Foreground Service and Bound Service?

## Navigation

44. What is the Navigation Component? What are its main parts?
45. How do you pass arguments between destinations?
46. What is `popUpTo` with `inclusive = true`?
47. How do you handle deep links?
48. How do you scope a ViewModel to a navigation graph?

## Permissions

49. What is the difference between normal and dangerous permissions?
50. How do you request runtime permissions using the Activity Result API?
51. What is `shouldShowRequestPermissionRationale`? When does it return true/false?

## Performance & Optimization

52. What is ANR? What causes it and how do you prevent it?
53. What are memory leaks in Android? Common causes?
54. What is the difference between `Parcelable` and `Serializable`?
55. What is ProGuard/R8? What does it do?

---

> Write your answers below each question as you prepare. Include code examples where relevant.
