package com.serdigital.pataditas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Rutas de navegación tipadas y centralizadas.
 */
sealed class Screen(val route: String) {
    // Bottom nav
    data object Home : Screen("home")
    data object History : Screen("history")
    data object Stats : Screen("stats")
    data object Notes : Screen("notes")
    data object Profile : Screen("profile")

    // Auth (placeholders para Firebase)
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")

    // Detalle
    data object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: Long) = "session_detail/$sessionId"
    }
    data object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long) = "note_detail/$noteId"
    }
}

/**
 * Items de la barra de navegación inferior.
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        label = "Inicio",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItem(
        screen = Screen.History,
        label = "Historial",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    ),
    BottomNavItem(
        screen = Screen.Stats,
        label = "Estadísticas",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    ),
    BottomNavItem(
        screen = Screen.Notes,
        label = "Notas",
        selectedIcon = Icons.Filled.Note,
        unselectedIcon = Icons.Outlined.Note
    ),
    BottomNavItem(
        screen = Screen.Profile,
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)
