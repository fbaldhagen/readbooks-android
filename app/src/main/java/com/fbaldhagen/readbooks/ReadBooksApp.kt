package com.fbaldhagen.readbooks

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.SearchAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadBooksApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val bottomBarScreens = listOf(Screen.Home, Screen.Library, Screen.Discover, Screen.Profile)
    val shouldShowBottomBar = currentRoute in bottomBarScreens.map { it.route }

    var topBarState: TopBarState by remember { mutableStateOf(TopBarState.Standard()) }

    Scaffold(
        topBar = {
            when (val state = topBarState) {
                is TopBarState.Standard -> {
                    TopAppBar(
                        title = { /* Intentionally blank for top-level screens */ },
                        actions = state.actions
                    )
                }
                is TopBarState.Detail -> {
                    TopAppBar(
                        title = {
                            AnimatedContent(targetState = state.title, label = "DetailTitle") { title ->
                                Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    )
                }
                is TopBarState.Search -> {
                    SearchAppBar(
                        query = state.query,
                        onQueryChange = state.onQueryChange,
                        onClose = state.onClose,
                        hint = state.hint
                    )
                }
            }
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = screen.icon!!, contentDescription = screen.label) },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            contentPadding = innerPadding,
            onConfigureTopBar = { newState -> topBarState = newState }
        )
    }
}