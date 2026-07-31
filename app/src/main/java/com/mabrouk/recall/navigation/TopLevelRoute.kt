package com.mabrouk.recall.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Top-level Navigation 3 keys. Serializable [NavKey]s are required for
 * [androidx.navigation3.runtime.rememberNavBackStack] to survive config change
 * and process death. UI metadata (label/icon) lives in [TopLevelDestination].
 */
@Serializable
sealed interface TopLevelRoute : NavKey {
    @Serializable
    data object Capture : TopLevelRoute

    @Serializable
    data object Library : TopLevelRoute

    @Serializable
    data object Ask : TopLevelRoute

    @Serializable
    data object Lab : TopLevelRoute

    companion object {
        val entries: List<TopLevelRoute> = listOf(Capture, Library, Ask, Lab)
    }
}

data class TopLevelDestination(
    val route: TopLevelRoute,
    val label: String,
    val icon: ImageVector,
)

val TopLevelDestinations: List<TopLevelDestination> = listOf(
    TopLevelDestination(TopLevelRoute.Capture, "Capture", Icons.Filled.AddCircle),
    TopLevelDestination(TopLevelRoute.Library, "Library", Icons.AutoMirrored.Filled.MenuBook),
    TopLevelDestination(TopLevelRoute.Ask, "Ask", Icons.Filled.QuestionAnswer),
    TopLevelDestination(TopLevelRoute.Lab, "Lab", Icons.Filled.Science),
)
