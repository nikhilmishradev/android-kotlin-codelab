package com.androidtechguru.codelab.android.navigation

/**
 * NAVIGATION — Type-Safe Routes
 *
 * Key concepts:
 * 1. Sealed class/object for route definitions
 * 2. Arguments encoded in route pattern
 * 3. Helper to build route with actual argument values
 */

// ── Type-safe route definitions ──
sealed class Screen(val route: String) {
    // Simple route — no arguments
    data object Home : Screen("home")
    data object Settings : Screen("settings")

    // Route with required argument
    data object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String): String = "article/$articleId"
    }

    // Route with optional argument
    data object Search : Screen("search?query={query}") {
        fun createRoute(query: String = ""): String = "search?query=$query"
    }

    // Route with multiple arguments
    data object UserProfile : Screen("user/{userId}?tab={tab}") {
        fun createRoute(userId: String, tab: String = "posts"): String =
            "user/$userId?tab=$tab"
    }
}

// ── Navigation argument keys ──
object NavArgs {
    const val ARTICLE_ID = "articleId"
    const val QUERY = "query"
    const val USER_ID = "userId"
    const val TAB = "tab"
}

// INTERVIEW TIP: Kotlin Serialization-based type-safe navigation
// (Navigation 2.8+) replaces string routes with actual objects:
//
//   @Serializable data object Home : Route
//   @Serializable data class Detail(val id: String) : Route
//
// This is the modern approach — but string routes are still widely used.
