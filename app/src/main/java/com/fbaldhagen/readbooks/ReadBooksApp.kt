package com.fbaldhagen.readbooks

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fbaldhagen.readbooks.ui.common.TopBarBackground
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.SearchAppBar
import com.fbaldhagen.readbooks.ui.main.MainViewModel
import com.fbaldhagen.readbooks.ui.main.TtsMiniPlayer

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

    val mainViewModel: MainViewModel = hiltViewModel()
    val miniPlayerState by mainViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(currentDestination) {
        when (currentRoute) {
            Screen.Home.route -> {
                topBarState = TopBarState.Standard(
                    background = TopBarBackground.Scrim
                )
            }

            Screen.Library.route -> {
                topBarState = TopBarState.Standard()
            }

            Screen.Discover.route -> {
                topBarState = TopBarState.Standard(title = "Discover")
            }

            Screen.Profile.route -> {
                topBarState = TopBarState.Standard(title = "Profile")
            }

            else -> {
                topBarState = TopBarState.Standard()
            }
        }
    }

    Scaffold(
        topBar = {
            when (val state = topBarState) {
                is TopBarState.Standard -> {
                    when (state.background) {
                        is TopBarBackground.Solid -> {
                            TopAppBar(
                                title = { },
                                actions = state.actions,
                                colors = TopAppBarDefaults.topAppBarColors()
                            )
                        }
                        is TopBarBackground.Transparent -> {
                            TopAppBar(
                                title = { },
                                actions = state.actions,
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        is TopBarBackground.Scrim -> {
                            Box {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                TopAppBar(
                                    title = { },
                                    actions = state.actions,
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color.Transparent,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
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
                        },
                        actions = state.actions
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
            Column {
                if (miniPlayerState.isVisible) {
                    val miniPlayerModifier = if (shouldShowBottomBar) {
                        Modifier
                    } else {
                        Modifier.navigationBarsPadding()
                    }
                    TtsMiniPlayer(
                        uiState = miniPlayerState,
                        onPlayPause = mainViewModel::onPlayPause,
                        onClose = mainViewModel::onClose,
                        modifier = miniPlayerModifier
                    )
                }

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
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            contentPadding = innerPadding,
            onConfigureTopBar = { newState -> topBarState = newState }
        )
    }
}