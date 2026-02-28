package co.edu.unicauca.DopamiNah.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.edu.unicauca.DopamiNah.ui.screens.dashboard.DashboardScreen
import co.edu.unicauca.DopamiNah.ui.theme.DopaminahPurple

@Composable
fun DopamiNahApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { DopamiNahBottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
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
