package com.mabrouk.recall.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mabrouk.recall.feature.ask.AskScreen
import com.mabrouk.recall.feature.capture.CaptureScreen
import com.mabrouk.recall.feature.lab.LabScreen
import com.mabrouk.recall.feature.library.LibraryScreen
import com.mabrouk.recall.navigation.TopLevelDestinations
import com.mabrouk.recall.navigation.TopLevelRoute
import com.mabrouk.recall.navigation.rememberNavigationState
import com.mabrouk.recall.navigation.rememberNavigator

@Composable
fun RecallApp(
    modifier: Modifier = Modifier,
    viewModel: RecallViewModel = hiltViewModel(),
) {
    val navigationState = rememberNavigationState(startRoute = TopLevelRoute.Library)
    val navigator = rememberNavigator(navigationState)
    val cloudAiEnabled by viewModel.cloudAiEnabled.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    val entryProvider = entryProvider<NavKey> {
        entry<TopLevelRoute.Capture> { CaptureScreen() }
        entry<TopLevelRoute.Library> { LibraryScreen() }
        entry<TopLevelRoute.Ask> { AskScreen() }
        entry<TopLevelRoute.Lab> {
            LabScreen(
                cloudAiEnabled = cloudAiEnabled,
                onCloudAiEnabledChange = viewModel::setCloudAiEnabled,
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                TopLevelDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = destination.route == navigationState.currentTopLevel,
                        onClick = { navigator.navigate(destination.route) },
                        icon = {
                            Icon(destination.icon, contentDescription = destination.label)
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = {
                if (!navigator.goBack()) {
                    activity?.finish()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}
