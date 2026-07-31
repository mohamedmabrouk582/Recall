package com.mabrouk.recall.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * Create navigation state that persists config changes and process death.
 *
 * Each top-level route gets its own [rememberNavBackStack]. The [NavigationState]
 * wrapper itself uses [remember] (not [androidx.compose.runtime.retain.retain]) so
 * it rebinds to restored saveable children after composition recreate. Adapted
 * from the Navigation 3 multiple-backstacks recipe ("exit through start" pattern).
 */
@Composable
fun rememberNavigationState(
    startRoute: TopLevelRoute,
    topLevelRoutes: Set<TopLevelRoute> = TopLevelRoute.entries.toSet(),
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) {
        mutableStateOf<NavKey>(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

/**
 * State holder for multi-tab navigation. Mutated via [Navigator], not in place.
 */
class NavigationState(
    val startRoute: TopLevelRoute,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<TopLevelRoute, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute

    val currentTopLevel: TopLevelRoute
        get() = topLevelRoute as TopLevelRoute

    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
    ): List<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            )
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider,
            )
        }

        return getTopLevelRoutesInUse()
            .flatMap { route -> decoratedEntries[route].orEmpty() }
    }

    /**
     * Start route is always first ("exit through home"). At most one other
     * top-level route is active. Unused tabs still retain their [NavBackStack].
     */
    private fun getTopLevelRoutesInUse(): List<TopLevelRoute> =
        if (currentTopLevel == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, currentTopLevel)
        }
}

/** Applies navigation events to [NavigationState]. */
class Navigator(private val state: NavigationState) {
    fun navigate(route: NavKey) {
        if (route is TopLevelRoute && route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.currentTopLevel]?.add(route)
        }
    }

    /**
     * @return false when already at the start route root (caller may finish Activity).
     */
    fun goBack(): Boolean {
        val currentStack = state.backStacks[state.currentTopLevel]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        return if (currentRoute == state.topLevelRoute) {
            if (state.currentTopLevel == state.startRoute) {
                false
            } else {
                state.topLevelRoute = state.startRoute
                true
            }
        } else {
            currentStack.removeLastOrNull()
            true
        }
    }
}

@Composable
fun rememberNavigator(state: NavigationState): Navigator =
    remember(state) { Navigator(state) }
