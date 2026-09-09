package com.vc.composecore.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vc.composecore.theme.CoreAnimationDuration

/**
 * Marker interface for strongly-typed navigation destinations.
 */
interface NavRoute {
    val route: String
    val arguments: List<NamedNavArgument> get() = emptyList()
    val deepLinks: List<NavDeepLink> get() = emptyList()
}

/**
 * High-level navigator abstraction decoupling ViewModels and screens from raw NavController.
 */
class CoreNavigator(val navController: NavHostController) {

    fun navigate(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = true,
        restoreState: Boolean = true
    ) {
        navController.navigate(route) {
            if (popUpToRoute != null) {
                popUpTo(popUpToRoute) {
                    this.inclusive = inclusive
                    this.saveState = true
                }
            }
            launchSingleTop = singleTop
            this.restoreState = restoreState
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun <T> navigateBackWithResult(key: String, result: T) {
        navController.previousBackStackEntry?.savedStateHandle?.set(key, result)
        navController.popBackStack()
    }

    fun <T> getNavigationResult(key: String): T? {
        return navController.currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }

    fun clearBackStack(rootRoute: String) {
        navController.navigate(rootRoute) {
            popUpTo(0) { inclusive = true }
        }
    }
}

@Composable
fun rememberCoreNavigator(navController: NavHostController = rememberNavController()): CoreNavigator {
    return remember(navController) { CoreNavigator(navController) }
}

/**
 * Standard animated NavHost wrapper with modern transitions.
 */
@Composable
fun CoreNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        fadeIn(animationSpec = tween(CoreAnimationDuration.normal)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(CoreAnimationDuration.normal))
    },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        fadeOut(animationSpec = tween(CoreAnimationDuration.normal)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(CoreAnimationDuration.normal))
    },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        fadeIn(animationSpec = tween(CoreAnimationDuration.normal)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(CoreAnimationDuration.normal))
    },
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        fadeOut(animationSpec = tween(CoreAnimationDuration.normal)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(CoreAnimationDuration.normal))
    },
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        builder = builder
    )
}

/**
 * Generic deep-link router and pending destination store for post-login redirection.
 */
class CoreDeepLinkHandler {
    private var pendingDestination: String? = null

    fun setPendingDestination(route: String) {
        pendingDestination = route
    }

    fun consumePendingDestination(): String? {
        val dest = pendingDestination
        pendingDestination = null
        return dest
    }

    fun hasPendingDestination(): Boolean = pendingDestination != null
}
