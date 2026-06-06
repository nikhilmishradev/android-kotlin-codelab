package com.androidtechguru.codelab.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

/**
 * NAVIGATION — NavHost & Nav Graph Setup
 *
 * Key concepts:
 * 1. NavHost — container for navigation destinations
 * 2. composable() — register a destination
 * 3. navArgument — declare arguments with types and defaults
 * 4. Nested navigation — feature-scoped nav graphs
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // ── Simple destination ──
        composable(route = Screen.Home.route) {
            // HomeScreen(
            //     onArticleClick = { id ->
            //         navController.navigate(Screen.ArticleDetail.createRoute(id))
            //     },
            //     onSettingsClick = { navController.navigate(Screen.Settings.route) }
            // )
        }

        // ── Destination with required argument ──
        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument(NavArgs.ARTICLE_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString(NavArgs.ARTICLE_ID) ?: return@composable
            // ArticleDetailScreen(articleId = articleId)

            // INTERVIEW TIP: hiltViewModel() auto-receives SavedStateHandle
            // with nav arguments. No manual extraction needed in ViewModel.
        }

        // ── Destination with optional argument ──
        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument(NavArgs.QUERY) {
                    type = NavType.StringType
                    defaultValue = ""  // makes it optional
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString(NavArgs.QUERY) ?: ""
            // SearchScreen(initialQuery = query)
        }

        // ── Nested Navigation Graph ──
        // Groups related destinations — useful for feature modules
        // ViewModel scoped to nested graph is shared across its destinations
        navigation(
            startDestination = "settings_main",
            route = Screen.Settings.route
        ) {
            composable("settings_main") {
                // SettingsMainScreen(
                //     onProfileClick = { navController.navigate("settings_profile") }
                // )
            }
            composable("settings_profile") {
                // SettingsProfileScreen()
                // viewModel scoped to "settings" nav graph:
                // val sharedVM = hiltViewModel<SettingsViewModel>(
                //     navController.getBackStackEntry(Screen.Settings.route)
                // )
            }
        }
    }
}

// ── Navigation helpers ──
// Common navigation patterns

fun NavHostController.navigateToArticle(articleId: String) {
    navigate(Screen.ArticleDetail.createRoute(articleId)) {
        // Avoid multiple copies of the same destination
        launchSingleTop = true
    }
}

fun NavHostController.navigateToHomeClearing() {
    navigate(Screen.Home.route) {
        // Pop everything up to and including Home, then navigate to Home
        // Results in a clean back stack with just Home
        popUpTo(Screen.Home.route) { inclusive = true }
        launchSingleTop = true
    }
}

// INTERVIEW TIP — Back Stack Management:
//
// popUpTo("route") — pops destinations until reaching "route"
//   inclusive = true  → also pops "route" itself
//   inclusive = false → stops before "route" (keeps it)
//
// launchSingleTop = true — prevents duplicate destination on top
//
// Example: Login → Home (clear login from stack)
//   navigate("home") { popUpTo("login") { inclusive = true } }
