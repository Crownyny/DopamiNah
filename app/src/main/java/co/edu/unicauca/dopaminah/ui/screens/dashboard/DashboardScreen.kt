package co.edu.unicauca.dopaminah.ui.screens.dashboard

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.DailyUnlocksCard
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.HeaderSection
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.MostUsedAppsSection
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val gamificationStats by viewModel.gamificationState.collectAsState()
    val dailyUnlocks by viewModel.dailyUnlocks.collectAsState()
    val yesterdayUnlocks by viewModel.yesterdayUnlocks.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAndIncrementStreak()
                viewModel.refreshStats()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar / Header region passing the dynamic stats
        HeaderSection(gamificationStats = gamificationStats)

        // Main content area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
        ) {
            item {
                val hasPermission by viewModel.hasUsagePermission.collectAsState()
                val context = LocalContext.current

                DailyUnlocksCard(
                    dailyUnlocks = dailyUnlocks,
                    yesterdayUnlocks = yesterdayUnlocks
                )
                Spacer(modifier = Modifier.height(24.dp))
                MostUsedAppsSection()
            }
        }
    }
}
