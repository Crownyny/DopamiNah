package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.data.repository.DeviceUsageRepositoryImpl

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
                // To be implemented
                Text("Stats Screen")
            }
            composable(Screen.Goals.route) {
                // To be implemented
                Text("Goals Screen")
            }
            composable(Screen.Achievements.route) {
                // To be implemented
                Text("Achievements Screen")
            }
            composable(Screen.Settings.route) {
                // To be implemented
                Text("Settings Screen")
            }
        }
    }
}

@Composable
fun DopamiNahBottomBar(navController: NavHostController) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Stats,
        Screen.Goals,
        Screen.Achievements,
        Screen.Settings
    )

    NavigationBar(
        containerColor = Color.White
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
                    selectedIconColor = DopaminahPurple,
                    selectedTextColor = DopaminahPurple,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
