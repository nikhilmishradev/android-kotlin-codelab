# Jetpack Compose Interview Questions

Compose-specific interview questions. Practice answering these.

---

## Fundamentals

1. What is Jetpack Compose? How is it different from the View system?
2. What is a `@Composable` function? What makes it special?
3. What is composition? What is recomposition?
4. What triggers recomposition? How does Compose know what to recompose?
5. What is the Compose compiler plugin? What does it do?

## State

6. What is `remember`? What happens without it?
7. What is the difference between `remember` and `rememberSaveable`?
8. What is state hoisting? Why is it important?
9. What is `mutableStateOf`? How does it trigger recomposition?
10. What is `derivedStateOf`? When should you use it?
11. What is `snapshotFlow`? Give a use case.
12. How do you collect a `StateFlow` in Compose? Why use `collectAsStateWithLifecycle`?

## Side Effects

13. What is `LaunchedEffect`? When is it cancelled?
14. What is `DisposableEffect`? Give a real use case.
15. What is `SideEffect`? How is it different from `LaunchedEffect`?
16. What is `rememberCoroutineScope`? When to use it vs `LaunchedEffect`?
17. What is `produceState`? Give an example.
18. What is `rememberUpdatedState`? Why is it needed inside long-lived effects?

## Performance & Stability

19. What is recomposition skipping? When does Compose skip a composable?
20. What is `@Stable`? What is `@Immutable`? What's the difference?
21. Why does modifier order matter? Give an example.
22. What is `Modifier.Node`? How is it different from `Modifier.composed`?
23. How do you optimize `LazyColumn` performance?
24. What is the `key` parameter in `items()`? Why is it important?
25. What is `contentType` in lazy lists? How does it help?

## Layouts & Theming

26. What is `Scaffold`? What are its slots?
27. What is `CompositionLocal`? When should you use it?
28. What is the difference between `staticCompositionLocalOf` and `compositionLocalOf`?
29. How do you create a custom theme in Compose?
30. What is `MaterialTheme`? How do you access its values?

## Navigation

31. How does Navigation Compose work? What is `NavHost`?
32. How do you pass arguments between composable destinations?
33. How do you handle back stack in Compose Navigation?
34. How do you scope a ViewModel to a nav graph?

## Interop

35. How do you use Compose inside an XML-based Activity/Fragment?
36. How do you use an Android View inside Compose? (`AndroidView`)
37. What are the challenges of gradual migration to Compose?

## Testing

38. How do you test composables? What is `ComposeTestRule`?
39. What is `onNodeWithText`, `performClick`, `assertIsDisplayed`?
40. How do you test state changes in composables?

---

> Write your answers below each question. Draw diagrams for lifecycle/recomposition questions.
