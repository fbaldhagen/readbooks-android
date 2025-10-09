package com.fbaldhagen.readbooks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fbaldhagen.readbooks.ui.bookdetails.BookDetailsScreen
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.debug.DebugScreen
import com.fbaldhagen.readbooks.ui.discover.DiscoverScreen
import com.fbaldhagen.readbooks.ui.home.HomeScreen
import com.fbaldhagen.readbooks.ui.library.LibraryScreen
import com.fbaldhagen.readbooks.ui.profile.ProfileScreen
import com.fbaldhagen.readbooks.ui.progress.ProgressScreen
import com.fbaldhagen.readbooks.ui.reader.ReaderActivity
import com.fbaldhagen.readbooks.ui.toc.TableOfContentsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onConfigureTopBar: (TopBarState) -> Unit
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                contentPadding = contentPadding,
                onConfigureTopBar = onConfigureTopBar,
                onBookClick = { localId ->
                    navController.navigate(Screen.BookDetails.createRouteForLocal(localId))
                },
                onDiscoverBookClick = { remoteId ->
                    navController.navigate(Screen.BookDetails.createRouteForRemote(remoteId))
                },
                onSeeAllClick = {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onDiscoverMoreClicked = {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(route = Screen.Library.route) {
            LibraryScreen(
                onConfigureTopBar = onConfigureTopBar,
                contentPadding = contentPadding,
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRouteForLocal(bookId))
                },
                onNavigateToDiscover = {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(route = Screen.Discover.route) {
            DiscoverScreen(
                onConfigureTopBar = onConfigureTopBar,
                contentPadding = contentPadding,
                onBookClick = { remoteId ->
                    navController.navigate(Screen.BookDetails.createRouteForRemote(remoteId))
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onConfigureTopBar = onConfigureTopBar,
                contentPadding = contentPadding,
                onNavigateToDebug = {
                    navController.navigate(Screen.Debug.route)
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.Progress.route)
                }
            )
        }

        composable(route = Screen.Debug.route) {
            DebugScreen(contentPadding = contentPadding)
        }

        composable(route = Screen.Progress.route) {
            ProgressScreen(
                onConfigureTopBar = onConfigureTopBar,
                contentPadding = contentPadding
            )
        }

        composable(
            route = Screen.BookDetails.route,
            arguments = Screen.BookDetails.arguments
        ) {
            BookDetailsScreen(
                onConfigureTopBar = onConfigureTopBar,
                contentPadding = contentPadding,
                onNavigateBack = { navController.popBackStack() },
                onReadClick = { bookId ->
                    val intent = ReaderActivity.createIntent(context, bookId)
                    val optionsSlide = ActivityOptionsCompat.makeCustomAnimation(
                        context,
                        R.anim.slide_in_bottom,
                        R.anim.fade_out
                    )
                    context.startActivity(intent, optionsSlide.toBundle())
                },
                onTocClick = { bookId ->
                    navController.navigate(Screen.TableOfContents.createRoute(bookId))
                },
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRouteForLocal(bookId))
                }
            )
        }

        composable(
            route = Screen.TableOfContents.route,
            arguments = Screen.TableOfContents.arguments
        ) { navBackStackEntry ->
            TableOfContentsScreen(
                onConfigureDetailTitle = { title -> onConfigureTopBar(TopBarState.Detail(title)) },
                contentPadding = contentPadding,
                onItemClick = { href ->
                    val bookId = navBackStackEntry.arguments?.getLong(Screen.BOOK_ID_ARG)
                    if (bookId != null && bookId != 0L) {
                        val intent = ReaderActivity.createIntent(context, bookId, href.toString())
                        val optionsFadeScale = ActivityOptionsCompat.makeCustomAnimation(
                            context,
                            R.anim.scale_in,
                            R.anim.fade_out
                        )
                        context.startActivity(intent, optionsFadeScale.toBundle())
                    }
                }
            )
        }
    }
}