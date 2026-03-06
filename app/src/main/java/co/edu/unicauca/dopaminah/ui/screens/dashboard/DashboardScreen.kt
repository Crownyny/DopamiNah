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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.UsageSummaryCarousel
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.HeaderSection
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.MostUsedAppsSection
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val gamificationStats by viewModel.gamificationState.collectAsState()
    val dailyUnlocks by viewModel.dailyUnlocks.collectAsState()
    val yesterdayUnlocks by viewModel.yesterdayUnlocks.collectAsState()
    val totalDailyUsageMs by viewModel.totalDailyUsageMs.collectAsState()
    val hasPermission by viewModel.hasUsagePermission.collectAsState()
    val dailyUsageStats by viewModel.dailyUsageStats.collectAsState()
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

    DashboardContent(
        gamificationStats = gamificationStats,
        dailyUnlocks = dailyUnlocks,
        yesterdayUnlocks = yesterdayUnlocks,
        totalDailyUsageMs = totalDailyUsageMs,
        hasPermission = hasPermission,
        dailyUsageStats = dailyUsageStats,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    gamificationStats: UserGamificationStats,
    dailyUnlocks: Int,
    yesterdayUnlocks: Int,
    totalDailyUsageMs: Long,
    hasPermission: Boolean,
    dailyUsageStats: List<AppUsageSummary>,
    modifier: Modifier = Modifier
) {
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
                UsageSummaryCarousel(
                    dailyUnlocks = dailyUnlocks,
                    yesterdayUnlocks = yesterdayUnlocks,
                    totalDailyUsageMs = totalDailyUsageMs
                )
                Spacer(modifier = Modifier.height(24.dp))
                MostUsedAppsSection(
                    dailyUsageStats = dailyUsageStats,
                    hasPermission = hasPermission
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DopamiNahTheme {
        DashboardContent(
            gamificationStats = UserGamificationStats(
                level = 3,
                currentPoints = 150,
                pointsToNextLevel = 300,
                activeBadges = listOf("Focus Master", "Early Bird")
            ),
            dailyUnlocks = 12,
            yesterdayUnlocks = 15,
            totalDailyUsageMs = 7200000L,
            hasPermission = true,
            dailyUsageStats = listOf(
                AppUsageSummary("com.instagram.android", "Instagram", 3600000L, 10, 0L),
                AppUsageSummary("com.whatsapp", "WhatsApp", 1800000L, 25, 0L),
                AppUsageSummary("com.youtube", "YouTube", 1200000L, 5, 0L)
            )
        )
    }
}


