package com.serdigital.pataditas.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.serdigital.pataditas.ui.screen.auth.ProfileScreen
import com.serdigital.pataditas.ui.screen.history.HistoryScreen
import com.serdigital.pataditas.ui.screen.history.SessionDetailScreen
import com.serdigital.pataditas.ui.screen.home.HomeScreen
import com.serdigital.pataditas.ui.screen.notes.NoteDetailScreen
import com.serdigital.pataditas.ui.screen.notes.NotesScreen
import com.serdigital.pataditas.ui.screen.stats.StatsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) +
                    slideInHorizontally(animationSpec = androidx.compose.animation.core.tween(220)) { it / 10 }
        },
        exitTransition = {
            fadeOut(animationSpec = androidx.compose.animation.core.tween(180))
        },
        popEnterTransition = {
            fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) +
                    slideInHorizontally(animationSpec = androidx.compose.animation.core.tween(220)) { -it / 10 }
        },
        popExitTransition = {
            fadeOut(animationSpec = androidx.compose.animation.core.tween(180)) +
                    slideOutHorizontally(animationSpec = androidx.compose.animation.core.tween(180)) { it / 10 }
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                }
            )
        }

        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStack ->
            val sessionId = backStack.arguments?.getLong("sessionId") ?: return@composable
            SessionDetailScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen()
        }

        composable(Screen.Notes.route) {
            NotesScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onNewNote = {
                    navController.navigate(Screen.NoteDetail.createRoute(-1L))
                }
            )
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStack ->
            val noteId = backStack.arguments?.getLong("noteId") ?: -1L
            NoteDetailScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Pantallas donde NO se muestra el bottom nav (detalle)
    val hideBottomBarRoutes = setOf(
        Screen.SessionDetail.route,
        Screen.NoteDetail.route,
        Screen.Login.route,
        Screen.Register.route,
        Screen.ForgotPassword.route
    )

    val showBottomBar = hideBottomBarRoutes.none { route ->
        currentDestination?.route?.startsWith(route.substringBefore("{")) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(tonalElevation = 0.dp) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(item.label, style = MaterialTheme.typography.labelSmall)
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
