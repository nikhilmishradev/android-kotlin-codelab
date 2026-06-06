# Project Structure

## Overview

**Android Kotlin Codelab** — Interview prep project covering Kotlin core concepts and Android development from basics to advanced.

- **Package:** `com.androidtechguru.codelab`
- **Min SDK:** 26 | **Target SDK:** 35
- **Tech Stack:** Kotlin 2.1, Compose BOM, Hilt, Retrofit, Room, Navigation Compose, JUnit5, Turbine

---

## Directory Tree

```
android-kotlin-codelab/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/androidtechguru/codelab/
│       │   │   ├── CodelabApp.kt                    — @HiltAndroidApp Application class
│       │   │   ├── MainActivity.kt                  — @AndroidEntryPoint Compose entry point
│       │   │   ├── ui/
│       │   │   │   └── theme/
│       │   │   │       └── Theme.kt                 — Material3 dynamic color theme
│       │   │   │
│       │   │   ├── kotlin/                           ── KOTLIN CORE CONCEPTS ──
│       │   │   │   ├── basics/
│       │   │   │   │   └── Topics.kt                — Variables, null safety, type casting, control flow
│       │   │   │   ├── oops/
│       │   │   │   │   └── Topics.kt                — Classes, sealed, enum, data, inheritance, interfaces
│       │   │   │   ├── functions/
│       │   │   │   │   └── Topics.kt                — Lambdas, extensions, scope functions, inline, operators
│       │   │   │   ├── collections/
│       │   │   │   │   └── Topics.kt                — List, Set, Map, sequences, transformations, aggregation
│       │   │   │   ├── coroutines/
│       │   │   │   │   └── Topics.kt                — launch, async, Flow, channels, structured concurrency
│       │   │   │   ├── generics/
│       │   │   │   │   └── Topics.kt                — Variance (in/out), reified, type constraints, star projection
│       │   │   │   ├── delegation/
│       │   │   │   │   └── Topics.kt                — lazy, observable, custom delegates, class delegation
│       │   │   │   └── dsl/
│       │   │   │       └── Topics.kt                — Type-safe builders, lambda with receiver, @DslMarker
│       │   │   │
│       │   │   └── android/                          ── ANDROID CONCEPTS ──
│       │   │       ├── lifecycle/
│       │   │       │   └── Topics.kt                — Activity/Fragment lifecycle, ViewModel, LiveData
│       │   │       ├── compose/
│       │   │       │   └── Topics.kt                — State, side effects, lazy lists, animations, stability
│       │   │       ├── architecture/
│       │   │       │   └── Topics.kt                — MVVM, Clean Architecture, UDF, MVI, Repository
│       │   │       ├── di/
│       │   │       │   └── Topics.kt                — Hilt modules, scopes, qualifiers, assisted inject
│       │   │       ├── networking/
│       │   │       │   └── Topics.kt                — Retrofit, OkHttp, interceptors, error handling
│       │   │       ├── storage/
│       │   │       │   └── Topics.kt                — Room, DataStore, migrations, relations, type converters
│       │   │       ├── concurrency/
│       │   │       │   └── Topics.kt                — WorkManager, services, coroutine scopes, thread safety
│       │   │       ├── testing/
│       │   │       │   └── Topics.kt                — JUnit5, Turbine, MockK, Compose UI tests
│       │   │       ├── navigation/
│       │   │       │   └── Topics.kt                — NavHost, type-safe routes, deep links, back stack
│       │   │       └── permissions/
│       │   │           └── Topics.kt                — Runtime permissions, Activity Result API
│       │   │
│       │   └── res/
│       │       └── values/
│       │           ├── strings.xml
│       │           └── themes.xml
│       │
│       └── test/
│           └── java/com/androidtechguru/codelab/
│               └── ExampleUnitTest.kt               — JUnit5 sanity check
│
├── docs/                                             ── DOCUMENTATION ──
│   ├── project-structure.md                         — This file
│   ├── kotlin/
│   │   ├── kotlin-cheatsheet.md                     — Quick reference for Kotlin syntax & features
│   │   └── kotlin-interview-questions.md            — Common Kotlin interview Q&A
│   ├── android/
│   │   ├── android-cheatsheet.md                    — Quick reference for Android components & APIs
│   │   └── android-interview-questions.md           — Common Android interview Q&A
│   ├── compose/
│   │   ├── compose-cheatsheet.md                    — Compose patterns & best practices reference
│   │   └── compose-interview-questions.md           — Compose-specific interview Q&A
│   └── references/
│       └── useful-links.md                          — Curated links to official docs & articles
│
├── build.gradle.kts                                 — Root build script (plugin aliases)
├── settings.gradle.kts                              — Project settings & module includes
├── gradle.properties                                — Gradle & Android build properties
├── gradle/
│   ├── libs.versions.toml                           — Version catalog (all dependencies)
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew                                          — Gradle wrapper (Unix)
├── gradlew.bat                                      — Gradle wrapper (Windows)
└── .gitignore
```

---

## Package Mapping

| Package | Category | Key Topics |
|---------|----------|------------|
| `kotlin.basics` | Kotlin Core | Variables, null safety, type casting, control flow |
| `kotlin.oops` | Kotlin Core | Classes, sealed/enum/data, inheritance, interfaces |
| `kotlin.functions` | Kotlin Core | Lambdas, extensions, scope functions, inline |
| `kotlin.collections` | Kotlin Core | List/Set/Map, sequences, transformations |
| `kotlin.coroutines` | Kotlin Core | Builders, Flow, channels, structured concurrency |
| `kotlin.generics` | Kotlin Core | Variance, reified, type constraints |
| `kotlin.delegation` | Kotlin Core | lazy, observable, custom delegates |
| `kotlin.dsl` | Kotlin Core | Type-safe builders, @DslMarker |
| `android.lifecycle` | Android | Activity/Fragment lifecycle, ViewModel, LiveData |
| `android.compose` | Android | State, side effects, lazy lists, animations |
| `android.architecture` | Android | MVVM, Clean Architecture, UDF, MVI |
| `android.di` | Android | Hilt modules, scopes, qualifiers |
| `android.networking` | Android | Retrofit, OkHttp, interceptors |
| `android.storage` | Android | Room, DataStore, migrations |
| `android.concurrency` | Android | WorkManager, services, coroutine scopes |
| `android.testing` | Android | JUnit5, Turbine, MockK, Compose UI tests |
| `android.navigation` | Android | NavHost, type-safe routes, deep links |
| `android.permissions` | Android | Runtime permissions, Activity Result API |
