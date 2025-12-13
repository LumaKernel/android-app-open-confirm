package dev.luma.appopenconfirmation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.luma.appopenconfirmation.ui.screen.AppSelectionScreen
import dev.luma.appopenconfirmation.ui.screen.MainScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object AppSelection : Screen("app_selection")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToAppSelection = {
                    navController.navigate(Screen.AppSelection.route)
                }
            )
        }

        composable(Screen.AppSelection.route) {
            AppSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
