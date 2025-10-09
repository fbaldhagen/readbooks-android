package com.fbaldhagen.readbooks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val arguments: List<NamedNavArgument> = emptyList()
) {

    // -- BOTTOM SCREENS --
    data object Home : Screen(
        route = "home",
        label = "Home",
        icon = Icons.Default.Home
    )

    data object Library : Screen(
        route = "library",
        label = "Library",
        icon = Icons.AutoMirrored.Filled.MenuBook
    )

    data object Discover : Screen(
        route = "discover",
        label = "Discover",
        icon = Icons.Default.FindReplace
    )

    data object Profile : Screen(
        route = "profile",
        label = "Profile",
        icon = Icons.Default.Person
    )

    // -- END BOTTOM SCREENS --

    data object Progress : Screen(
        route = "progress",
        label = "Progress",
    )

    data object Debug : Screen(
        route = "debug",
        label = "Debug",
        icon = Icons.Default.BugReport
    )

    data object BookDetails : Screen(
        route = "book_details?localId={$LOCAL_ID_ARG}&remoteId={$REMOTE_ID_ARG}",
        label = "Book Details",
        arguments = listOf(
            navArgument(LOCAL_ID_ARG) {
                type = NavType.LongType
                defaultValue = 0L
            },
            navArgument(REMOTE_ID_ARG) {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {
        fun createRouteForLocal(localId: Long): String = "book_details?localId=$localId"
        fun createRouteForRemote(remoteId: String): String = "book_details?remoteId=$remoteId"
    }

    data object TableOfContents : Screen(
        route = "toc/{$BOOK_ID_ARG}",
        label = "Table of Contents",
        arguments = listOf(navArgument(BOOK_ID_ARG) { type = NavType.LongType })
    ) {
        fun createRoute(bookId: Long): String = "toc/$bookId"
    }

    companion object {
        const val BOOK_ID_ARG = "bookId"
        const val LOCAL_ID_ARG = "localId"
        const val REMOTE_ID_ARG = "remoteId"
    }
}