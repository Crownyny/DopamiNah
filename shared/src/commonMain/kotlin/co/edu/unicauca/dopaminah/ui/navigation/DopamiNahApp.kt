package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme
import co.edu.unicauca.dopaminah.ui.screens.dashboard.DashboardScreen

@Composable
fun DopamiNahApp() {
    DopamiNahTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DashboardScreen()
        }
    }
}
