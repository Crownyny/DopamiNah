package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import co.edu.unicauca.dopaminah.ui.screens.dashboard.DashboardScreen
import co.edu.unicauca.dopaminah.ui.screens.onboarding.OnboardingPermissionScreen
import co.edu.unicauca.dopaminah.ui.screens.settings.SettingsScreen
import co.edu.unicauca.dopaminah.ui.screens.achievements.AchievementsScreen
import co.edu.unicauca.dopaminah.ui.screens.goals.GoalsScreen

import co.edu.unicauca.dopaminah.data.repository.DeviceUsageRepositoryImpl

/**
 * Main Composable entry point for the app's UI and Navigation.
 * * Key Features:
 * - Dynamic Start Destination: Checks for [hasUsageStatsPermission] to decide whether 
 * to show the Onboarding/Permission flow or the main Dashboard.
 * - Navigation Host: Manages the transitions between all primary screens.
 * - Conditional Bottom Bar: Automatically hides the navigation bar when the user 
 * is on the Onboarding screen to ensure a focused user experience.
 */
@Composable
fun DopamiNahApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Quick check to determine the initial route
    val hasPermission = remember {
        DeviceUsageRepositoryImpl(context).hasUsageStatsPermission()
    }

    val startRoute = if (hasPermission) Screen.Dashboard.route else Screen.OnboardingPermission.route

    Scaffold(
        bottomBar = {
            // Only show bottom bar if we are not in onboarding
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute != Screen.OnboardingPermission.route) {
                DopamiNahBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.OnboardingPermission.route) {
                OnboardingPermissionScreen(
                    onPermissionGranted = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.OnboardingPermission.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Stats.route) {
                co.edu.unicauca.dopaminah.ui.screens.stats.StatsScreen()
            }
            composable(Screen.Goals.route) {
                GoalsScreen()
            }
            composable(Screen.Achievements.route) {
                AchievementsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

/**
 * Custom Bottom Navigation Bar for the application.
 * * Implementation Details:
 * - Uses [NavigationBarItem] to represent each primary destination.
 * - Navigation logic includes [launchSingleTop] and [restoreState] to prevent 
 * multiple instances of the same screen and preserve UI state when switching tabs.
 */
@Composable
fun DopamiNahBottomBar(navController: NavHostController) {
    val colorScheme = MaterialTheme.colorScheme
    val items = listOf(
        Screen.Dashboard,
        Screen.Stats,
        Screen.Goals,
        Screen.Achievements,
        Screen.Settings
    )

    NavigationBar(
        containerColor = colorScheme.surface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.primary,
                    selectedTextColor = colorScheme.primary,
                    unselectedIconColor = colorScheme.onSurfaceVariant,
                    unselectedTextColor = colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
