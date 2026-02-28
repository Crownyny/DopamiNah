package co.edu.unicauca.DopamiNah.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.unicauca.DopamiNah.ui.screens.dashboard.components.DailyUnlocksCard
import co.edu.unicauca.DopamiNah.ui.screens.dashboard.components.HeaderSection
import co.edu.unicauca.DopamiNah.ui.screens.dashboard.components.MostUsedAppsSection

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar / Header region
        HeaderSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Main content area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                DailyUnlocksCard()
                Spacer(modifier = Modifier.height(24.dp))
                MostUsedAppsSection()
            }
        }
    }
}
